package com.teamohno;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PatientSubject extends Subject {
    private PatientRecord state;
    private IGenericClient client;
    private FhirContext context;

    public PatientSubject(){
        context = FhirContext.forR4();
        client = context.newRestfulGenericClient("https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/");

    }
    public PatientRecord getState() {
        return state;
    }

    public void setState(PatientRecord patient) {
        state = patient;
    }

    public void retrieveCholVal() {
        String id = state.getId();
        // code for getting total cholesterol
        String cholCode = "2093-3";
        try {
            String searchURLchol = "https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/Observation?code=" + cholCode + "&subject=" + id;
            Bundle choleResults = client.search().byUrl(searchURLchol).sort().descending("date")
                    .returnBundle(Bundle.class).execute();
            // gets latest observation
            Observation observation = (Observation) choleResults.getEntry().get(0).getResource();
            Date date = observation.getIssued();
            BigDecimal totalChol = observation.getValueQuantity().getValue();
            state.addCholesterolMeasurement(totalChol,date);
            System.out.println("Total chol value for " + observation.getValueQuantity().getValue());
            System.out.println(date);
        } catch (Exception e) {
            System.out.println("no chol level available");
        }
        notifyObservers();
    }

}