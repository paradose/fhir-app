package com.teamohno;

public class BloodPressure extends MeasurementType {
    public BloodPressure() {
        super("Blood Pressure", "55284-4");
        type = MeasurementType.Type.BLOODPRESSURE;
        addChildCode("8462-4");
        childTypeNames.add("Diastolic Blood Pressure");
        addChildCode("8480-6");
        childTypeNames.add("Systolic Blood Pressure");
        numberStoredRecordings = 5;
    }
}