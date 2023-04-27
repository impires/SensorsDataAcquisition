package ubi.com.sensorsdataacquisition.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import ubi.com.sensorsdataacquisition.R;
import ubi.com.sensorsdataacquisition.services.AccelerometerCapture;
import ubi.com.sensorsdataacquisition.services.GyroscopeCapture;
import ubi.com.sensorsdataacquisition.services.LocationCapture;
import ubi.com.sensorsdataacquisition.services.MagnetometerCapture;
import ubi.com.sensorsdataacquisition.utils.Variables;

public class MainActivity extends AppCompatActivity {

    @SuppressWarnings("deprecation")
    private static final String PATH_NAME = Environment.getExternalStoragePublicDirectory(Environment
            .DIRECTORY_DOCUMENTS).getAbsolutePath() + "/VeraoComCiencia2022/";
    private static final String[] INITIAL_PERMS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.WAKE_LOCK,
            android.Manifest.permission.ACTIVITY_RECOGNITION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
    };
    private static final int INITIAL_REQUEST = 1340;
    private final Handler handler = new Handler();
    private File dir;
    private PowerManager.WakeLock wakeLock;

    @BindView(R.id.info_mainActivity)
    TextView text;

    @BindView(R.id.startButton_mainActivity)
    Button startBtn;

    @BindView(R.id.stopButton_mainActivity)
    Button stopBtn;

    @BindView(R.id.dataButton_mainActivity)
    Button dataBtn;

    @BindView(R.id.preferencesButton_mainActivity)
    Button preferencesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        assert powerManager != null;
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "VeraoConCiencia2022:Wakelock");
        stopBtn.setEnabled(false);
        text.setText(getString(R.string.waiting));
        this.dir = new File(PATH_NAME);
        Variables.rootPath = PATH_NAME;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!canWriteExternalStorage() || !canWakeLock() || !canActivityRecognition() || !canAccessCoarseLocation() || !canAccessFineLocation() || !canManageExternalStorage()) {
                requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
            }
        }

        if(!Variables.id_study.equals("")) {
            startBtn.setEnabled(true);
        } else {
            startBtn.setEnabled(false);
        }

        dataBtn.setEnabled(true);
        preferencesBtn.setEnabled(true);
    }

    private boolean canWriteExternalStorage() {
        return (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean canActivityRecognition() {
        return (hasPermission(Manifest.permission.ACTIVITY_RECOGNITION));
    }

    private boolean canWakeLock() {
        return (hasPermission(Manifest.permission.WAKE_LOCK));
    }

    private boolean canAccessFineLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean canAccessCoarseLocation() {
        return (hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION));
    }

    private boolean canManageExternalStorage() {
        return (hasPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm));
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!Variables.id_study.equals("")) {
            startBtn.setEnabled(true);
        } else {
            startBtn.setEnabled(false);
        }
    }

    public void onClickEditData(View view) {
        Intent i = new Intent(getApplicationContext(), DataActivity.class);
        startActivity(i);
    }

    @SuppressLint("WakelockTimeout")
    public void onClickStartCapture(View view) {
        wakeLock.acquire();
        try {
            boolean error = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    if(!Files.exists(Paths.get(dir.getAbsolutePath()))) {
                        Files.createDirectory(Paths.get(dir.getAbsolutePath()));
                    }
                } catch (Exception e) {
                    error = true;
                }
            } else {

                if (!dir.mkdirs() && !dir.exists()) {
                    error = true;
                }
            }


            if (!error) {

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                Date now = new Date();
                String fileName = Variables.test + "_Capture_" + Variables.id_study + "_" + formatter.format(now) + "_" + now.getTime();
                Variables.folderPath = PATH_NAME + fileName + "/";
                File currFolder = new File(Variables.folderPath);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        if(!Files.exists(Paths.get(currFolder.getAbsolutePath()))) {
                            Files.createDirectory(Paths.get(currFolder.getAbsolutePath()));
                        }
                    } catch (Exception e) {
                        error = true;
                    }
                } else {

                    if (!currFolder.mkdirs() && !currFolder.exists()) {
                        error = true;
                    }
                }

                if (!error) {
                    runOnUiThread(() -> {
                        startBtn.setEnabled(false);
                        stopBtn.setEnabled(false);
                        dataBtn.setEnabled(false);
                        preferencesBtn.setEnabled(false);
                    });

                    String filePath = Variables.folderPath + "user_data.txt";
                    File file = new File(filePath);

                    if (!file.exists()) {
                        try {
                            if (!file.createNewFile()) {
                                error = true;
                            }
                        } catch (Exception e) {
                            error = true;
                        }
                    }

                    if (!error) {

                        try {
                            //BufferedWriter for performance, true to set append to file flag
                            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
                            buf.append("ID: ").append(Variables.id_study).append("\n");
                            buf.append("Age: ").append(Variables.age_person).append("\n");
                            buf.append("Gender: ").append(Variables.gender_person).append("\n");
                            buf.append("Height: ").append(Variables.height_person).append(" cm\n");
                            buf.append("Weight: ").append(Variables.weight_person).append(" kg\n");
                            buf.append("Physical Test: ").append(Variables.test).append("\n");
                            buf.close();

                        } catch (Exception e) {
                            // nao faz nada
                        }
                    }

                    Variables.isCapturing = true;

                    if (Variables.accEnabled) {
                        Intent j = new Intent(MainActivity.this, AccelerometerCapture.class);
                        MainActivity.this.startService(j);
                    }
                    if (Variables.gyroEnabled) {
                        Intent m = new Intent(MainActivity.this, GyroscopeCapture.class);
                        MainActivity.this.startService(m);
                    }
                    if (Variables.magEnabled) {
                        Intent n = new Intent(MainActivity.this, MagnetometerCapture.class);
                        MainActivity.this.startService(n);
                    }
                    if (Variables.gpsEnabled) {
                        Intent k = new Intent(MainActivity.this, LocationCapture.class);
                        MainActivity.this.startService(k);
                    }

                }

            }

        } catch (Exception e) {
            runOnUiThread(() -> {
                startBtn.setEnabled(true);
                stopBtn.setEnabled(false);
                dataBtn.setEnabled(true);
                preferencesBtn.setEnabled(true);
            });
            Variables.isCapturing = false;
        }

        Runnable runnable_start = () -> {
            MediaPlayer player = MediaPlayer.create(getApplicationContext(), R.raw.beep);
            player.start();

            runOnUiThread(() -> {
                text.setText(getString(R.string.recording));
                startBtn.setEnabled(false);
                stopBtn.setEnabled(true);
                dataBtn.setEnabled(false);
                preferencesBtn.setEnabled(false);
            });

            Variables.isCapturing = true;

        };

        handler.postDelayed(runnable_start, Variables.TIME_BEFORE);
    }

    public void onClickStopCapture(View view) {
        wakeLock.release();

        stopBtn.setEnabled(false);
        startBtn.setEnabled(true);
        dataBtn.setEnabled(true);
        preferencesBtn.setEnabled(true);
        text.setText(getString(R.string.waiting));
        Variables.isCapturing = false;

        Runnable runnable_stop = () -> {
            if (Variables.accEnabled) {
                Intent j = new Intent(MainActivity.this, AccelerometerCapture.class);
                MainActivity.this.stopService(j);
            }
            if (Variables.gyroEnabled) {
                Intent m = new Intent(MainActivity.this, GyroscopeCapture.class);
                MainActivity.this.stopService(m);
            }
            if (Variables.magEnabled) {
                Intent n = new Intent(MainActivity.this, MagnetometerCapture.class);
                MainActivity.this.stopService(n);
            }
            if (Variables.gpsEnabled) {
                Intent k = new Intent(MainActivity.this, LocationCapture.class);
                MainActivity.this.stopService(k);
            }

        };
        handler.postDelayed(runnable_stop, Variables.TIME_AFTER);

    }

    public void onClickOpenPreferences(View view) {
        Intent i = new Intent(getApplicationContext(), PreferencesActivity.class);
        startActivity(i);
    }
}
