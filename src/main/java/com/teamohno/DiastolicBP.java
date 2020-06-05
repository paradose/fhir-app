package com.teamohno;

public class DiastolicBP extends MeasurementType {
    public DiastolicBP() {
        super("Diastolic Blood Pressure", "55284-4");
        this.childCode = "8462-4";
        this.type = Type.BLOODPRESSURE;
        numberStoredRecordings = 5;
    }
}
