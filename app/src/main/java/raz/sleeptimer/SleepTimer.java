package raz.sleeptimer;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SleepTimer extends AppCompatActivity
{

    //Global Variables
    private int timerMinutes;

    //Class Variables
    private float xTouch, yTouch;
    private float xCenter, yCenter;

    //UI elements
    private RelativeLayout layout;
    private TextView text;
    private ProgressBar progressCircle;
    private FloatingActionButton slider, centerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleep_timer_activity);

        layout = findViewById(R.id.rLayout);
        text = findViewById(R.id.text);
        progressCircle = findViewById(R.id.progressCircle);
        slider = findViewById(R.id.slider);
        centerButton = findViewById(R.id.centerButton);

        setTitle("Sleep Timer");
        layout.setOnTouchListener(handleTouch);

        centerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
//                System.exit(0);
            }
        });

        //Create handler which updates the UI
        final Handler h = new Handler();

        h.postDelayed(new Runnable() {
            public void run() {
                updateUI();
                timerMinutes = (int) angleToClock(getAngle());

                h.postDelayed(this, 50);
            }
        }, 100);
    }

    //Needed to access some values after UI is created
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
        {
            //Values can only be properly acquired after instantiation (after onCreate() is ran)
            getCenter();
            //Elements won't update on first click unless the layout has focus
            layout.requestFocus();

            xTouch = xCenter;
            timerMinutes = 59;

            startTimerService();
            TileService.setActive();
        }
    }

    //Custom touch controls
    private View.OnTouchListener handleTouch = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent e)
        {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                //Need finger coordinates for slider rotation
                case MotionEvent.ACTION_MOVE:
                    xTouch = e.getX();
                    yTouch = e.getY();
                    break;
                //Automatically start timer on finger release
                case MotionEvent.ACTION_UP:
                    startTimerService();
                    break;
            }

            return false;
        }
    };

    public void startTimerService() {
        Intent i = new Intent(SleepTimer.this, NotificationService.class);
        i.setAction(NotificationService.ACTION_START_TIMER_SERVICE);
        i.putExtra("minutes", timerMinutes);
        startService(i);
    }

    //Rotates the slider around the circular center button
    private void rotateSlider()
    {
        float x, y;
        float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());

        x = (float) (xCenter + radius * (xTouch - xCenter) / Math.sqrt((xTouch - xCenter) * (xTouch - xCenter) + (yTouch - yCenter) * (yTouch - yCenter)));
        y = (float) (yCenter + radius * (yTouch - yCenter) / Math.sqrt((xTouch - xCenter) * (xTouch - xCenter) + (yTouch - yCenter) * (yTouch - yCenter)));

        slider.setX(x - slider.getWidth() / 2);
        slider.setY(y - slider.getHeight() / 2);

        progressCircle.setProgress(timerMinutes);
    }

    //Converts the slider's location relative to the center to a 0-360 circle angle
    private double getAngle()
    {
        return Math.toDegrees(Math.atan2(
                ((slider.getY() - yCenter) / Math.sqrt(Math.pow(slider.getX() - xCenter, 2) + (Math.pow(slider.getY() - yCenter, 2)))),
                ((slider.getX() - xCenter) / Math.sqrt(Math.pow(slider.getX() - xCenter, 2) + (Math.pow(slider.getY() - yCenter, 2))))));
    }

    private double radToAngle(double ang)
    {
        ang = getAngle();

        if (ang < 0) return (ang + 360);

        return (ang);
    }

    private double angleToX()
    {
        float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());

        return xCenter + radius * Math.cos(Math.atan2(
                ((slider.getY() - yCenter) / Math.sqrt(Math.pow(slider.getX() - xCenter, 2) + (Math.pow(slider.getY() - yCenter, 2)))),
                ((slider.getX() - xCenter) / Math.sqrt(Math.pow(slider.getX() - xCenter, 2) + (Math.pow(slider.getY() - yCenter, 2))))
        ));
    }

    private double angleToY()
    {
        float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());

        return yCenter + radius * Math.sin(Math.atan2(
                ((slider.getY() - yCenter) / Math.sqrt(Math.pow(slider.getX() - xCenter, 2) + (Math.pow(slider.getY() - yCenter, 2)))),
                ((slider.getX() - xCenter) / Math.sqrt(Math.pow(slider.getX() - xCenter, 2) + (Math.pow(slider.getY() - yCenter, 2))))
        ));
    }

    private void setSlider(int x, int y)
    {
        x = (int) angleToX();
        y = (int) angleToY();

        slider.setX(x);
        slider.setY(y);
    }

    //Converts an angle to a 1-60 minutes clock
    private double angleToClock(double angle)
    {
        if (radToAngle(getAngle()) < 270) return (angle / 6 + 15) + 1;
        return (angle / 6 - 45) + 1;
    }

    private double angleToClock2(double angle)
    {

        return 0;
    }

    private double clockToAngle(double clock)
    {
//        if (clock < 45) return ((clock - 1) * 6);
        return ((clock - 1) * 6);
    }


    //Gets relative layout center
    private void getCenter()
    {
        xCenter = progressCircle.getX() + progressCircle.getWidth()/2;
        yCenter = progressCircle.getY() + progressCircle.getHeight()/2;
    }


    //Updates UI elements
    private void updateUI()
    {
        final float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());

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
                                text.setText((CircleFunctions.getPointOnCircle((int) xCenter, (int) yCenter, (float) getAngle(), radius).toString()));
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

    public static void setTimerMinutes(int minutes)
    {
//        timerMinutes = minutes;
    }
}
