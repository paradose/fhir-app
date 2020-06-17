package com.teamohno;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PatientRecord{
    // Instance variables
    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private String birthDate;
    private String address;
    private ArrayList<MeasurementRecording> measurementRecordings;
    private ArrayList<ArrayList<MeasurementRecording>> listLastRecordings;

    // Constructor
    public PatientRecord(String patientId, String patientFirstName, String patientLastName,String patientGender, String patientBirthDate, String patientAddress){
        id=patientId;
        firstName = patientFirstName;
        lastName = patientLastName;
        measurementRecordings = new ArrayList<>();
        listLastRecordings = new ArrayList<>();
        gender = patientGender;
        birthDate = patientBirthDate;
        address = patientAddress;
    }

    // Accessors and Mutators
    public String getFirstName() {
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getId(){
        return id;
    }

    public String getGender(){
        return gender;
    }

    public String getBirthDate(){
        return birthDate;
    }

    public String getAddress(){
        return address;
    }

    // Add a type of measurement for a patient
    public void initialiseMeasurements(ArrayList<MeasurementType> newList){
        for (int i = 0; i < newList.size(); i++) {
            MeasurementRecording newEmptyRecording = new MeasurementRecording(BigDecimal.ZERO, new Date(2323223231L), newList.get(i));
            measurementRecordings.add(newEmptyRecording);
            ArrayList<MeasurementRecording> newLastRecording = new ArrayList<>();
            listLastRecordings.add(newLastRecording);
        }
    }

    //gets current measurement recording
    public MeasurementRecording getMeasurement(MeasurementType newType){
        // make a default measurement object to return if no measurements
        MeasurementRecording returnRecording = new MeasurementRecording(newType);
        System.out.println("Size of measurement reocrding list: " + measurementRecordings.size());
        for (int i = 0; i < measurementRecordings.size(); i++) {
                if(measurementRecordings.get(i).getType().type == newType.type){
                    returnRecording = measurementRecordings.get(i);
                }
            }

        if(returnRecording.getMeasurementValue().compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("Potential error with retrieving patients recording.");
            System.out.println("Current reading for this patient's " + newType.getName() + " is zero.");
        }
        return returnRecording;
    }

    public void setMeasurementRecordings(BigDecimal newValue, Date newDate, MeasurementType newType){
        for (int i = 0; i < measurementRecordings.size(); i++) {
            if(measurementRecordings.get(i).getType().type == newType.type){
                System.out.println("Type about to set is correct");
                measurementRecordings.get(i).setMeasurementValue(newValue);
                measurementRecordings.get(i).setDateMeasured(newDate);
            }
            else{
                System.out.println("Type not matched");
            }
        }
    }

    // push current into history list - parameter - MeasurementType
    public void pushNewRecordingHistory(MeasurementType newType){
        for (int i = measurementRecordings.size() - 1; i >= 0; i--) {
            if(measurementRecordings.get(i).getType().type.equals(newType.type)){
                MeasurementRecording newRecording = new MeasurementRecording(newType);
                newRecording.cloneRecording(measurementRecordings.get(i));
                // shuffle history -> add new recording at the end
                listLastRecordings.get(i).add(newRecording);
                if(listLastRecordings.get(i).size() > newType.getNumberStoredRecordings()) {
                    listLastRecordings.get(i).remove(0);
                }
            }
        }
    }

    public ArrayList<MeasurementRecording> getLastRecordings(MeasurementType newType){
        ArrayList<MeasurementRecording> returningList = new ArrayList<>();
        for (int i = 0; i < measurementRecordings.size(); i++) {
            if (measurementRecordings.get(i).getType().type == newType.type) {
                returningList = listLastRecordings.get(i);
            }
        }
        return returningList;
    }

    public void resetRecording(MeasurementType newType){
        for (int i = 0; i < measurementRecordings.size(); i++) {
            if(measurementRecordings.get(i).getType().type == newType.type){
                System.out.println("Type about to set is correct");
                measurementRecordings.get(i).setMeasurementValue(BigDecimal.ONE.negate());
                measurementRecordings.get(i).setDateMeasured(new Date(2323223231L));
            }
        }
    }

    public String toString(){
        return "Patient ID: " + id + ", " + firstName + " " + lastName + " ,"+ gender+", Address: "+address+", Birthdate: "+birthDate;
    }
}
