package com.teamohno;

import java.util.ArrayList;
import java.util.TimerTask;

public class PeriodicCholesterolCall extends PeriodicMeasurementCall {
    public PeriodicCholesterolCall(){ super(); }

    public PeriodicCholesterolCall(ArrayList<PatientSubject> newPatientSubjectList){
        super(newPatientSubjectList, Measurement.Type.CHOLESTEROL);
    }

    @Override
    public void run() {
        super.run();

        System.out.println("waited for " + frequency/1000 + " seconds.");
        for (int i = 0; i < patientSubjectList.size(); i++) {
            patientSubjectList.get(i).updateCholVal();

            // For testing
            System.out.println("calling for cholesterol of patient " + " name:" + patientSubjectList.get(i).getState().getFirstName());
        }
    }
}
