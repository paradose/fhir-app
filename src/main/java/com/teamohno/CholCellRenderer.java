package com.teamohno;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;

public class CholCellRenderer extends DefaultTableCellRenderer {
    private static BigDecimal cholAverage = BigDecimal.ZERO;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int col) {

        Component c = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, col);
        // checks for only column with cholesterol
        if (col == 1) {
            if (value instanceof String) {
                try {
                    BigDecimal cholLevel = new BigDecimal(value.toString());
                    if (cholLevel.compareTo(cholAverage) > 0) {
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

    public void updateCholAverage(BigDecimal average){
        cholAverage=average;
    }
}
