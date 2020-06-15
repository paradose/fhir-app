package com.teamohno;

public class Cholesterol extends MeasurementType {
    public Cholesterol() {
        super("Cholesterol", "2093-3");
        type = Constants.MeasurementType.CHOLESTEROL;
        numberStoredRecordings = 1;
    }
}
