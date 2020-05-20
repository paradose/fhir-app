package com.teamohno;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;

public class MeasurementCellRenderer extends DefaultTableCellRenderer {
    private static double cholAverage = 0;
    private int column;
    public MeasurementCellRenderer(int measurementColumnNumber){
        column= measurementColumnNumber;
    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int col) {
        Component c = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, col);
        c.setForeground(Color.red);
        if (col == column) {
            if (value instanceof String) {

                try {
                    double cholLevel = Double.parseDouble(value.toString());
                    if (cholLevel > (cholAverage)) {
                        c.setForeground(Color.RED);
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

    public void updateCholAverage(double average){
        cholAverage=average;
        System.out.println("new average" + average);
    }
}
