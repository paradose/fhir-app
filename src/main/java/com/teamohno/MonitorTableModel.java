package com.teamohno;

import javax.swing.table.AbstractTableModel;
import java.nio.channels.MulticastChannel;
import java.util.ArrayList;

public class MonitorTableModel extends AbstractTableModel {
    // Table array - containts lists (columns) of data
    protected ArrayList<ArrayList<String>> monitoredData;
    protected ArrayList<String> columnNames;
    protected ArrayList<String> monitoredPatientNames;

    // used to track index of patient that are being monitored within table
    protected ArrayList<ArrayList<String>> patientMeasurementTable;
    protected ArrayList<String> monitoredPatientID;
    protected ArrayList<ArrayList<PatientSubject>> monitoredMeasurementSubjects;

    // watches the average value
    protected MeasurementCellRenderer measurementAverageWatcher;

    // Constructor
    public MonitorTableModel(ArrayList<MeasurementType> newTypes) {
        // list of types -> use the types to get access to all the subject lists
        monitoredMeasurementSubjects = new ArrayList<>();
        patientMeasurementTable = new ArrayList<>();
        monitoredPatientID = new ArrayList<>();
        patientMeasurementTable.add(monitoredPatientID);

        columnNames = new ArrayList<>();
        columnNames.add("Name");

        monitoredData = new ArrayList<>();
        monitoredPatientNames = new ArrayList<>();
        monitoredData.add(monitoredPatientNames);
        for (int i = 0; i < newTypes.size(); i++) {
            addMeasurementType(newTypes.get(i));
        }
    }

    public void addMeasurementType(MeasurementType newType){
        ArrayList<String> listVlaues = new ArrayList<>();
        ArrayList<String> listDates = new ArrayList<>();
        monitoredData.add(listVlaues);
        monitoredData.add(listDates);
        columnNames.add(newType.getName());
        // Storing current column index for the measurement
        measurementAverageWatcher = new MeasurementCellRenderer(columnNames.size() - 1);
        columnNames.add("Date Measured");
        monitoredMeasurementSubjects.add(newType.getMonitorredSubjects());
    }

    public boolean addMonitorPatient(String newPatientID, String newPatientName, MeasurementType newType){
        // check subject list if currently already monitorring
        boolean returnResult = false, monitorringThisType = false;
        ArrayList<PatientSubject> subjectList = newType.getMonitorredSubjects();

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
            // add (potentially) multiple values first
            for (int i = 0; i < columnNames.size(); i++) {
                if(columnNames.get(i).equals(newType.getName())) {
                    monitoredData.get(i).add("-");
                    // add date
                    monitoredData.get(i+1).add("-");
                }
            }

            fireTableDataChanged();
            returnResult = true;
        }
        return returnResult;
    }

    public void removePatientFromTable(int newPatientIndex){
        // remove to track index
        monitoredPatientID.remove(newPatientIndex);

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
        for (int i = 0; i < columnNames.size(); i++) {
            if (columnNames.get(i).equals(newMeasurement.getType().getName())) {
                monitoredData.get(i).remove(currentIndex);
                monitoredData.get(i + 1).remove(currentIndex);

                monitoredData.get(i).add(currentIndex, newMeasurement.getMeasurementValue().toString());
                monitoredData.get(i + 1).add(currentIndex, newMeasurement.getDateMeasured().toString());
            }
            fireTableDataChanged();
        }
    }

    public MeasurementCellRenderer getMeasurementRenderer(){
        return measurementAverageWatcher;
    }

    public Object getValueAt(int row, int col) {
        // column index = for the list of names / measurements / dates
        // row index = for the value that column
        return monitoredData.get(col).get(row);
    }

    @Override
    public String getColumnName(int column) {
        return columnNames.get(column);
    }

    public int getColumnCount() {
        return columnNames.size();
    }
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }
    public int getRowCount() {
        // all columns should have same numbers of rows - observing patient name column to avoid issues
        int rowCount = monitoredData.get(0).size();
        for (int i = 1; i < monitoredData.size() ; i++) {
            if (rowCount != monitoredData.get(i).size()) {
                System.out.println("Error: col " + i + " contains different amount of elements to col 0");
                rowCount = -1;
            }
        }
        return rowCount;
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

    public ArrayList<String> getMonitoredPatientID() {
        return monitoredPatientID;
    }
}
