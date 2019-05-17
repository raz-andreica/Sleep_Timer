package raz.sleeptimer;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SleepTimer extends AppCompatActivity
{
    private boolean initialized = false;
    private static int minutes;
    private int xTouch, yTouch, angle;
    private static int xCenter, yCenter, radius;

    private RelativeLayout layout;
    private TextView text;
    private ProgressBar progressCircle;
    private FloatingActionButton centerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleep_timer_activity);

        layout = findViewById(R.id.rLayout);
        text = findViewById(R.id.text);
        progressCircle = findViewById(R.id.progressCircle);
        centerButton = findViewById(R.id.centerButton);

        final Handler h = new Handler();

        setTitle("Sleep Timer");
        layout.setOnTouchListener(handleTouch);

        centerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                setTimer(30);
            }
        });

        h.postDelayed(new Runnable()
        {
            public void run()
            {
                updateUI();

                h.postDelayed(this, 50);
            }
        }, 100);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
        {
//            updateValues();
            initialize();
            layout.requestFocus();

            startTimerService();
            TileService.setActive();
        }
    }

    private View.OnTouchListener handleTouch = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent e)
        {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    xTouch = Math.round(e.getX());
                    yTouch = Math.round(e.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    startTimerService();
                    break;
            }

            return false;
        }
    };

    private void updateUI()
    {
        new Thread()
        {
            public void run()
            {
                if (layout.isFocused())
                {
                    try
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                rotateSlider();
                                text.setText(Integer.toString(minutes));
                            }
                        });
                        Thread.sleep(100);
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void updateValues()
    {
        radius = Math.round((float) progressCircle.getWidth()/2);

        xCenter = Math.round(progressCircle.getX() + progressCircle.getWidth()/2);
        yCenter = Math.round(progressCircle.getY() + progressCircle.getHeight()/2);
    }

    private void initialize()
    {
        if (!initialized)
        {
            minutes = 30;
            radius = Math.round((float) progressCircle.getWidth() / 2);
            xCenter = Math.round(progressCircle.getX() + progressCircle.getWidth() / 2);
            yCenter = Math.round(progressCircle.getY() + progressCircle.getHeight() / 2);
            progressCircle.setProgress(minutes);

            initialized = true;
        }
    }

    private void rotateSlider()
    {
        angle = CircleFunctions.pointsToAngle(xCenter, yCenter, xTouch, yTouch);
        minutes = CircleFunctions.angleToClock(angle);

        progressCircle.setProgress(minutes);
    }

    private void setTimer(int mins)
    {
        minutes = mins;
        angle = CircleFunctions.clockToAngle(minutes);

        progressCircle.setProgress(minutes);
    }

    public static void setMinutes(int mins)
    {
        minutes = mins;
    }

    public void startTimerService()
    {
        Intent i = new Intent(SleepTimer.this, NotificationService.class);
        i.setAction(NotificationService.ACTION_START_TIMER_SERVICE);
        i.putExtra("minutes", minutes);
        startService(i);
    }
}