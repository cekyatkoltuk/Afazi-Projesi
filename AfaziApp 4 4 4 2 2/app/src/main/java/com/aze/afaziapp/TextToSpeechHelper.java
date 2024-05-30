package com.aze.afaziapp;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class TextToSpeechHelper {

    private static TextToSpeech textToSpeech;

    public static void initialize(Context context) {
        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = textToSpeech.setLanguage(new Locale("tr", "TR"));
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Toast.makeText(context, "TextToSpeech dil desteği mevcut değil.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "TextToSpeech başlatılamadı.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    public static void speak(String text) {
        if (textToSpeech != null && !text.isEmpty()) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    public static void release() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
    }
}
