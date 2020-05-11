package com.teamohno;

import ca.uhn.fhir.rest.gclient.StringClientParam;
import org.hl7.fhir.r4.model.Patient;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Date;

public class Model {
    MonitorTableModel myMonitorTableModel;
    DefaultListModel patientListModel;
    PractitionerRecord loggedInPractitioner;

    public Model() {
        myMonitorTableModel = new MonitorTableModel();

        // ListModel
        patientListModel = new DefaultListModel();
    }

    public MonitorTableModel getMonitorTable(){
        return myMonitorTableModel;
    }

    public void createPractitoner(String newIdentifier){
        loggedInPractitioner = new PractitionerRecord(newIdentifier);
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
}