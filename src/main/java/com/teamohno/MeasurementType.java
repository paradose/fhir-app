package com.teamohno;

import java.util.ArrayList;

public abstract class MeasurementType {
    protected String fhirCode;
    protected String name;
    protected ArrayList<PatientSubject> monitorredSubjects;
    protected Type type;

    protected enum Type{
        CHOLESTEROL;
    }

    public MeasurementType(String newName, String newCode){
        this.fhirCode = newCode;
        this.name = newName;
        this.monitorredSubjects = new ArrayList<>();
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

    public ArrayList<PatientSubject> getMonitorredSubjects() {
        return monitorredSubjects;
    }
}
