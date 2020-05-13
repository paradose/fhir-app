package com.teamohno;

import java.math.BigDecimal;

public class CholObserver extends Observer {
    private PatientSubject observerState;
    private Cholesterol lastState;
    private MonitorTableModel monitorredData;

    // is it right ??? patientSubject is passed
    public CholObserver(PatientSubject patient, MonitorTableModel newModelTable){
        observerState = patient;
        // sets last state as patients current cholesterol value
        lastState = patient.getState().getCholesterolMeasurement();
        monitorredData = newModelTable;
    }
    @Override
    public void update() {
        PatientRecord newState = observerState.getState();

        Cholesterol patientsNewChol = newState.getCholesterolMeasurement();
        BigDecimal newTotalcholVal = patientsNewChol.getCholesterolValue();

        //check first then set value (check date?)
        if (!(newTotalcholVal == lastState.getCholesterolValue())){
            // send update to model
            monitorredData.updateMeasurements(observerState.getState(), patientsNewChol);
        }
        else{
            System.out.println("Patient " + observerState.getState().getId() + " has no change in cholesterol :)");
        }
        System.out.println("Observer spotted new chol val: " + patientsNewChol.getCholesterolValue());
    }
}
