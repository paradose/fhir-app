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

    public void storePracIdentifier() {
/*  checks (any existing?)
	-> if no
        create prac -> set logged in
	-> if yes
        clear first
                - (monitorredSubjects).clear()
                - monitorred table model
                - patient list model
        set loggedInPrac
        update patient list */

        String newPracIdentifier = myView.getPracIDfield().getText();
        boolean createPrac = false, clearExisting = false, foundIdentifier = false;
        if (!newPracIdentifier.isEmpty()) {
            if (myModel.getStoredIdentifiers().size() > 0) {
                String oldPracIdentifier = myModel.getLoggedInPractitioner().getPractitionerIdentifier();

                // check last logged in first
                if (oldPracIdentifier.equals(newPracIdentifier)) {
                    System.out.println("Current practitioner identifier is the same as the previous entered.");
                    createPrac = false;
                    clearExisting = false;
                }
                else {
                    clearExisting = true;
                    // search within stored identifiers
                    for (int i = 0; i < myModel.getStoredIdentifiers().size(); i++) {
                        if (newPracIdentifier.equals(myModel.getStoredIdentifiers().get(i))) {
                            foundIdentifier = true;
                            myModel.setLoggedInPractitioner(myModel.getStoredPractitioners().get(i));
                        }
                    }
                    if (!foundIdentifier) {
                        createPrac = true;
                        System.out.println("Practitioner has not been entered into system.");
                    }
                }
            } else {
                createPrac = true;
                clearExisting = false;
            }

            if(clearExisting){
                // clear out existing subject lists - loop through all measurement types
                myModel.clearSubjectLists();
                // clear monitor table entries - loop through all measurement types
                myModel.getMonitorTable().clearDataValues();
                // clear patient list model
                myModel.getPatientListModel().clear(); // can make this a method inside myModel/listModel (inside listmodel can fire -> update)
            }
            if (createPrac) {
                PractitionerRecord newPrac = myModel.createPractitoner(newPracIdentifier);
                myModel.getStoredIdentifiers().add(newPracIdentifier);
                myModel.getStoredPractitioners().add(newPrac);
                myModel.setLoggedInPractitioner(newPrac);
                updatePatientList(newPracIdentifier, true);
            }
            if (foundIdentifier){
                updatePatientList(newPracIdentifier, false);
            }
        }
    }

    public void updatePatientList(String newIdentifier, boolean retrievePatientsFromServer){
        if(retrievePatientsFromServer){
            myModel.getLoggedInPractitioner().retrievePractitionerPatients();
            System.out.println("Update patient list: accessing server.");
        }
        else{
            System.out.println("Update patient list: not accessing server.");
        }
        myModel.updatePatientNamesList();
    }

    public void monitorSelectedPatients(Measurement.Type newType) {
        // get selected indexes from JList
        int[] selectedIndices = myView.getPatientJList().getSelectedIndices();

        for (int i = 0; i < selectedIndices.length ; i++) {
            PatientRecord processPatient = myModel.getLoggedInPractitioner().getPractitionerPatients().get(selectedIndices[i]);

            // add patients to monitorTable's indexArray - if haven't monitored returns false
            if (myModel.getMonitorTable().addMonitorPatient(processPatient.getId(), processPatient.getFirstName() + " " + processPatient.getLastName(), newType.toString())) {

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
            PatientRecord processPatient = myModel.getLoggedInPractitioner().getPractitionerPatients().get(selectedIndices[i]);
            PatientSubject processSubject = myModel.getMonitoredSubjects(Measurement.Type.CHOLESTEROL).get(selectedIndices[i]);

            // remove patient row's
            myModel.getMonitorTable().removePatientFromTable(selectedIndices[i]);

            // check measurement observers

            //remove processing subject + observer(?)
            myModel.removeMonitoredSubject(processSubject, newType);
        }
        // if no more patients monitored - scheduler runs but no patients to process
    }

    // pass in measurement type parameter
    public void scheduleMonitor(Measurement.Type newType) {
            // implement an abstract parent class for measurementCalls - contains patientSubArray and measurementType, constructor involves both
//        TimerTask measurementCall = new PeriodicMeasurementCall(patientSubArray);
        String currentFreq = myView.getFreqValueLabel().getText();
        myPeriodicCholesterol.setFrequency(Integer.parseInt(currentFreq)* 1000);

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
