package com.teamohno;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

// change to general measurement (pass in measurement type in constructor to make column name

public class MonitorTableModel extends AbstractTableModel {
    private ArrayList<String> dataColumns;
    private ArrayList<ArrayList<String>> monitoredData;
    private ArrayList<String> monitoredPatientNames;
    private ArrayList<String> cholesterolLevels;
    private ArrayList<String> effectiveDate;

    // used to gather index of patient that are being monitored - might be redundant due to lack of knowledge of row for measurement - will use from model
    private ArrayList<ArrayList<String>> patientMeasurementTable;
    private ArrayList<String> monitoredPatientID;
    private ArrayList<String> monitoredType;

    //watches the average of chol
    private BigDecimal totalChol;
    private BigDecimal totalAverageChol;
    private CholCellRenderer cholAverageWatcher;


    public MonitorTableModel() {
        patientMeasurementTable = new ArrayList<ArrayList<String>>();
        monitoredPatientID = new ArrayList<String>();
        monitoredType = new ArrayList<String>();
        patientMeasurementTable.add(monitoredPatientID);
        patientMeasurementTable.add(monitoredType);
        totalAverageChol = BigDecimal.ZERO;
        totalChol = BigDecimal.ZERO;
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
        cholAverageWatcher = new CholCellRenderer();
    }

    public ArrayList<ArrayList<String>> getIndexPatientsMeasurement(){
        return  patientMeasurementTable;
    }

    // returns number of patients being monitored (from data table)
    public int getMonitoredRowCount(Measurement.Type newType){
        int size = -1;
        if(monitoredPatientID.size() == monitoredType.size()){
            size = monitoredPatientID.size();
        }
        else{
            System.out.println("Error: patient and type data inconsistent");
        }
        return size;
    }

    // use this to replace addPatientName
    public boolean addMonitorPatient(String newPatientID, String newPatientName, String newType){
        boolean returnResult = false;
        // if patient NOT monitored (OR patient monitored but different type) ->
        if (monitoredPatientID.contains(newPatientID) && monitoredType.contains(newType)){
            System.out.println("Error: attempting monitor patient's " + newType + ", with id: " + newPatientID + ", which is already monitored");
        }
        else {
            monitoredPatientID.add(newPatientID);
            monitoredPatientNames.add(newPatientName);
            monitoredType.add(newType);
            cholesterolLevels.add("N/A");
            effectiveDate.add("N/A");
            fireTableDataChanged();
            returnResult = true;
        }
        return returnResult;
    }

    public void removePatientFromTable(int newPatientIndex){
        // remove to track index
        monitoredPatientID.remove(newPatientIndex);
        monitoredType.remove(newPatientIndex);

        monitoredPatientNames.remove(newPatientIndex);
        //
        if (!cholesterolLevels.get(newPatientIndex).equals("N/A"))
        totalChol = totalChol.subtract(new BigDecimal(cholesterolLevels.get(newPatientIndex)));

        cholesterolLevels.remove(newPatientIndex);
        effectiveDate.remove(newPatientIndex);
        fireTableDataChanged();
    }

    public void updateMeasurements(PatientRecord newPatient, Measurement newMeasurement){
        // obtain index to navigate inside table data
        int currentIndex = monitoredPatientID.indexOf(newPatient.getId());
        BigDecimal oldValue;
        if (newMeasurement.getMeasurementType() == Measurement.Type.CHOLESTEROL) {
            // where value cant be casted into big decimal consider??
            if (!cholesterolLevels.get(currentIndex).equals("N/A")) {
                oldValue = new BigDecimal(cholesterolLevels.get(currentIndex));
            } else oldValue = BigDecimal.ZERO;

            cholesterolLevels.remove(currentIndex);
            effectiveDate.remove(currentIndex);

            BigDecimal newValue =newMeasurement.getMeasurementValue();
            updateNewCholAverage(oldValue,newValue);

            cholesterolLevels.add(currentIndex, newMeasurement.getMeasurementValue().toString());
            effectiveDate.add(currentIndex, newMeasurement.getDateMeasured().toString());
        }
        else {
            System.out.println("Error: measurement value being updated contains invalid measurement type");
        }
        fireTableDataChanged();
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
        return dataColumns.get(column);
    }

    public int getColumnCount() {
        return dataColumns.size();
    }
    // gets class of the column ?
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
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

    public ArrayList<String> getMonitoredPatientID(){
        return monitoredPatientID;
    }
    public void clearDataValues(){
        System.out.println("Clearing monitorred data in table");
        monitoredPatientNames.clear();
        cholesterolLevels.clear();
        effectiveDate.clear();

        monitoredPatientID.clear();
        fireTableDataChanged();
    }
}
