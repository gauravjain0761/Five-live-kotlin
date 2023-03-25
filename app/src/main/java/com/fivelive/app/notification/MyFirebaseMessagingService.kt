package com.fivelive.app.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.fivelive.app.R
import com.fivelive.app.activity.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d(TAG, "onNewToken: $s")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "onMessageReceived: " + remoteMessage.data)
        createNotification(this, remoteMessage.data)
    }

    fun createNotification(context: Context, payload: Map<String?, String?>) {

        //recipe_id key contain both id's like recipe id and Chef id
        // chef id need redirect to chef profile when someone follow and review you
        //recipe id need when someone like your recipe comment on your recipe

        /* {notification_id=251, time=16:50:35, type=like, badge=14, batch=4, sound=default,
         title=Woohoo! You got a like on your recipe, recipe_id=104, message=Test has liked your recipe.}*/
        val body = payload["message"]
        val title = payload["title"]
        val type = payload["type"]
        val recipe_id = payload["recipe_id"]
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        var largeIcon = BitmapFactory.decodeResource(resources, R.drawable.app_icon)
        val res = this.resources
        val height = res.getDimension(android.R.dimen.notification_large_icon_height).toInt()
        val width = res.getDimension(android.R.dimen.notification_large_icon_width).toInt()
        largeIcon = Bitmap.createScaledBitmap(largeIcon!!, width, height, false)
        val channelId = "com.fivelive"
        val channelName: CharSequence = resources.getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_HIGH
        var notificationChannel: NotificationChannel? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern =
                longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.app_icon)
            .setLargeIcon(largeIcon)
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentText(body).setColor(Color.WHITE)
            .setChannelId(channelId)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        val notificationIntent: Intent

        /*if(type.equalsIgnoreCase("like") || type.equalsIgnoreCase("comment")){
            notificationIntent = new Intent(context, RecipeDetailsActivity.class);
            notificationIntent.putExtra("RECIPE_ID",recipe_id);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }else if(type.equalsIgnoreCase("follower")){
            notificationIntent = new Intent(context, ChefProfileActivity.class);
            notificationIntent.putExtra("chefId",recipe_id);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }else if(type.equalsIgnoreCase("review")){
            notificationIntent = new Intent(context, HomeActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }else if(type.equalsIgnoreCase("reminder")){
            notificationIntent = new Intent(context, MyCartActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        else{
            notificationIntent = new Intent(context, HomeActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }*/notificationIntent = Intent(context, HomeActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val intent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        mBuilder.setContentIntent(intent)
        val notification = mBuilder.build()
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
        notificationManager.notify(1, notification)
    }

    companion object {
        private const val TAG = "MyNotificationService"
    }
}