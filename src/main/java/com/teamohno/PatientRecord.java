package com.teamohno;

public class PatientRecord {
    private String id;
    private String firstName;
    private String lastName;

    public PatientRecord(String patientId, String patientFirstName, String patientLastName){
        id=patientId;
        firstName = patientFirstName;
        lastName = patientLastName;
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

    public String toString(){
        return "Patient ID: " + id + ", " + firstName + " " + lastName;
    }
}
