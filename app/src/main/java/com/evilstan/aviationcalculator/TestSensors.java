package com.evilstan.aviationcalculator;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class TestSensors extends AppCompatActivity {


    private int[] indicator1Array, indicator2Array, indicator3Array;

    private MechanicalIndicator[] mechInd1;
    private MechanicalIndicator[] mechInd2;
    private MechanicalIndicator[] mechInd3;
    private LightButton btnAccel;
    private LightButton btnGyro;
    private LightButton btnMagn;
    private LightButton btnLight;
    private SensorManager sensorManager;
    private Sensor defaultGyroscope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_test_sensors);
        init();
    }


    private void setIndicator1(int number) {
        if (number > 999999 || number < 0) return;

        int x = 100000;
        int k = 0;

        for (int i = indicator1Array.length - 1; i >= 0; i--) {                                // parse input value and fill array
            indicator1Array[i] = number / x;
            number = number % x;
            x = x / 10;
        }


        for (MechanicalIndicator ind : mechInd1) {                                                //set numbersArray to indicators
            ind.setNumber(indicator1Array[k]);
            k++;
        }
    }

    private void setIndicator2(int number) {
        if (number > 999999 || number < 0) return;

        int x = 100000;
        int k = 0;

        for (int i = indicator2Array.length - 1; i >= 0; i--) {                                // parse input value and fill array
            indicator2Array[i] = number / x;
            number = number % x;
            x = x / 10;
        }


        for (MechanicalIndicator ind : mechInd2) {                                                //set numbersArray to indicators
            ind.setNumber(indicator2Array[k]);
            k++;
        }
    }

    private void setIndicator3(int number) {
        if (number > 999999 || number < 0) return;

        int x = 100000;
        int k = 0;

        for (int i = indicator3Array.length - 1; i >= 0; i--) {                                // parse input value and fill array
            indicator3Array[i] = number / x;
            number = number % x;
            x = x / 10;
        }


        for (MechanicalIndicator ind : mechInd3) {                                                //set numbersArray to indicators
            ind.setNumber(indicator3Array[k]);
            k++;
        }
    }


    public void init() {
        findComponents();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        defaultGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(new SensorEventListener() {
                                           @Override
                                           public void onSensorChanged(SensorEvent event) {
                                           }

                                           @Override
                                           public void onAccuracyChanged(Sensor sensor, int accuracy) {

                                           }
                                       },
                defaultGyroscope,
                SensorManager.SENSOR_DELAY_NORMAL);
        indicator1Array = new int[6];
        indicator2Array = new int[6];
        indicator3Array = new int[6];
    }


    private void findComponents() {
        btnAccel = findViewById(R.id.btn_accel);
        btnGyro = findViewById(R.id.btn_gyro);
        btnMagn = findViewById(R.id.btn_magnetic);
        btnLight = findViewById(R.id.btn_light);

        mechInd1 = new MechanicalIndicator[6];
        mechInd1[0] = findViewById(R.id.sensor_int_11);
        mechInd1[1] = findViewById(R.id.sensor_int_110);
        mechInd1[2] = findViewById(R.id.sensor_int_1100);
        mechInd1[3] = findViewById(R.id.sensor_int_11000);
        mechInd1[4] = findViewById(R.id.sensor_int_110000);
        mechInd1[5] = findViewById(R.id.sensor_int_1100000);

        mechInd2 = new MechanicalIndicator[6];
        mechInd2[0] = findViewById(R.id.sensor_int_21);
        mechInd2[1] = findViewById(R.id.sensor_int_210);
        mechInd2[2] = findViewById(R.id.sensor_int_2100);
        mechInd2[3] = findViewById(R.id.sensor_int_21000);
        mechInd2[4] = findViewById(R.id.sensor_int_210000);
        mechInd2[5] = findViewById(R.id.sensor_int_2100000);

        mechInd3 = new MechanicalIndicator[6];
        mechInd3[0] = findViewById(R.id.sensor_int_31);
        mechInd3[1] = findViewById(R.id.sensor_int_310);
        mechInd3[2] = findViewById(R.id.sensor_int_3100);
        mechInd3[3] = findViewById(R.id.sensor_int_31000);
        mechInd3[4] = findViewById(R.id.sensor_int_310000);
        mechInd3[5] = findViewById(R.id.sensor_int_3100000);
    }

}
