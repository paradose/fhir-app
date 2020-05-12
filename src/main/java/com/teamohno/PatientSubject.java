package com.teamohno;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;

import java.math.BigDecimal;
import java.util.Date;

public class PatientSubject extends Subject {
    private PatientRecord patientState;
    private Server server;

    public PatientSubject(PatientRecord initialPatientData, Server inputServer){
        patientState = initialPatientData;
        server = inputServer;
    }
    public PatientRecord getState() {
        return patientState;
    }

    public void setState(PatientRecord patient) {
        patientState = patient;
    }

    public void updateCholVal() {
        String patientsId = patientState.getId();
        Cholesterol updatedTotalChol = server.retrieveCholVal(patientsId);
        //sets the states chol measurement
        patientState.setCholesterolMeasurement(updatedTotalChol.getCholesterolValue(),updatedTotalChol.getDateMeasured());
    }
}