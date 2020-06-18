package com.teamohno;

import javax.print.attribute.standard.DateTimeAtCreation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class MeasurementRecording {
    // Instance variables
    private MeasurementType type;
    private BigDecimal measurementValue;
    private Date dateMeasured;
    private ArrayList<BigDecimal> childValues;

    // Constructors
    public MeasurementRecording(){
        measurementValue = BigDecimal.ZERO;
        dateMeasured = new Date(2323223231L);
        // initialise child values
        childValues = new ArrayList<>();
    }

    public MeasurementRecording(MeasurementType newType){
        measurementValue = BigDecimal.ZERO;
        dateMeasured = new Date(2323223231L);
        // initialise child values
        childValues = new ArrayList<>();
        for (int i = 0; i < newType.getComponentSize(); i++) {
            childValues.add(BigDecimal.ZERO);
        }
    }

    public MeasurementRecording(BigDecimal newValue, Date newDate, MeasurementType newType){
        measurementValue = newValue;
        dateMeasured = newDate;
        // initialise child values
        childValues = new ArrayList<>();
        this.setType(newType);
    }

    //Accessors and Mutators
    public BigDecimal getMeasurementValue() {
        return measurementValue;
    }

    public void setMeasurementValue(BigDecimal newMeasurementValue) {
        this.measurementValue = newMeasurementValue;
    }

    public BigDecimal getMeasurementValue(Constants.MeasurementType newChildType) {
        return childValues.get(type.getIndexChild(newChildType));
    }

    public void setMeasurementValue(BigDecimal newMeasurementValue, Constants.MeasurementType newChildType){
        childValues.set(type.getIndexChild(newChildType), newMeasurementValue);
    }

    public Date getDateMeasured() {
        return dateMeasured;
    }

    public void setDateMeasured(Date dateMeasured) {
        this.dateMeasured = dateMeasured;
    }

    public MeasurementType getType() {
        return type;
    }

    public void setType(MeasurementType newType) {
        this.type = newType;
        childValues.clear();
        for (int i = 0; i < newType.getComponentSize(); i++) {
            childValues.add(BigDecimal.ZERO);
        }
    }

    public void cloneRecording(MeasurementRecording newRecording){
        measurementValue = newRecording.getMeasurementValue();
        dateMeasured = newRecording.getDateMeasured();
        type = newRecording.getType();

        // Clone child values if there are any
        for (int i = 0; i < newRecording.getType().getComponentSize(); i++) {
            this.childValues.set(i, newRecording.childValues.get(i));
        }
    }

    public String toString(){
        String returnStr = "Measurement: " + type.getName() + ", Value: " + measurementValue + ", Date: " + dateMeasured;
        if(childValues.size() >0){
            returnStr += "\nChild components: ";
            for (int i = 0; i < childValues.size(); i++) {
                returnStr += type.getChildTypeNames().get(i) + " value: " + childValues.get(i) + " ";
            }
        }
        return returnStr;
    }
}
