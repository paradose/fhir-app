package com.teamohno;

import java.util.Date;

public class Cholesterol {
    private int cholesterolValue;
    private Date dateMeasured;

    public Cholesterol(int newCholValue, Date newCholDate){
        cholesterolValue = newCholValue;
        dateMeasured = newCholDate;
    }

    public int getCholesterolValue() {
        return cholesterolValue;
    }

    public void setCholesterolValue(int cholesterolValue) {
        this.cholesterolValue = cholesterolValue;
    }

    public Date getDateMeasured() {
        return dateMeasured;
    }

    public void setDateMeasured(Date dateMeasured) {
        this.dateMeasured = dateMeasured;
    }
}
