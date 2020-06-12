package com.teamohno;

import java.util.Date;

public class TextualObserver extends Observer {
    private PatientSubject observerSubject;
    private Date lastState;
    private HistoricalTableModel monitorredData;
    private MeasurementType type;

    // Constructor
    public TextualObserver(PatientSubject patient, HistoricalTableModel newHistoricalTable, MeasurementType newType){
        observerSubject = patient;
        // sets last state as patients current measurement recorded date
        lastState = patient.getState().getMeasurement(newType).getDateMeasured();
        monitorredData = newHistoricalTable;
        type = newType;
    }

    @Override
    public void update() {
        System.out.println("Size of observer's total subject list:" + type.getMonitorredSubjects().size());
        System.out.println("Number of Valid subjects: " + type.getValidMonitored());
        // this should be their history
        MeasurementRecording patientsNewRecording = observerSubject.getState().getMeasurement(type);
        Date newState = patientsNewRecording.getDateMeasured();

        //check if date of new recording after old state date
        if (newState.compareTo(lastState) > 0) {
            System.out.println("Old state: " + lastState + ", new state " + newState);
            // push new recording into history
            monitorredData.updateHistory(observerSubject.getState().getLastRecordings(type),observerSubject.getState());
        }
    }

}
