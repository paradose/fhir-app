package com.teamohno;

import java.util.ArrayList;

public abstract class MeasurementType {
    // Instance variables
    protected String fhirCode;

    protected ArrayList<String> listChildCode;
    protected ArrayList<String> childTypeNames;
    protected ArrayList<Constants.MeasurementType> childTypes;

    protected String name;
    protected ArrayList<PatientSubject> monitorredSubjects;
    protected Constants.MeasurementType type;
    protected int numberStoredRecordings;
    private double measurementAverage;
    private double measurementTotal;


    // current minimum value (for child values)
    protected ArrayList<Integer> minimumValues;

    // Constructor
    public MeasurementType(String newName, String newCode){
        this.fhirCode = newCode;
        this.name = newName;
        this.monitorredSubjects = new ArrayList<>();
        measurementTotal = 0;
        measurementAverage = 0;

        listChildCode = new ArrayList<>();
        childTypeNames = new ArrayList<>();
        childTypes = new ArrayList<>();
        minimumValues = new ArrayList<>();
    }

    // Accessors and Mutators
    public ArrayList<String> getChildTypeNames(){return childTypeNames;}
    public void addChildType(String newCode, String newName, Constants.MeasurementType newType){
        listChildCode.add(newCode);
        childTypeNames.add(newName);
        childTypes.add(newType);
        minimumValues.add(0);
    }
    public ArrayList<Constants.MeasurementType> getChildTypes(){return childTypes;}
    public ArrayList<String> getListChildCode(){return listChildCode;}
    public int getIndexChild(Constants.MeasurementType childType){
        int index = -1;
        for (int i = 0; i < childTypes.size(); i++) {
            if(childTypes.get(i).equals(childType)){
                index = i;
            }
        }
        if(index == -1){
            System.out.println("Error this type does not contain the child code: " + childType);
        }
        return index;
    }
    public int getComponentSize(){return getListChildCode().size();}
    public void setMinimumValue(int newValue, Constants.MeasurementType newType){
        int index = childTypes.indexOf(newType);
        if(index == -1){
            System.out.println("Error: could not find this measurement type");
        }
        minimumValues.set(index, newValue);
    }
    public int getMinVal(Constants.MeasurementType newType){
        int retMin = -1;
        int index = childTypes.indexOf(newType);
        if(index == -1){
            System.out.println("Error: could not find this measurement type");
        }
        retMin = minimumValues.get(index);
        return retMin;
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

    public Constants.MeasurementType getType(){return type;}

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
