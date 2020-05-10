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

    // Empty constructor
    public View(){

    }

    public View(TableModel dataModel) {
        super("Title");

        //Creating a JTable and adding it to the scrollpane
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
}
