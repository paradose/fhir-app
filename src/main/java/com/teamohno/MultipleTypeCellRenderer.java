package com.teamohno;

import java.awt.*;
import java.util.ArrayList;

public class MultipleTypeCellRenderer extends MeasurementCellRenderer {
    // Instance Variables
    private ArrayList<Double> minValList;
    private ArrayList<Integer> listColumns;
    private int currentIndex;

    // Constructor
    public MultipleTypeCellRenderer(Color colour, int newColumns) {
        super(colour);
        listColumns = new ArrayList<>();
        minValList = new ArrayList<>();
        addColumnIndex(newColumns);
        checkValue();
    }

    public void addColumnIndex(int numberColumns){
        // adding 1 implies first column aside from name
        if(numberColumns > 1) {
            for (int i = 1; i <= numberColumns; i++) {
                listColumns.add(i);
                // default initialise
                minValList.add(0.0);
            }
        }
        else{
            System.out.println("Error: invalid number of columns");
        }
    }

    @Override
    public void updateMinColouredValue(double newValue, int column){
        minValList.set(column, newValue);
    }

    @Override
    //Iterate through columns
    public void first() {
        currentIndex = 0;
        checkValue();
    }

    @Override
    public void next(){
        currentIndex++;
        if(hasNextColumn()) {
            checkValue();
        }
    }

    @Override
    public boolean hasNextColumn(){
        boolean retBool = false;
        if(currentIndex < listColumns.size()){
            retBool = true;
        }
        return retBool;
    }

    private void checkValue(){
        column = listColumns.get(currentIndex);
        minimumValue = minValList.get(currentIndex);
    }
}
