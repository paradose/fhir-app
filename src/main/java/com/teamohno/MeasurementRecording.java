package com.teamohno;

import javax.print.attribute.standard.DateTimeAtCreation;
import java.math.BigDecimal;
import java.util.Date;

public class MeasurementRecording {
    private MeasurementType type;
    private BigDecimal measurementValue;
    private Date dateMeasured;
//    private Boolean active;


    public MeasurementRecording(){
        measurementValue = BigDecimal.ZERO;
        dateMeasured = new Date();
//        type = new MeasurementType()
    }

    public MeasurementRecording(BigDecimal newValue, Date newDate, MeasurementType newType){
        measurementValue = newValue;
        dateMeasured = newDate;
        type = newType;
//        active = false;
    }

    public BigDecimal getMeasurementValue() {
        return measurementValue;
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
//    public boolean getStatus(){
//        return active;
//    }
    public MeasurementType getType() {
        return type;
    }
//    public void setStatus(Boolean set){
//        active = set;
//    }
    public void setType(MeasurementType type) {
        this.type = type;
    }
}
