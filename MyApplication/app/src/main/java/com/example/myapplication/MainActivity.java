package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";
    
    private SensorManager sensorManager;

    private Sensor accelerometer, mGyro, mMagno;

    TextView xValue, yValue, zValue, xGyroValue, yGyroValue, zGyroValue, xMagnoValue, yMagnoValue, zMagnoValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xValue = (TextView) findViewById(R.id.xValue);
        yValue = (TextView) findViewById(R.id.yValue);
        zValue = (TextView) findViewById(R.id.zValue);

        xGyroValue = (TextView) findViewById(R.id.xGyroValue);
        yGyroValue = (TextView) findViewById(R.id.yGyroValue);
        zGyroValue = (TextView) findViewById(R.id.zGyroValue);

        xMagnoValue = (TextView) findViewById(R.id.xMagnoValue);
        yMagnoValue = (TextView) findViewById(R.id.yMagnoValue);
        zMagnoValue = (TextView) findViewById(R.id.zMagnoValue);

        Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometer != null) {
            sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered accelerometer listener");
        } else {
            xValue.setText("Accelerometer not supported");
            yValue.setText("Accelerometer not supported");
            zValue.setText("Accelerometer not supported");
        }

        mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (mGyro != null) {
            sensorManager.registerListener(MainActivity.this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Gyro listener");
        } else {
            xGyroValue.setText("Gyro not supported");
            yGyroValue.setText("Gyro not supported");
            zGyroValue.setText("Gyro not supported");
        }

        mMagno = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (mMagno != null) {
            sensorManager.registerListener(MainActivity.this, mMagno, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Magno listener");
        } else {
            xMagnoValue.setText("Magno not supported");
            yMagnoValue.setText("Magno not supported");
            zMagnoValue.setText("Magno not supported");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;

        if (sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            Log.d(TAG, "X: " + sensorEvent.values[0] + "Y: " + sensorEvent.values[1] + "Z: " + sensorEvent.values[2]);
            xValue.setText("xValue:" + sensorEvent.values[0]);
            yValue.setText("yValue:" + sensorEvent.values[1]);
            zValue.setText("zValue:" + sensorEvent.values[2]);
        } else if (sensor.getType()==Sensor.TYPE_GYROSCOPE) {
            xGyroValue.setText("xGyroValue:" + sensorEvent.values[0]);
            yGyroValue.setText("yGyroValue:" + sensorEvent.values[1]);
            zGyroValue.setText("zGyroValue:" + sensorEvent.values[2]);
        } else if (sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD) {
            xMagnoValue.setText("xMagnoValue:" + sensorEvent.values[0]);
            yMagnoValue.setText("yMagnoValue:" + sensorEvent.values[1]);
            zMagnoValue.setText("zMagnoValue:" + sensorEvent.values[2]);
        }
    }

    
} 