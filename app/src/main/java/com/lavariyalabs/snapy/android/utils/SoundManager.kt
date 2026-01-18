package com.lavariyalabs.snapy.android.utils

import android.media.AudioManager
import android.media.ToneGenerator

/**
 * SoundManager - Handles app-wide sound effects
 */
object SoundManager {
    // Volume 100 (Max)
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    /**
     * playClickSound - Plays a short blip sound
     * 
     * Uses system ToneGenerator to avoid needing an external MP3 file immediately.
     * TONE_CDMA_PIP is a short, high-pitched beep often used in UI.
     */
    fun playClickSound() {
        try {
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
