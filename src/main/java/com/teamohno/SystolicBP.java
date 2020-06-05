package com.teamohno;

public class SystolicBP extends MeasurementType {
    public SystolicBP() {
        super("Systolic Blood Pressure", "55284-4");
        this.childCode = "8480-6";
        this.type = Type.BLOODPRESSURE;
        numberStoredRecordings = 5;
    }
}
