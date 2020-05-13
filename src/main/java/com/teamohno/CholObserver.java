package com.teamohno;

import java.math.BigDecimal;

public class CholObserver extends Observer {
    private PatientSubject observerSubject;
    private Cholesterol lastState;
    private MonitorTableModel monitorredData;

    public CholObserver(PatientSubject patient, MonitorTableModel newModelTable){
        observerSubject = patient;
        // sets last state as patients current cholesterol value
        lastState = patient.getState().getCholesterolMeasurement();
        monitorredData = newModelTable;
    }
    @Override
    public void update() {
        Cholesterol patientsNewChol = observerSubject.getState().getCholesterolMeasurement();
        BigDecimal newTotalcholVal = patientsNewChol.getCholesterolValue();

        //check first then set value
        if (!(newTotalcholVal == lastState.getCholesterolValue())){
            // send update to model
            monitorredData.updateMeasurements(observerSubject.getState(), patientsNewChol);
            System.out.println("Observer spotted new chol val: " + patientsNewChol.getCholesterolValue());
        }
        else{
            System.out.println("Patient " + observerSubject.getState().getId() + " has no change in cholesterol :)");
        }

        System.out.println("Observer updated");
        lastState = patientsNewChol;
    }

    public void setObserverSubject(PatientSubject newSubject){
        observerSubject = newSubject;
    }
}
