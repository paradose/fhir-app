package com.teamohno;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Server {

    private IGenericClient client;
    private FhirContext context;
    private String serverBase;

    public Server(String inputServerBase){
        context = FhirContext.forR4();
        client = context.newRestfulGenericClient(inputServerBase);
        serverBase = inputServerBase;
    }
    public ArrayList<PatientRecord> retrievePractitionerPatients(String practitionerIdentifier) {

        ArrayList<PatientRecord> practitionerPatients = new ArrayList<>();
        System.out.println("Retrieving patients for practitioner " + practitionerIdentifier);
        try {

            String encounterUrl = serverBase + "Encounter?practitioner.identifier=" + practitionerIdentifier;
            System.out.println(encounterUrl);
            Bundle encounters = client.search().byUrl(encounterUrl)
                    .returnBundle(Bundle.class).execute();
            System.out.println("Practitioner has " + encounters.getEntry().size() + " encounters");

        /* don't need to load subsequent pages?
        while (encounters.getLink(IBaseBundle.LINK_NEXT) != null) {
            encounters = client
                    .loadPage()
                    .next(encounters)
                    .execute();
            // process bundle
        }        */
            ArrayList<String> patientsIds = new ArrayList<>();
            for (int i = 0; i < encounters.getEntry().size(); i++) {
//            System.out.println("Checking encounter: " + i);
                Encounter practitionerEncounter = (Encounter) encounters.getEntry().get(i).getResource();
                String patientRef = practitionerEncounter.getSubject().getReference();
                String id = patientRef.substring(8);

                // storing patients that have not yet been added to the arraylist
                if (!patientsIds.contains(id)) {
                    patientsIds.add(id);
                    PatientRecord patient = retrievePatient(id);
                    System.out.println(patient.toString());
                    practitionerPatients.add(patient);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Practitioner doesn't exist");
        }
        return practitionerPatients;
    }

    public PatientRecord retrievePatient(String id ){
        Patient newPatient = client.read().resource(Patient.class).withId(id).execute();
        String firstName = newPatient.getName().get(0).getGivenAsSingleString();
        String lastName = newPatient.getName().get(0).getFamily();
        String birthDate = newPatient.getBirthDate().toString();
        String gender = newPatient.getGender().toString();
        Address address = newPatient.getAddress().get(0);
        String location = address.getLine().get(0) +","+address.getCity()+","+address.getState()+","+address.getCountry();

        return new PatientRecord(id,firstName,lastName,gender,birthDate,location);
    }

    public Cholesterol retrieveCholVal(String patientId) {
        Cholesterol newChol = new Cholesterol(BigDecimal.ZERO,null);
        // code for getting total cholesterol
        String cholCode = "2093-3";
        try {
            String searchURLchol = serverBase+"Observation?code=" + cholCode + "&subject=" + patientId;
            Bundle choleResults = client.search().byUrl(searchURLchol).sort().descending("date")
                    .returnBundle(Bundle.class).execute();
            // gets latest observation
            Observation observation = (Observation) choleResults.getEntry().get(0).getResource();
            Date date = observation.getIssued();
            BigDecimal totalChol = observation.getValueQuantity().getValue();
            newChol.setCholesterolValue(totalChol);
            newChol.setDateMeasured(date);
            System.out.println("Total chol value for " + observation.getValueQuantity().getValue());
            System.out.println(date);
        } catch (Exception e) {
            System.out.println("no chol level available");
        }
        return newChol;
    }

}
