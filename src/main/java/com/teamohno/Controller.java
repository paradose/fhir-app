package com.teamohno;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;

public class Controller {
    private Model myModel;
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
        myView.getStopMonitorButton().addActionListener(e -> stopMonitorPatients());
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

                // trigger scheduler
            }
        }
    }

    public void stopMonitorPatients() {
        // get selected indexes from JTable
        int[] selectedIndices = myView.getMonitorTable().getSelectedRows();

        // (Can assume already monitored)
        for (int i = 0; i < selectedIndices.length ; i++) {
            PatientRecord processPatient = myModel.getPractitioner().getPractitionerPatients().get(selectedIndices[i]);
            // remove patients to monitorTable's array
            myModel.getMonitorTable().removeMonitoredPatient(processPatient);
            // remove patients to monitorTableData (names)
            myModel.getMonitorTable().removePatientFromTable(selectedIndices[i]);
            processPatient.triggerMonitorState();

            // stop scheduler
            //....
        }
    }

    public void updateFrequency(){
        int newFreq;
        if(!myView.getFreqField().getText().isEmpty()) {
            // update frequency with value
            try {
                newFreq = Integer.parseInt(myView.getFreqField().getText());
            }
            catch (NumberFormatException e)
            {
                System.out.println("Error with input");
                newFreq = -1;
            }

            if (newFreq > 0) {
                // update the frequency

            }
        }
    }

    public void scheduleMonitor(){


    }
}
