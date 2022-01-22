package com.evilstan.aviationcalculator;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;


public class Sensors extends AppCompatActivity {


    private TextView[] resultOne;
    private TextView[] resultTwo;
    private TextView[] resultThree;
    private SensorManager sensorManager;
    private Sensor defaultGyroscope, defaultAccel, defaultMagnet;
    private Button btnPlay;
    private SoundPool soundPool;
    private boolean isLoaded = false;
    private int idDown;
    private float volume;
    private Gauge gauge;
    private Handler mHandler = new Handler();
    private boolean continueToRun = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sensors);
        init();
    }

    public void init() {
        initComponents();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        defaultAccel = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        defaultGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        defaultMagnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        btnPlay = findViewById(R.id.btn_new);
        gauge = findViewById(R.id.gauge);
        sensorManager.registerListener(new SensorEventListener() {
                                           @Override
                                           public void onSensorChanged(SensorEvent event) {
                                               int x = Math.round(event.values[0]);
                                               //gauge.indicate(x);
                                               resultOne[0].setText(String.valueOf(event.values[0]));
                                               resultOne[1].setText(String.valueOf(event.values[1]));
                                               resultOne[2].setText(String.valueOf(event.values[2]));
                                           }

                                           @Override
                                           public void onAccuracyChanged(Sensor sensor, int accuracy) {

                                           }
                                       },
                defaultAccel,
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(new SensorEventListener() {
                                           @Override
                                           public void onSensorChanged(SensorEvent event) {
                                               resultTwo[0].setText(String.valueOf(event.values[0]));
                                               resultTwo[1].setText(String.valueOf(event.values[1]));
                                               resultTwo[2].setText(String.valueOf(event.values[2]));
                                           }

                                           @Override
                                           public void onAccuracyChanged(Sensor sensor, int accuracy) {

                                           }
                                       },
                defaultGyroscope,
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(new SensorEventListener() {
                                           @Override
                                           public void onSensorChanged(SensorEvent event) {
                                               resultThree[0].setText(String.valueOf(event.values[0]));
                                               resultThree[1].setText(String.valueOf(event.values[1]));
                                               resultThree[2].setText(String.valueOf(event.values[2]));
                                           }

                                           @Override
                                           public void onAccuracyChanged(Sensor sensor, int accuracy) {

                                           }
                                       },
                defaultMagnet,
                SensorManager.SENSOR_DELAY_NORMAL);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
            }
        });
            mHandler.postDelayed(mRunnable, mSampleDurationTime);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }


    private int mSampleDurationTime = 50; // 1 sec


    private final Runnable mRunnable = new Runnable() {

        //...
        public void run() {
            //gauge.indicate(Math.round(10 * Float.parseFloat(resultOne[1].getText().toString())));
            gauge.indicate(10 * Float.parseFloat(resultOne[1].getText().toString()));

            // do your stuff here, like update
            // this block of code you going to reach every  second

            if(continueToRun == true){
                mHandler.postDelayed(mRunnable, mSampleDurationTime);
            }

        }
    };


    private void initComponents() {

        resultOne = new TextView[3];
        resultOne[0] = findViewById(R.id.text_top_one);
        resultOne[1] = findViewById(R.id.text_top_two);
        resultOne[2] = findViewById(R.id.text_top_three);

        resultTwo = new TextView[3];
        resultTwo[0] = findViewById(R.id.text_middle_one);
        resultTwo[1] = findViewById(R.id.text_middle_two);
        resultTwo[2] = findViewById(R.id.text_middle_three);

        resultThree = new TextView[3];
        resultThree[0] = findViewById(R.id.text_bottom_one);
        resultThree[1] = findViewById(R.id.text_bottom_two);
        resultThree[2] = findViewById(R.id.text_bottom_three);
    }

}
