package ru.nikshlykov.englishwordsapp.ui.main;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

import ru.nikshlykov.englishwordsapp.notifications.AlarmReceiver;
import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.ui.modes.ModesActivity;
import ru.nikshlykov.englishwordsapp.ui.settings.SettingsActivity;

public class ProfileFragment extends Fragment {
    private final static String LOG_TAG = "ProfileFragment";
    private Context context;

    // View элементы.
    private ImageButton settings;
    private Button modes;
    private Button statistics;
    private Button initNotificationsButton;
    private Button sendNotificationButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_profile, null);
        findViews(view);
        modes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ModesActivity.class);
                startActivity(intent);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingsActivity.class);
                startActivity(intent);
            }
        });
        initNotificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRepeatingNotifications();
            }
        });
        sendNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManager notificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);

                Intent notificationIntent = new Intent(context, MainActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context, "1")
                        .setChannelId(getString(R.string.notification_channel___remember_to_study___id))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Пора изучать слова")
                        .setContentText("Нажмите, чтобы преисполниться в своём познании")
                        .setSound(alarmSound)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                notificationManager.notify(1, mNotifyBuilder.build());
            }
        });
        return view;
    }

    private void findViews(View view) {
        settings = view.findViewById(R.id.fragment_profile___button___settings);
        modes = view.findViewById(R.id.fragment_profile___button___modes);
        statistics = view.findViewById(R.id.fragment_profile___button___statistics);
        initNotificationsButton = view.findViewById(R.id.fragment_profile___button___init_notifications);
        sendNotificationButton = view.findViewById(R.id.fragment_profile___button___send_notification);
    }

    // TODO Перенести этот код в слушателя настроек, когда сделаю TimePicker.
    private void initRepeatingNotifications() {
        // Выставляем время, которое нам необходимо.
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 10);
        calendar.set(Calendar.SECOND, 0);

        // Получаем activity, к которому прикреплён фрагмент, чтобы получить контекст.
        Activity activity = getActivity();

        // Делаем PendingIntent. Возможно, будет необходимо сделать другой флаг, чтобы не было Update,
        // если пользователь хочет сделать несколько уведомлений в день.
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 1,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Получаем manager и сетим тревогу.
        // Прежде всего используем setExactAndAllowIdle, т.к. он точно будет работать (даже когда
        // приложение закрыто).
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Activity.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pendingIntent);
        } else alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), 60 * 1000, pendingIntent);
    }

    private void cancelRepeatingNotifications() {
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getService(context, 2, intent,
                        PendingIntent.FLAG_NO_CREATE);
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}

