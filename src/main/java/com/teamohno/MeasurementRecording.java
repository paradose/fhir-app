package com.teamohno;

import javax.print.attribute.standard.DateTimeAtCreation;
import java.math.BigDecimal;
import java.util.Date;

public class MeasurementRecording {
    // Instance variables
    private MeasurementType type;
    private BigDecimal measurementValue;
    private Date dateMeasured;

    public MeasurementRecording(){
        measurementValue = BigDecimal.ZERO;
        dateMeasured = new Date(232322323L);
    }

    // Constructor
    public MeasurementRecording(BigDecimal newValue, Date newDate, MeasurementType newType){
        measurementValue = newValue;
        dateMeasured = newDate;
        type = newType;
    }

    //Accessors and Mutators
    public BigDecimal getMeasurementValue() {
        return measurementValue;
    }

    public BigDecimal getMeasurementValue(String code) {
        return BigDecimal.ZERO;
    }

    public void setMeasurementValue(BigDecimal measurementValue) {
        this.measurementValue = measurementValue;
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
        return returnStr;
    }

    public void cloneRecording(MeasurementRecording newRecording){
        measurementValue = newRecording.getMeasurementValue();
        dateMeasured = newRecording.getDateMeasured();
        type = newRecording.getType();
    }
}
