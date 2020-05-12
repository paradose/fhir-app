package com.teamohno;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;

import java.util.ArrayList;
import java.util.List;

public class PractitionerRecord {

    private String practitionerID;
    private ArrayList<PatientRecord> practitionerPatients;
    private String practitionerIdentifier;
    private IGenericClient client;
    private FhirContext context;
    private ArrayList<PatientRecord> monitoredPatients;
    private ArrayList<String> patientsIds = new ArrayList<String>();
    private ArrayList<PatientSubject> monitoredSubjects;

    public PractitionerRecord(String inputIdentifier){
        context = FhirContext.forR4();
        client = context.newRestfulGenericClient("https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/");

//        practitionerID = inputID;
//        practitionerIdentifier = retrieveIdentifier(inputID);
        practitionerIdentifier = inputIdentifier;
        practitionerPatients = new ArrayList<PatientRecord>();
    }

    public String getPractitionerIdentifier() {
        return practitionerIdentifier;
    }

    public String getPractitionerID(){
        return practitionerID;
    }

//    public String retrievePractitionerID(String identifier){
//        Practitioner practitioner;
//        String pracID = "";
//        Bundle practitionerBundle = client.search()
//                .forResource(Practitioner.class)
//                .where(Practitioner.IDENTIFIER.exactly().identifier(identifier))
//                .returnBundle(Bundle.class).execute();
//        if (practitionerBundle.getEntry().size() > 0) {
//            practitioner = (Practitioner) practitionerBundle.getEntry().get(0).getResource();
////            pracID = practitioner
////          practitioner.getIdentifier().get(0).getSystem() +"|"+ practitioner.getIdentifier().get(0).getValue();
//        }
//        return pracID;
//    }

    public void retrievePractitionerPatients() {
//        List<IBaseResource> encountersWithId = new ArrayList<>();
        System.out.println("Retrieving patients for practitioner " + practitionerIdentifier);
        String encounterUrl = "https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/Encounter?practitioner.identifier=" + practitionerIdentifier;
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
    }

    public PatientRecord retrievePatient(String id ){
        Patient newPatient = client.read().resource(Patient.class).withId(id).execute();
        String firstName = newPatient.getName().get(0).getGivenAsSingleString();
        String lastName = newPatient.getName().get(0).getFamily();
        String birthDate = newPatient.getBirthDate().toString();
        String gender = newPatient.getGender().toString();
        String address = newPatient.getAddress().toString();
        return new PatientRecord(id,firstName,lastName,gender,birthDate,address);
    }

    public ArrayList<PatientRecord> getPractitionerPatients(){
        return practitionerPatients;
    }
}



        // Search for Encounters and extract first page
//        Bundle bundle = client.search()
//                .forResource(Encounter.class)
//                .where(Encounter.PRACTITIONER.hasId(practitionerID))
//                .returnBundle(Bundle.class)
//                .execute();
//        patients.addAll(BundleUtil.toListOfResources(context, bundle));
//
//        // loads the subsequent pages
//        while (bundle.getLink(IBaseBundle.LINK_NEXT) != null) {
//            bundle = client
//                    .loadPage()
//                    .next(bundle)
//                    .execute();
//            patients.addAll(BundleUtil.toListOfResources(context, bundle));
//        }
//        return patients.size();

    //    // read patient with given ID
//    Patient patient = client.read().resource(Patient.class).withId("10489587").execute();
//
//    // print patient name
//    String patientName = patient.getName().get(0).getGivenAsSingleString();

//    public String getPatientName() {
//        return patientName;


