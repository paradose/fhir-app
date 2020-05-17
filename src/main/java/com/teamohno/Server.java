package com.teamohno;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Server {

    private IGenericClient client;
    private FhirContext context;
    private String serverBase;
    private int entriesPerPage;

    public Server(String inputServerBase){
        context = FhirContext.forR4();
        client = context.newRestfulGenericClient(inputServerBase);
        serverBase = inputServerBase;
        entriesPerPage = 100;
    }

    public ArrayList<String> retrievePractitionerIDs(String practitionerIdentifier) {
        ArrayList<String> practitionerIDs = new ArrayList<String>();
        Bundle practitionerObjects = client.search()
                .forResource(Practitioner.class)
                .count(entriesPerPage)
                .where(Practitioner.IDENTIFIER.exactly().identifier(practitionerIdentifier))
                .returnBundle(Bundle.class)
                .execute();

        addPracIDsToList(practitionerObjects, practitionerIDs);
        System.out.println("Size of practitioner bundle page: " + practitionerObjects.getEntry().size());

        while (practitionerObjects.getLink(IBaseBundle.LINK_NEXT) != null) {
            // processing bundle before next page

            // get next page
            practitionerObjects = client
                    .loadPage()
                    .next(practitionerObjects)
                    .execute();
            // process after next page
            addPracIDsToList(practitionerObjects, practitionerIDs);
            System.out.println("Number of practitioner objects found on next page: " + practitionerObjects.getEntry().size());
        }
        System.out.println("ID array size: " + practitionerIDs.size() + ", id array: " + practitionerIDs);
        return practitionerIDs;
    }

    public void addPracIDsToList(Bundle newBundle, ArrayList<String> newList) {
        for (int i = 0; i < newBundle.getEntry().size(); i++) {
            Practitioner processingPrac = (Practitioner) newBundle.getEntry().get(i).getResource();
            String newID = processingPrac.getIdElement().getIdPart();

            if (!newList.contains(newID)) {
                newList.add(newID);
                System.out.println("Added new ID: " + newID);
            } else {
                System.out.println(newID + " already inside");
            }
        }
    }

    public ArrayList<PatientRecord> retrievePractitionerPatients(String practitionerIdentifier) {
        // used to find patients for each ID
        ArrayList<String> practitionerIds = retrievePractitionerIDs(practitionerIdentifier);
        // used to track which patients have been added
        ArrayList<String> patientIdentifiers = new ArrayList<>();
        // returning object
        ArrayList<PatientRecord> practitionerPatients = new ArrayList<>();

        System.out.println("Retrieving patients for practitioner " + practitionerIdentifier);

        // using search URL - checking for all id's
        String patientsURL = serverBase + "Patient?_count=" + entriesPerPage + "&_has:Encounter:patient:participant=";
        System.out.println("Prac ids array before looking for patients: " + practitionerIds);
        for (int i = 0; i < practitionerIds.size(); i++) {
            if(practitionerIds.size() -1 != i) {
                patientsURL += practitionerIds.get(i) + ",";
            }
            else{
                patientsURL += practitionerIds.get(i);
            }
        }
        if(practitionerIds.size() == 0){
            System.out.println("Practitioner doesn't have any existing id linked to them.");
        }

        System.out.println("Searching using URL: " + patientsURL);
        Bundle patientsBundle = client.search().byUrl(patientsURL)
                .returnBundle(Bundle.class).execute();

        // pre-process - add patient to patient objects / list
        addPatientToList(patientIdentifiers, practitionerPatients, patientsBundle);

        // loop for all pages
        while (patientsBundle.getLink(IBaseBundle.LINK_NEXT) != null) {
            //Loading next page
            patientsBundle = client
                    .loadPage()
                    .next(patientsBundle)
                    .execute();
            // next page of bundle process - add patient to patient objects / list
            addPatientToList(patientIdentifiers, practitionerPatients, patientsBundle);
        }
        System.out.println("Size of practitioner patients: " + practitionerPatients.size());
        return practitionerPatients;
    }

    public PatientRecord retrievePatient(String id ){
        Patient newPatient = client.read()
                .resource(Patient.class)
                .withId(id)
                .execute();
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
            Bundle choleResults = client.search()
                    .byUrl(searchURLchol)
                    .sort()
                    .descending("date")
                    .returnBundle(Bundle.class)
                    .execute();
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

    public void addPatientToList(ArrayList<String> identifierList, ArrayList<PatientRecord> patientList, Bundle newBundle){
        // looping through bundle
        for (int i = 0; i < newBundle.getEntry().size(); i++) {
           // current patient being processed
            Patient patientObject = (Patient) newBundle.getEntry().get(i).getResource();
            String id = patientObject.getIdElement().getIdPart();
            String identifier = patientObject.getIdentifierFirstRep().getValue();

            boolean addPatient = false;
            // need to check if empty -> add first entry
            if(identifierList.size() == 0) {
                addPatient = true;
            }
            else {
                for (int j = 0; j < identifierList.size(); j++) {
                    // check if identifier already stored - one patient may have same identifier - but diff id
                    if (!(identifierList.contains(identifier))) {
                        addPatient = true;
                    }
                }
            }

            if(addPatient){
                identifierList.add(identifier);
                PatientRecord patient = retrievePatient(id);
                patientList.add(patient);
                System.out.println(patient.toString());
            }
        }
    }
}
