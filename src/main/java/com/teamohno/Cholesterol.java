package com.teamohno;

import java.math.BigDecimal;
import java.util.Date;

public class Cholesterol {
    private BigDecimal cholesterolValue;
    private Date dateMeasured;

    public Cholesterol(BigDecimal newCholValue, Date newCholDate){
        cholesterolValue = newCholValue;
        dateMeasured = newCholDate;
    }

    public BigDecimal getCholesterolValue() {
        return cholesterolValue;
    }

    public void setCholesterolValue(BigDecimal cholesterolValue) {
        this.cholesterolValue = cholesterolValue;
    }

    public Date getDateMeasured() {
        return dateMeasured;
    }

    public void setDateMeasured(Date dateMeasured) {
        this.dateMeasured = dateMeasured;
    }
}
