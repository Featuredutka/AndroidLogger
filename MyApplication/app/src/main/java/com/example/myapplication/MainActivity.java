package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "MainActivity";
    
    private SensorManager sensorManager;

    private Sensor accelerometer, mGyro, mMagno;

    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;

    //Operator to set sensors go on/off. Default: OFF
    boolean updating = false;

    TextView xValue, yValue, zValue, xGyroValue, yGyroValue, zGyroValue, xMagnoValue, yMagnoValue, zMagnoValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To prevent app restarting on rotation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
        if (accelerometer != null) {
            sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
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
        //Plotting settings declaration
        mChart = (LineChart)  findViewById(R.id.chart1);

        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText("Real Time Accelerometer Plot");
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(true);
        mChart.setPinchZoom(false);
        mChart.setBackgroundColor(Color.WHITE);

        //Working with data on the plot
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis yl = mChart.getAxisLeft();
        yl.setTextColor(Color.WHITE);
        yl.setDrawGridLines(true);
        yl.setAxisMaximum(10f);
        yl.setAxisMinimum(0f);
        yl.setEnabled(true);

        YAxis yr = mChart.getAxisRight();
        yr.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);

        startPlot();

//        //START button actions
//        Button firstbutton = findViewById(R.id.firstbutton);
//        firstbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updating = true;
//            }
//        });
//
//        //END button actions
//        Button secondbutton = findViewById(R.id.secondbutton);
//        secondbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updating = false;
//                xValue.setText(" ");
//                yValue.setText(" ");
//                zValue.setText(" ");
//
//                xGyroValue.setText(" ");
//                yGyroValue.setText(" ");
//                zGyroValue.setText(" ");
//
//                xMagnoValue.setText(" ");
//                yMagnoValue.setText(" ");
//                zMagnoValue.setText(" ");
//            }
//        });

    }

    public void startPlot(){
        if(thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(10);

                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //Pausing for power saving
    @Override
    protected void onPause() {
        super.onPause();

        if(thread != null){
            thread.interrupt();
        }
        sensorManager.unregisterListener(this);
    }

    //Resuming listening
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void addEntry(SensorEvent event){
        LineData data = mChart.getData();

        if(data != null){
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set != null){
                set = createSet();
                data.addDataSet(set);
            }
            //Accelerometer data can be negative or positive so "+5" is needed to cover those values
            data.addEntry(new Entry(set.getEntryCount(), event.values[0] + 5), 0);
            data.notifyDataChanged();

            //Syncing plot with the moving data
            mChart.setMaxVisibleValueCount(150);
            mChart.moveViewToX(data.getEntryCount());
        }
    }

    //Plot settings
    private LineDataSet createSet(){
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3);
        set.setColor(Color.GREEN);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
            if(plotData){
                addEntry(sensorEvent);
                plotData = false;
            }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(MainActivity.this);
        thread.interrupt();
        super.onDestroy();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }
}