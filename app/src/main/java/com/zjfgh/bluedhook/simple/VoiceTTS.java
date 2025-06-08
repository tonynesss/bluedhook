package com.zjfgh.bluedhook.simple;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Locale;

public class VoiceTTS {
    private static volatile VoiceTTS instance;
    private TextToSpeech textToSpeech;
    private final WeakReference<Context> contextRef;

    private VoiceTTS(Context context) {
        this.contextRef = new WeakReference<>(context);
        initTTS(getSafeContext());
    }

    public static VoiceTTS getInstance(Context context) {
        if (instance == null) {
            synchronized (VoiceTTS.class) {
                if (instance == null) {
                    instance = new VoiceTTS(context);
                }
            }
        }
        return instance;
    }

    private void initTTS(Context context) {
        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(context, status -> {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.CHINESE);
                    ModuleTools.showBluedToast("语音合成初始化成功!");
                } else {
                    Toast.makeText(context, "语音合成初始化失败!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void speak(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    public void speakAdd(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, null);
        }
    }

    private Context getSafeContext() {
        return contextRef != null ? contextRef.get() : null;
    }
}
