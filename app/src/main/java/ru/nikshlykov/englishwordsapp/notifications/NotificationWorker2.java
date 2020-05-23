package ru.nikshlykov.englishwordsapp.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.ui.main.MainActivity;

public class NotificationWorker2 extends Worker{

    public NotificationWorker2(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i("Notifications", "NotificationWorker2 doWork()");
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Пора изучать слова")
                .setContentText("Нажмите, чтобы преисполниться в своём познании")
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String channelId = context.getString(R.string.notification_channel___remember_to_study___id);
            if (notificationManager.getNotificationChannel(channelId) == null)
                createNotificationChannel(context);
            mNotifyBuilder.setChannelId(channelId);
        }
        notificationManager.notify(1, mNotifyBuilder.build());
        Calendar currentDate = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();

        // Set Execution around 05:00:00 AM
        dueDate.set(Calendar.HOUR_OF_DAY, 20);
        dueDate.set(Calendar.MINUTE, 55);
        dueDate.set(Calendar.SECOND, 30);

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }
        long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();
        OneTimeWorkRequest dailyWorkRequest = new OneTimeWorkRequest.Builder(this.getClass())
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .addTag("myTag")
                .build();
        WorkManager.getInstance(getApplicationContext())
                .enqueueUniqueWork("NotificationWork1", ExistingWorkPolicy.REPLACE, dailyWorkRequest);
        return Result.success();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(Context context) {
        String id = context.getString(R.string.notification_channel___remember_to_study___id);
        CharSequence name = context.getString(R.string.notification_channel___remember_to_study___name);
        String description = context.getString(R.string.notification_channel___remember_to_study___description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}