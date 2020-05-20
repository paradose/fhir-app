package com.teamohno;

import java.math.BigDecimal;
import java.util.ArrayList;

public class MeasurementObserver extends Observer {
    private PatientSubject observerSubject;
    private BigDecimal lastState;
    private MonitorTableModel monitorredData;
    private MeasurementType measurementType;

    // MeasurementObserver(subject, table, measurementName)
    public MeasurementObserver(PatientSubject patient, MonitorTableModel newModelTable, MeasurementType newType){
        observerSubject = patient;
        // sets last state as patients current cholesterol value
        lastState = patient.getState().getMeasurement(newType).getMeasurementValue();
        monitorredData = newModelTable;
        measurementType = newType;
    }
    @Override
    public void update() {
        System.out.println("Size of observer's subject list:" + measurementType.getMonitorredSubjects());
        MeasurementRecording patientsNewChol = observerSubject.getState().getMeasurement(measurementType);
        BigDecimal newTotalcholVal = patientsNewChol.getMeasurementValue();

        //check first then set value
        System.out.println("Old state: " + lastState + ", new state " + newTotalcholVal);
        if (!(newTotalcholVal == lastState)){
            // send update to model

            //... calculate new average
            MeasurementType type = patientsNewChol.getType();
            // fix big decimal ? not sure how we deal with decimal types
            type.updateAverage(lastState.doubleValue(),newTotalcholVal.doubleValue());
            monitorredData.getMeasurementRenderer().updateCholAverage(type.getAverage());


            monitorredData.updateMeasurements(observerSubject.getState(), patientsNewChol);



            System.out.println("Observer spotted new chol val: " + patientsNewChol.getMeasurementValue());
        }
        else{
            System.out.println("Patient " + observerSubject.getState().getId() + " has no change in " + measurementType);
        }

        System.out.println("Observer updated");
        lastState = patientsNewChol.getMeasurementValue();

        // set subjects chol value
        observerSubject.getState().setMeasurementRecordings(patientsNewChol.getMeasurementValue(), patientsNewChol.getDateMeasured(), patientsNewChol.getType());
    }

    public void setObserverSubject(PatientSubject newSubject){
        observerSubject = newSubject;
    }
}
