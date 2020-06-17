package com.teamohno;

import java.util.ArrayList;
import java.util.Date;

public class TextualObserver extends Observer {
    // Instance variables
    private PatientSubject observerSubject;
    private Date lastState;
    private HistoricalTableModel monitorredData;
    private MeasurementType type;

    // Constructor
    public TextualObserver(PatientSubject patient, HistoricalTableModel newHistoricalTable, MeasurementType newType){
        observerSubject = patient;
        // sets last state as patient's latest measurement recorded date
        lastState = patient.getState().getMeasurement(newType).getDateMeasured();
        monitorredData = newHistoricalTable;
        type = newType;
    }

    @Override
    public void update() {
//      Updating new history of recordings
        ArrayList<MeasurementRecording> patientsNewRecordings = observerSubject.getState().getLastRecordings(type);
        // Get most recent recording (need to update)
        MeasurementRecording patientsLatestRecording = patientsNewRecordings.get(patientsNewRecordings.size()-1);
        //check if date of new recording is after the last updated recording's date
        Date newState = patientsLatestRecording.getDateMeasured();
        if (newState.compareTo(lastState) > 0){
            monitorredData.updateHistory(observerSubject.getState().getLastRecordings(type),observerSubject.getState());
            lastState = newState;
        }
    }

}
