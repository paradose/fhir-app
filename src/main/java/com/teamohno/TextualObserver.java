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
//         this should be their history
        ArrayList<MeasurementRecording> patientsNewRecordings = observerSubject.getState().getLastRecordings(type);
        MeasurementRecording patientsLatestRecording = patientsNewRecordings.get(patientsNewRecordings.size()-1);
        //check if date of new recording after old state date
        Date newState = patientsLatestRecording.getDateMeasured();
        // this wont work with incremental testing unless we can change the dates ?? which i have not figured out.
        // otherwise comment out if statement,
        if (newState.compareTo(lastState) > 0){
            monitorredData.updateHistory(observerSubject.getState().getLastRecordings(type),observerSubject.getState());
            lastState = newState;
        }
    }

}
