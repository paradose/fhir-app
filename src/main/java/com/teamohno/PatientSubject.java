package com.teamohno;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Measure;
import org.hl7.fhir.r4.model.Observation;

import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.util.Calendar;
import java.util.Date;

public class PatientSubject extends Subject {
    // Instance variables
    private PatientRecord patientState;
    private Server server;
    private boolean active;

    // Constructors
    public PatientSubject(PatientRecord initialPatientData, Server inputServer){
        patientState = initialPatientData;
        server = inputServer;
        active = true;
    }

    // Accessors and Mutators
    public PatientRecord getState() {
        return patientState;
    }

    public boolean getActive(){ return active;}

    public void setState(PatientRecord patient) {
        patientState = patient;
    }

    // Updates patient recordings data
    public void updateMeasurementValue(MeasurementType newType) {
        String patientsId = patientState.getId();
        MeasurementRecording updatedMeasurement;

        System.out.println("Size of historic recordings: " + patientState.getLastRecordings(newType).size());
        for (int i = 0; i < patientState.getLastRecordings(newType).size(); i++) {
            System.out.println(i + "th recording: " + patientState.getLastRecordings(newType).get(i).toString());
        }

//        /* Comment out section to test
        if(active){
            // checks if initial value
            updatedMeasurement = server.retrieveMeasurement(patientsId, newType);
            if (updatedMeasurement.getMeasurementValue().compareTo(BigDecimal.ZERO)==0){
                // if the value hasn't been changed from zero inside the server -> subject set inactive
                active = false;
                System.out.println("Patient set inactive!");
            }
            else{
                // updating subject state
                patientState.getMeasurement(newType).cloneRecording(updatedMeasurement);
                notifyObservers();
            }
        }
//         */

        /* For testing - uncomment this for recordings to continually increment **
        updatedMeasurement = patientState.getMeasurement(newType);
        // Increment date by 1 day
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(patientState.getMeasurement(newType).getDateMeasured());
        currentDate.add(Calendar.HOUR_OF_DAY, 24);
        patientState.getMeasurement(newType).setDateMeasured(currentDate.getTime());

        // Increment all values by 1
        patientState.getMeasurement(newType).setMeasurementValue(patientState.getMeasurement(newType).getMeasurementValue().add(BigDecimal.ONE));

        if (newType.getComponentSize() > 0){
            for (int i = 0; i < newType.getChildTypes().size(); i++) {
                patientState.getMeasurement(newType).setMeasurementValue(patientState.getMeasurement(newType).getMeasurementValue().add(BigDecimal.ONE), newType.getChildTypes().get(i));
            }
        }
        else {
            patientState.setMeasurementRecordings(updatedMeasurement.getMeasurementValue(), updatedMeasurement.getDateMeasured(), newType);
        }
        notifyObservers();
         */
    }
}