package com.teamohno;

import javax.swing.table.AbstractTableModel;

public class Model extends AbstractTableModel {
    private String[] monitoredColumns = {"Name","Total Cholesterol Level","Date Measured"};
    private String[][] monitoredData = {};

    private String[] patientColumns = {"First Name", "Last Name", "Monitored"};
    private Object[][] patientData = {};

    public Model() {

    }

    public void setValueAt(int row, int col, String value){
        monitoredData[row][col] = value;
    }

    @Override
    public String getColumnName(int column) {
        return monitoredColumns[column];
    }

    public int getColumnCount() {
        return monitoredColumns.length;
    }

    public int getRowCount() {
        return monitoredData.length;
    }

    public Object getValueAt(int row, int col) {
        return monitoredData[row][col];
    }

    // Patient List data
    public Object getPatientDataAt(int row, int col) {
        return patientData[row][col];
    }

    public void setPatientValueAt(int row, int col, String value){
        patientData[row][col] = value;
    }
}