package com.gift.compass.view;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

public interface CompassListener {

    void onSensorChanged(SensorEvent event);

    void onAccuracyChanged(Sensor sensor, int accuracy);
}
