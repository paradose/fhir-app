package com.teamohno;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class HistoricalTableModel extends AbstractTableModel {
    // Table array - containts lists (columns) of data
    private ArrayList<ArrayList<String>> monitoredData;
    private ArrayList<String> columnNames;
    private ArrayList<String> monitoredPatientNames;

    // used to track index of patient that are being monitored within table
    private ArrayList<String> monitoredPatientID;

    public HistoricalTableModel(){
        createTable();
    }

    public void createTable(){
        monitoredPatientID = new ArrayList<>();

        columnNames = new ArrayList<>();
        columnNames.add("Name");

        monitoredData = new ArrayList<>();
        monitoredPatientNames = new ArrayList<>();
        monitoredData.add(monitoredPatientNames);
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

    }
}
