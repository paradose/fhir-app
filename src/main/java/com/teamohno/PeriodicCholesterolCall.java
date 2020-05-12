package com.teamohno;

import java.util.ArrayList;
import java.util.TimerTask;

public class PeriodicCholesterolCall extends TimerTask {
    public static int iteration;
    private ArrayList<PatientSubject> patientSubjectList;

    public PeriodicCholesterolCall(ArrayList<PatientSubject> newPatientSubjectList){
        patientSubjectList = newPatientSubjectList;
        iteration = 0;
    }

    @Override
    public void run() {
        for (int i = 0; i < patientSubjectList.size(); i++) {
//            patientSubjectList.get(i).getCholVal();
            System.out.println("Iteration " + iteration + ", Finding cholesterol for patient " + i);
            System.out.println("patient " + i + " , name:" + patientSubjectList.get(i).getState().getFirstName());
        }


        iteration++;
    }
}
