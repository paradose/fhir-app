package com.teamohno;

import java.util.ArrayList;

public abstract class MeasurementType {
    // Instance variables
    protected String fhirCode;

    protected ArrayList<String> listChildCode;
    protected ArrayList<String> childTypeNames;

    protected String name;
    protected ArrayList<PatientSubject> monitorredSubjects;
    protected Type type;
    protected int numberStoredRecordings;
    private double measurementAverage;
    private double measurementTotal;

    // current minimum value (for child values)

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
//        childCode = "n/a";

        listChildCode = new ArrayList<>();
        childTypeNames = new ArrayList<>();
    }

    // Accessors and Mutators
    public ArrayList<String> getChildTypeNames(){return childTypeNames;}
    public void addChildCode(String newCode){
        listChildCode.add(newCode);}
    public ArrayList<String> getListChildCode(){return listChildCode;}
    public int getIndexChild(String childCode){
        int index = -1;
        for (int i = 0; i < listChildCode.size(); i++) {
            if(listChildCode.get(i).equals(childCode)){
                index = i;
            }
        }
        if(index == -1){
            System.out.println("Error this type does not contain the child code: " + childCode);
        }
        return index;
    }
    public int getComponentSize(){return getListChildCode().size();}

    public String getFhirCode() {
        return fhirCode;
    }

    public void setFhirCode(String fhirCode) {
        this.fhirCode = fhirCode;
    }

    // don't need(?)
//    public String getChildCode(){return childCode;}
//    public void setChildCode(String newChildCode){this.childCode = newChildCode;}

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
