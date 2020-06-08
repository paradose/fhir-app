package com.teamohno;

import java.awt.*;
import java.util.ArrayList;

public class MultipleTypeCellRenderer extends MeasurementCellRenderer {
    private ArrayList<Double> minValList;
    private ArrayList<Integer> listColumns;
    private int currentIndex;

    public MultipleTypeCellRenderer(Color colour, int newColumns) {
        super(colour);
        listColumns = new ArrayList<>();
        minValList = new ArrayList<>();
        addColumnIndex(newColumns);
        checkValue();
    }

    public void addColumnIndex(int numberColumns){
        // adding 1 implies first column aside from name
        for (int i = 1; i <= numberColumns; i++) {
            listColumns.add(i);
            // default initialise
            minValList.add(0.0);
        }
    }

    public void updateMinColouredValue(double newValue, int column){
        minValList.set(column, newValue);
    }

    //Iterate through columns
    public void first() {
        currentIndex = 0;
        checkValue();
    }

    public void next(){
        currentIndex++;
        if(hasNextColumn()) {
            checkValue();
        }
    }

    public boolean hasNextColumn(){
        boolean retBool = false;
        if(currentIndex < listColumns.size()){
            retBool = true;
        }
        return retBool;
    }

    public void checkValue(){
        column = listColumns.get(currentIndex);
        minimumValue = minValList.get(currentIndex);
    }
}
