package com.teamohno;

import java.util.ArrayList;
import java.util.TimerTask;

public class PeriodicMeasurementCall extends TimerTask {
    // used for testing
//    public static int iteration;

    // static variable inside parent class (MeasurementCholesterolCall) -> all measurement periodic calls to have same frequency
    public static int frequency;

    // Instance Variables
    private MeasurementType type;
    private ArrayList<PatientSubject> patientSubjectList;

    // Constructor
    public PeriodicMeasurementCall(MeasurementType typeMeasurements){
        patientSubjectList = typeMeasurements.getMonitorredSubjects();
        type = typeMeasurements;

//        iteration = 0;
    }

    // Accessors and Mutators
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

        System.out.println("Periodic caller waited for " + frequency/1000 + " seconds.");
        for (int i = 0; i < patientSubjectList.size(); i++) {
            if(patientSubjectList.get(i).getActive()) {
                patientSubjectList.get(i).updateMeasurementValue(type);
            }
            System.out.println("Calling for measurement of subject name: " + patientSubjectList.get(i).getState().getFirstName() + " " + patientSubjectList.get(i).getState().getLastName());
        }

        // For testing
//        System.out.println("Iteration: " + iteration);
//        iteration++;
    }
}
