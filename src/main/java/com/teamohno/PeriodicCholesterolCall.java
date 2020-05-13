package com.teamohno;

import java.util.ArrayList;
import java.util.TimerTask;

public class PeriodicCholesterolCall extends PeriodicMeasurementCall {
    public PeriodicCholesterolCall(ArrayList<PatientSubject> newPatientSubjectList){
        super(newPatientSubjectList, Measurement.Type.CHOLESTEROL);
    }

    @Override
    public void run() {
        super.run();

        for (int i = 0; i < patientSubjectList.size(); i++) {
            //commented out to test without server
            patientSubjectList.get(i).updateCholVal();

            // For testing
            System.out.println("Iteration " + iteration + ", Finding cholesterol/measurement for patient " + i);
            System.out.println("patient " + i + " , name:" + patientSubjectList.get(i).getState().getFirstName());
        }
    }
}
