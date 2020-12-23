package ru.zkdev.notificationapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.android.material.snackbar.Snackbar
import com.kirich1409.androidnotificationdsl.notification
import kotlin.random.Random

/**
 * Simple app use case for show notification in app
 */
class MainActivity : AppCompatActivity() {

    private var isSimple = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<Button>(R.id.button_first).setOnClickListener { view ->
            /**
             * Simple example show default show push notification
             */
            if (isSimple) {
                sendNotificationSimple(this, Random.nextInt(), "title", "notification simple")
                showSnackbar(view, "Show notification simple")
            } else {
                /**
                 * example show push notification DSL
                 */
                showSnackbar(view, "Show notification DSL")
                sendNotificationDSL(this, "title", "notification DSL")
            }
            isSimple = !isSimple
        }
    }

    private fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }

    private fun sendNotificationSimple(
            context: Context,
            id: Int,
            title: String?,
            content: String?,
            intentIn: Intent? = null
    ) {

        var intent = Intent(context, MainActivity::class.java)
        if (intentIn != null) {
            intent = intentIn
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
                context, id, intent,
                PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = context.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    channelId,
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableVibration(true)
            channel.description = context.getString(R.string.app_name)
            channel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(id, notificationBuilder.build())
    }

    private fun sendNotificationDSL(
            context: Context,
            title: String?,
            content: String?
    ) {
        val channelId = context.getString(R.string.default_notification_channel_id)
        notification(context, channelId, smallIcon = R.mipmap.ic_launcher) {
            contentTitle("$title")
            contentText("$content")
            priority(NotificationCompat.PRIORITY_HIGH)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}