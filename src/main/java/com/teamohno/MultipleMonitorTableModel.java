package com.teamohno;

import java.awt.*;
import java.util.ArrayList;

public class MultipleMonitorTableModel extends MonitorTableModel {
    private MeasurementType type;
    private ArrayList<MeasurementCellRenderer> childCellRenderers;

    public MultipleMonitorTableModel(MeasurementType newType, Color c){
        super();
        childCellRenderers = new ArrayList<>();
        setObservedCellColour(c);
        type = newType;

        this.addMeasurementType(newType);
//        MeasurementCellRenderer measurementMinWatcher1 = new MeasurementCellRenderer(1, this.getObservedCellColour());
//        childCellRenderers.add(measurementMinWatcher1);
//        MeasurementCellRenderer measurementMinWatcher2 = new MeasurementCellRenderer(2, this.getObservedCellColour());
//        childCellRenderers.add(measurementMinWatcher2);
    }

    public void addMeasurementType(MeasurementType newType) {
        for (int i = 0; i < newType.getComponentSize(); i++) {
            ArrayList<String> listVlaues = new ArrayList<>();
            monitoredData.add(listVlaues);
            columnNames.add(newType.getChildTypeNames().get(i));
            // Storing current column index for the measurement
            measurementMinWatcher = new MeasurementCellRenderer(columnNames.size() - 1, this.getObservedCellColour());
            childCellRenderers.add(measurementMinWatcher);
        }
        // add date after type here
        ArrayList<String> listDates = new ArrayList<>();
        monitoredData.add(listDates);
        columnNames.add("Date Measured");
    }

    @Override
    protected void addComponentNames() {
        super.addComponentNames();
        int lastIndex = -1;
        for (int i = 0; i < columnNames.size(); i++) {
            // check if type has component types
            // add (potentially) multiple values first
            for (int j = 0; j < type.getChildTypeNames().size(); j++) {
                // check current name with all of child types - j
                if(columnNames.get(i).equals(type.getChildTypeNames().get(j))){
                    monitoredData.get(i).add("-");
                }
                if (j == type.getChildTypeNames().size() - 1){
                    lastIndex = i;
                }
            }
        }
        // add date for this type after all its components
        monitoredData.get(lastIndex).add("-");
        fireTableDataChanged();
    }

    public void updateMeasurements(PatientRecord newPatient, MeasurementRecording newMeasurement) {
        // obtain index to navigate inside table data
        int currentIndex = monitoredPatientID.indexOf(newPatient.getId());
        for (int i = 1; i < columnNames.size(); i++) {
            // if column name matches with a child component name of recording types -> set values
            // iterator...
            for (int j = 0; j < newMeasurement.getType().getComponentSize(); j++) {
                // current name
                String currentName = newMeasurement.getType().getChildTypeNames().get(j);
                String currentCode = newMeasurement.getType().getListChildCode().get(j);
                System.out.println("measurment type name: " + currentName);
                System.out.println("column name: " + columnNames.get(i));
                System.out.println("value: " + newMeasurement.getMeasurementValue(currentCode).toString());
                if(columnNames.get(i).equals(currentName)){
                    monitoredData.get(i).set(currentIndex, newMeasurement.getMeasurementValue(currentCode).toString());
                }
            }
        }
        // date - single measurement type per table - hence date == last
        monitoredData.get(monitoredData.size() - 1).set(currentIndex, newMeasurement.getDateMeasured().toString());
        fireTableDataChanged();
    }

    public MeasurementCellRenderer getMeasurementRenderer(int columnIndex){
        return childCellRenderers.get(columnIndex - 1);
    }

    public void setMinColouredValue(double newValue, int column){
        getMeasurementRenderer(column).updateMinColouredValue(newValue);
    }
}
