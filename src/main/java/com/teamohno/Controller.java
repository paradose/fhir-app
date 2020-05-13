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
        myTimer = new Timer();
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

                // create observers
                CholObserver newObserver = new CholObserver(newSubject, myModel.getMonitorTable());
                myModel.getCholObserverArray().add(newObserver);

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

            //remove processing subject + observer
            PatientSubject processSubject = myModel.getMonitoredSubjects().get(selectedIndices[i]);
            CholObserver processObserver = myModel.getCholObserverArray().get(selectedIndices[i]);
            myModel.getMonitoredSubjects().remove(processSubject);
            myModel.getCholObserverArray().remove(processObserver);
        }
        // if no more patients monitored - scheduler runs but no patients to process
    }

    // pass in measurement type parameter
    public void scheduleMonitor() {
            String inputFrequency = myView.getFreqValueLabel().getText();
            int intFreq = Integer.parseInt(inputFrequency);

            // implement an abstract parent class for measurementCalls - contains patientSubArray and measurementType, constructor involves both
//        TimerTask measurementCall = new PeriodicMeasurementCall(patientSubArray);
            myPeriodicTask = new PeriodicCholesterolCall(myModel.getMonitoredSubjects());

            PeriodicCholesterolCall.frequency = intFreq * 1000;
            myTimer.scheduleAtFixedRate(myPeriodicTask, 0, 1);
    }

    public void updateFrequency(){
        String inputFrequency = myView.getFreqField().getText();
        if(StringUtils.isNumeric(inputFrequency)) {
            int intFreq = Integer.parseInt(inputFrequency);
            myView.getFreqValueLabel().setText(inputFrequency);
            myView.getFreqField().setText("");

            if (intFreq * 1000 >= 1000) {
                PeriodicCholesterolCall.frequency = intFreq * 1000;
            }
            else{
                System.out.println("Error: frequency must be greater or equal to 1000ms (1 second).");
            }
        }
        else{
            System.out.println("Error: frequency value is not numeric");
        }
    }
}
