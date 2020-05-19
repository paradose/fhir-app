package com.teamohno;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Array;
import java.sql.SQLOutput;
import java.util.ArrayList;

// change to general measurement (pass in measurement type in constructor to make column name

public class MonitorTableModel extends AbstractTableModel {
    private ArrayList<ArrayList<String>> patientMeasurementTable;

    private ArrayList<String> columnNames;

    // monitorred patient data
    private ArrayList<ArrayList<String>> monitoredData;
    private ArrayList<String> monitoredPatientNames;

    // used to gather index of patient that are being monitored - might be redundant due to lack of knowledge of row for measurement - will use from model
    private ArrayList<String> monitoredPatientID;
    private ArrayList<ArrayList<PatientSubject>> monitoredMeasurementSubjects;

    //watches the average of chol
    private BigDecimal totalChol;
    private BigDecimal totalAverageChol;
    private CholCellRenderer cholAverageWatcher;

    public MonitorTableModel(ArrayList<MeasurementType> newTypes) {
        // list of types -> use the types to get access to all the subject lists
        monitoredMeasurementSubjects = new ArrayList<>();

        patientMeasurementTable = new ArrayList<ArrayList<String>>();
        monitoredPatientID = new ArrayList<String>();
        patientMeasurementTable.add(monitoredPatientID);

        columnNames = new ArrayList<String>();
        columnNames.add("Name");

        monitoredData = new ArrayList<ArrayList<String>>();
        monitoredPatientNames = new ArrayList<String>();
        monitoredData.add(monitoredPatientNames);
        for (int i = 0; i < newTypes.size(); i++) {
            addMeasurementType(newTypes.get(i));
        }

        totalAverageChol = BigDecimal.ZERO;
        totalChol = BigDecimal.ZERO;
        cholAverageWatcher = new CholCellRenderer();
    }

    public void addMeasurementType(MeasurementType newType){
        ArrayList<String> listVlaues = new ArrayList<String>();
        ArrayList<String> listDates = new ArrayList<String>();
        monitoredData.add(listVlaues);
        monitoredData.add(listDates);
        columnNames.add(newType.getName());
        columnNames.add("Date Measured");

        monitoredMeasurementSubjects.add(newType.getMonitorredSubjects());
    }

    public ArrayList<ArrayList<String>> getIndexPatientsMeasurement(){
        return  patientMeasurementTable;
    }

    public boolean addMonitorPatient(String newPatientID, String newPatientName, MeasurementType newType){
        // check subject list if monitorring
        boolean returnResult = false, monitorringThisType = false;
        ArrayList<PatientSubject> subjectList = getMonitoredSubjects(newType);

        for (int i = 0; i < subjectList.size(); i++) {
            if (subjectList.get(i).getState().getId().equals(newPatientID)){
                monitorringThisType = true;
            }
        }

        // if patient NOT monitored (OR patient monitored but different type) ->
        if (monitoredPatientID.contains(newPatientID) && monitorringThisType){
            System.out.println("Error: attempting monitor patient's " + newType.getName() + ", with id: " + newPatientID + ", which is already monitored");
        }
        else if (!monitoredPatientID.contains(newPatientID)){
            monitoredPatientID.add(newPatientID);
            monitoredPatientNames.add(newPatientName);
        }
        if(!monitorringThisType){
            for (int i = 0; i < columnNames.size(); i++) {
                if(columnNames.get(i).equals(newType.getName())){
                    monitoredData.get(i).add("-");
                    monitoredData.get(i + 1).add("-");
                    fireTableDataChanged();
                    returnResult = true;
                }
            }
        }
        return returnResult;
    }

    public void removePatientFromTable(int newPatientIndex){
        // remove to track index
        monitoredPatientID.remove(newPatientIndex);

        // need to catch condition for cholesterol (index = 0 => column for cholesterol)
        totalChol = totalChol.subtract(new BigDecimal(monitoredData.get(1).get(newPatientIndex)));
        // loop through data columns
        for (int i = 0; i < monitoredData.size(); i++) {
            // this removes ALL data
            monitoredData.get(i).remove(newPatientIndex);
        }
        fireTableDataChanged();
    }

