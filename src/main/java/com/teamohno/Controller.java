package com.teamohno;

//import com.sun.org.apache.bcel.internal.Const;
import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.table.AbstractTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Timer;


public class Controller {
    //Instance variables
    private Model myModel;
    private Timer myTimer;
    private View myView;
    private Server server;
    private PeriodicMeasurementCall myPeriodicCholesterol, myPeriodicBP;
    private ArrayList<MeasurementType> allTypes, bpTypes, cholTypes;

    //Constructor
    public Controller(Model newModel, View newView){
        myModel = newModel;
        myView = newView;
        server = newModel.getServer();
    }

    // Initialise the controller - attach listeners to view components
    public void initController(){
        // Add ALL high SBP patients
//        myView.getAddPanelButton().addActionListener(e -> ());

        allTypes = myModel.getAllTypes();
        cholTypes = myModel.getTableTypes(Constants.MeasurementType.CHOLESTEROL);

        // Add listeners to UI elements
        myView.getUpdatePracButton().addActionListener(e -> storePracIdentifier());
        myView.getUpdateFreqButton().addActionListener(e -> updateFrequency());

        // loop through all measurement types - attach listeners for corresponding buttons, create periodic caller
        for (int i = 0; i < allTypes.size(); i++) {
            if(allTypes.get(i).type == Constants.MeasurementType.CHOLESTEROL){
                int index = i;
                myView.getMonitorCholButton().addActionListener(e -> monitorSelectedPatients(allTypes.get(index)));
                myView.getStopMonitorButton().addActionListener(e -> stopMonitorSelectedPatients(allTypes.get(index)));
                myView.getDisplayChartButton().addActionListener(e -> displayChart(allTypes.get(index)));
                myPeriodicCholesterol = new PeriodicMeasurementCall(allTypes.get(index));
                // adds mouse listener to monitor table
                myView.getCholMonitorTable().addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        int rowIndex = myView.getCholMonitorTable().getSelectedRow();
                        displaySelectedPatient(rowIndex, allTypes.get(index));
                    }
                });
                // Set renderer for table
                myView.getCholMonitorTable().setDefaultRenderer(String.class,myModel.getMonitorTable(Constants.MeasurementType.CHOLESTEROL).getMeasurementRenderer());
            }
            if (allTypes.get(i).type == Constants.MeasurementType.BLOOD_PRESSURE) {
                int index = i;
                bpTypes = myModel.getTableTypes(Constants.MeasurementType.BLOOD_PRESSURE);
                myView.getMonitorBPButton().addActionListener(e -> monitorSelectedPatients(bpTypes.get(0)));
                myView.getStopMonitorBPButton().addActionListener(e -> stopMonitorSelectedPatients(bpTypes.get(0)));

                myPeriodicBP = new PeriodicMeasurementCall(bpTypes.get(0));

                myView.getBpMonitorTable().addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        int rowIndex = myView.getBpMonitorTable().getSelectedRow();
                        displaySelectedPatient(rowIndex, allTypes.get(index));
                    }
                });

                myView.getUpdateDbpMinButton().addActionListener(e -> updateMinValue(1, allTypes.get(index)));
                myView.getUpdateSbpMinButton().addActionListener(e -> updateMinValue(2, allTypes.get(index)));
                // displays textual monitor.
                myView.getHistMonButton().addActionListener(e -> displayHighPatients(bpTypes.get(0)) );
                myView.getDisplaySystolicGraphButton().addActionListener(e -> displayXYgraph(bpTypes.get(0)));
                // Set renderer for table
                myView.getBpMonitorTable().setDefaultRenderer(String.class,myModel.getMonitorTable(Constants.MeasurementType.BLOOD_PRESSURE).getMeasurementRenderer());
                // add table for SBP
                AbstractTableModel histTableModel = myModel.getHistoricalMonitorTable(Constants.MeasurementType.BLOOD_PRESSURE);
                myView.createHistTablePanel(histTableModel);
            }
        }
        // initialise timer and schedule all periodic callers
        myTimer = new Timer();
        scheduleMonitor();
    }

    public void initModel(){
        // Set initial minimums
        myModel.getMonitorTable(Constants.MeasurementType.BLOOD_PRESSURE).setMinColouredValue(75, 0);
        myModel.getMonitorTable(Constants.MeasurementType.BLOOD_PRESSURE).setMinColouredValue(120, 1);
        myModel.getMonitorTable(Constants.MeasurementType.BLOOD_PRESSURE).fireTableDataChanged();
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

                //temporary
                // loop through all stored types
                myModel.getMonitorTable(Constants.MeasurementType.CHOLESTEROL).clearDataValues();
                myModel.getMonitorTable(Constants.MeasurementType.BLOOD_PRESSURE).clearDataValues();

                // clear patient list model
                myModel.getPatientListModel().clear(); // can make this a method inside myModel/listModel (inside listmodel can fire -> update)
            }

            if (createPrac) {
                PractitionerRecord newPrac = myModel.createPractitoner(newPracIdentifier);
                myModel.getStoredIdentifiers().add(newPracIdentifier);
                myModel.getStoredPractitioners().add(newPrac);
                myModel.setLoggedInPractitioner(newPrac);
                updatePatientList(true);
            }
            if (foundIdentifier) {
                updatePatientList(false);
            }
        }
    }

    public void updatePatientList(boolean retrievePatientsFromServer){
        if(retrievePatientsFromServer){
            myModel.getLoggedInPractitioner().retrievePractitionerPatients();
            for (int i = 0; i < myModel.getLoggedInPractitioner().getPractitionerPatients().size() ; i++) {
                myModel.getLoggedInPractitioner().getPractitionerPatients().get(i).initialiseMeasurements(cholTypes);
                myModel.getLoggedInPractitioner().getPractitionerPatients().get(i).initialiseMeasurements(bpTypes);
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
            if (myModel.getMonitorTable(newType.getType()).addMonitorPatient(processPatient.getId(), processPatient.getFirstName() + " " + processPatient.getLastName(), newType)) {
                if(!processPatient.getMeasurement(newType).getDateMeasured().equals(2323223231L)) {
                    myModel.getMonitorTable(newType.getType()).updateMeasurements(processPatient, processPatient.getMeasurement(newType));
                }

                // add patient to subjectArray and attach server for requests
                PatientSubject newSubject = new PatientSubject(processPatient, server);
                newType.getMonitorredSubjects().add(newSubject);

                // create observers (for measurement type)
                MeasurementObserver newObserver = new MeasurementObserver(newSubject, myModel.getMonitorTable(newType.getType()), newType);
                newSubject.attach(newObserver);
                // gets initial values -> if has a value -> notify observer to update table
                newSubject.updateMeasurementValue(newType);
            }
            else{
                System.out.println("Error attempting to monitor an already monitored patient+measurement combo.");
            }
            System.out.println("Before updating average - average value: " + newType.getAverage());
            // updating initial average
            newType.updateAverage();
            System.out.println("Current average: " + newType.getAverage());
        }
    }

    public void stopMonitorSelectedPatients(MeasurementType newType) {
        // get selected indexes from JTable
        int[] selectedIndices = null;
        if(newType.type == Constants.MeasurementType.CHOLESTEROL) {
            selectedIndices = myView.getCholMonitorTable().getSelectedRows();
        }
        else if (newType.type == Constants.MeasurementType.BLOOD_PRESSURE){
            selectedIndices = myView.getBpMonitorTable().getSelectedRows();
        }

        for (int i = 0; i < selectedIndices.length ; i++) {
            PatientSubject processSubject = newType.getMonitorredSubjects().get(selectedIndices[i]);
//            processSubject.getState().resetRecording(newType);
            // remove patient row's
            newType.getMonitorredSubjects().remove(processSubject);
            newType.updateAverage();

            // remove from historical table if there is one
            if (myModel.getHistoricalMonitorTable(newType.getType()) != null){
                String patientId = myModel.getMonitorTable(newType.getType()).getMonitoredPatientID().get(selectedIndices[i]);
                myModel.getHistoricalMonitorTable(newType.getType()).removePatient(patientId);
            }
            myModel.getMonitorTable(newType.getType()).removePatientFromTable(selectedIndices[i]);
            myModel.getMonitorTable(newType.getType()).getMeasurementRenderer().updateMinColouredValue(newType.getAverage());

        }
        // if no more patients monitored - scheduler runs but no patients to process
    }

    // displays patient on patient display panel - configure for only cholesterol?
    public void displaySelectedPatient(int patientIndex, MeasurementType newType){
        PatientRecord chosenPatient = null;
        String currentPatientID =  myModel.getMonitorTable(newType.type).getMonitoredPatientID().get(patientIndex);
        ArrayList<PatientRecord> listPatients = myModel.getLoggedInPractitioner().getPractitionerPatients();
        for (int i = 0; i < listPatients.size(); i++) {
            if(listPatients.get(i).getId().equals(currentPatientID)){
                chosenPatient = listPatients.get(i);
            }
        }
        if(chosenPatient.equals(null)){
            System.out.println("Error: patient doesn't exist.");
        }

        myView.getPatientNameLabel().setText("Name: "+ chosenPatient.getFirstName() + " " + chosenPatient.getLastName());
        myView.getPatientBirthDateLabel().setText("BirthDate: " + chosenPatient.getBirthDate());
        myView.getPatientGenderLabel().setText("Gender: " + chosenPatient.getGender());
        myView.getPatientAddressLabel().setText( "Address: " + chosenPatient.getAddress());
    }

    // called once
    public void scheduleMonitor() {
        String currentFreq = myView.getFreqValueLabel().getText();
        myPeriodicCholesterol.setFrequency(Integer.parseInt(currentFreq)* 1000);
        myTimer.scheduleAtFixedRate(myPeriodicCholesterol, 0, 1);
        myTimer.scheduleAtFixedRate(myPeriodicBP, 0, 1);
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
                myPeriodicCholesterol.setFrequency(intFreq * 1000);
                // Since all measurements are being updated at same frequency, frequency is static - and hence only needs to be set from one instance
            }
            else{
                System.out.println("Error: frequency must be greater or equal to 1000ms (1 second).");
            }
        }
        else if (inputFrequency.isEmpty()) {
            // If called and no new input is provided, just set periodic frequency to label's value
            myPeriodicCholesterol.setFrequency(Integer.parseInt(currentFreq)* 1000);
        }
        else{
            System.out.println("Error: frequency value is not numeric");
        }
    }

    public void updateMinValue(int newColumnIndex, MeasurementType newType){
        String columnName = myModel.getMonitorTable(newType.type).getColumnName(newColumnIndex);
        String input = "-1";
        if(columnName.equals("Systolic Blood Pressure")){
            input = myView.getSbpMinField().getText();
        }
        else if(columnName.equals("Diastolic Blood Pressure")){
            input = myView.getDbpMinField().getText();
        }
        if(StringUtils.isNumeric(input)){
            int newValue = Integer.parseInt(input);
            if(newValue > 0) {
                if(newColumnIndex == 1){
                    newType.setMinimumValue(newValue, Constants.MeasurementType.DIASTOLIC_BP);
                    myView.getDbpMinLabel().setText(input);
                    myView.getDbpMinField().setText("");
                }
                else if (newColumnIndex == 2){
                    newType.setMinimumValue(newValue, Constants.MeasurementType.SYSTOLIC_BP);
                    myView.getSbpMinLabel().setText(input);
                    myView.getSbpMinField().setText("");
                }
                // passing in data columns ( exclude first name column )
                myModel.getMonitorTable(newType.type).setMinColouredValue(newValue, newColumnIndex - 1);
                myModel.getMonitorTable(newType.type).fireTableDataChanged();
            }
        }
    }
    // gets data of currently monitored patients  from table of current measurements and sets it as default display
    private void displayChart(MeasurementType chartType) {
        DefaultCategoryDataset initialData = myModel.getMonitorTable(chartType.getType()).getMonitoredMeasurements(chartType);
        JFreeChart jchart = ChartFactory.createBarChart(chartType.getName()+" Levels", "Monitored Patients",
                chartType.getName()+" Levels", initialData, PlotOrientation.VERTICAL, true, true, false);
        myModel.getMonitorTable(chartType.getType()).addChart(chartType,jchart,initialData);
    }

    private void displayHighPatients(MeasurementType textualType){
        // gets monitored subjects from monitor table
        ArrayList<PatientSubject> monitoredSubjects = textualType.getMonitorredSubjects();
        double setMinValue =  myModel.getMonitorTable(textualType.getType()).getMeasurementRenderer().getMinValue();
        myModel.getHistoricalMonitorTable(textualType.getType()).clearDataValues();
        Constants.MeasurementType childType = myModel.getHistoricalMonitorTable(textualType.getType()).getChildType();
        // check their state / whether above chol, could be in measurement recording but just testing for now
        for (PatientSubject subjectCheck: monitoredSubjects){
            double patientsCurrentMeasurement = subjectCheck.getState().getMeasurement(textualType)
                    .getMeasurementValue(childType).doubleValue();
            if (patientsCurrentMeasurement>setMinValue){
                // adds the patient subject to historical
                HistoricalTableModel typeHistory =  myModel.getHistoricalMonitorTable(textualType.getType());
                typeHistory.addPatient(subjectCheck);
                // attach model
                subjectCheck.attach(new TextualObserver(subjectCheck, typeHistory, textualType ));
            }
        }

        // add them to HistoricalTableModel.

    }

    // gets last 5 values from table of Systolic Pressure and sets it as default
    public void displayXYgraph(MeasurementType chartType){
        // dataset is a collection os XYSeries which will represent each patient.
        XYSeriesCollection recordingChartData = myModel.getHistoricalMonitorTable(chartType.getType()).createDataSet();
        String measurementName = chartType.toString();
        JFreeChart measurementGraph = ChartFactory.createXYLineChart(
                measurementName + " Graph",
                "Time",
                "Levels",
                recordingChartData,
                PlotOrientation.VERTICAL,
                true,true,false);
        NumberAxis domain = (NumberAxis) measurementGraph.getXYPlot().getDomainAxis();
        domain.setRange(1.0,5.0);
        domain.setTickUnit(new NumberTickUnit(1.0));
        domain.setVerticalTickLabels(true);
        myModel.getHistoricalMonitorTable(chartType.getType()).addChart(chartType,measurementGraph,recordingChartData);
    }

}



