package com.teamohno;

import java.util.ArrayList;
import java.util.Date;

public class TextualObserver extends Observer {
    private PatientSubject observerSubject;
//    private ArrayList<MeasurementRecording> lastState;
    private Date lastState;
    private HistoricalTableModel monitorredData;
    private MeasurementType type;

    // Constructor
    public TextualObserver(PatientSubject patient, HistoricalTableModel newHistoricalTable, MeasurementType newType){
        observerSubject = patient;
        // sets last state as patients current measurement recorded date
//        lastState = patient.getState().getLastRecordings(newType);
        lastState = patient.getState().getMeasurement(newType).getDateMeasured();
        monitorredData = newHistoricalTable;
        type = newType;
    }

    @Override
    public void update() {
        // this should be their history
//        ArrayList<MeasurementRecording> patientsNewRecordings = observerSubject.getState().getLastRecordings(type);
        MeasurementRecording patientsNewRecording = observerSubject.getState().getMeasurement(type);
        //check if date of new recording after old state date
        Date newState = patientsNewRecording.getDateMeasured();

        //check if date of new recording after old state date
//        if (newState.compareTo(lastState) > 0){

//            if (lastState != patientsNewRecordings) {
            // push new recording into history
            monitorredData.updateHistory(observerSubject.getState().getLastRecordings(type),observerSubject.getState());
            lastState = newState;
//        }
    }

}
