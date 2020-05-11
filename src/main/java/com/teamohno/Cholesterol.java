package com.teamohno;

import java.math.BigDecimal;
import java.util.Date;

public class Cholesterol extends Measurement{

    public Cholesterol(BigDecimal newCholValue, Date newCholDate){
        measurementValue = newCholValue;
        dateMeasured = newCholDate;
        measurementType = Type.CHOLESTEROL;
    }

    public BigDecimal getCholesterolValue() {
        return measurementValue;
    }

    public void setCholesterolValue(BigDecimal cholesterolValue) {
        this.measurementValue = cholesterolValue;
    }

    public Date getDateMeasured() {
        return dateMeasured;
    }

    public void setDateMeasured(Date dateMeasured) {
        this.dateMeasured = dateMeasured;
    }
}
