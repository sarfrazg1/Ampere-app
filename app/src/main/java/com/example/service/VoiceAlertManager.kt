package com.example.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.util.AppLanguage
import com.example.util.Strings
import java.util.Locale

class VoiceAlertManager(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingSpeech: Pair<String, AppLanguage>? = null

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("VoiceAlertManager", "Error initializing TTS", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setSpeechRate(0.95f)
            tts?.setPitch(1.0f)
            pendingSpeech?.let { (text, lang) ->
                speak(text, lang)
                pendingSpeech = null
            }
        } else {
            Log.e("VoiceAlertManager", "TTS initialization failed with status $status")
        }
    }

    fun speakCableAssessment(currentMa: Int, estimatedMinutes: Int, language: AppLanguage) {
        val message = StringBuilder()

        when (language) {
            AppLanguage.URDU -> {
                when {
                    currentMa >= 1500 -> message.append(Strings.voiceFastUrdu())
                    currentMa in 800..1499 -> message.append(Strings.voiceNormalUrdu())
                    else -> message.append(Strings.voiceSlowUrdu())
                }
                if (estimatedMinutes > 0) {
                    message.append(". ").append(Strings.voiceTimeUrdu(estimatedMinutes))
                }
            }
            AppLanguage.ENGLISH -> {
                when {
                    currentMa >= 1500 -> message.append(Strings.voiceFastEnglish())
                    currentMa in 800..1499 -> message.append(Strings.voiceNormalEnglish())
                    else -> message.append(Strings.voiceSlowEnglish())
                }
                if (estimatedMinutes > 0) {
                    message.append(". ").append(Strings.voiceTimeEnglish(estimatedMinutes))
                }
            }
        }

        speak(message.toString(), language)
    }

    fun speak(text: String, language: AppLanguage) {
        if (!isInitialized || tts == null) {
            pendingSpeech = Pair(text, language)
            return
        }

        val targetLocale = when (language) {
            AppLanguage.URDU -> Locale("ur", "PK")
            AppLanguage.ENGLISH -> Locale.US
        }

        val langResult = tts?.setLanguage(targetLocale)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w("VoiceAlertManager", "Locale $targetLocale not fully supported, trying fallback")
            if (language == AppLanguage.URDU) {
                // If ur-PK is missing, try generic Urdu
                val fallbackUrdu = Locale("ur")
                if (tts?.setLanguage(fallbackUrdu) == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Speak Roman Urdu / phonetic or English fallback
                    tts?.language = Locale.US
                }
            } else {
                tts?.language = Locale.US
            }
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "smart_charging_alert_${System.currentTimeMillis()}")
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
        } catch (e: Exception) {
            Log.e("VoiceAlertManager", "Error shutting down TTS", e)
        }
    }
}
