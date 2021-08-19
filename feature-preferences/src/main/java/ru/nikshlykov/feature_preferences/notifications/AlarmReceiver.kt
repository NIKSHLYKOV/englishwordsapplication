package ru.nikshlykov.feature_preferences.notifications

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ru.nikshlykov.feature_preferences.R
import java.util.*

internal class AlarmReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    createNotification(context)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      val calendar = Calendar.getInstance()
      calendar.add(Calendar.DATE, 1)
      // TODO Сделать уточнение времени, с помощью получения extra из Intent.
      val nextNotificationIntent = Intent(context, AlarmReceiver::class.java)
      val nextNotificationPendingIntent = PendingIntent.getBroadcast(
        context,
        1, nextNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
      )
      val am = context.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
      am.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
        nextNotificationPendingIntent
      )
    }
  }

  private fun createNotification(context: Context) {
    val notificationIntent = Intent(context, context.activityClassProvider.activityClass)
    notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    val pendingIntent = PendingIntent.getActivity(
      context, 0,
      notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
    )
    val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val builder = NotificationCompat.Builder(context, "1")
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle("Пора изучать слова")
      .setContentText("Нажмите, чтобы войти в приложение")
      .setSound(alarmSound)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)
    val notificationManager = NotificationManagerCompat.from(context)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      createNotificationChannel(context)
      builder.setChannelId(context.getString(R.string.notification_channel___remember_to_study___id))
    }
    notificationManager.notify(1, builder.build())
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun createNotificationChannel(context: Context) {
    val id = context.getString(R.string.notification_channel___remember_to_study___id)
    val name: CharSequence =
      context.getString(R.string.notification_channel___remember_to_study___name)
    val description =
      context.getString(R.string.notification_channel___remember_to_study___description)
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(id, name, importance)
    channel.description = description
    val notificationManager = context.getSystemService(
      NotificationManager::class.java
    )
    notificationManager.createNotificationChannel(channel)
  }
}

// TODO придумать потом что-нибудь более адекватное
val Context.activityClassProvider: ActivityClassProvider
  get() = when (this) {
    is ActivityClassProvider -> this
    is Application -> error("Error for AlarmReceiver")
    else                     -> applicationContext.activityClassProvider
  }

interface ActivityClassProvider {
  val activityClass: Class<out Activity>
}