    public void updateMeasurements(PatientRecord newPatient, MeasurementRecording newMeasurement) {
        // obtain index to navigate inside table data
        int currentIndex = monitoredPatientID.indexOf(newPatient.getId());
        BigDecimal oldValue;

        System.out.println("Column name size:" + columnNames.size());
        for (int i = 0; i < columnNames.size(); i++) {
            System.out.println("col i entry:" + columnNames.get(i));
            System.out.println("type name:" + newMeasurement.getType().getName());
            if (columnNames.get(i).equals(newMeasurement.getType().getName())) {

                // where value cant be casted into big decimal consider??
                if (!monitoredData.get(i).get(currentIndex).equals("-")) {
                    oldValue = new BigDecimal(monitoredData.get(i).get(currentIndex));
                } else oldValue = BigDecimal.ZERO;

                System.out.println("Replacing data");
                monitoredData.get(i).remove(currentIndex);
                monitoredData.get(i + 1).remove(currentIndex);

                BigDecimal newValue = newMeasurement.getMeasurementValue();
                updateNewCholAverage(oldValue, newValue);

                monitoredData.get(i).add(currentIndex, newMeasurement.getMeasurementValue().toString());
                monitoredData.get(i + 1).add(currentIndex, newMeasurement.getDateMeasured().toString());
            } else {
                System.out.println("Error: measurement value being updated contains invalid measurement type");
            }
            fireTableDataChanged();
        }
    }

    // have to deal with cases where they have no cholesterol levels being updated?
    private void updateNewCholAverage(BigDecimal oldValue, BigDecimal newValue){
        totalChol= (totalChol.add(newValue)).subtract(oldValue);
        totalAverageChol = totalChol.divide(new BigDecimal(monitoredPatientNames.size()),3, RoundingMode.CEILING);
        // updates cholesterol average in renderer that observes all the data
        cholAverageWatcher.updateCholAverage(totalAverageChol);
    }

    public Object getValueAt(int row, int col) {
        return monitoredData.get(col).get(row);
    }

    @Override
    public String getColumnName(int column) {
        return columnNames.get(column);
    }

    public int getColumnCount() {
        return columnNames.size();
    }

    public int getRowCount() {
        // all columns should have same numbers of rows
        int rowCount = monitoredData.get(0).size();
        for (int i = 1; i < monitoredData.size() ; i++) {
            if (rowCount != monitoredData.get(i).size()) {
                System.out.println("Error: col " + i + " contains different amount of elements to col 0");
                rowCount = -1;
            }
        }
        return rowCount;
    }

    public ArrayList<ArrayList<String>> getMonitorData(){
        return monitoredData;
    }

    public void clearDataValues(){
        System.out.println("Clearing monitorred data in table");
        // loop all data columns
        for (int i = 0; i < monitoredData.size(); i++) {
            monitoredData.get(i).clear();
        }
        monitoredPatientID.clear();
        fireTableDataChanged();
    }

    public ArrayList<PatientSubject> getMonitoredSubjects(MeasurementType newType) {
        return newType.getMonitorredSubjects();
    }

    // put this inside monitor table!
    public void addMonitoredSubjects(PatientSubject newSubject, MeasurementType newType) {
        newType.getMonitorredSubjects().add(newSubject);
    }
    public void removeMonitoredSubject(PatientSubject newSubject, MeasurementType newType) {
        newType.getMonitorredSubjects().remove(newSubject);
    }

    public void clearSubjectLists(){
        //loop through for all types
        for (int i = 0; i < monitoredMeasurementSubjects.size(); i++) {
            monitoredMeasurementSubjects.get(i).clear();
        }
    }
}
