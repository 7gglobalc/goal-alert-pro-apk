package com.example.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.model.GoalEvent

class GoalNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "goal_alerts_high_priority_v1"
        const val CHANNEL_NAME = "Live Goal Alerts (Stadium Roar)"
    }

    private val soundUri: Uri by lazy {
        Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/${R.raw.stadium_cheer}")
    }

    init {
        createNotificationChannel()
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Loud stadium crowd alerts with player, assist, minute and score"
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 100, 250, 100, 450)
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                setBypassDnd(false)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun showGoalNotification(goal: GoalEvent, matchSummary: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionGranted = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!permissionGranted) {
                Log.w("GoalNotificationManager", "POST_NOTIFICATIONS permission not granted")
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_GOAL_ID", goal.id)
            putExtra("EXTRA_MATCH_ID", goal.matchId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            goal.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "⚽ GOAL! ${goal.team.name} [${goal.homeScore} - ${goal.awayScore}]"
        val assistText = if (!goal.assist.isNullOrBlank()) " • Assist: ${goal.assist}" else ""
        val body = "${goal.scorer} ${goal.displayMinute}$assistText\n$matchSummary"

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .setBigContentTitle(title)
            .setSummaryText("${goal.league.displayName} Goal Alert")
            .bigText(
                "🔥 ${goal.scorer} scores for ${goal.team.name} at ${goal.displayMinute}!\n" +
                "Current Score: ${goal.homeScore} - ${goal.awayScore}\n" +
                (if (!goal.assist.isNullOrBlank()) "🎯 Assist: ${goal.assist}\n" else "") +
                "🏟️ $matchSummary"
            )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(bigTextStyle)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 250, 100, 250, 100, 450))
            .setColor(0xFF00E676.toInt()) // Neon green
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(goal.id.hashCode(), notification)
        } catch (e: SecurityException) {
            Log.e("GoalNotificationManager", "SecurityException posting notification", e)
        }
    }
}
