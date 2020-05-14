package com.teamohno;

import org.apache.commons.lang3.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

public class Controller {
    private Model myModel;
    private Timer myTimer;
    private View myView;
    private Server server;
    private PeriodicCholesterolCall myPeriodicCholesterol;

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
        myView.getStopMonitorButton().addActionListener(e -> stopMonitorSelectedPatients(Measurement.Type.CHOLESTEROL));

        // initialise timer and periodic caller
        myTimer = new Timer();
        // passing in subject list (empty list at start)
        myPeriodicCholesterol = new PeriodicCholesterolCall(myModel.getMonitoredSubjects(Measurement.Type.CHOLESTEROL));
    }

    public void storePracIdentifier(){
        if(!myView.getPracIDfield().getText().isEmpty()) {
            // Initialise a practitioner - (to do: deal with non-existent practitioner identifier(?)
            myModel.createPractitoner(myView.getPracIDfield().getText(), server);
            updatePatientList();

            System.out.println("Entered Prac Identifier: " + myView.getPracIDfield().getText());
        }
    }

    public void updatePatientList(){
        // retrieve patients for the given practitioner id

        // server access - deal with empty patient list(?)
//        myModel.getPractitioner().retrievePractitionerPatients();

        // using for testing w/o server
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
                myModel.addMonitoredSubjects(newSubject, newType);

                // create observers (for measurement type...)**
                CholObserver newObserver = new CholObserver(newSubject, myModel.getMonitorTable());

                //attach
                newSubject.attach(newObserver);

                //trigger scheduler - will schedule entire monitored subject list
                scheduleMonitor(newType);
            }
            // else if monitored but not this measurement -> add new measurement observer...?
            else{
                System.out.println("Error attempting to monitor an already monitored patient+measurement combo.");
            }
        }
    }


    // pass in measurement type
    public void stopMonitorSelectedPatients(Measurement.Type newType) {
        // get selected indexes from JTable - only concern is if these indexes don't line up?
        int[] selectedIndices = myView.getMonitorTable().getSelectedRows();

        for (int i = 0; i < selectedIndices.length ; i++) {
            PatientRecord processPatient = myModel.getPractitioner().getPractitionerPatients().get(selectedIndices[i]);
            PatientSubject processSubject = myModel.getMonitoredSubjects(Measurement.Type.CHOLESTEROL).get(selectedIndices[i]);

            // remove patient row's
            myModel.getMonitorTable().removePatientFromTable(selectedIndices[i]);
            processPatient.triggerMonitorState();

            // check measurement observers

            //remove processing subject + observer(?)
            myModel.removeMonitoredSubject(processSubject, newType);

            // destroy observer for given measurement from subject (?) - or add to a list to reuse non-asssigned (specific measurement) observers?
            // detach...!
        }
        // if no more patients monitored - scheduler runs but no patients to process
    }

    // pass in measurement type parameter
    public void scheduleMonitor(Measurement.Type newType) {
            // implement an abstract parent class for measurementCalls - contains patientSubArray and measurementType, constructor involves both
//        TimerTask measurementCall = new PeriodicMeasurementCall(patientSubArray);

        updateFrequency();

        if (newType == Measurement.Type.CHOLESTEROL) {
            // if monitor not turned on yet
            if (!myPeriodicCholesterol.getTurnedOn()) {
                myTimer.scheduleAtFixedRate(myPeriodicCholesterol, 0, 1);
                myPeriodicCholesterol.setTurnedOn();
            } else {
                System.out.println("Cholesterol monitor already turned on");
            }
        }
        else{
            System.out.println("Error: Measurement type does not exist");
        }
    }

    public void updateFrequency(){
        String currentFreq = myView.getFreqValueLabel().getText();
        String inputFrequency = myView.getFreqField().getText();

        if(StringUtils.isNumeric(inputFrequency)) {
            int intFreq = Integer.parseInt(inputFrequency);
            myView.getFreqValueLabel().setText(inputFrequency);
            myView.getFreqField().setText("");

            if (intFreq * 1000 >= 1000) {
                // Update frequency for cholesterol only in this system - however can take parameter and deal with different monitor frequencies.
                // if want to update all - can consider too by looping through all periodic tasks
                myPeriodicCholesterol.setFrequency(intFreq * 1000);

                // other measurement types can set same frequency here
            }
            else{
                System.out.println("Error: frequency must be greater or equal to 1000ms (1 second).");
            }
        }
        else if (inputFrequency.isEmpty()) {
            myPeriodicCholesterol.setFrequency(Integer.parseInt(currentFreq)* 1000);
        }
        else{
            System.out.println("Error: frequency value is not numeric");
        }
    }
}
