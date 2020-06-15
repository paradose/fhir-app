package com.teamohno;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;

public class Model {
    // Instance Variables
    private HistoricalTableModel histTableModel;
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
        cholType = new ArrayList<>();
        bpType = new ArrayList<>();
        allTypes = new ArrayList<>();

        MeasurementType cholesterol = new Cholesterol();
        addTypeToSystem(cholesterol);
        MeasurementType bp = new BloodPressure();
        addTypeToSystem(bp);

        cholMonitorTableModel = new MonitorTableModel(cholType, Color.RED);
        bpMonitorTableModel = new MultipleMonitorTableModel(bp, Color.MAGENTA);
        histTableModel = new HistoricalTableModel(bp);

        patientListModel = new DefaultListModel();
        storedIdentifiers = new ArrayList<>();
        storedPractitioners = new ArrayList<>();
    }

    public AbstractTableModel getHistTableModel(){
        return histTableModel;
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

    public MonitorTableModel getMonitorTable(Constants.MeasurementType type){
        MonitorTableModel tableModel = null;
        if(type == Constants.MeasurementType.CHOLESTEROL){
            tableModel = cholMonitorTableModel;
        }
        else if(type == Constants.MeasurementType.BLOOD_PRESSURE){
            tableModel = bpMonitorTableModel;
        }
        return tableModel;
    }

    public HistoricalTableModel getHistorialMonitorTable(Constants.MeasurementType type){
        HistoricalTableModel historicalTableModel = null;
        if (type == Constants.MeasurementType.BLOOD_PRESSURE){
            historicalTableModel = histTableModel;
        }
        return historicalTableModel;
    }

    // can create a new method to add different types to other tables
    public void addTypeToSystem(MeasurementType newType){
        allTypes.add(newType);
        getTableTypes(newType.type).add(newType);
    }

    public ArrayList<MeasurementType> getAllTypes() {
        return allTypes;
    }

    public ArrayList<MeasurementType> getTableTypes(Constants.MeasurementType type){
        ArrayList<MeasurementType> listTypes = new ArrayList<>();
        if(type == Constants.MeasurementType.CHOLESTEROL){
            listTypes = cholType;
        }
        else if (type == Constants.MeasurementType.BLOOD_PRESSURE){
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