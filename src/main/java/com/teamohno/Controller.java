package com.teamohno;

import org.apache.commons.lang3.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

public class Controller {
    private Model myModel;
    private Timer myTimer;
    private TimerTask myPeriodicTask;
    private View myView;
    private Server server;

    public Controller(Model newModel, View newView, Server inputServer){
        myModel = newModel;
        myView = newView;
        server = inputServer;
    }

    public void initView(){
        //initialise view - set default text from model
    }

    public void initController(){
        //initialise controller - add listeners to UI elements
        myView.getUpdatePracButton().addActionListener(e -> storePracIdentifier());
        myView.getUpdateFreqButton().addActionListener(e -> updateFrequency());

        myView.getMonitorCholButton().addActionListener(e -> monitorSelectedPatients());
        myView.getStopMonitorButton().addActionListener(e -> stopMonitorSelectedPatients());
    }

    public void storePracIdentifier(){
        if(!myView.getPracIDfield().getText().isEmpty()) {
            myModel.createPractitoner(myView.getPracIDfield().getText(),server);
            System.out.println("Entered Prac Identifier: " + myView.getPracIDfield().getText());

            updatePatientList();
        }
    }

    public void updatePatientList(){
        // retrieve patients for the given practitioner id
//        myModel.getPractitioner().retrievePractitionerPatients();
        myModel.getPractitioner().makeFake();

        // update model with new practitioner id now stored in model
        myModel.updatePatientNamesList();
    }

    public void monitorSelectedPatients() {
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

                // add patient to subjectArray and attach server for requests
                PatientSubject newSubject = new PatientSubject(processPatient, server);
                myModel.getMonitoredSubjects().add(newSubject);

                //stop current schedule if necessary???

//                 trigger scheduler - pass in patientSubject Array
                scheduleMonitor();
            }
        }
    }

    public void stopMonitorSelectedPatients() {
        // get selected indexes from JTable
        int[] selectedIndices = myView.getMonitorTable().getSelectedRows();

        for (int i = 0; i < selectedIndices.length ; i++) {
            PatientRecord processPatient = myModel.getPractitioner().getPractitionerPatients().get(selectedIndices[i]);
            // check if monitored
            if(processPatient.getIsMonitored()) {
                // remove patientRecords from monitorTable's (index) array
                myModel.getMonitorTable().removeMonitoredPatient(processPatient);
                // remove patient row's from monitorTableData (names)
                myModel.getMonitorTable().removePatientFromTable(selectedIndices[i]);
                processPatient.triggerMonitorState();

                // remove patientSubject corresponding to processPatientRecord
                for (int j = 0; j < myModel.getMonitoredSubjects().size(); j++) {
                    if (myModel.getMonitoredSubjects().get(j).getState().getId() == processPatient.getId()) {
                        myModel.getMonitoredSubjects().remove(myModel.getMonitoredSubjects().get(j));
                    }
                }
            }
        }

        // if no more patients monitored stop scheduler?
    }

    // add everyoine back in!!
    public void scheduleMonitor(){
        myTimer = new Timer();

        // check the monitored list of patients... is inside... monitor table
//        if(myModel..size()){/
//
//        }
//
//        for (int i = 0; i < ; i++) {
            // add back into the monitor table...?
//        }

        String inputFrequency = myView.getFreqValueLabel().getText();
        int intFreq = Integer.parseInt(inputFrequency);
        // implement a parent class for measurementCalls
//        TimerTask measurementCall = new PeriodicMeasurementCall(patientSubArray);
        myPeriodicTask = new PeriodicCholesterolCall(myModel.getMonitoredSubjects());

        myTimer.scheduleAtFixedRate(myPeriodicTask, 0, 1000 * intFreq);
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
        // stop timerTask
    }

    public void updateFrequency(){
        String inputFrequency = myView.getFreqField().getText();
        if(StringUtils.isNumeric(inputFrequency)) {
            int intFreq = Integer.parseInt(inputFrequency);

            myView.getFreqValueLabel().setText(inputFrequency);
            myView.getFreqField().setText("");

            if(myModel.getMonitoredSubjects().size() > 0) {
                stopMonitor();
                scheduleMonitor();
            }
        }
        else{
            System.out.println("Error: frequency value is not numeric");
        }
    }
}
