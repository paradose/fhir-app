package com.teamohno;

import java.util.ArrayList;
import java.util.TimerTask;

public abstract class PeriodicMeasurementCall extends TimerTask {
    // used for testing
    public static int iteration;

    // static variable inside parent class (MeasurementCholesterolCall) -> all measurement periodic calls to have same frequency
    protected int frequency;
    protected Measurement.Type type;
    protected ArrayList<PatientSubject> patientSubjectList;
    protected boolean isTurnedOn;

    protected PeriodicMeasurementCall(){ isTurnedOn = false;}

    protected PeriodicMeasurementCall(ArrayList<PatientSubject> newPatientSubjectList, Measurement.Type typeMeasurement){
        isTurnedOn = false;
        patientSubjectList = newPatientSubjectList;
        type = typeMeasurement;

        iteration = 0;
    }

    protected void setTurnedOn(){isTurnedOn = true;}

    public boolean getTurnedOn() {return isTurnedOn;}

    public void setFrequency(int newFreq){
        frequency = newFreq;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(frequency);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Iteration " + iteration);
        iteration++;
    }
}
