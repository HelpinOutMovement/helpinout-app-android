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
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.BillionLightsApplication
import org.helpinout.billonlights.model.dagger.PreferencesService
import org.helpinout.billonlights.service.OfferRequestListService
import org.helpinout.billonlights.utils.*
import org.helpinout.billonlights.view.activity.RequestDetailActivity
import org.jetbrains.anko.runOnUiThread
import javax.inject.Inject

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val CHANNEL_NAME = "FCM"
    private val CHANNEL_DESC = "Firebase Cloud Messaging"
    private var numMessages = 0

    @Inject
    lateinit var preferencesService: PreferencesService

    @Inject
    lateinit var offerRequestListService: OfferRequestListService


    override fun onCreate() {
        (application as BillionLightsApplication).getAppComponent().inject(this)
        super.onCreate()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data.isNotEmpty().let {
            handleNow(remoteMessage.data)
        }
    }

    override fun onNewToken(token: String) {

        token.let {
            preferencesService.firebaseId = token
        }
    }

    private fun handleNow(data: MutableMap<String, String>) {
        sendNotification(data)
    }

    private fun sendNotification(data: Map<String, String>) {
        fetchData(data)
    }

    private fun fetchData(data: Map<String, String>) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val activity_uuid = data[ACTIVITY_UUID].toString()
               val response = offerRequestListService.getUserRequestsOfferList(this@MyFirebaseMessagingService, 0,activity_uuid)
               runOnUiThread {
                   showNotification(data)
               }
            } catch (e: Exception) {
                runOnUiThread {
                    showNotification(data)
                }
            }
        }
    }
    private fun showNotification(data: Map<String, String>) {
        val activityType = data[ACTIVITY_TYPE]?.toInt() ?: 0
        val action = data[ACTION]?.toInt() ?: 0
        val sendName = data[SENDER_NAME].toString()
        val activity_uuid = data[ACTIVITY_UUID].toString()
        var message = "New offer or request"
        if (activityType == 1) {
            if (action == 2) {//request accepted
                message = getString(R.string.someone_accept_offer, sendName)
            }
        } else if (activityType == 2) {
            if (action == 1) {//offer accepted
                message = getString(R.string.someone_receive_your_request, sendName)
            }
        }

        val intent = Intent(this, RequestDetailActivity::class.java)
        intent.putExtra(OFFER_TYPE, activityType)
        intent.putExtra(INITIATOR, action)
        intent.putExtra(ACTIVITY_UUID, activity_uuid)
        intent.putExtra(FROM_NOTIFICATION, true)
        intent.putExtra(HELP_TYPE, activityType)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
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
}