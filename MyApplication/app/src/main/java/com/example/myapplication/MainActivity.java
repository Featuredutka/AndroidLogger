package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";

    private SensorManager sensorManager;

    private Sensor accelerometer, mGyro, mMagno;

    //Operator to set sensors go on/off. Default: OFF
    boolean updating = false;

    TextView xValue, yValue, zValue, xGyroValue, yGyroValue, zGyroValue, xMagnoValue, yMagnoValue, zMagnoValue;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To prevent app restarting on rotation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);

        xValue = findViewById(R.id.xValue);
        yValue = findViewById(R.id.yValue);
        zValue = findViewById(R.id.zValue);

        xGyroValue = findViewById(R.id.xGyroValue);
        yGyroValue = findViewById(R.id.yGyroValue);
        zGyroValue = findViewById(R.id.zGyroValue);

        xMagnoValue = findViewById(R.id.xMagnoValue);
        yMagnoValue = findViewById(R.id.yMagnoValue);
        zMagnoValue = findViewById(R.id.zMagnoValue);

        Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered accelerometer listener");
        }
        else {
            xValue.setText("Accelerometer not supported");
            yValue.setText("Accelerometer not supported");
            zValue.setText("Accelerometer not supported");
        }

        mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (mGyro != null) {
            sensorManager.registerListener(MainActivity.this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Gyro listener");
        }
        else {
            xGyroValue.setText("Gyro not supported");
            yGyroValue.setText("Gyro not supported");
            zGyroValue.setText("Gyro not supported");
        }

        mMagno = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (mMagno != null) {
            sensorManager.registerListener(MainActivity.this, mMagno, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Magno listener");
        }
        else {
            xMagnoValue.setText("Magno not supported");
            yMagnoValue.setText("Magno not supported");
            zMagnoValue.setText("Magno not supported");
        }

        //START button actions with v lambda func
        Button firstbutton = findViewById(R.id.firstbutton);
        firstbutton.setOnClickListener(v -> updating = true);

        //END button actions with v lambda func
        Button secondbutton = findViewById(R.id.secondbutton);
        secondbutton.setOnClickListener(v -> {
            updating = false;
            xValue.setText(" ");
            yValue.setText(" ");
            zValue.setText(" ");

            xGyroValue.setText(" ");
            yGyroValue.setText(" ");
            zGyroValue.setText(" ");

            xMagnoValue.setText(" ");
            yMagnoValue.setText(" ");
            zMagnoValue.setText(" ");
        });

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        if (updating) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                Log.d(TAG, "X: " + sensorEvent.values[0] + "Y: " + sensorEvent.values[1] + "Z: " + sensorEvent.values[2]);
                xValue.setText("xValue:" + sensorEvent.values[0]);
                yValue.setText("yValue:" + sensorEvent.values[1]);
                zValue.setText("zValue:" + sensorEvent.values[2]);
            } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                xGyroValue.setText("xGyroValue:" + sensorEvent.values[0]);
                yGyroValue.setText("yGyroValue:" + sensorEvent.values[1]);
                zGyroValue.setText("zGyroValue:" + sensorEvent.values[2]);
            } else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                xMagnoValue.setText("xMagnoValue:" + sensorEvent.values[0]);
                yMagnoValue.setText("yMagnoValue:" + sensorEvent.values[1]);
                zMagnoValue.setText("zMagnoValue:" + sensorEvent.values[2]);
            }
        }
    }

    
} 