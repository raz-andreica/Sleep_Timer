package raz.sleeptimer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


public class NotificationService extends Service
{
    private static final String TAG_TIMER_SERVICE = "Alarm service";
    public static final String ACTION_START_TIMER_SERVICE = "ACTION_START_ALARM_SERVICE";
    public static final String ACTION_STOP_TIMER_SERVICE = "ACTION_STOP_ALARM_SERVICE";
    public static final String ACTION_PAUSE_TIMER = "ACTION_PAUSE_TIMER";
    public static final String ACTION_EXTEND_TIMER = "ACTION_EXTEND_TIMER";
    public static final String ACTION_RESUME_TIMER = "ACTION_RESUME_TIMER";

    private final IBinder binder = new timerBinder();

    private NotificationCompat.BigTextStyle bigTextStyle;
    private NotificationCompat.Builder builder;

    private static CountDownTimer timer;
    private int minutes;
    private boolean paused = false;

    @Override
    public IBinder onBind(Intent intent)
    {
         return binder;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG_TIMER_SERVICE, "Timer service created");
    }

    private void minimizeApp()
    {
        Intent minimize = new Intent(Intent.ACTION_MAIN);

        minimize.addCategory(Intent.CATEGORY_HOME);
        minimize.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(minimize);
        stopTimerService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (intent != null)
        {
            String action = intent.getAction();

            try
            {
                minutes = intent.getExtras().getInt("minutes", 0) + 1;
            }
            catch (NullPointerException ex)
            {

            }

            switch (action)
            {
                case ACTION_START_TIMER_SERVICE:
                    startTimerService();
                    startTimer();
//                    Toast.makeText(getApplicationContext(), "Timer service started.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP_TIMER_SERVICE:
                    stopTimerService();
//                    Toast.makeText(getApplicationContext(), "Timer service stopped.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_RESUME_TIMER:
                    startTimer();
                    Toast.makeText(getApplicationContext(), "Timer resumed", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_PAUSE_TIMER:
                    pauseTimer();
                    Toast.makeText(getApplicationContext(), "Timer paused", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_EXTEND_TIMER:
                extendTimer(10);
                Toast.makeText(getApplicationContext(), "Timer extended to " + minutes + " minutes", Toast.LENGTH_LONG).show();
                break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /* Used to build and start timer service. */
    private void startTimerService()
    {
        //Create notification channel
        NotificationChannel channel = new NotificationChannel("42069", "Timer", NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("Sleep timer updates");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        // Create notification default intent.
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        builder = new NotificationCompat.Builder(this);
        builder.setChannelId("42069");

        // Make notification show big text.
        bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("Timer running");
        bigTextStyle.bigText("Remaining time: " + minutes + " minutes.");
        // Set big text style.
        builder.setStyle(bigTextStyle);

        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ring);
        // Make the notification max priority.
        builder.setPriority(Notification.PRIORITY_MAX);
        // Make head-up notification.
        builder.setFullScreenIntent(pendingIntent, true);

        // Add Resume button intent in notification.
        Intent resumeIntent = new Intent(this, NotificationService.class);
        resumeIntent.setAction(ACTION_RESUME_TIMER);
        PendingIntent pendingResumeIntent = PendingIntent.getService(this, 0, resumeIntent, 0);
        NotificationCompat.Action playAction = new NotificationCompat.Action(android.R.drawable.ic_media_play, "Resume", pendingResumeIntent);
        builder.addAction(playAction);

        // Add Pause button intent in notification.
        Intent pauseIntent = new Intent(this, NotificationService.class);
        pauseIntent.setAction(ACTION_PAUSE_TIMER);
        PendingIntent pendingPauseIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
        NotificationCompat.Action prevAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pendingPauseIntent);
        builder.addAction(prevAction);

        // Add Extend button intent in notification.
        Intent extendIntent = new Intent(this, NotificationService.class);
        extendIntent.setAction(ACTION_EXTEND_TIMER);
        PendingIntent pendingExtendIntent = PendingIntent.getService(this, 0, extendIntent, 0);
        NotificationCompat.Action extendAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Extend", pendingExtendIntent);
        builder.addAction(extendAction);

        // Build the notification.
        Notification notification = builder.build();

        // Start timer service.
        startForeground(1, notification);
    }

    private void updateNotifications()
    {
        if (minutes == 0)
        {
            Toast.makeText(getApplicationContext(),  "Timer finished", Toast.LENGTH_LONG).show();

            bigTextStyle.setBigContentTitle("Timer finished");
            bigTextStyle.bigText("Remaining time: " + minutes + " minutes.");
            Notification notification = builder.build();
            startForeground(1, notification);
        }
        else if ((minutes % 10 == 0) || (minutes < 5 && minutes > 0))
        {
            Toast.makeText(getApplicationContext(), minutes + " minutes remaining", Toast.LENGTH_LONG).show();
        }

        bigTextStyle.setBigContentTitle("Timer running");
        bigTextStyle.bigText("Remaining time: " + minutes + " minutes.");
        Notification notification = builder.build();
        startForeground(1, notification);

    }

    private void stopTimerService()
    {
        Log.d(TAG_TIMER_SERVICE, "Timer service stopped");

        stopForeground(true);
        stopSelf();
    }

    public void extendTimer(int amount)
    {
        minutes += amount;

        updateNotifications();

        startTimer();
    }

    private void startTimer()
    {
        if (timer != null) timer.cancel();
        TileService.setActive();

        timer = new CountDownTimer(minutes * 60000, 60000)

        {
            @Override
            public void onTick(long millisUntilFinished)
            {

                minutes--;

                SleepTimer.setTimerMinutes(minutes);
                updateNotifications();
            }

            @Override
            public void onFinish()
            {
                minimizeApp();
            }
        }.start();
    }

    public void pauseTimer()
    {
        paused = true;
        timer.cancel();
        TileService.setInactive();

        bigTextStyle.setBigContentTitle("Timer paused");
        Notification notification = builder.build();
        startForeground(1, notification);
    }

    public class timerBinder extends Binder
    {
        NotificationService getService()
        {
            return NotificationService.this;

        }
    }
}
