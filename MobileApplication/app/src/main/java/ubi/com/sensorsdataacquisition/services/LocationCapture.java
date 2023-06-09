package ubi.com.sensorsdataacquisition.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import ubi.com.sensorsdataacquisition.utils.Variables;

public class LocationCapture extends Service {

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 500;
    private static final float LOCATION_DISTANCE = 0;
    private Handler Handler = null;
    private Runnable runnableCode = null;
    private Location mLastLocation = null;

    private class LocationListener implements android.location.LocationListener {

        LocationListener(String provider)
        {
            mLastLocation = new Location(provider);
        }
        @Override
        public void onLocationChanged(Location location)
        {
            mLastLocation = location;
        }
        @Override
        public void onProviderDisabled(String provider)
        {
            // disabled
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            // enabled
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            // status changed
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    public LocationCapture() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread thread = new HandlerThread("VeraoComCiencia2022 - Location", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Looper serviceLooper = thread.getLooper();
        this.Handler = new Handler(serviceLooper);

        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListeners[1]);
        } catch (SecurityException | IllegalArgumentException ex) {
            // nao faz nada
        }
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListeners[0]);
        } catch (SecurityException | IllegalArgumentException ex) {
            // nao faz nada
        }

        // Define the code block to be executed
        this.runnableCode = () -> {
            // Do something here on the main thread

            long currTime = System.currentTimeMillis();

            if (Variables.isCapturing) {

                if(mLastLocation != null) {

                    boolean error = false;

                    String filePath = Variables.folderPath + "location.txt";
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

                        double latitude = mLastLocation.getLatitude();
                        double longitude = mLastLocation.getLongitude();

                        try {
                            //BufferedWriter for performance, true to set append to file flag
                            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
                            buf.append(String.valueOf(currTime)).append("\t").append(String.valueOf(latitude)).append("\t").append(String.valueOf(longitude)).append("\n");
                            buf.close();

                        } catch (Exception e) {
                            // nao faz nada
                        }
                    }
                }
            }

            // Repeat this the same runnable code block again another 500 milliseconds
            Handler.postDelayed(runnableCode, 500);
        };

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if(Handler != null) {
            Handler.removeCallbacks(runnableCode);
        }

        if (mLocationManager != null) {
            for (LocationListener mLocationListener : mLocationListeners) {
                try {
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (SecurityException ex) {
                    // nao faz nada
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) { // May not have an Intent is the service was killed and restarted (See STICKY_SERVICE).
            Handler.post(runnableCode);
        }
        return Service.START_STICKY;
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
