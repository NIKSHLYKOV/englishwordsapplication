package ru.nikshlykov.englishwordsapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.ui.MainActivity

class NotificationWorker(
  context: Context,
  params: WorkerParameters
) : Worker(context, params) {
  // TODO разобраться с этим кодом.
  override fun doWork(): Result {
    Log.i("Notification11", "doWork()")
    val context = applicationContext
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notificationIntent = Intent(context, MainActivity::class.java)
    notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    val pendingIntent = PendingIntent.getActivity(
      context, 0,
      notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
    )
    val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val mNotifyBuilder = NotificationCompat.Builder(context, "1")
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle("Пора изучать слова")
      .setContentText("Нажмите, чтобы преисполниться в своём познании")
      .setSound(alarmSound)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)
      .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channelId =
        context.getString(ru.nikshlykov.feature_preferences.R.string.notification_channel___remember_to_study___id)
      if (notificationManager.getNotificationChannel(channelId) == null) createNotificationChannel(
        context
      )
      mNotifyBuilder.setChannelId(channelId)
    }
    notificationManager.notify(1, mNotifyBuilder.build())
    return Result.success()
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun createNotificationChannel(context: Context) {
    val id =
      context.getString(ru.nikshlykov.feature_preferences.R.string.notification_channel___remember_to_study___id)
    val name: CharSequence =
      context.getString(ru.nikshlykov.feature_preferences.R.string.notification_channel___remember_to_study___name)
    val description =
      context.getString(ru.nikshlykov.feature_preferences.R.string.notification_channel___remember_to_study___description)
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(id, name, importance)
    channel.description = description
    val notificationManager = context.getSystemService(
      NotificationManager::class.java
    )
    notificationManager.createNotificationChannel(channel)
  }
}