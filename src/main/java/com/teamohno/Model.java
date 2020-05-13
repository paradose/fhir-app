package com.teamohno;

import ca.uhn.fhir.rest.gclient.StringClientParam;
import org.hl7.fhir.r4.model.Patient;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Date;

public class Model {
    private MonitorTableModel myMonitorTableModel;
    private DefaultListModel patientListModel;
    private PractitionerRecord loggedInPractitioner;
    private ArrayList<PatientSubject> monitoredSubjects;
    private ArrayList<CholObserver> cholObserverArray;

    public Model() {
        myMonitorTableModel = new MonitorTableModel();
        patientListModel = new DefaultListModel();
        monitoredSubjects = new ArrayList<PatientSubject>();
        cholObserverArray = new ArrayList<CholObserver>();
    }

    public MonitorTableModel getMonitorTable(){
        return myMonitorTableModel;
    }

    public void createPractitoner(String newIdentifier, Server inputServer){
        loggedInPractitioner = new PractitionerRecord(newIdentifier, inputServer);
    }

    public PractitionerRecord getPractitioner(){
        return loggedInPractitioner;
    }

    public DefaultListModel getList(){
//        return patientListNames;
        return patientListModel;
    }

    public void updatePatientNamesList(){
        patientListModel.clear();

        for (int i = 0; i < loggedInPractitioner.getPractitionerPatients().size(); i++) {
            patientListModel.add(i, loggedInPractitioner.getPractitionerPatients().get(i).getFirstName() + " " +
                    loggedInPractitioner.getPractitionerPatients().get(i).getLastName());
        }
    }

    public ArrayList<PatientSubject> getMonitoredSubjects() {
        return monitoredSubjects;
    }

    public void addMonitoredSubjects(PatientSubject newSubject){
        monitoredSubjects.add(newSubject);
    }

    public ArrayList<CholObserver> getCholObserverArray() {
        return cholObserverArray;
    }
}