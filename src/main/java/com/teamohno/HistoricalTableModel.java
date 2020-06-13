package com.teamohno;

//import jdk.javadoc.internal.doclets.formats.html.PackageUseWriter;

import org.hl7.fhir.r4.model.Measure;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;

public class HistoricalTableModel extends AbstractTableModel {
    // Table array - containts lists (columns) of data
    private ArrayList<ArrayList<String>> monitoredData;
    private ArrayList<String> columnNames;
    private ArrayList<String> monitoredPatientNames;
    private MeasurementType historicalType;
    private ArrayList<String> monitoredLastRecordings;
    private JFreeChart recordingsGraph;
    // used to track index of patient that are being monitored within table
    private ArrayList<String> monitoredPatientID;
    private ArrayList<PatientSubject> subjects;
    private String childCode;
    private XYSeriesCollection recordingChartData;
    private boolean graphMonitor;
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
        graphMonitor = false;
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
        monitoredPatientID.add(patientSubject.getState().getId());
        fireTableDataChanged();
    }

    public void removePatient(PatientSubject patientSubject){
        int index = subjects.indexOf(patientSubject);
        subjects.remove(patientSubject);
    }

    public void updateHistory(ArrayList<MeasurementRecording> lastRecordings, PatientRecord newRecord){
        // gets index in table
        int patientIndex = monitoredPatientID.indexOf(newRecord.getId());
        String newTextualRecording = lastRecordingsToString(lastRecordings, "8480-6");
        monitoredData.get(1).remove(patientIndex);
        monitoredData.get(1).add(patientIndex,newTextualRecording);

        // removes old history from the chart and creates
        if (graphMonitor) {
            recordingChartData.removeSeries(recordingChartData.getSeries(newRecord.getFirstName() + newRecord.getLastName()));
            XYSeries updatedPatient = new XYSeries(newRecord.getFirstName() + newRecord.getLastName());
            for (int i = 0; i < newRecord.getLastRecordings(historicalType).size(); i++) {
                updatedPatient.add(i, newRecord.getLastRecordings(historicalType)
                        .get(i).getMeasurementValue("8480-6").doubleValue());
            }
            recordingChartData.addSeries(updatedPatient);
        }
        fireTableDataChanged();
        System.out.println("Historical Table is being Updated !");
    }

    // need an extra function that converts last recordings to data values for xy graph
    //some reason setting default size of recording as 0?
    public XYSeriesCollection createDataSet(){
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (PatientSubject monitoredSubject: subjects){
            PatientRecord monitoredPatient = monitoredSubject.getState();
            XYSeries newPatient = new XYSeries(monitoredPatient.getFirstName() + monitoredPatient.getLastName());
            for (int i=0; i<monitoredPatient.getLastRecordings(historicalType).size(); i++){
                newPatient.add(i,monitoredPatient.getLastRecordings(historicalType)
                        .get(i).getMeasurementValue("8480-6").doubleValue());
            }
            dataset.addSeries(newPatient);
        }
        return dataset;
    }
    // need to test that this is being called by altering dates.
    public String lastRecordingsToString(ArrayList<MeasurementRecording> lastRecordings, String childCode){
        String returnString = "";
        for (MeasurementRecording lastRecording : lastRecordings){
            returnString += lastRecording.getMeasurementValue(childCode).toString() + " " + lastRecording.getDateMeasured() + ", ";
        }
        return returnString;
    }

    public void addChart(){
        recordingChartData = createDataSet();
        graphMonitor = true;
        JFreeChart measurementGraph = ChartFactory.createXYLineChart(
                "Systolic",
                "Time",
                "Levels",
                recordingChartData,
               PlotOrientation.VERTICAL,
                true,true,false);

        ChartFrame chartFrm = new ChartFrame( "Systolic" + " Levels", measurementGraph);
        chartFrm.setSize(450, 350);
        chartFrm.setVisible(true);

    }

    public void clearDataValues(){
        System.out.println("Clearing monitorred data in table");
        // loop all data columns
        for (int i = 0; i < monitoredData.size(); i++) {
            monitoredData.get(i).clear();
        }
        monitoredPatientID.clear();
        subjects.clear();
        fireTableDataChanged();
    }

}
