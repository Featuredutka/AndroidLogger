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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";

    private SensorManager sensorManager;

    private Sensor accelerometer, mGyro, mMagno;

    //Operator to set sensors on/off. Default: OFF
    boolean updating = false;

    TextView xValue, yValue, zValue, xGyroValue, yGyroValue, zGyroValue, xMagnoValue, yMagnoValue, zMagnoValue,
            xlValue, ylValue, zlValue, xlGyroValue, ylGyroValue, zlGyroValue, xlMagnoValue, ylMagnoValue, zlMagnoValue;

    FileWriter acc_writer, gyr_writer, mgn_writer;
    //Variable to count time
    long timeZeroPoint = 0;

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

        xlValue = findViewById(R.id.xlValue);
        ylValue = findViewById(R.id.ylValue);
        zlValue = findViewById(R.id.zlValue);

        xlGyroValue = findViewById(R.id.xlGyroValue);
        ylGyroValue = findViewById(R.id.ylGyroValue);
        zlGyroValue = findViewById(R.id.zlGyroValue);

        xlMagnoValue = findViewById(R.id.xlMagnoValue);
        ylMagnoValue = findViewById(R.id.ylMagnoValue);
        zlMagnoValue = findViewById(R.id.zlMagnoValue);

        Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (accelerometer != null) {
            sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            Log.d(TAG, "onCreate: Registered accelerometer listener");
            xlValue.setText("X-Acc:");
            ylValue.setText("Y-Acc:");
            zlValue.setText("Z-Acc:");
        } else {
            xValue.setText("Accelerometer not supported");
            yValue.setText("Accelerometer not supported");
            zValue.setText("Accelerometer not supported");
        }

        mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (mGyro != null) {
            sensorManager.registerListener(MainActivity.this, mGyro, SensorManager.SENSOR_DELAY_FASTEST);
            Log.d(TAG, "onCreate: Registered Gyro listener");
            xlGyroValue.setText("X-Gyr:");
            ylGyroValue.setText("Y-Gyr:");
            zlGyroValue.setText("Z-Gyr:");
        } else {
            xGyroValue.setText("Gyro not supported");
            yGyroValue.setText("Gyro not supported");
            zGyroValue.setText("Gyro not supported");
        }

        mMagno = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (mMagno != null) {
            sensorManager.registerListener(MainActivity.this, mMagno, SensorManager.SENSOR_DELAY_FASTEST);
            Log.d(TAG, "onCreate: Registered Magno listener");
            xlMagnoValue.setText("X-Mgn:");
            ylMagnoValue.setText("Y-Mgn:");
            zlMagnoValue.setText("Z-Mgn:");
        } else {
            xMagnoValue.setText("Magno not supported");
            yMagnoValue.setText("Magno not supported");
            zMagnoValue.setText("Magno not supported");
        }

        //START button actions with v lambda func
        Button firstbutton = findViewById(R.id.firstbutton);
        firstbutton.setOnClickListener(v -> {
            updating = true;
            timeZeroPoint = System.currentTimeMillis();
            try {
                acc_writer = new FileWriter(new File(getStorageDir(), "acc_sensor" + labelFormatter() + ".csv"));
                gyr_writer = new FileWriter(new File(getStorageDir(), "gyr_sensor" + labelFormatter() + ".csv"));
                mgn_writer = new FileWriter(new File(getStorageDir(), "mgn_sensor" + labelFormatter() + ".csv"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        //END button actions with v lambda func
        Button secondbutton = findViewById(R.id.secondbutton);
        secondbutton.setOnClickListener(v -> {
            updating = false;
            //To clear the view when data is not needed
            xValue.setText(" ");
            yValue.setText(" ");
            zValue.setText(" ");

            xGyroValue.setText(" ");
            yGyroValue.setText(" ");
            zGyroValue.setText(" ");

            xMagnoValue.setText(" ");
            yMagnoValue.setText(" ");
            zMagnoValue.setText(" ");

            try {
                acc_writer.close();
                gyr_writer.close();
                mgn_writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
    //Creating beautiful output file names
    private String labelFormatter(){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String test = ""+ts;
        test = test.replaceAll(" ", "_");
        return test.substring(0, test.length()-7);
    }

    private String getStorageDir() {
        //Path for saving files (~/Android/data/com.example.myapplication)
        return this.getExternalFilesDir(null).getAbsolutePath();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        if (updating) {
            if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                Log.d(TAG, " X: " + sensorEvent.values[0] + "Y: " + sensorEvent.values[1] + "Z: " + sensorEvent.values[2]);
                try {
                    acc_writer.write(String.format("%f, ACC, %f, %f, %f\n", (System.currentTimeMillis() - timeZeroPoint)/1000.0, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                xValue.setText(String.format("%.2g",(sensorEvent.values[0])));
                yValue.setText(String.format("%.2g",(sensorEvent.values[1])));
                zValue.setText(String.format("%.2g",(sensorEvent.values[2])));
            } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                try {
                    gyr_writer.write(String.format("%f, GYR, %f, %f, %f\n", (System.currentTimeMillis() - timeZeroPoint)/1000.0, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                xGyroValue.setText(String.format("%.2g",(sensorEvent.values[0])));
                yGyroValue.setText(String.format("%.2g",(sensorEvent.values[1])));
                zGyroValue.setText(String.format("%.2g",(sensorEvent.values[2])));
            } else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                try {
                    mgn_writer.write(String.format("%f, MGN, %f, %f, %f\n", (System.currentTimeMillis() - timeZeroPoint)/1000.0, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                xMagnoValue.setText(String.format("%.2g",(sensorEvent.values[0])));
                yMagnoValue.setText(String.format("%.2g",(sensorEvent.values[1])));
                zMagnoValue.setText(String.format("%.2g",(sensorEvent.values[2])));
            }
        }
    }
    
} 