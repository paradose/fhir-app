package com.teamohno;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Measure;
import org.hl7.fhir.r4.model.Observation;

import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.util.Date;

public class PatientSubject extends Subject {
    private PatientRecord patientState;
    private Server server;
    private boolean active;
    public PatientSubject(PatientRecord initialPatientData, Server inputServer){
        patientState = initialPatientData;
        server = inputServer;
        active = true;
    }
    public PatientRecord getState() {
        return patientState;
    }
    public boolean getActive(){ return active;}
    public void setState(PatientRecord patient) {
        patientState = patient;
    }

    public void updateMeasurementValue(MeasurementType newType) {
        String patientsId = patientState.getId();
        BigDecimal prevCholVal = patientState.getMeasurement(newType).getMeasurementValue();

        if(active) {
            System.out.println("Comparing using big decimal -> found out previous is NOT zero: previous:" + prevCholVal.toString());
            MeasurementRecording updatedMeasurement = server.retrieveMeasurement(patientsId, newType);
            if (updatedMeasurement.getMeasurementValue().compareTo(BigDecimal.ZERO) == 0 && prevCholVal.compareTo(BigDecimal.ZERO) == 0) {
                active = false;
            }
            System.out.println("Updated measurement value about to set: " + updatedMeasurement.getMeasurementValue());
            patientState.setMeasurementRecordings(updatedMeasurement.getMeasurementValue(), updatedMeasurement.getDateMeasured(), newType);
            if (active) {
                notifyObservers();
            }
        }
    }
}