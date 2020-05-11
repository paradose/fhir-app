package com.teamohno;

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
        // e.g. view.getField().setText(model.getValue...());
    }

    public void initController(){
        //initialise controller - add listeners to UI elements
        myView.getUpdatePracButton().addActionListener(e -> storePracIdentifier());
        myView.getUpdateFreqButton().addActionListener(e -> updateFrequency());
    }

    private void storePracIdentifier(){
        if(!myView.getPracIDfield().getText().isEmpty()) {
            myModel.createPractitoner(myView.getPracIDfield().getText());
            System.out.println("Entered Prac Identifier: " + myView.getPracIDfield().getText());

            updatePatientList();
        }
    }

    private void updatePatientList(){
        // retrieve patients for the given practitioner id
        myModel.getPractitioner().retrievePractitionerPatients();

        // update model with new practitioner id now stored in model
        myModel.updatePatientNamesList();

        // add listener
    }

    private void updateFrequency(){
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

    // addMonitoredPatient()
}
