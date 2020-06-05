package com.teamohno;

import java.math.BigDecimal;

public class MeasurementObserver extends Observer {
    // Instance variables
    private PatientSubject observerSubject;
    private BigDecimal lastState;
    private MonitorTableModel monitorredData;
    private MeasurementType type;

    // Constructor
    public MeasurementObserver(PatientSubject patient, MonitorTableModel newModelTable, MeasurementType newType){
        observerSubject = patient;
        // sets last state as patients current cholesterol value
        lastState = patient.getState().getMeasurement(newType).getMeasurementValue();
        monitorredData = newModelTable;
        type = newType;
    }

    @Override
    public void update() {
        System.out.println("Size of observer's total subject list:" + type.getMonitorredSubjects().size());
        System.out.println("Number of Valid subjects: " + type.getValidMonitored());
        MeasurementRecording patientsNewRecording = observerSubject.getState().getMeasurement(type);
        BigDecimal newTotalVal = patientsNewRecording.getMeasurementValue();

        //check first then set value
        System.out.println("Old state: " + lastState + ", new state " + newTotalVal);
        if (newTotalVal.compareTo(lastState) != 0){
            // send update to model
            monitorredData.updateMeasurements(observerSubject.getState(), patientsNewRecording);

            //update average -> get renderer to change colour
            type.updateAverage();
            monitorredData.getMeasurementRenderer().updateCellValue(type.getAverage());

            System.out.println("Observer's new measurement value: " + patientsNewRecording.getMeasurementValue());
        }
        // no change in measurement value
        else{
            System.out.println("Patient " + observerSubject.getState().getId() + " has no change in " + type);
        }
        // Update observer state
        lastState = patientsNewRecording.getMeasurementValue();
        System.out.println("Observer updated");
    }
}
