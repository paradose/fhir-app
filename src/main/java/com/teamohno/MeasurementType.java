package com.teamohno;

import java.util.ArrayList;

public abstract class MeasurementType {
    protected String fhirCode;
    protected String name;
    protected ArrayList<PatientSubject> monitorredSubjects;
    protected Type type;
    private double measurementAverage;
    private double measurementTotal;
    protected enum Type{
        CHOLESTEROL;
    }

    public MeasurementType(String newName, String newCode){
        this.fhirCode = newCode;
        this.name = newName;
        this.monitorredSubjects = new ArrayList<>();
        measurementTotal = 0;
        measurementAverage = 0;
    }

    public String getFhirCode() {
        return fhirCode;
    }

    public void setFhirCode(String fhirCode) {
        this.fhirCode = fhirCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void updateAverage(double oldValue, double newValue) {
        measurementTotal  = measurementTotal-oldValue+newValue;
        measurementAverage = measurementTotal/monitorredSubjects.size();
        System.out.println(measurementAverage);
    }
    public double getAverage(){return measurementAverage;}

    public ArrayList<PatientSubject> getMonitorredSubjects() {
        return monitorredSubjects;
    }

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
