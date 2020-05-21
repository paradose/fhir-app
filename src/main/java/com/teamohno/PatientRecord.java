package com.teamohno;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    // Constructor
    public PatientRecord(String patientId, String patientFirstName, String patientLastName,String patientGender, String patientBirthDate, String patientAddress){
        id=patientId;
        firstName = patientFirstName;
        lastName = patientLastName;
        measurementRecordings = new ArrayList<>();
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
    public void addMeasurementObject(ArrayList<MeasurementType> newList){
        for (int i = 0; i < newList.size(); i++) {
            MeasurementRecording newEmptyRecording = new MeasurementRecording(BigDecimal.ONE.negate(), new Date(2323223232L), newList.get(i));
            measurementRecordings.add(newEmptyRecording);
        }
    }

    public MeasurementRecording getMeasurement(MeasurementType newType){
        // make a default measurement object to return if no measurements
        MeasurementRecording returnRecording = new MeasurementRecording(BigDecimal.ZERO, new Date(2323223232L), newType);
        System.out.println("Size of measurement reocrding list: " + measurementRecordings.size());
        for (int i = 0; i < measurementRecordings.size(); i++) {
                if(measurementRecordings.get(i).getType().type == newType.type){
                    returnRecording = measurementRecordings.get(i);
                }
            }
        if(returnRecording.getMeasurementValue().compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("Patient doesn't have a recording for " + newType.getName());
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

    public void resetRecording(MeasurementType newType){
        for (int i = 0; i < measurementRecordings.size(); i++) {
            if(measurementRecordings.get(i).getType().type == newType.type){
                System.out.println("Type about to set is correct");
                measurementRecordings.get(i).setMeasurementValue(BigDecimal.ONE.negate());
                measurementRecordings.get(i).setDateMeasured(new Date(2323223232L));
            }
            else{
                System.out.println("Type not matched");
            }
        }
    }

    public String toString(){
        return "Patient ID: " + id + ", " + firstName + " " + lastName + " ,"+ gender+", Address: "+address+", Birthdate: "+birthDate;
    }
}
