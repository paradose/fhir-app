package com.teamohno;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class PatientRecord{
    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private String birthDate;
    private String address;
//    private Cholesterol cholesterolMeasurement;
        // pass this in model/controller before start getting patients....?
    private ArrayList<MeasurementRecording> measurementRecordings;

    public PatientRecord(String patientId, String patientFirstName, String patientLastName,String patientGender, String patientBirthDate, String patientAddress){
        id=patientId;
        firstName = patientFirstName;
        lastName = patientLastName;
       // cholesterolMeasurement = new Cholesterol(BigDecimal.ZERO, null);
        measurementRecordings = new ArrayList<MeasurementRecording>();
        gender = patientGender;
        birthDate = patientBirthDate;
        address = patientAddress;
    }

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

    public void addMeasurementObject(ArrayList<MeasurementType> newList){
        for (int i = 0; i < newList.size(); i++) {
            MeasurementRecording newEmptyRecording = new MeasurementRecording(BigDecimal.ZERO, new Date(2323223232L), newList.get(i));
            measurementRecordings.add(newEmptyRecording);
        }
    }

    public MeasurementRecording getMeasurement(MeasurementType newType){
        // make a default measurement object to return if no measurements
        MeasurementRecording returnRecording = new MeasurementRecording(BigDecimal.ZERO, new Date(2323223232L), new Cholesterol());
        System.out.println("Size of measurement reocrding list: " + measurementRecordings.size());
        for (int i = 0; i < measurementRecordings.size(); i++) {
                if(measurementRecordings.get(i).getType().type == newType.type){
                    returnRecording = measurementRecordings.get(i);
                }
            }
        if(returnRecording.equals(null)) {
            System.out.println("Error: Patient doesn't have a recording for " + newType.getName());
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

    /*
    public Cholesterol getCholesterolMeasurement() {
        return cholesterolMeasurement;
    }
    public void setCholesterolMeasurement(BigDecimal newCholValue, Date newCholDate) {
        this.cholesterolMeasurement.setCholesterolValue(newCholValue);
        this.cholesterolMeasurement.setDateMeasured(newCholDate);
    }
    */
    public String toString(){
        return "Patient ID: " + id + ", " + firstName + " " + lastName + " ,"+ gender+", Address: "+address+", Birthdate: "+birthDate;
    }
}
