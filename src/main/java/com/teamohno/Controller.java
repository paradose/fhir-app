package com.teamohno;

import org.apache.commons.lang3.StringUtils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

public class Controller {
    private Model myModel;
    private Timer myTimer;
    private View myView;
    private Server server;
    private PeriodicMeasurementCall myPeriodicCholesterol;
    private ArrayList<MeasurementType> allTypes;

    public Controller(Model newModel, View newView, Server inputServer){
        myModel = newModel;
        myView = newView;
        server = inputServer;
    }

    public void initView(){
        //initialise view - set default text from model
    }

    public void initController(){
        allTypes = myModel.getTypes();
        // need to decide where we make this...? -> if we had more types

        //initialise controller - add listeners to UI elements
        myView.getUpdatePracButton().addActionListener(e -> storePracIdentifier());
        myView.getUpdateFreqButton().addActionListener(e -> updateFrequency());

        // loop through all measurement types - pass through
//        Cholesterol cholesterol = new Cholesterol();
        for (int i = 0; i < allTypes.size(); i++) {
            if(allTypes.get(i).type == MeasurementType.Type.CHOLESTEROL){
                int index = i;
                myView.getMonitorCholButton().addActionListener(e -> monitorSelectedPatients(allTypes.get(index)));
                myView.getStopMonitorButton().addActionListener(e -> stopMonitorSelectedPatients(allTypes.get(index)));
                myPeriodicCholesterol = new PeriodicMeasurementCall(allTypes.get(index));
            }
        }

        // adds mouse listener to monitor table
        myView.getMonitorTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int rowIndex = myView.getMonitorTable().getSelectedRow();
                displaySelectedPatient(rowIndex);
            }
        });
        myView.getMonitorTable().setDefaultRenderer(String.class,myModel.getMonitorTable().getMeasurementRenderer());
        // initialise timer and periodic caller
        myTimer = new Timer();
        // passing in subject list (empty list at start) - gets called once
        scheduleMonitor();
    }

    public void storePracIdentifier() {
        String newPracIdentifier = myView.getPracIDfield().getText();
        boolean createPrac = false, clearExisting = false, foundIdentifier = false;
        // if no identifier entered
        if (!newPracIdentifier.isEmpty()) {
            if (myModel.getStoredIdentifiers().size() > 0) {
                String oldPracIdentifier = myModel.getLoggedInPractitioner().getPractitionerIdentifier();

                // check last logged in first
                if (oldPracIdentifier.equals(newPracIdentifier)) {
                    System.out.println("Current practitioner identifier is the same as the previous entered.");
                    createPrac = false;
                    clearExisting = false;
                } else {
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

            if (clearExisting) {
                // clear out existing subject lists - loop through all measurement types
                for (int i = 0; i < allTypes.size(); i++) {
                    MeasurementType currentType = allTypes.get(i);
                    for (int j = 0; j < currentType.getMonitorredSubjects().size(); j++) {
                        currentType.getMonitorredSubjects().get(j).getState().resetRecording(currentType);
                    }
                    currentType.getMonitorredSubjects().clear();
                    // update(clear) averages for types after clearing subject list
                    currentType.updateAverage();
                }
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
            if (foundIdentifier) {
                updatePatientList(newPracIdentifier, false);
            }
        }
    }

    public void updatePatientList(String newIdentifier, boolean retrievePatientsFromServer){
        if(retrievePatientsFromServer){
            myModel.getLoggedInPractitioner().retrievePractitionerPatients();
            for (int i = 0; i < myModel.getLoggedInPractitioner().getPractitionerPatients().size() ; i++) {
                myModel.getLoggedInPractitioner().getPractitionerPatients().get(i).addMeasurementObject(allTypes);
            }
            System.out.println("Update patient list: accessing server.");
        }
        else{
            System.out.println("Update patient list: not accessing server.");
        }
        myModel.updatePatientNamesList();
    }

    public void monitorSelectedPatients(MeasurementType newType) {
        // get selected indexes from JList
        int[] selectedIndices = myView.getPatientJList().getSelectedIndices();

        for (int i = 0; i < selectedIndices.length ; i++) {
            PatientRecord processPatient = myModel.getLoggedInPractitioner().getPractitionerPatients().get(selectedIndices[i]);

            // add patients to monitorTable's indexArray - if haven't monitored returns false
            if (myModel.getMonitorTable().addMonitorPatient(processPatient.getId(), processPatient.getFirstName() + " " + processPatient.getLastName(), newType)) {

                // add patient to subjectArray and attach server for requests
                PatientSubject newSubject = new PatientSubject(processPatient, server);
                newType.getMonitorredSubjects().add(newSubject);

                // create observers (for measurement type)
                MeasurementObserver newObserver = new MeasurementObserver(newSubject, myModel.getMonitorTable(), newType);
                newSubject.attach(newObserver);
            }
            else{
                System.out.println("Error attempting to monitor an already monitored patient+measurement combo.");
            }
        }
    }

    public void stopMonitorSelectedPatients(MeasurementType newType) {
        // get selected indexes from JTable - only concern is if these indexes don't line up?
        int[] selectedIndices = myView.getMonitorTable().getSelectedRows();

        for (int i = 0; i < selectedIndices.length ; i++) {
            PatientSubject processSubject = myModel.getMonitorTable().getMonitoredSubjects(newType).get(selectedIndices[i]);
            processSubject.getState().resetRecording(newType);
            // remove patient row's
            myModel.getMonitorTable().removePatientFromTable(selectedIndices[i]);
            myModel.getMonitorTable().getMeasurementRenderer().updateCholAverage(newType.getAverage());
            newType.getMonitorredSubjects().remove(processSubject);
            newType.updateAverage();
        }
        // if no more patients monitored - scheduler runs but no patients to process
    }

    // displays patient on patient display panel
    public void displaySelectedPatient(int patientIndex){
        PatientSubject chosenMonitoredPatient = allTypes.get(0).getMonitorredSubjects().get(patientIndex);
        PatientRecord chosenPatient = chosenMonitoredPatient.getState();
        myView.getPatientNameLabel().setText("Name: "+ chosenPatient.getFirstName() + " " + chosenPatient.getLastName());
        myView.getPatientBirthDateLabel().setText("BirthDate: " + chosenPatient.getBirthDate());
        myView.getPatientGenderLabel().setText("Gender: " + chosenPatient.getGender());
        myView.getPatientAddressLabel().setText( "Address: " + chosenPatient.getAddress());
    }

    // pass in measurement type parameter
    public void scheduleMonitor() {
            // implement an abstract parent class for measurementCalls - contains patientSubArray and measurementType, constructor involves both
//        TimerTask measurementCall = new PeriodicMeasurementCall(patientSubArray);
        String currentFreq = myView.getFreqValueLabel().getText();
        myPeriodicCholesterol.setFrequency(Integer.parseInt(currentFreq)* 1000);

        myTimer.scheduleAtFixedRate(myPeriodicCholesterol, 0, 1);
        myPeriodicCholesterol.setTurnedOn();
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
