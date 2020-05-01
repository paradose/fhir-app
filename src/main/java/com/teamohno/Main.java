package com.teamohno;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Patient;


public class Main {

    public static void main(String[] args) {
	// write your code here
        //create a context
        FhirContext context = FhirContext.forR4();

        // create a client
        IGenericClient client = context.newRestfulGenericClient("https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/");

        // read patient with given ID
        Patient patient = client.read().resource(Patient.class).withId("10489587").execute();

        // print patient name
        String patientName = patient.getName().get(0).getGivenAsSingleString();
        System.out.println("Patient given name:" + patientName);

    }
}
