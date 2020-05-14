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

import javax.print.attribute.standard.DateTimeAtCreation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PractitionerRecord {

    // not used??
    private String practitionerID;
    private ArrayList<PatientRecord> monitoredPatients;
    private ArrayList<PatientSubject> monitoredSubjects;

    private String practitionerIdentifier;
    private ArrayList<PatientRecord> practitionerPatients;
    private ArrayList<String> patientsIds = new ArrayList<String>();

    private Server server;


    public PractitionerRecord(String inputIdentifier, Server inputServer){
        practitionerIdentifier = inputIdentifier;
        practitionerPatients = new ArrayList<PatientRecord>();
        server = inputServer;
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

    // check existing practitioner (?)

    // used for testing
    public void makeFake(){
        for (int i = 0; i < 3; i++) {
            PatientRecord newPatient = new PatientRecord("123" + i, "First" + i, "Last", "male", null, "aad");
            practitionerPatients.add(newPatient);

            //sample initial record date / cholesterol value
            Date newDate = new Date(2323223232L);
            newPatient.setCholesterolMeasurement(BigDecimal.ONE, newDate);
        }
    }
}
