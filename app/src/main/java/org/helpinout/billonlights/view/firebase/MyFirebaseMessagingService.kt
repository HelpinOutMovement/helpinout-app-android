package org.helpinout.billonlights.view.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.BillionLightsApplication
import org.helpinout.billonlights.model.dagger.PreferencesService
import org.helpinout.billonlights.service.LocationService
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.activity.HomeActivity
import javax.inject.Inject

class MyFirebaseMessagingService : FirebaseMessagingService() {
    val FCM_PARAM = "picture"
    private val CHANNEL_NAME = "FCM"
    private val CHANNEL_DESC = "Firebase Cloud Messaging"
    private var numMessages = 0

    @Inject
    lateinit var preferencesService: PreferencesService

    @Inject
    lateinit var locationService: LocationService

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        remoteMessage.data.isNotEmpty().let {
            handleNow(remoteMessage.data)
        }
    }

    override fun onNewToken(token: String) {
        (application as BillionLightsApplication).getAppComponent().inject(this)
        token.let {
            preferencesService.firebaseId = token
        }
    }

    private fun handleNow(data: MutableMap<String, String>) {
        sendNotification(data)
    }

    private fun sendNotification(data: Map<String, String>) {
        val bundle = Bundle()
        bundle.putString(FCM_PARAM, data[FCM_PARAM])
        val intent1 = Intent(DATA_REFRESH)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent1)
        val activityType = data[ACTIVITY_TYPE]?.toInt() ?: 0
        val action = data[ACTION]?.toInt() ?: 0
        var message = "New offer or request"
        if (activityType == 1) {
            //action 1
            if (action == 1) {//request accepted
                message = getString(R.string.someone_accept_offer)
            }
        } else if (activityType == 2) {
            if (action == 2) {//offer accepted
                message = getString(R.string.someone_receive_your_request)
            }
        }
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra(SELECTED_INDEX, activityType)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtras(bundle)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(this, "default").setContentTitle(data[TITLE]).setContentText(message).setAutoCancel(true).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).setContentIntent(pendingIntent).setContentInfo("Hello").setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.icon)).setColor(getColor(R.color.colorAccent)).setLights(Color.RED, 1000, 300).setDefaults(Notification.DEFAULT_VIBRATE).setNumber(++numMessages).setSmallIcon(R.drawable.icon)


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("default", CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = CHANNEL_DESC
            channel.setShowBadge(true)
            channel.canShowBadge()
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500)

            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}