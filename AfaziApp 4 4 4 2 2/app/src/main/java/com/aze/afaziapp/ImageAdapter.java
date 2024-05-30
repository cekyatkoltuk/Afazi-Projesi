package com.aze.afaziapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.nl.translate.TranslateLanguage;

import java.io.File;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<String> imagePaths;
    private Context context;
    private OnItemClickListener listener;
    private TextToSpeech textToSpeech;

    public ImageAdapter(Context context, List<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
        TextToSpeechHelper.initialize(context);  // Initialize TextToSpeech
        this.textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.SUCCESS) {
                    Toast.makeText(context, "TextToSpeech başlatılamadı.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(String imagePath);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_ai_card, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.bind(imagePaths.get(position), position);
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private Button deleteButton;
        private Button ttsButton;
        private TextView textView;
        private Bitmap imageBitmap;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.aiCardImageView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            ttsButton = itemView.findViewById(R.id.ttsButton);
            textView = itemView.findViewById(R.id.aiCardNameTextView);
            itemView.setOnClickListener(this);
        }

        public void bind(final String imagePath, final int position) {
            imageBitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(imageBitmap);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        final String imagePath = imagePaths.get(position);
                        File imageFile = new File(imagePath);
                        if (imageFile.exists()) {
                            if (imageFile.delete()) {
                                imagePaths.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, imagePaths.size());
                            }
                        }
                    }
                }
            });

            ttsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call the method to speak the detected text
                    speakDetectedText(textView);
                }
            });

            // Perform object detection and update the text view
            performObjectDetection(textView, position);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(imagePaths.get(position));
                }
            }
        }

        private void performObjectDetection(final TextView textView, final int position) {
            FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);

            labeler.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                            if (!labels.isEmpty()) {
                                FirebaseVisionImageLabel firstLabel = labels.get(0);
                                String text = firstLabel.getText();
                                // Translate the detected text
                                translateText(text, textView);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Nesne tespiti yaparken bir hata oluştu", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        private void translateText(String sourceText, final TextView textView) {
            TranslatorOptions options = new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.TURKISH)
                    .build();

            final Translator translator = Translation.getClient(options);

            // Download translation model if needed
            translator.downloadModelIfNeeded()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void v) {
                            // Model downloaded successfully. Translate the text
                            translator.translate(sourceText)
                                    .addOnSuccessListener(new OnSuccessListener<String>() {
                                        @Override
                                        public void onSuccess(String translatedText) {
                                            // Set the translated text to the text view
                                            textView.setText(translatedText);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle the error
                                            Toast.makeText(context, "Çeviri sırasında bir hata oluştu", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the error
                            Toast.makeText(context, "Model indirme sırasında bir hata oluştu", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        private void speakDetectedText(final TextView textView) {
            // Get the detected text from the text view
            String detectedText = textView.getText().toString().trim();

            // Speak the detected text
            TextToSpeechHelper.speak(detectedText);
        }
    }
}
