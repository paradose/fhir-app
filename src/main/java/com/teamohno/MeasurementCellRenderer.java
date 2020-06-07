package com.teamohno;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * observes table columns for cells that are above a value specified in the constructor
 */
public class MeasurementCellRenderer extends DefaultTableCellRenderer {
    // Instance variables
    private double minimumValue = 0;
    private int column;
    private Color cellColour;
    // Constructor, takes column number as input and observers this column
    public MeasurementCellRenderer(int measurementColumnNumber, Color colour){
        column= measurementColumnNumber;
        cellColour = colour;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int col) {
        Component c = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, col);
        c.setForeground(Color.black);
        if (col == column) {
            if (value instanceof String) {
                try {
                    double observedValue = Double.parseDouble(value.toString());
                    if (observedValue > (minimumValue)) {
                        c.setForeground(cellColour);
                    } else {
                        c.setForeground(Color.black);
                    }
                }catch (Exception e){
                    // column isnt an BigDecimal
                    c.setForeground(Color.black);
                }
            }
        } else c.setForeground(Color.black);
        return c;
    }
    // updates the measurements value, called from the controller/observer when
    // patient is monitored or value is updated
    public void updateMinColouredValue(double value){
        minimumValue = value;
        System.out.println("new minimum: " + value);
    }
}
