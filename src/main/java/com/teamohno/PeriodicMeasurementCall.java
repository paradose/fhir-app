package com.teamohno;

import java.util.ArrayList;
import java.util.TimerTask;

public abstract class PeriodicMeasurementCall extends TimerTask {
    public static int iteration;

    // put a static variable inside parent class (MeasurementCholesterolCall) -> so this would cause all meassurement periodic calls to have saem frequency?
    protected int frequency;
    protected Measurement.Type type;
    protected ArrayList<PatientSubject> patientSubjectList;

    protected PeriodicMeasurementCall(){}

    protected PeriodicMeasurementCall(ArrayList<PatientSubject> newPatientSubjectList, Measurement.Type typeMeasurement){
        patientSubjectList = newPatientSubjectList;
        type = typeMeasurement;

        iteration = 0;
        // default 2 seconds
        frequency = 2000;
    }

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
        System.out.println("Iteration " + iteration + ", Repeat measurement calls");
        iteration++;
    }
}
