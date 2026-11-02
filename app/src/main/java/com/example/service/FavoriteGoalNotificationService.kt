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

class FavoriteGoalNotificationService(
    private val context: Context,
    val favoritesManager: FavoritesManager = FavoritesManager(context),
    val soundPlayer: StadiumSoundPlayer = StadiumSoundPlayer(context)
) {

    companion object {
        const val CHANNEL_ID = "favorite_goal_alerts_v1"
        const val CHANNEL_NAME = "⭐ Favorite Match Goal Alerts (Stadium Roar)"
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
                description = "Loud stadium crowd cheers specifically for your favorited matches"
                enableLights(true)
                lightColor = Color.YELLOW
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 150, 300, 150, 600)
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                setBypassDnd(false)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    /**
     * Checks if the match is in the user's favorite list.
     * If favorited, triggers the custom stadium goal sound and posts a high-priority notification.
     */
    fun triggerIfFavoriteGoal(
        matchId: String,
        leagueName: String,
        homeTeamName: String,
        awayTeamName: String,
        scoringTeamName: String,
        scorer: String,
        assist: String?,
        minute: Int,
        homeScore: Int,
        awayScore: Int,
        forceTrigger: Boolean = false
    ): Boolean {
        val isFavorite = forceTrigger || favoritesManager.isFavorite(matchId)
        if (!isFavorite) {
            return false
        }

        // 1. Play Custom Loud Stadium Goal Sound
        soundPlayer.playStadiumCheer(fullVolume = true)

        // 2. Post High Priority Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionGranted = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!permissionGranted) {
                Log.w("FavGoalService", "Notification permission not granted, but stadium sound was played.")
                return true
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_MATCH_ID", matchId)
            putExtra("EXTRA_IS_FAVORITE", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            matchId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "⭐ FAVORITE GOAL! ⚽ $scoringTeamName [$homeScore - $awayScore]"
        val assistText = if (!assist.isNullOrBlank()) " (Assist: $assist)" else ""
        val body = "$scorer $minute'$assistText\n$homeTeamName vs $awayTeamName ($leagueName)"

        val bigText = "⭐ GOAL IN FAVORITE MATCH! ⚽\n" +
                "🔥 $scorer scores for $scoringTeamName at $minute'!\n" +
                "Current Score: $homeScore - $awayScore\n" +
                (if (!assist.isNullOrBlank()) "🎯 Assist: $assist\n" else "") +
                "🏟️ $homeTeamName vs $awayTeamName • $leagueName\n" +
                "🔊 Stadium roar alert triggered!"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 300, 150, 300, 150, 600))
            .setColor(0xFFFFD700.toInt()) // Gold accent
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(matchId.hashCode() + 999, notification)
        } catch (e: SecurityException) {
            Log.e("FavGoalService", "SecurityException posting favorite notification", e)
        }

        return true
    }
}
