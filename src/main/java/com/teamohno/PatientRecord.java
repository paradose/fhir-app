package com.teamohno;

import java.util.Date;

public class PatientRecord {
    private String id;
    private String firstName;
    private String lastName;
    private Cholesterol cholesterolMeasurement;

    public PatientRecord(String patientId, String patientFirstName, String patientLastName){
        id=patientId;
        firstName = patientFirstName;
        lastName = patientLastName;
        cholesterolMeasurement = new Cholesterol(0, null);
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

    public Cholesterol getCholesterolMeasurement() {
        return cholesterolMeasurement;
    }

    public void addCholesterolMeasurement(int newCholValue, Date newCholDate) {
        this.cholesterolMeasurement.setCholesterolValue(newCholValue);
        this.cholesterolMeasurement.setDateMeasured(newCholDate);
    }

    public String toString(){
        return "Patient ID: " + id + ", " + firstName + " " + lastName;
    }
}
