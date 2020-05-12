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
    private Server server;
    private ArrayList<PatientRecord> monitoredPatients;
    private ArrayList<PatientSubject> monitoredSubjects;

    public PractitionerRecord(String inputIdentifier,Server inputServer){

//        practitionerID = inputID;
//        practitionerIdentifier = retrieveIdentifier(inputID);
        practitionerIdentifier = inputIdentifier;
        practitionerPatients = new ArrayList<PatientRecord>();
        server=inputServer;
    }

    public String getPractitionerIdentifier() {
        return practitionerIdentifier;
    }

    public String getPractitionerID(){
        return practitionerID;
    }

    public void retrievePractitionerPatients() {
        practitionerPatients = server.retrievePractitionerPatients(practitionerIdentifier);
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


