package pk.taylor_darzi.reminder

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import pk.taylor_darzi.R
import pk.taylor_darzi.activities.DashBoard
import pk.taylor_darzi.utils.Config
import pk.taylor_darzi.utils.Utils


class ReminderService {
    // [END receive_message]
    private fun sendNotification(message: String, title: String, notification: String) {
        val intent = Intent(Utils.mContext, DashBoard::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("Type", notification)
        val pendingIntent = PendingIntent.getActivity(
            null,
            0 /* Request code */,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(message)
            .setBigContentTitle(title)
        val inboxStyle = NotificationCompat.InboxStyle()
            .setBigContentTitle(title)
        val notificationrcvd: Notification = NotificationCompat.Builder(Utils.mContext!!, "channelId")
            .setSmallIcon(R.mipmap.taylor_darzi) //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
            .setContentTitle(title)
            .setStyle(bigTextStyle)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSound(defaultSoundUri)
            .setGroup("Time_group")
            .setContentIntent(pendingIntent)
            .build()
        val summaryNotification: Notification = NotificationCompat.Builder(Utils.mContext!!, "channelId")
            .setContentTitle(Utils.curentActivity!!.getString(R.string.app_name))
            .setSmallIcon(R.mipmap.taylor_darzi)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI) //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
            .setAutoCancel(true) //build summary info into InboxStyle template
            .setStyle(inboxStyle) //specify which group this notification belongs to
            .setGroupSummary(true)
            .setGroup("Time_group")
            .setContentIntent(pendingIntent)
            .build()
        val notificationManager = NotificationManagerCompat.from(Utils.mContext!!)

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Utils!!.curentActivity!!.getString(R.string.default_notification_channel_id)",
                Utils.curentActivity!!.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        if (ActivityCompat.checkSelfPermission(
                Utils.mContext!!,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(Config.NOTIFICATION_ID, notificationrcvd)
        notificationManager.notify(14000, summaryNotification)
    }
}