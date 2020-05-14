package com.teamohno;

import java.math.BigDecimal;

public class CholObserver extends Observer {
    private PatientSubject observerSubject;
    private BigDecimal lastState;
    private MonitorTableModel monitorredData;

    public CholObserver(PatientSubject patient, MonitorTableModel newModelTable){
        observerSubject = patient;
        // sets last state as patients current cholesterol value
        lastState = patient.getState().getCholesterolMeasurement().getCholesterolValue();
        monitorredData = newModelTable;
    }
    @Override
    public void update() {
        Cholesterol patientsNewChol = observerSubject.getState().getCholesterolMeasurement();
        BigDecimal newTotalcholVal = patientsNewChol.getCholesterolValue();

        //check first then set value
        if (!(newTotalcholVal == lastState)){
            // send update to model
            monitorredData.updateMeasurements(observerSubject.getState(), patientsNewChol);

            //... calculate new average

            System.out.println("Observer spotted new chol val: " + patientsNewChol.getCholesterolValue());
    }
        else{
            System.out.println("Patient " + observerSubject.getState().getId() + " has no change in cholesterol :)");
        }

        System.out.println("Observer updated");
        lastState = patientsNewChol.getCholesterolValue();
    }

    public void setObserverSubject(PatientSubject newSubject){
        observerSubject = newSubject;
    }
}
