package com.teamohno;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;

public class MonitorTableModel extends PatientTableModel {
    // Instance Variables
    // Arraylist of subject list(s)
    protected ArrayList<ArrayList<PatientSubject>> monitoredMeasurementSubjects;
    // Renderer watches the average value
    protected MeasurementCellRenderer measurementMinWatcher;
    protected Color observedCellColour;
    protected DefaultCategoryDataset measurementData;

    // Constructor
    public MonitorTableModel(){
        createTable();
    }

    public MonitorTableModel(ArrayList<MeasurementType> newTypes, Color colour) {
        createTable();
        this.setObservedCellColour(colour);
        for (int i = 0; i < newTypes.size(); i++) {
            addMeasurementType(newTypes.get(i));
        }
    }

    public void createTable(){
        monitoredMeasurementSubjects = new ArrayList<>();
        super.createTable();
    }

    // Accessors and Mutators
    public void addMeasurementType(MeasurementType newType){
        ArrayList<String> listVlaues = new ArrayList<>();
        ArrayList<String> listDates = new ArrayList<>();
        monitoredData.add(listVlaues);
        monitoredData.add(listDates);
        columnNames.add(newType.getName());
        // Storing current column index for the measurement
        measurementMinWatcher = new MeasurementCellRenderer(columnNames.size() - 1,observedCellColour);
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
            if(newType.getComponentSize() > 0){
                addComponentNames();
            }
            else {
                for (int i = 0; i < columnNames.size(); i++) {
                    if (columnNames.get(i).equals(newType.getName())) {
                        monitoredData.get(i).add("-");
                        // add date
                        monitoredData.get(i + 1).add("-");
                    }
                }
            }

            fireTableDataChanged();
            returnResult = true;
        }
        return returnResult;
    }

    // Used by when measurement type has multiple components
    protected void addComponentNames(){}

    public void removePatientFromTable(int newPatientIndex){
        // remove to track index
        monitoredPatientID.remove(newPatientIndex);
        // need to make this specific to measurememt
        if (measurementData !=null)
        measurementData.removeValue("Cholesterol",monitoredPatientNames.get(newPatientIndex));
        // loop through data columns
        for (int i = 0; i < monitoredData.size(); i++) {
            // this removes ALL data
            monitoredData.get(i).remove(newPatientIndex);
        }
        fireTableDataChanged();
    }

    public ArrayList<String> getMonitoredPatientID() {
        return monitoredPatientID;
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
                if (measurementData !=null)
                    // updates the value in the chart with new value
                    measurementData.setValue(newMeasurement.getMeasurementValue().intValue(), newMeasurement.getType().getName(), monitoredPatientNames.get(currentIndex));
            }
            fireTableDataChanged();
        }
    }

    public MeasurementCellRenderer getMeasurementRenderer(){
        return measurementMinWatcher;
    }

    public void setMinColouredValue(double newValue, int column){
        getMeasurementRenderer().updateMinColouredValue(newValue);
    }

    public Color getObservedCellColour(){return observedCellColour;}

    public void setObservedCellColour(Color c){observedCellColour = c;}
    // gets initial measurements of monitored patients and sets to dataset for chart

    public DefaultCategoryDataset getMonitoredMeasurements(MeasurementType selectedType) {
        DefaultCategoryDataset dod = new DefaultCategoryDataset();
        // Currently table only has one measurement - hence retrieving from first subject list
        /* If more types are added to this table, iterate through each column name and only process subject list of the given type */
        for (int j=0;j<monitoredMeasurementSubjects.get(0).size();j++){
            PatientSubject processSubject = monitoredMeasurementSubjects.get(0).get(j);
            int value = processSubject.getState().getMeasurement(selectedType).getMeasurementValue().intValue();
            String subjectFirstName = processSubject.getState().getFirstName();
            String subjectLastName = processSubject.getState().getLastName();
            dod.setValue(value, selectedType.getName(), subjectFirstName +" "+ subjectLastName);
        }
        return dod;
    }

    public void addChart(DefaultCategoryDataset chartData){
        // adds observed data from chart.
        measurementData = chartData;
    }
}
