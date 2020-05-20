package com.teamohno;

import java.util.ArrayList;
import java.util.TimerTask;

public class PeriodicMeasurementCall extends TimerTask {
    // used for testing
    public static int iteration;

    // static variable inside parent class (MeasurementCholesterolCall) -> all measurement periodic calls to have same frequency
    public static int frequency;
    private MeasurementType type;
    private ArrayList<PatientSubject> patientSubjectList;
    private boolean isTurnedOn;

    public PeriodicMeasurementCall(){ isTurnedOn = false;}

    public PeriodicMeasurementCall(MeasurementType typeMeasurements){
        isTurnedOn = false;
        patientSubjectList = typeMeasurements.getMonitorredSubjects();
        type = typeMeasurements;

        iteration = 0;
    }

    public void setTurnedOn(){isTurnedOn = true;}

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

        System.out.println("waited for " + frequency/1000 + " seconds.");
        for (int i = 0; i < patientSubjectList.size(); i++) {
            if(patientSubjectList.get(i).getActive()) {
                patientSubjectList.get(i).updateMeasurementValue(type);
            }
            // For testing
            System.out.println("calling for cholesterol of patient " + " name:" + patientSubjectList.get(i).getState().getFirstName());
        }

        System.out.println("Iteration " + iteration);
        iteration++;
    }
}
