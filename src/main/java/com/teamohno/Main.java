package com.teamohno;


import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        // TEST ENUM TO STRING
//        System.out.println(Measurement.Type.CHOLESTEROL.toString());

        // setup before getting input from user
        Server fhirServer = new Server("https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/");
        Model m = new Model(fhirServer);
        View v = new View(m);
        Controller c = new Controller(m, v, fhirServer);

        // Add listeners to view objects
        c.initController();

        // Setup view values
        c.initView();

        // prac Identifier: 65440 - returns 39 encounters , 270 - 50 returns encounters
    }
}
