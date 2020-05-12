package com.teamohno;

import org.apache.commons.lang3.StringUtils;

import java.util.Timer;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.TimerTask;

public class Controller {
    private Model myModel;
    private Timer myTimer;
    private View myView;

    public Controller(Model newModel, View newView){
        myModel = newModel;
        myView = newView;
    }

    public void initView(){
        //initialise view - set default text from model
    }

    public void initController(){
        //initialise controller - add listeners to UI elements
        myView.getUpdatePracButton().addActionListener(e -> storePracIdentifier());
        myView.getUpdateFreqButton().addActionListener(e -> updateFrequency());

        myView.getMonitorCholButton().addActionListener(e -> monitorPatients());
        myView.getStopMonitorButton().addActionListener(e -> stopMonitorSelectedPatients());
    }

    public void storePracIdentifier(){
        if(!myView.getPracIDfield().getText().isEmpty()) {
            myModel.createPractitoner(myView.getPracIDfield().getText());
            System.out.println("Entered Prac Identifier: " + myView.getPracIDfield().getText());

            updatePatientList();
        }
    }

    public void updatePatientList(){
        // retrieve patients for the given practitioner id
        myModel.getPractitioner().retrievePractitionerPatients();

        // update model with new practitioner id now stored in model
        myModel.updatePatientNamesList();
    }

    public void monitorPatients() {
        // get selected indexes from JList
        int[] selectedIndices = myView.getPatientJList().getSelectedIndices();

        for (int i = 0; i < selectedIndices.length ; i++) {
            // if not already monitored
            if (!myModel.getPractitioner().getPractitionerPatients().get(selectedIndices[i]).getIsMonitored()){
                PatientRecord processPatient = myModel.getPractitioner().getPractitionerPatients().get(selectedIndices[i]);

                // add patients to monitorTable's array
                myModel.getMonitorTable().addMonitoredPatient(processPatient);
                // make a function inside myModel to pass patient objects from practitioner -> monitorTable? more cleaner?

                // add patients to monitorTableData (names)
                myModel.getMonitorTable().addPatientName(processPatient.getFirstName() + " " + processPatient.getLastName());
                processPatient.triggerMonitorState();

                // add patient to subjectArray
                PatientSubject newSubject = new PatientSubject(processPatient);
                myModel.getMonitoredSubjects().add(newSubject);

                //stop current schedule if necessary???

//                 trigger scheduler - pass in patientSubject Array
                scheduleMonitor();
            }
        }
    }

    public void stopMonitor(){
        for (int i = 0; i < myModel.getMonitoredSubjects().size(); i++) {
            PatientRecord processPatient = myModel.getMonitoredSubjects().get(i).getState();
            processPatient.triggerMonitorState();

            //iterating through each column of data
            for (int j = 0; j < myModel.getMonitorTable().getColumnCount(); j++) {
                myModel.getMonitorTable().getMonitorData().get(j).clear();
            }
        }
        myModel.getMonitoredSubjects().clear();
//        myTimer.cancel();
    }

    public void stopMonitorSelectedPatients() {
        // get selected indexes from JTable
        int[] selectedIndices = myView.getMonitorTable().getSelectedRows();

        for (int i = 0; i < selectedIndices.length ; i++) {
            PatientRecord processPatient = myModel.getPractitioner().getPractitionerPatients().get(selectedIndices[i]);
            // check if monitored
            if(processPatient.getIsMonitored()) {
                // remove patients to monitorTable's array
                myModel.getMonitorTable().removeMonitoredPatient(processPatient);
                // remove patients to monitorTableData (names)
                myModel.getMonitorTable().removePatientFromTable(selectedIndices[i]);
                processPatient.triggerMonitorState();

                // stop scheduler
                // remove patientSubject corresponding to processPatientRecord
                for (int j = 0; j < myModel.getMonitoredSubjects().size(); j++) {
                    if (myModel.getMonitoredSubjects().get(j).getState().getId() == processPatient.getId()) {
                        myModel.getMonitoredSubjects().remove(myModel.getMonitoredSubjects().get(j));
                    }
                }
            }
        }
    }

    public void updateFrequency(){
        String inputFrequency = myView.getFreqField().getText();
        if(StringUtils.isNumeric(inputFrequency)) {
            int intFreq = Integer.parseInt(inputFrequency);
            if(myModel.getMonitoredSubjects().size() > 0) {
                stopMonitor();
                scheduleMonitor();
            }
        }
    }


//    public void scheduleMonitor(ArrayList<PatientSubject> newPatientSubArray){
    public void scheduleMonitor(){
            myTimer = new Timer();
        String inputFrequency = myView.getFreqField().getText();
        if(StringUtils.isNumeric(inputFrequency)) {
            int intFreq = Integer.parseInt(inputFrequency);

            // implement a parent class for measurementCalls
//        TimerTask measurementCall = new PeriodicMeasurementCall(patientSubArray);

            // remove parameter - redundant
//            TimerTask measurementCall = new PeriodicCholesterolCall(newPatientSubArray);
            TimerTask measurementCall = new PeriodicCholesterolCall(myModel.getMonitoredSubjects());

            myTimer.scheduleAtFixedRate(measurementCall, 0, 1000 * intFreq);
        }
        else {
            System.out.println("Error input not a valid integer for frequency");
        }
    }
}
