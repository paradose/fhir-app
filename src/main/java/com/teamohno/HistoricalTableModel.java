package com.teamohno;

//import jdk.javadoc.internal.doclets.formats.html.PackageUseWriter;

import org.hl7.fhir.r4.model.Measure;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class HistoricalTableModel extends AbstractTableModel {
    // Table array - containts lists (columns) of data
    private ArrayList<ArrayList<String>> monitoredData;
    private ArrayList<String> columnNames;
    private ArrayList<String> monitoredPatientNames;
    private MeasurementType historicalType;
    private ArrayList<String> monitoredLastRecordings;
    // used to track index of patient that are being monitored within table
    private ArrayList<String> monitoredPatientID;
    private ArrayList<PatientSubject> subjects;
    private String childCode;
    public HistoricalTableModel(){
        createTable();
    }

    public HistoricalTableModel(MeasurementType type){
        this.historicalType = type;
        createTable();
    }

    public HistoricalTableModel(MeasurementType type, String childCode){
        this.historicalType = type;
        this.childCode = childCode;
        createTable();
    }

    public void createTable(){
        monitoredPatientID = new ArrayList<>();

        columnNames = new ArrayList<>();
        columnNames.add("Name");
        columnNames.add("Last Recordings");
        monitoredData = new ArrayList<>();
        monitoredPatientNames = new ArrayList<>();
        monitoredLastRecordings = new ArrayList<>();
        subjects = new ArrayList<>();
        monitoredData.add(monitoredPatientNames);
        monitoredData.add(monitoredLastRecordings);
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

    public void addPatient(PatientSubject patientSubject){
        subjects.add(patientSubject);
        ArrayList<MeasurementRecording> lastRecordings = patientSubject.getState().getLastRecordings(historicalType);
        monitoredPatientNames.add(patientSubject.getState().getFirstName() + patientSubject.getState().getLastName());
        monitoredLastRecordings.add(lastRecordingsToString(lastRecordings,"8480-6"));
        fireTableDataChanged();
    }
    public void updateHistory(ArrayList<MeasurementRecording> lastRecordings, PatientRecord newRecord){
        System.out.println("Historical Table is being Updated !");
    }
    public String lastRecordingsToString(ArrayList<MeasurementRecording> lastRecordings, String childCode){
        String returnString = "";
        for (MeasurementRecording lastRecording : lastRecordings){
            returnString += lastRecording.getMeasurementValue(childCode).toString() + ", ";
        }
        return returnString;
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

}
