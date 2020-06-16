package com.teamohno;

//import jdk.javadoc.internal.doclets.formats.html.PackageUseWriter;

import org.hl7.fhir.r4.model.Measure;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
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
        recordingChartData = createDataSet();
        monitoredData.add(monitoredPatientNames);
        monitoredData.add(monitoredLastRecordings);
        this.fireTableDataChanged();
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
        monitoredLastRecordings.add(lastRecordingsToString(lastRecordings, Constants.MeasurementType.SYSTOLIC_BP));
        monitoredPatientID.add(patientSubject.getState().getId());
        PatientRecord monitoredPatient = patientSubject.getState();
        XYSeries newPatient = new XYSeries(monitoredPatient.getFirstName() + monitoredPatient.getLastName());
        for (int i=1; i<monitoredPatient.getLastRecordings(historicalType).size()+1; i++){
            newPatient.add(i,monitoredPatient.getLastRecordings(historicalType)
                    .get(i-1).getMeasurementValue(Constants.MeasurementType.SYSTOLIC_BP).doubleValue());
        }
        recordingChartData.addSeries(newPatient);

        fireTableDataChanged();
    }

    public void removePatient(String patientId){
        int index = monitoredPatientID.indexOf(patientId);
        if (index!=-1) {
            recordingChartData.removeSeries(recordingChartData.getSeries(monitoredPatientNames.get(index)));
            for (int i = 0; i < monitoredData.size(); i++) {
                // this removes ALL data
                monitoredData.get(i).remove(index);
            }
            subjects.remove(index);
            monitoredPatientID.remove(index);

            fireTableDataChanged();
        }
    }

    public boolean updateHistory(ArrayList<MeasurementRecording> lastRecordings, PatientRecord newRecord){
        // gets index in table
        int patientIndex = monitoredPatientID.indexOf(newRecord.getId());

        if(patientIndex == -1) return false;

        String newTextualRecording = lastRecordingsToString(lastRecordings, Constants.MeasurementType.SYSTOLIC_BP);
        monitoredData.get(1).remove(patientIndex);
        monitoredData.get(1).add(patientIndex,newTextualRecording);

        // removes old history from the chart and creates
        if (graphMonitor) {
            recordingChartData.removeSeries(recordingChartData.getSeries(newRecord.getFirstName() + newRecord.getLastName()));
            XYSeries updatedPatient = new XYSeries(newRecord.getFirstName() + newRecord.getLastName());
            for (int i = 1; i < newRecord.getLastRecordings(historicalType).size()+1; i++) {
                updatedPatient.add(i, newRecord.getLastRecordings(historicalType)
                        .get(i-1).getMeasurementValue(Constants.MeasurementType.SYSTOLIC_BP).doubleValue());
            }
            recordingChartData.addSeries(updatedPatient);
        }
        fireTableDataChanged();
        System.out.println("Historical Table is being Updated !");
        return true;
    }

    // need an extra function that converts last recordings to data values for xy graph

    public XYSeriesCollection createDataSet(){
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (PatientSubject monitoredSubject: subjects){
            PatientRecord monitoredPatient = monitoredSubject.getState();
            XYSeries newPatient = new XYSeries(monitoredPatient.getFirstName() + monitoredPatient.getLastName());
            for (int i=1; i<monitoredPatient.getLastRecordings(historicalType).size()+1; i++){
                newPatient.add(i,monitoredPatient.getLastRecordings(historicalType)
                        .get(i-1).getMeasurementValue(Constants.MeasurementType.SYSTOLIC_BP).doubleValue());
            }
            dataset.addSeries(newPatient);
        }
        return dataset;
    }
    // need to test that this is being called by altering dates.
    public String lastRecordingsToString(ArrayList<MeasurementRecording> lastRecordings, Constants.MeasurementType childType){
        String returnString = "";
        for (MeasurementRecording lastRecording : lastRecordings){
            returnString += lastRecording.getMeasurementValue(childType).toString() + " " + lastRecording.getDateMeasured() + "\n";
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
        NumberAxis domain = (NumberAxis) measurementGraph.getXYPlot().getDomainAxis();
        domain.setRange(1.0,5.0);
        domain.setTickUnit(new NumberTickUnit(1.0));
        domain.setVerticalTickLabels(true);
//        domain.setRange(0.00, 1.00);
        ChartFrame chartFrm = new ChartFrame( "Systolic Blood" + " Levels", measurementGraph);
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
        // need to clear the dataset as well. ****
        recordingChartData.removeAllSeries();
        fireTableDataChanged();
    }

}
