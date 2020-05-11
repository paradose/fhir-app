package com.teamohno;

import java.math.BigDecimal;

public class CholObserver extends Observer {
    private PatientSubject observerState;
    private Cholesterol lastState;

    // is it right ??? patientSubject is passed
    public CholObserver(PatientSubject patient){
        observerState = patient;
        // sets last state as null by default
        lastState= new Cholesterol(BigDecimal.ZERO, null);
    }
    @Override
    public void update() {
        PatientRecord newState = observerState.getState();
        Cholesterol patientsCurrentChol = newState.getCholesterolMeasurement();
        BigDecimal totalchol = patientsCurrentChol.getCholesterolValue();
        // compares last state with current
        boolean sameChol = totalchol.equals(lastState.getCholesterolValue());
        if (!sameChol){
            lastState.setCholesterolValue(totalchol);
            // send update to model
            System.out.println(patientsCurrentChol.getCholesterolValue());
        }else{
            System.out.println("no change : )");
        }
    }
}
