package ru.nikshlykov.englishwordsapp.notifications;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.ui.activities.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotification(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);
            // TODO Сделать уточнение времени, с помощью получения extra из Intent.
            Intent nextNotificationIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent nextNotificationPendingIntent = PendingIntent.getBroadcast(context,
                    1, nextNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    nextNotificationPendingIntent);
        }
    }

    private void createNotification(Context context) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Пора изучать слова")
                .setContentText("Нажмите, чтобы войти в приложение")
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context);
            builder.setChannelId(context.getString(R.string.notification_channel___remember_to_study___id));
        }
        notificationManager.notify(1, builder.build());
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