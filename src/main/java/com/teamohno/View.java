package com.teamohno;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

public class View extends JFrame{
    private JLabel pracIDLabel;
    private JLabel monitorLabel;
    private JLabel selectedLabel;
    private JPanel selectedMonitoredPanel;
    private JTextField pracIDfield;
    private JButton updatePracButton;
    private JLabel freqLabel;
    private JTextField freqField;
    private JButton updateFreqButton;
    private JScrollPane monitorScrollPane;
    private JScrollPane patientListScrollPane;
    private JPanel mainParentPanel;
    private JList patientJList;
    private JButton monitorCholButton;
    private JButton stopMonitorButton;
    private JLabel freqValueLabel;
    private JPanel displayPatient;
    private JLabel patientGender;
    private JLabel patientAddress;
    private JLabel patientBirthDate;
    private JLabel patientName;

    private DefaultListModel listModel;
    private JTable monitorTable;

    // Empty constructor
    public View(){

    }

    public View(Model model) {
        super("FHIR Patient Monitor");

        //Instantiating JList object
        patientJList = new JList(model.getPatientListModel());
        patientJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        patientJList.setLayoutOrientation(JList.VERTICAL);
        patientListScrollPane.setViewportView(patientJList);

        //Creating a JTable and adding it to the scroll pane
        monitorTable = new JTable(model.getMonitorTable());

        monitorTable.setPreferredScrollableViewportSize(new Dimension(400, 100));
        monitorScrollPane.setViewportView(monitorTable);

        // added to work around error from removing all monitored patients at once - look over when have time
        monitorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // instantiate the view
        setContentPane(mainParentPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public JTextField getPracIDfield() {
        return pracIDfield;
    }

    public JTextField getFreqField() {
        return freqField;
    }

    public JButton getUpdatePracButton() {
        return updatePracButton;
    }

    public JList getPatientJList() {
        return patientJList;
    }

    public void setPatientJList(JList patientJList) {
        this.patientJList = patientJList;
    }

    public JButton getUpdateFreqButton() {
        return updateFreqButton;
    }

    public JButton getMonitorCholButton() {
        return monitorCholButton;
    }

    public JButton getStopMonitorButton() {
        return stopMonitorButton;
    }
    public JPanel getDisplayPatient(){
        return displayPatient;
    }
    public JTable getMonitorTable(){
        return monitorTable;
    }

    public JLabel getFreqValueLabel(){
        return freqValueLabel;
    }

    public JLabel getPatientGenderLabel() {return patientGender;}
    public JLabel getPatientAddressLabel() {return patientAddress;}
    public JLabel getPatientBirthDateLabel() {return patientBirthDate;}
    public JLabel getPatientNameLabel() {return patientName;}
}
