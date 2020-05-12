package com.teamohno;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Patient;


public class Main {

    public static void main(String[] args) {
        // TEST ENUM TO STRING
//        System.out.println(Measurement.Type.CHOLESTEROL.toString());

        // setup before getting input from user
        Model m = new Model();
        View v = new View(m.getMonitorTable(), m.getList());
        Server fhirServer = new Server("http://hapi.fhir.org/baseR4/");
        Controller c = new Controller(m, v, fhirServer);

        // Add listeners to view objects
        c.initController();

        // Setup view values
        c.initView();

        // prac Identifier: 65440 - returns 39 encounters , 270 - 50 returns encounters
    }
}
