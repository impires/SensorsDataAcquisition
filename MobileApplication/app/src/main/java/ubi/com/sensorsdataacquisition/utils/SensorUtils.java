package ubi.com.sensorsdataacquisition.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorUtils {
    private static HashMap<String,Integer> sensorsMap=new HashMap<>();
    static {
        sensorsMap.put("Accelerometer",Sensor.TYPE_ACCELEROMETER);
        sensorsMap.put("Gyroscope",Sensor.TYPE_GYROSCOPE);
        sensorsMap.put("Magnetometer",Sensor.TYPE_MAGNETIC_FIELD);
    }

    public static List<ubi.com.sensorsdataacquisition.model.Sensor> getAvailableSensors(Context context){
        List<ubi.com.sensorsdataacquisition.model.Sensor> sensors=new ArrayList<>();
        SensorManager sensormanager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        for (Map.Entry<String, Integer> entry:sensorsMap.entrySet()){
            assert sensormanager != null;
            if (sensormanager.getDefaultSensor(entry.getValue()) != null){
                ubi.com.sensorsdataacquisition.model.Sensor sensor=new ubi.com.sensorsdataacquisition.model.Sensor();
                sensor.setName(entry.getKey());
                sensor.setSubtitle("");
                switch (entry.getKey()) {
                    case "Accelerometer":
                        sensor.setChecked(Variables.accEnabled);
                        break;
                    case "Gyroscope":
                        sensor.setChecked(Variables.gyroEnabled);
                        break;
                    case "Magnetometer":
                        sensor.setChecked(Variables.magEnabled);
                        break;
                }
                sensors.add(sensor);
            }
        }
        sensors.addAll(getPredefinedSensors());
        return sensors;
    }

    private static List<? extends ubi.com.sensorsdataacquisition.model.Sensor> getPredefinedSensors() {
        List<ubi.com.sensorsdataacquisition.model.Sensor> sensors=new ArrayList<>();
        sensors.add(new ubi.com.sensorsdataacquisition.model.Sensor("GPS","",Variables.gpsEnabled));
        return sensors;
    }
}
