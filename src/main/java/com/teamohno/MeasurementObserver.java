package com.teamohno;


import java.util.Date;

public class MeasurementObserver extends Observer {
    // Instance variables
    private PatientSubject observerSubject;
    private Date lastState;
    private MonitorTableModel monitorredData;
    private MeasurementType type;

    // Constructor
    public MeasurementObserver(PatientSubject patient, MonitorTableModel newModelTable, MeasurementType newType){
        observerSubject = patient;
        // sets last state as patients current measurement recorded date
        lastState = patient.getState().getMeasurement(newType).getDateMeasured();
        monitorredData = newModelTable;
        type = newType;
    }

    @Override
    public void update() {
        System.out.println("Size of observer's total subject list:" + type.getMonitorredSubjects().size());
        System.out.println("Number of Valid subjects: " + type.getValidMonitored());
        MeasurementRecording patientsNewRecording = observerSubject.getState().getMeasurement(type);
        Date newState = patientsNewRecording.getDateMeasured();

        //check if date of new recording after old state date
        if (newState.compareTo(lastState) > 0){
            System.out.println("Old state: " + lastState + ", new state " + newState);
            // push new recording into history
            observerSubject.getState().pushNewRecordingHistory(type);

            // send update to model
            monitorredData.updateMeasurements(observerSubject.getState(), patientsNewRecording);

            //update average
            type.updateAverage();

            // Used for single value measurements - updates renderer for the table assigned to this observer
            monitorredData.getMeasurementRenderer().updateMinColouredValue(type.getAverage());

            System.out.println("Observer's new measurement value: " + patientsNewRecording.getMeasurementValue());
        }
        // no change in measurement value
        else{
            System.out.println("Patient " + observerSubject.getState().getId() + " has no change in " + type);
        }
        // Update observer state
        lastState = patientsNewRecording.getDateMeasured();
        System.out.println("Observer updated");
    }
}
