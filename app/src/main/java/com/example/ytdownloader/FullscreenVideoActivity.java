package com.example.ytdownloader;

import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FullscreenVideoActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_video); // XML dosyan

        videoView = findViewById(R.id.fullScreen);

        String videoPath = getIntent().getStringExtra("videoPath");
        if (videoPath == null) {
            Toast.makeText(this, "Video yolu bulunamadı.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // MediaController kurulumu burada
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Videoyu yükle ve başlat
        videoView.setVideoPath(videoPath);
        videoView.start();

        // Video bittiğinde aktiviteyi kapat (opsiyonel)
        videoView.setOnCompletionListener(mp -> finish());
    }
}
