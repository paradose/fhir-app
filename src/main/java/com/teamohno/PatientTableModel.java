package com.teamohno;

import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;

public abstract class PatientTableModel extends AbstractTableModel {
    // Table array - containts lists (columns) of data
    protected ArrayList<ArrayList<String>> monitoredData;
    protected ArrayList<String> columnNames;
    protected ArrayList<String> monitoredPatientNames;
    // used to track index of patient that are being monitored within table
    protected ArrayList<String> monitoredPatientID;

    public PatientTableModel(){
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

    public void clearDataValues(){
        System.out.println("Clearing monitorred data in table");
        // loop all data columns
        for (int i = 0; i < monitoredData.size(); i++) {
            monitoredData.get(i).clear();
        }
        monitoredPatientID.clear();
        fireTableDataChanged();
    }

    @Override
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

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames.get(column);
    }

    @Override
    public Object getValueAt(int row, int col) {
        // column index = for the list of names / measurements / dates
        // row index = for the value that column
        return monitoredData.get(col).get(row);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }
}
