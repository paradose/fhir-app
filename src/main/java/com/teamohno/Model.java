package com.teamohno;

import ca.uhn.fhir.rest.gclient.StringClientParam;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Date;

public class Model {
    private MonitorTableModel myMonitorTableModel;
    private DefaultListModel patientListModel;
    private PractitionerRecord loggedInPractitioner;
    private ArrayList<PatientSubject> monitoredCholSubjects;
    private Server myServer;
    private ArrayList<String> storedIdentifiers;
    private ArrayList<PractitionerRecord> storedPractitioners;

    public Model(Server newServer) {
        myMonitorTableModel = new MonitorTableModel();
        patientListModel = new DefaultListModel();
        monitoredCholSubjects = new ArrayList<PatientSubject>();
        storedIdentifiers = new ArrayList<String>();
        storedPractitioners = new ArrayList<PractitionerRecord>();
        myServer = newServer;
    }

    public MonitorTableModel getMonitorTable(){
        return myMonitorTableModel;
    }

    public PractitionerRecord createPractitoner(String newIdentifier){
        PractitionerRecord newPrac = new PractitionerRecord(newIdentifier, myServer);
        return newPrac;
    }

    //change this funciton so that it doesnt change the logged in - keep functions minimal
    public PractitionerRecord getPractitioner(String newIdentifier){
        PractitionerRecord storedPrac = loggedInPractitioner;
        boolean foundPrac = false;
        // if found in existing
        for (int i = 0; i < storedIdentifiers.size(); i++) {
            if (storedIdentifiers.get(i).equals(newIdentifier)){
                foundPrac = true;
                // set logged in prac as well after finding
                storedPrac = storedPractitioners.get(i);
            }
        }

        if (!foundPrac){
            storedPrac = createPractitoner(newIdentifier);
            System.out.println("Could not find practitioner inside stored practitioners, created practitioner with identifier: " + newIdentifier);
        }
        return storedPrac;
    }

    public PractitionerRecord getLoggedInPractitioner(){
        return loggedInPractitioner;
    }

    public void setLoggedInPractitioner(PractitionerRecord newPrac){
        loggedInPractitioner = newPrac;
    }

    public ArrayList<String> getStoredIdentifiers() {
        return storedIdentifiers;
    }

    public ArrayList<PractitionerRecord> getStoredPractitioners() {
        return storedPractitioners;
    }

    public DefaultListModel getPatientListModel(){
        return patientListModel;
    }

    public void updatePatientNamesList(){
//        patientListModel.clear();
//
        for (int i = 0; i < loggedInPractitioner.getPractitionerPatients().size(); i++) {
            patientListModel.add(i, loggedInPractitioner.getPractitionerPatients().get(i).getFirstName() + " " +
                    loggedInPractitioner.getPractitionerPatients().get(i).getLastName());
        }
    }

    public ArrayList<PatientSubject> getMonitoredSubjects(Measurement.Type newType) {
        ArrayList <PatientSubject> returnList = null;
        if(newType == Measurement.Type.CHOLESTEROL) {
            returnList = monitoredCholSubjects;
        }
        else{
            System.out.println("Error: measurement type not valid.");
        }
        return returnList;
    }

    public void addMonitoredSubjects(PatientSubject newSubject, Measurement.Type newType){
        if(newType == Measurement.Type.CHOLESTEROL) {
            monitoredCholSubjects.add(newSubject);
        }
        else{
            System.out.println("Error: measurement type not valid.");
        }
    }

    public void removeMonitoredSubject(PatientSubject newSubject, Measurement.Type newType){
        // destroy observer for given measurement (?)
        if (newType == Measurement.Type.CHOLESTEROL) {
            monitoredCholSubjects.remove(newSubject);
        }
        else{
            System.out.println("Error: measurement type not valid.");
        }
    }

    public void clearSubjectLists(){
        //loop through for all types
        monitoredCholSubjects.clear();
    }
}