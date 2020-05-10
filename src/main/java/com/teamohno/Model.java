package com.teamohno;

import ca.uhn.fhir.rest.gclient.StringClientParam;
import org.hl7.fhir.r4.model.Patient;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class Model extends AbstractTableModel {
    private ArrayList<String> monitoredColumns;
    private ArrayList<ArrayList<String>> monitoredData;
    private ArrayList<String> monitoredPatientNames;
    private ArrayList<String> cholesterolLevels;
    private ArrayList<String> dateMeasured;

    private PractitionerRecord loggedInPractitioner;
    private DefaultListModel patientListModel;
//    private ArrayList<String> patientListNames;


    public Model() {
        monitoredColumns = new ArrayList<String>();
        monitoredColumns.add("Name");
        monitoredColumns.add("Cholesterol Level");
        monitoredColumns.add("Date Measured");

        monitoredData = new ArrayList<ArrayList<String>>();
        monitoredPatientNames = new ArrayList<String>();
        cholesterolLevels = new ArrayList<String>();
        dateMeasured = new ArrayList<String>();
        monitoredData.add(monitoredPatientNames);
        monitoredData.add(cholesterolLevels);
        monitoredData.add(dateMeasured);

//        patientListNames = new ArrayList<String>();

        // ListModel
        patientListModel = new DefaultListModel();
    }

    public void setValueAt(int row, int col, String value){
        monitoredData.get(col).set(row, value);
    }

    @Override
    public String getColumnName(int column) {
        return monitoredColumns.get(column);
    }

    public int getColumnCount() {
        return monitoredColumns.size();
    }

    public int getRowCount() {
        // enforce that all columns must have an element
        int largestColumnSize = -1;
        for (int i = 0; i < monitoredData.size() ; i++) {
            if (largestColumnSize < monitoredData.get(i).size()) {
                largestColumnSize = monitoredData.get(i).size();
            }
        }
        if (largestColumnSize == -1){
            System.out.println("Error: number of rows calculation error");
        }
        return largestColumnSize;
    }

    public Object getValueAt(int row, int col) {
        return monitoredData.get(col).get(row);
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