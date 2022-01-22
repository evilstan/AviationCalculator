package com.evilstan.aviationcalculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    LightButton btnCompass, btnCalc, btnCharge, btnTime, btnDate, btnSensor;
    Toast toast;
    Gauge gauge;
    Vibrator vibrator;
    MechanicalIndicator[] mIndArray;
    ImageView lampRed;
    ImageView lampGreen;
    List<LightButton> buttons = new ArrayList<>();
    RelativeLayout motherLayout;
    boolean on = false;
    private SensorManager sensorManage;
    private float DegreeStart = 0f;
    private int azimut = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManage = (SensorManager) getSystemService(SENSOR_SERVICE);

        btnSensor = findViewById(R.id.btn_sensor);
        btnCompass = findViewById(R.id.btn_compass);
        btnCalc = findViewById(R.id.btn_calc);
        btnCharge = findViewById(R.id.btn_charge);
        btnTime = findViewById(R.id.btn_time);
        btnDate = findViewById(R.id.btn_date);

        buttons = Arrays.asList(btnCompass, btnCalc, btnCharge, btnTime, btnDate);

        gauge = findViewById(R.id.gauge1);
        motherLayout = findViewById(R.id.main_lay);

        List<MechanicalIndicator> indicatorsList = getAllChildren(motherLayout);

        Collections.sort(indicatorsList, new Comparator<MechanicalIndicator>() {

            public int compare(MechanicalIndicator o1, MechanicalIndicator o2) {
                return getResources().getResourceEntryName(o1.getId()).compareTo(getResources().getResourceEntryName(o2.getId()));
            }
        });

        mIndArray = new MechanicalIndicator[indicatorsList.size()];
        mIndArray = indicatorsList.toArray(mIndArray);

        lampRed = findViewById(R.id.lamp_red);
        lampGreen = findViewById(R.id.lamp_green);

        initListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        sensorManage.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // code for system's orientation sensor registered listeners
        sensorManage.registerListener((SensorEventListener) this, sensorManage.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
        azimut = (int) degree;
        DegreeStart = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    private List<MechanicalIndicator> getAllChildren(ViewGroup mother) {
        List<MechanicalIndicator> result = new ArrayList<>();
        try {
            for (int i = 0; i < mother.getChildCount(); i++) {
                View v = mother.getChildAt(i);
                if (v instanceof MechanicalIndicator) {
                    result.add((MechanicalIndicator) v);
                } else if (v instanceof ViewGroup) {
                    result.addAll(getAllChildren((ViewGroup) v));
                }
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void showMessage(String text) {

        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void blink() {
        if (on) {
            lampRed.setImageResource(R.drawable.lamp_red_off);
            // lampGreen.setImageResource(R.drawable.lamp_green_on);
            on = false;
        } else {
            lampRed.setImageResource(R.drawable.lamp_red_on);
            // lampGreen.setImageResource(R.drawable.lamp_green_off);
            on = true;
        }
    }

    private void setTime() {
        String hour = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10) hour = 0 + hour;
        String minute = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
        if (Calendar.getInstance().get(Calendar.MINUTE) < 10) minute = 0 + minute;
        String second = String.valueOf(Calendar.getInstance().get(Calendar.SECOND));
        if (Calendar.getInstance().get(Calendar.SECOND) < 10) second = 0 + second;
        String s = hour + minute + second;
        setNumbers(Integer.parseInt(s));
    }

    private void setNumbers(int number) {
        if (number > 999999) return;
        int[] mIndNumbersArray = new int[mIndArray.length];
        int x = 100000;
        int k = 0;


        for (int i = mIndNumbersArray.length - 1; i >= 0; i--) {
            mIndNumbersArray[i] = number / x;
            number = number % x;
            x = x / 10;
        }
        for (MechanicalIndicator ind : mIndArray) {
            ind.setNumber(mIndNumbersArray[k]);
            k++;
        }
/*        for (int i = 0; i < mIndArray.length; i++) {
            mIndArray[i].setNumber(mIndNumbersArray[i]);
        }*/

    }


    public void initListeners() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final Timer[] timer = {new Timer(), new Timer()};

        btnSensor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                Intent intentSensor = new Intent(MainActivity.this, Sensors.class);
                intentSensor.putExtra("Toast", "HelloWorld");
                startActivity(intentSensor);

                return false;
            }
        });


        btnCompass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                for (LightButton lb : buttons) {
                    lb.enable(false);
                }
                btnCompass.enable(true);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    timer[1].cancel();
                    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                    Intent batteryStatus = MainActivity.this.registerReceiver(null, ifilter);

                    int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                    float batteryPct = level * 100 / (float) scale;
                    int bat = (int) batteryPct;
                    gauge.indicate(bat);
                    vibrator.vibrate(20);
                    setNumbers(azimut);

                    final int[] x = {0};
                    timer[0] = new Timer();
                    timer[0].schedule(new TimerTask() {
                        @Override
                        public void run() {
                            blink();
                        }
                    }, 250, 250);

                    Handler h = new Handler();

                    timer[1] = new Timer();
                    timer[1].schedule(new TimerTask() {
                        @Override
                        public void run() {
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    //setNumbers(x[0]);
                                    setNumbers(azimut);
                                }
                            });
                            x[0]++;
                        }
                    }, 1000, 1000);


                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    gauge.indicate(0);
                    timer[0].cancel();
                    lampRed.setImageResource(R.drawable.lamp_red_off);
                }
                return false;
            }
        });
        final int[] x = {0, 0};

        btnCalc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                for (LightButton lb : buttons) {
                    lb.enable(false);
                }
                btnCalc.enable(true);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    timer[1].cancel();
                    vibrator.vibrate(50);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(MainActivity.this, CalcActivity.class);
                    intent.putExtra("Toast", "HelloWorld");
                    startActivity(intent);
                }
                return false;
            }
        });


        btnCharge.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    for (LightButton lb : buttons) {
                        lb.enable(false);
                    }
                    btnCharge.enable(true);

                    timer[1].cancel();
                    vibrator.vibrate(50);

                    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                    Intent batteryStatus = MainActivity.this.registerReceiver(null, ifilter);
                    int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                    float batteryPct = level * 100 / (float) scale;
                    int bat = (int) batteryPct;
                    setNumbers(bat);
                }
                return false;
            }
        });

        btnTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                for (LightButton lb : buttons) {
                    lb.enable(false);
                }
                btnTime.enable(true);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    timer[1].cancel();
                    setTime();
                    timer[1] = new Timer();
                    timer[1].schedule(new TimerTask() {
                        @Override
                        public void run() {
                            setTime();
                        }
                    }, 1000, 1000);
                }
                return false;
            }
        });

        btnDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    for (LightButton lb : buttons) {
                        lb.enable(false);
                    }
                    btnDate.enable(true);

                    timer[1].cancel();
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        String month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
                        if (Calendar.getInstance().get(Calendar.MONTH) < 10) month = 0 + month;
                        String day = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                        if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10) day = 0 + day;
                        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                        String s = day + month + year.substring(2);
                        setNumbers(Integer.parseInt(s));
                    }
                }
                return false;
            }
        });
/*        tacho.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                arrow.setPivotY(arrow.getY());
                arrow.setPivotX(arrow.getWidth() / 2);
                arrow.setRotation(48);
                tacho.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
    });*/
    }

}