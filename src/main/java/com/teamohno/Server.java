package com.teamohno;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Server {
    // Instance variables
    private IGenericClient client;
    private FhirContext context;
    private String serverBase;
    private int entriesPerPage;

    // Constructor
    public Server(String inputServerBase){
        context = FhirContext.forR4();
        client = context.newRestfulGenericClient(inputServerBase);
        serverBase = inputServerBase;
        entriesPerPage = 100;
    }
    // uses practitioners identifier to get an array of their ids
    public ArrayList<String> retrievePractitionerIDs(String practitionerIdentifier) {
        ArrayList<String> practitionerIDs = new ArrayList<String>();
        Bundle practitionerObjects = client.search()
                .forResource(Practitioner.class)
                .count(entriesPerPage)
                .where(Practitioner.IDENTIFIER.exactly().identifier(practitionerIdentifier))
                .returnBundle(Bundle.class)
                .execute();

        // processing first page in bundle
        addPracIDsToList(practitionerObjects, practitionerIDs);
        System.out.println("Size of practitioner bundle page: " + practitionerObjects.getEntry().size());

        while (practitionerObjects.getLink(IBaseBundle.LINK_NEXT) != null) {

            // get next page
            practitionerObjects = client
                    .loadPage()
                    .next(practitionerObjects)
                    .execute();
            // process page
            addPracIDsToList(practitionerObjects, practitionerIDs);
            System.out.println("Number of practitioner objects found on next page: " + practitionerObjects.getEntry().size());
        }
        System.out.println("ID array size: " + practitionerIDs.size() + ", id array: " + practitionerIDs);
        return practitionerIDs;
    }

    // checks if practitioners id from bundle has already been added to list
    public void addPracIDsToList(Bundle newBundle, ArrayList<String> newList) {
        for (int i = 0; i < newBundle.getEntry().size(); i++) {
            Practitioner processingPrac = (Practitioner) newBundle.getEntry().get(i).getResource();
            String newID = processingPrac.getIdElement().getIdPart();

            if (!newList.contains(newID)) {
                newList.add(newID);
            } else {
                System.out.println("Adding practitioner ID's for logged in prac, id: " + newID + " already inside current list.");
            }
        }
    }

    // gets patients for the particular practitioner, by using their ids and going through encounters.
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

        System.out.println("Searching for patients using URL: " + patientsURL);
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

    // gets patients information using their Id and returns it as a PatientRecord object.
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

    // retrieves patient's measurement value by taking in the type (which contains code) for URL get request.
    // measurement value and date recorded & returned as a MeasurementRecording object.
    public MeasurementRecording retrieveMeasurement(String patientId, MeasurementType newType) {
        MeasurementRecording newRecording = new MeasurementRecording(BigDecimal.ZERO,null, newType);
        BigDecimal newValue = BigDecimal.ONE.negate();

        String measurementCode = newType.getFhirCode();
        try {
            String searchURL = serverBase+"Observation?code=" + measurementCode + "&subject=" + patientId;
            Bundle results = client.search()
                    .byUrl(searchURL)
                    .sort()
                    .descending("date")
                    .returnBundle(Bundle.class)
                    .execute();
            // gets latest observation
            Observation observation = (Observation) results.getEntry().get(0).getResource();

            //check if observation has multiple components
            if(observation.getComponent().size() > 1){
            // loop through components
                for (int i = 0; i < observation.getComponent().size(); i++) {
                    //  Check child code matches with observation component code in server
                    String currChildCode = observation.getComponent().get(i).getCode().getCodingFirstRep().getCode();
                    for (int j = 0; j < newType.getListChildCode().size(); j++) {
                        // iterator...!
                        if(newType.getListChildCode().get(j).equals(currChildCode)){
                            Constants.MeasurementType currentType = newType.getChildTypes().get(j);
                            newValue = observation.getComponent().get(i).getValueQuantity().getValue();
                            newRecording.setMeasurementValue(newValue, currentType);
                            //Set so that it won't be considered inactive?? [temporary]
                            newRecording.setMeasurementValue(BigDecimal.ONE);
                        }
                    }
                }
            }
            else {
                newValue = observation.getValueQuantity().getValue();
                newRecording.setMeasurementValue(newValue);
            }
            Date newDate = observation.getIssued();
            newRecording.setDateMeasured(newDate);
            System.out.println("Total " + newType.getName() + " value observed from server: " + newValue);
            System.out.println("Date of observation retrieved: " + newDate);
        }
        catch (Exception e) {
            System.out.println("No " + newType.getName() + " value available for patient ID: " + patientId + " from server.");
        }
        return newRecording;
    }

    // checks if patient already exists in the list of Patients for a particular practitioner.
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
