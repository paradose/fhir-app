package com.teamohno;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class MonitorTableModel extends AbstractTableModel {
    private ArrayList<String> dataColumns;
    private ArrayList<ArrayList<String>> monitoredData;
    private ArrayList<String> monitoredPatientNames;
    private ArrayList<String> cholesterolLevels;
    private ArrayList<String> effectiveDate;

    // Array list used to navigate to correct index inside the data columns for a given patient
    private ArrayList<PatientRecord> monitoredPatients;

    public MonitorTableModel() {
        monitoredPatients = new ArrayList<PatientRecord>();

        dataColumns = new ArrayList<String>();
        dataColumns.add("Name");
        dataColumns.add("Cholesterol Level");
        dataColumns.add("Date Measured");

        monitoredData = new ArrayList<ArrayList<String>>();

        monitoredPatientNames = new ArrayList<String>();
        cholesterolLevels = new ArrayList<String>();
        effectiveDate = new ArrayList<String>();

        monitoredData.add(monitoredPatientNames);
        monitoredData.add(cholesterolLevels);
        monitoredData.add(effectiveDate);
    }

    public void addMonitoredPatient(PatientRecord newMonPatient){
        monitoredPatients.add(newMonPatient);
    }
    public void removeMonitoredPatient(PatientRecord newMonPatient){
        if(monitoredPatients.contains(newMonPatient)){
            monitoredPatients.remove(newMonPatient);
        }
        else{
            System.out.println("Error: patient attempted to remove from monitored array does not exist in array");
        }
    }

    public void addPatientName(String newPatientName){
        monitoredPatientNames.add(newPatientName);
        cholesterolLevels.add("N/A");
        effectiveDate.add("N/A");
        fireTableDataChanged();
    }

    public void removePatientFromTable(int patientIndex){
        monitoredPatientNames.remove(patientIndex);
        cholesterolLevels.remove(patientIndex);
        effectiveDate.remove(patientIndex);
        fireTableDataChanged();
    }

    public void updateMeasurements(PatientRecord newPatient, Measurement newMeasurement){
        // obtain index to navigate inside table data
        int currentIndex = monitoredPatients.indexOf(newPatient);

        if (newMeasurement.getMeasurementType() == Measurement.Type.CHOLESTEROL) {
            cholesterolLevels.remove(currentIndex);
            cholesterolLevels.add(currentIndex, newMeasurement.getMeasurementValue().toString());
        }
        else{
            System.out.println("Error: measurement value being updated contains invalid measurement type");
        }

        effectiveDate.remove(currentIndex);
        effectiveDate.add(currentIndex, newMeasurement.getDateMeasured().toString());

        fireTableDataChanged();
    }

    // not used??
    /*
    public void addValue(String newMonPatientName, int newMonValue, Date date){
        monitoredPatientNames.add(newMonPatientName);
        cholesterolLevels.add(Integer.toString(newMonValue));
        effectiveDate.add(date.toString());
    }*/

    public Object getValueAt(int row, int col) {
        return monitoredData.get(col).get(row);
    }

    @Override
    public String getColumnName(int column) {
        return dataColumns.get(column);
    }

    public int getColumnCount() {
        return dataColumns.size();
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
}
