package ubi.com.sensorsdataacquisition.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import ubi.com.sensorsdataacquisition.utils.Variables;

public class GyroscopeCapture extends Service implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senGyro;

    public GyroscopeCapture() {

    }

    // BroadcastReceiver for handling ACTION_SCREEN_OFF.
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Check action just to be on the safe side.
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                // Unregisters the listener and registers it again.
                senSensorManager.unregisterListener(GyroscopeCapture.this);
                senSensorManager.registerListener(GyroscopeCapture.this, senGyro, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert senSensorManager != null;
        senGyro = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        senSensorManager.registerListener(this, senGyro, SensorManager.SENSOR_DELAY_NORMAL);

        // Register our receiver for the ACTION_SCREEN_OFF action. This will make our receiver
        // code be called whenever the phone enters standby mode.
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        senSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        boolean error = false;
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
            String filePath = Variables.folderPath + "gyroscope.txt";
            File file = new File(filePath);

            if (!file.exists()) {
                try {
                    //noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                } catch (Exception e) {
                    error = true;
                }
            }

            if(!error) {
                long currTime = System.currentTimeMillis();
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                if(Variables.isCapturing) {
                    try {
                        //BufferedWriter for performance, true to set append to file flag
                        BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
                        buf.append(String.valueOf(currTime)).append("\t").append(String.valueOf(x)).append("\t").append(String.valueOf(y)).append("\t").append(String.valueOf(z)).append("\n");
                        buf.close();

                    } catch (Exception e) {
                        // nao faz nada
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // nao faz nada
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }
}
