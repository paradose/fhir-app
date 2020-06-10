package com.teamohno;

public class BloodPressure extends MeasurementType {

    public BloodPressure() {
        super("Blood Pressure", "55284-4");
        type = MeasurementType.Type.BLOODPRESSURE;
        addChildType("8462-4", "Diastolic Blood Pressure", Constants.MeasurementType.DIASTOLIC_BP);
        addChildType("8480-6", "Systolic Blood Pressure", Constants.MeasurementType.SYSTOLIC_BP);
        numberStoredRecordings = 5;

//        childComponents = new BloodPressureChildMeasurementList();
    }
}