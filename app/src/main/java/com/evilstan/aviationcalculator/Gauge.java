package com.evilstan.aviationcalculator;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.view.animation.PathInterpolatorCompat;

public class Gauge extends RelativeLayout {
    Context context;
    private ImageView face, pointer;
    private int startAngle, maxAngle;
    private float pointerAxle;
    private Toast toast;
    private double speed = 0.8; // 1 is normal, 2 - 2x
    boolean hasDamper = false;
    boolean isfast = false;

    public Gauge(Context context) {
        super(context);
        this.context = context;
    }

    public Gauge(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context, attrs);
    }

    public Gauge(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context, attrs);
    }

    private void showMessage(String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }


    public void indicate(float percent) {
        float angle = (float) (startAngle + (maxAngle - startAngle) * percent / 100);
        //showMessage("Angle= " + angle);
        float deltaAngle = Math.abs(angle - pointer.getRotation());
        Interpolator interpolator = new LinearInterpolator();
        Interpolator mInterpolator = PathInterpolatorCompat.create(.35f,1.72f,.65f,0.74f);

        if (!hasDamper) {
            interpolator = new OvershootInterpolator(1.5f);
            interpolator = PathInterpolatorCompat.create(.7f,1.75f,.6f,.83f);
        }
        if (percent == 0) {
            interpolator = new BounceInterpolator();
        }
        pointer.animate().rotation(angle).setDuration((long) (deltaAngle * 4 * speed)).setInterpolator(interpolator);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.gauge, this);
        face = findViewById(R.id.face);
        pointer = findViewById(R.id.pointer);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Gauge);
        try {
            startAngle = ta.getInt(R.styleable.Gauge_start_angle, 0);
            maxAngle = ta.getInt(R.styleable.Gauge_max_angle, 360);
            pointerAxle = ta.getFloat(R.styleable.Gauge_pointer_axle, (float) 0.5);
            hasDamper = ta.getBoolean(R.styleable.Gauge_damped,false);
            isfast = ta.getBoolean(R.styleable.Gauge_fast, false);
        } finally {
            ta.recycle();
        }
        if (isfast) speed = 0.2;
        face.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                pointer.setX(face.getX() + (float) face.getWidth() / 2 - (float) pointer.getWidth() / 2);
                pointer.setY(face.getY() + (float) face.getHeight() / 2 - (pointer.getHeight()*pointerAxle));
                pointer.setPivotY(pointer.getHeight()*pointerAxle);
                pointer.setPivotX((float) pointer.getWidth() / 2);
                pointer.setRotation(startAngle);
                pointer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

}
