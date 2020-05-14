package com.teamohno;

import java.math.BigDecimal;
import java.util.Date;

public class PatientRecord{
    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private String birthDate;
    private String address;
    private Cholesterol cholesterolMeasurement;

    public PatientRecord(String patientId, String patientFirstName, String patientLastName,String patientGender, String patientBirthDate, String patientAddress){
        id=patientId;
        firstName = patientFirstName;
        lastName = patientLastName;
        cholesterolMeasurement = new Cholesterol(BigDecimal.ZERO, null);
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

    public Cholesterol getCholesterolMeasurement() {
        return cholesterolMeasurement;
    }

    public void setCholesterolMeasurement(BigDecimal newCholValue, Date newCholDate) {
        this.cholesterolMeasurement.setCholesterolValue(newCholValue);
        this.cholesterolMeasurement.setDateMeasured(newCholDate);
    }

    public String toString(){
        return "Patient ID: " + id + ", " + firstName + " " + lastName + " ,"+ gender+", Address: "+address+", Birthdate: "+birthDate;
    }
}
