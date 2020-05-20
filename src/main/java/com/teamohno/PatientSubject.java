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
    private boolean changed;
    public PatientSubject(PatientRecord initialPatientData, Server inputServer){
        patientState = initialPatientData;
        server = inputServer;
        changed=true;
    }
    public PatientRecord getState() {
        return patientState;
    }
    public boolean getActive(){ return changed;}
    public void setState(PatientRecord patient) {
        patientState = patient;
    }

    public void updateMeasurementValue(MeasurementType newType) {
        String patientsId = patientState.getId();
        BigDecimal prevCholVal = patientState.getMeasurement(newType).getMeasurementValue();

        // testing
        if (changed) {
            MeasurementRecording updatedMeasurement = server.retrieveMeasurement(patientsId, newType);
            if (updatedMeasurement.getMeasurementValue().equals(BigDecimal.ZERO) && prevCholVal.equals(BigDecimal.ZERO)){
                changed=false;
            }
            System.out.println("Updated measurement value about to set: " + updatedMeasurement.getMeasurementValue());
            patientState.setMeasurementRecordings(updatedMeasurement.getMeasurementValue(),updatedMeasurement.getDateMeasured(), newType);
            notifyObservers();
        }
//        Cholesterol updatedTotalChol = server.retrieveCholVal(patientsId);
        // figure out how to remove unchanged states from measurementType calculation
        //sets the states chol measurement

    }

    // needs update X val to be created for extension - unless.....
    /* updateMeasurementVal(MeasurementType / name){
        server.retrieveMeasurementVal(id, type)

        .....
    * */
}