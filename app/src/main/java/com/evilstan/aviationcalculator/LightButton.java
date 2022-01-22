package com.evilstan.aviationcalculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("AppCompatCustomView")

public class LightButton extends ImageButton {
    private MediaPlayer mp, mp2;
    private final Context context;
    private int imgOn, soundDown, soundUp;
    private Drawable imgOff;
    private boolean isEnabled = false;
    private boolean isBlinking = false;
    private boolean fixed = true;
    private int idDown, idUp;
    private float volume;
    private SoundPool soundPool;
    private boolean isLoaded = false;

    public LightButton(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public LightButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context, attrs);
    }

    public LightButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context, attrs);
    }

    public void enable(boolean isEnabled) {
        this.isEnabled = isEnabled;
        if (isEnabled)
            this.setImageResource(imgOn);
        else
            this.setImageDrawable(imgOff);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                clickSound(true);
                //this.setImageResource(imgOn);
                if (fixed) {
                    enable(true);
                }
                return true;

            case MotionEvent.ACTION_UP:
                clickSound(false);
                //this.setImageDrawable(imgOff);
                performClick();
                return true;
        }
        return false;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    Timer timer;

    public void blink(boolean blinkerEnabled) {
        if (!blinkerEnabled) {
            if (timer != null)
                timer.cancel();
            isBlinking = false;
        } else {
            if (!isBlinking) {
                isBlinking = true;
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (isEnabled) enable(false);
                        else enable(true);
                    }
                }, 500, 500);
            }
        }
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LightButton);
        try {
            imgOn = ta.getResourceId(R.styleable.LightButton_image_on, 0);
            fixed = ta.getBoolean(R.styleable.LightButton_fixed, true);
            imgOff = this.getDrawable();
            soundDown = ta.getResourceId(R.styleable.LightButton_sound_push, R.raw.click1);
            soundUp = ta.getResourceId(R.styleable.LightButton_sound_release, R.raw.click2);
        } finally {
            ta.recycle();
        }

        mp = MediaPlayer.create(context, soundDown);
        mp2 = MediaPlayer.create(context, soundUp);

        initSoundPool();
    }

    private void initSoundPool() {
        if (Build.VERSION.SDK_INT >= 21 ) {
            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            SoundPool.Builder builder= new SoundPool.Builder();
            builder.setAudioAttributes(audioAttrib).setMaxStreams(5);

            this.soundPool = builder.build();
        }
        // for Android SDK < 21
        else {
            // SoundPool(int maxStreams, int streamType, int srcQuality)
            this.soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                isLoaded = true;
            }
        });

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = curVolume / maxVolume;

        idDown = soundPool.load(context, soundDown, 1);
        idUp = soundPool.load(context, soundUp, 1);
    }


    private void init(Context context) {
    }


    private void clickSound(boolean isDown) {
        if (isDown) {
            soundPool.play(idDown, volume, volume, 1, 0, 1);
/*            try {
                if (mp.isPlaying()) {
                    mp.stop();
                    mp.reset();
                    mp.release();
                    mp = MediaPlayer.create(context, soundDown);
                }
                mp.start();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        } else {
            soundPool.play(idUp, volume, volume, 1, 0, 1);
            /*try {
                if (mp2.isPlaying()) {
                    mp2.stop();
                    mp2.reset();
                    mp2.release();
                    mp2 = MediaPlayer.create(context, soundUp);
                }
                mp2.start();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
    }
}
