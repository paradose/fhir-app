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

        myView.getMonitorCholButton().addActionListener(e -> monitorSelectedPatients(Measurement.Type.CHOLESTEROL));
        myView.getStopMonitorButton().addActionListener(e -> stopMonitorSelectedPatients());
    }

    public void storePracIdentifier(){
        if(!myView.getPracIDfield().getText().isEmpty()) {
            myModel.createPractitoner(myView.getPracIDfield().getText(), server);
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

    public void monitorSelectedPatients(Measurement.Type newType) {
        // get selected indexes from JList
        int[] selectedIndices = myView.getPatientJList().getSelectedIndices();

        for (int i = 0; i < selectedIndices.length ; i++) {
            PatientRecord processPatient = myModel.getPractitioner().getPractitionerPatients().get(selectedIndices[i]);

            // add patients to monitorTable's indexArray - if haven't monitored returns false
            if (myModel.getMonitorTable().addMonitorPatient(processPatient.getId(), processPatient.getFirstName() + " " + processPatient.getLastName(), newType.toString())) {
                processPatient.triggerMonitorState();

                // add patient to subjectArray and attach server for requests
                PatientSubject newSubject = new PatientSubject(processPatient, server);
                myModel.addMonitoredSubjects(newSubject);

                //stop current schedule if previously already monitoring schedule - (may causes issues)?

                //trigger scheduler - will schedule entire monitored subject list
                scheduleMonitor();
            }
            else{
                System.out.println("Error attempting to monitor an already monitored patient+measurement combo.");
            }
        }
    }

    public void stopMonitorSelectedPatients() {
        // get selected indexes from JTable - only concern is if these indexes don't line up?
        int[] selectedIndices = myView.getMonitorTable().getSelectedRows();

        for (int i = 0; i < selectedIndices.length ; i++) {
            PatientRecord processPatient = myModel.getPractitioner().getPractitionerPatients().get(selectedIndices[i]);

            // remove patient row's
            myModel.getMonitorTable().removePatientFromTable(selectedIndices[i]);
            processPatient.triggerMonitorState();
            myModel.getMonitoredSubjects().remove(myModel.getMonitoredSubjects().get(selectedIndices[i]));
        }
        // if no more patients monitored - scheduler runs but no patients to process
    }

    // add everyoine back in!!
    public void scheduleMonitor() {
        myTimer = new Timer();

        // check if monitored patients existing (but not inside table) - check rowCount of index tracker
//        int monitoredRows = myModel.getMonitorTable().getMonitoredRowCount();
//        if (monitoredRows > 0) {
//            for (int i = 0; i < monitoredRows; i++) {
////             add back into the monitor table...?
////                (String newPatientID, String newPatientName, Measurement.Type newType){
//                String prevID = myModel.getMonitorTable().getIndexPatientsMeasurement().get(0).get(i);
//                String prevName = myModel.getMonitorTable().getIndexPatientsMeasurement().get(1).get(i);
//                String prevType = myModel.getMonitorTable().getIndexPatientsMeasurement().get(2).get(i);
//                myModel.getMonitorTable().addMonitorPatient(prevID, prevName, prevType);
//            }

            String inputFrequency = myView.getFreqValueLabel().getText();
            int intFreq = Integer.parseInt(inputFrequency);

            // implement a parent class for measurementCalls
//        TimerTask measurementCall = new PeriodicMeasurementCall(patientSubArray);
            myPeriodicTask = new PeriodicCholesterolCall(myModel.getMonitoredSubjects());

            myTimer.scheduleAtFixedRate(myPeriodicTask, 0, 1000 * intFreq);
//        }
    }

    // stop = patients data is cleared... and monitoring is ceased => however they still exist in subject array -> ready for the scheduler to start again?
    public void stopMonitor(){
        for (int i = 0; i < myModel.getMonitoredSubjects().size(); i++) {
            PatientRecord processPatient = myModel.getMonitoredSubjects().get(i).getState();
            processPatient.triggerMonitorState();

            //iterating through each column of data
            for (int j = 0; j < myModel.getMonitorTable().getColumnCount(); j++) {
                myModel.getMonitorTable().getMonitorData().get(j).clear();
            }
        }

        // maybe instead of clearing subject array -> just stop timer ?
//        myModel.getMonitoredSubjects().clear();

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
