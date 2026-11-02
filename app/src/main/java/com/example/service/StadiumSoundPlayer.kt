package com.example.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.example.R

class StadiumSoundPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    fun playStadiumCheer(fullVolume: Boolean = true) {
        try {
            // Stop existing if playing
            stop()

            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            if (fullVolume && audioManager != null) {
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)
                val currentVol = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
                if (currentVol < (maxVolume * 0.75).toInt()) {
                    // Prompt/boost to high volume for the goal cheer
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_NOTIFICATION,
                        (maxVolume * 0.9).toInt(),
                        0
                    )
                }
            }

            mediaPlayer = MediaPlayer.create(context, R.raw.stadium_cheer)?.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                        .build()
                )
                isLooping = false
                setOnCompletionListener { mp ->
                    mp.release()
                    mediaPlayer = null
                }
                start()
            }

            vibrateGoal()
        } catch (e: Exception) {
            Log.e("StadiumSoundPlayer", "Failed to play stadium sound", e)
        }
    }

    private fun vibrateGoal() {
        try {
            val pattern = longArrayOf(0, 200, 100, 200, 100, 500)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createWaveform(pattern, -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(pattern, -1)
                }
            }
        } catch (e: Exception) {
            Log.w("StadiumSoundPlayer", "Vibration failed", e)
        }
    }

    fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.w("StadiumSoundPlayer", "Error stopping player", e)
        } finally {
            mediaPlayer = null
        }
    }
}
