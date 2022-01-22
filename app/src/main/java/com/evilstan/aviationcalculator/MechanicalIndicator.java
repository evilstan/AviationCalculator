package com.evilstan.aviationcalculator;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

@SuppressLint("ViewConstructor")
public class MechanicalIndicator extends ConstraintLayout {
    Context context;
    private ImageView digs1;
    private float[] digitsArray;
    private Toast toast;
    final int SPEED = 100;

    public MechanicalIndicator(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public MechanicalIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public MechanicalIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();

    }

    public void setNumber(int number) {

        int curr = this.getPosition();
        int duration = Math.abs(this.getPosition() - number) * SPEED;

        if (number >= 0 && number < 11) {


            if (curr < 5 &&  number-curr > 5) {

                int duration21 = curr * SPEED;
                digs1.animate().translationY(digitsArray[0]).setDuration(duration21).setInterpolator(new LinearInterpolator()).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        digs1.animate().translationY(digitsArray[10]).setDuration(1).setInterpolator(new LinearInterpolator()).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                int duration = Math.abs(number) * SPEED;
                                digs1.animate().translationY(digitsArray[number]).setDuration(1000-duration).setInterpolator(new LinearInterpolator());
                                digs1.animate().setListener(null).start();
                            }


                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            } else if (curr > 5 && curr - number > 5) {
                int duration2 = Math.abs(10 - curr) * SPEED;
                digs1.animate().translationY(digitsArray[10]).setDuration(duration2).setInterpolator(new LinearInterpolator()).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        digs1.animate().translationY(digitsArray[0]).setDuration(1).setInterpolator(new LinearInterpolator()).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                int duration = Math.abs(number) * SPEED;
                                digs1.animate().translationY(digitsArray[number]).setDuration(duration).setInterpolator(new LinearInterpolator());
                                digs1.animate().setListener(null).start();
                            }


                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

            } else
                digs1.animate().translationY(digitsArray[number]).setDuration(duration).setInterpolator(new LinearInterpolator());

            duration = Math.abs(this.getPosition() - number) * SPEED;
        } else
            digs1.setY(digitsArray[10]);

    }


    public int getPosition() {
        float min = Float.MAX_VALUE;
        int currentNumber = 11;

        for (int i = 0; i < digitsArray.length; i++) {
            final float diff = Math.abs(digitsArray[i] - digs1.getTranslationY());

            if (diff < min) {
                min = diff;
                currentNumber = i;
            }
        }
        return currentNumber;
    }


    public void showMessage(String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.mech_indicator, this);
        ImageView cell1 = findViewById(R.id.cell1);
        digs1 = findViewById(R.id.digits);

        digitsArray = new float[11]; //заполнение таблицы координат цифр

        digs1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                float h = digs1.getHeight();
                for (int i = 0; i < 11; i++) {
                    //digitsArray[0] = digs1.getTranslationY() + (float) (digs1.getHeight() / 10) * 9;
                    digitsArray[i] = digs1.getTranslationY() + (h / 11) * (i);
                }
                digs1.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        });

    }
}
