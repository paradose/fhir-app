package com.teamohno;

import java.math.BigDecimal;
import java.util.Date;

public abstract class Measurement {
    protected enum Type {
        CHOLESTEROL
    }

    protected BigDecimal measurementValue;
    protected Date dateMeasured;
    Type measurementType;

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

    public Type getMeasurementType(){
        return measurementType;
    }
}
