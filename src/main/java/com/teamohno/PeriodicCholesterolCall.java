package com.teamohno;

import java.util.ArrayList;
import java.util.TimerTask;

public class PeriodicCholesterolCall extends TimerTask {
    public static int iteration;

    // put a static variable inside parent class (MeasurementCholesterolCall) -> so this would cause all meassurement periodic calls to have saem frequency?
    public static int frequency;

    private ArrayList<PatientSubject> patientSubjectList;

    public PeriodicCholesterolCall(ArrayList<PatientSubject> newPatientSubjectList){
        patientSubjectList = newPatientSubjectList;
        iteration = 0;
        frequency = 2000;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(frequency);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < patientSubjectList.size(); i++) {
            //commented out to test without server
            patientSubjectList.get(i).updateCholVal();

            // For testing
            System.out.println("Iteration " + iteration + ", Finding cholesterol/measurement for patient " + i);
            System.out.println("patient " + i + " , name:" + patientSubjectList.get(i).getState().getFirstName());
        }

        iteration++;
    }
}
