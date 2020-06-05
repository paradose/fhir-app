package com.teamohno;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Model {
    // Instance Variables
    private MonitorTableModel cholMonitorTableModel, bpMonitorTableModel;
    private DefaultListModel patientListModel;
    private PractitionerRecord loggedInPractitioner;
    private Server myServer;
    private ArrayList<String> storedIdentifiers;
    private ArrayList<PractitionerRecord> storedPractitioners;
    // need to fix what is passed in for tables
    private ArrayList<MeasurementType> allTypes, cholType, bpType;

    // Constructor
    public Model() {
        myServer =  new Server("https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/");
//        cholType = new ArrayList<>();
        bpType = new ArrayList<>();
        allTypes = new ArrayList<>();

        MeasurementType cholesterol = new Cholesterol();
//        cholType.add(cholesterol);
//        cholMonitorTableModel = new MonitorTableModel(cholType);
        allTypes.add(cholesterol);
        cholMonitorTableModel = new MonitorTableModel(allTypes, Color.RED);

        MeasurementType sbp = new SystolicBP();
        MeasurementType dbp = new DiastolicBP();
        bpType.add(sbp);
        bpType.add(dbp);
        bpMonitorTableModel = new MonitorTableModel(bpType, Color.MAGENTA);

        patientListModel = new DefaultListModel();
        storedIdentifiers = new ArrayList<>();
        storedPractitioners = new ArrayList<>();
    }

    // Create new practitioner object based on identifier
    public PractitionerRecord createPractitoner(String newIdentifier){
        PractitionerRecord newPrac = new PractitionerRecord(newIdentifier, myServer);
        return newPrac;
    }

    // Updates based on logged in practitioner object stored
    public void updatePatientNamesList(){
        for (int i = 0; i < loggedInPractitioner.getPractitionerPatients().size(); i++) {
            patientListModel.add(i, loggedInPractitioner.getPractitionerPatients().get(i).getFirstName() + " " +
                    loggedInPractitioner.getPractitionerPatients().get(i).getLastName());
        }
    }

    // Accessors and Mutators
    public PractitionerRecord getLoggedInPractitioner(){
        return loggedInPractitioner;
    }

    public void setLoggedInPractitioner(PractitionerRecord newPrac){
        loggedInPractitioner = newPrac;
    }

    public ArrayList<PractitionerRecord> getStoredPractitioners() {
        return storedPractitioners;
    }

    public ArrayList<String> getStoredIdentifiers() {
        return storedIdentifiers;
    }

    public DefaultListModel getPatientListModel(){
        return patientListModel;
    }

    public MonitorTableModel getMonitorTable(MeasurementType.Type type){
        MonitorTableModel tableModel = null;
        if(type == MeasurementType.Type.CHOLESTEROL){
            tableModel = cholMonitorTableModel;
        }
        else if(type == MeasurementType.Type.BLOODPRESSURE){
            tableModel = bpMonitorTableModel;
        }
        return tableModel;
    }

    public ArrayList<MeasurementType> getTypes() {
        return allTypes;
    }

        public ArrayList<MeasurementType> getTypes(MeasurementType.Type type){
        ArrayList<MeasurementType> listTypes = new ArrayList<>();
        if(type == MeasurementType.Type.CHOLESTEROL){
            listTypes = cholType;
        }
        else if (type == MeasurementType.Type.BLOODPRESSURE){
            listTypes = bpType;
        }
        return listTypes;
    }

    public Server getServer() {
        return myServer;
    }

    public void setServer(Server myServer) {
        this.myServer = myServer;
    }
}