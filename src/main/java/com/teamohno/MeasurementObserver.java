package com.teamohno;

import java.math.BigDecimal;
import java.util.ArrayList;

public class MeasurementObserver extends Observer {
    private PatientSubject observerSubject;
    private BigDecimal lastState;
    private MonitorTableModel monitorredData;
    private MeasurementType measurementType;

    // MeasurementObserver(subject, table, measurementName)
    public MeasurementObserver(PatientSubject patient, MonitorTableModel newModelTable, MeasurementType newType){
        observerSubject = patient;
        // sets last state as patients current cholesterol value
        lastState = patient.getState().getMeasurement(newType).getMeasurementValue();
        monitorredData = newModelTable;
        measurementType = newType;
    }
    @Override
    public void update() {
        System.out.println("Size of observer's subject list:" + measurementType.getMonitorredSubjects());
        MeasurementRecording patientsNewRecording = observerSubject.getState().getMeasurement(measurementType);
        BigDecimal newTotalVal = patientsNewRecording.getMeasurementValue();

        //check first then set value
        System.out.println("Old state: " + lastState + ", new state " + newTotalVal);
        if (newTotalVal.compareTo(lastState) != 0){
            // send update to model
            monitorredData.updateMeasurements(observerSubject.getState(), patientsNewRecording);

            //update average -> get renderer to change colour
            measurementType.updateAverage();
            monitorredData.getMeasurementRenderer().updateCholAverage(measurementType.getAverage());

            System.out.println("Observer spotted new chol val: " + patientsNewRecording.getMeasurementValue());
        }
        else{
            System.out.println("Patient " + observerSubject.getState().getId() + " has no change in " + measurementType);
        }
        System.out.println("Observer updated");
        lastState = patientsNewRecording.getMeasurementValue();
    }

    public void setObserverSubject(PatientSubject newSubject){
        observerSubject = newSubject;
    }
}
