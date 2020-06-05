package com.teamohno;

import java.util.ArrayList;

public abstract class MeasurementType {
    // Instance variables
    protected String fhirCode;
    protected String childCode;

    protected String name;
    protected ArrayList<PatientSubject> monitorredSubjects;
    protected Type type;
    protected int numberStoredRecordings;
    private double measurementAverage;
    private double measurementTotal;
    protected enum Type{
        CHOLESTEROL,
        BLOODPRESSURE;
    }

    // Constructor
    public MeasurementType(String newName, String newCode){
        this.fhirCode = newCode;
        this.name = newName;
        this.monitorredSubjects = new ArrayList<>();
        measurementTotal = 0;
        measurementAverage = 0;
        childCode = "n/a";
    }

    // Accessors and Mutators

    public String getFhirCode() {
        return fhirCode;
    }

    public void setFhirCode(String fhirCode) {
        this.fhirCode = fhirCode;
    }

    public String getChildCode(){return childCode;}

    public void setChildCode(String newChildCode){this.childCode = newChildCode;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType(){return type;}

    public ArrayList<PatientSubject> getMonitorredSubjects() {
        return monitorredSubjects;
    }

    public int getNumberStoredRecordings(){return numberStoredRecordings;}

    public void setNumberStoredRecordings(int newNumber){this.numberStoredRecordings = newNumber;}

    public double getAverage(){return measurementAverage;}

    // Loops through valid subjects and calculates new average
    public void updateAverage(){
        measurementTotal = 0;
        if(getValidMonitored() > 1) {
            for (int i = 0; i < monitorredSubjects.size(); i++) {
                if (monitorredSubjects.get(i).getActive()) {
                    measurementTotal += monitorredSubjects.get(i).getState().getMeasurement(this).getMeasurementValue().doubleValue();
                }
            }
            measurementAverage = measurementTotal / getValidMonitored();
            System.out.println(getName() + " average: " + measurementAverage);
        }
        else{
            measurementAverage = Double.POSITIVE_INFINITY;
        }
    }

    // Checks number of valid subjects
    public int getValidMonitored(){
        int numberOfValid = 0;
        for (int i=0;i<monitorredSubjects.size();i++){
            if (monitorredSubjects.get(i).getActive()) {
                numberOfValid++;
            }
        }
        return  numberOfValid;
    }
}
