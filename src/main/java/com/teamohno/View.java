package com.teamohno;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;

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

    private DefaultListModel listModel;

    // Empty constructor
    public View(){

    }

    public View(TableModel dataModel, DefaultListModel listModel) {
        super("Title");

        //Instantiating JList object
        patientJList = new JList(listModel);
        patientJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientJList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        patientListScrollPane.setViewportView(patientJList);

        //Creating a JTable and adding it to the scroll pane
        JTable monitorTable = new JTable(dataModel);
        monitorTable.setPreferredScrollableViewportSize(new Dimension(400, 100));
        monitorScrollPane.setViewportView(monitorTable);

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
}
