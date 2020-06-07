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

    // consider making another object for these.. so they can have code/values stored together - and have methods such as "getValue(childCode) ?"
    private ArrayList<BigDecimal> childValues;

    public MeasurementRecording(){
        measurementValue = BigDecimal.ZERO;
        dateMeasured = new Date(232322323L);
        childValues = new ArrayList<>();
    }

    // Constructor
    public MeasurementRecording(BigDecimal newValue, Date newDate, MeasurementType newType){
        measurementValue = newValue;
        dateMeasured = newDate;
        type = newType;

        childValues = new ArrayList<>();
        // initialise component values
        if(newType.getComponentSize()>0){
            for (int i = 0; i < newType.getComponentSize(); i++) {
                childValues.add(BigDecimal.ZERO);
            }
        }
    }

    //Accessors and Mutators
    public BigDecimal getMeasurementValue() {
        return measurementValue;
    }
    public void setMeasurementValue(BigDecimal newMeasurementValue) {
        this.measurementValue = newMeasurementValue;
    }

    public BigDecimal getMeasurementValue(String childCode) {
        return childValues.get(type.getIndexChild(childCode));
    }
    public void setMeasurementValue(BigDecimal newMeasurementValue, String childCode){
        childValues.set(type.getIndexChild(childCode), newMeasurementValue);
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

    public void setType(MeasurementType type) {
        this.type = type;
    }

    public String toString(){
        String returnStr = "Measurement: " + type.getName() + ", Value: " + measurementValue + ", Date: " + dateMeasured;
        if(childValues.size() >0){
            returnStr += ", child components: ";
            for (int i = 0; i < childValues.size(); i++) {
                returnStr += type.getChildTypeNames().get(i) + " value: " + childValues.get(i) + " ";
            }
        }
        return returnStr;
    }

    public void cloneRecording(MeasurementRecording newRecording){
        measurementValue = newRecording.getMeasurementValue();
        dateMeasured = newRecording.getDateMeasured();
        type = newRecording.getType();

        // Clone child values(?)
        for (int i = 0; i < newRecording.getType().getComponentSize(); i++) {
            childValues.set(i, newRecording.childValues.get(i));
        }
    }
}
