package com.example.ytdownloader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainMenu extends AppCompatActivity {

    private VideoAdapter videoAdapter;
    private RecyclerView recyclerMusic, recyclerVideo;
    private TextView btnMusic, btnVideos;
    private String UserName;
    private MediaPlayer mediaPlayer;
    private File currentMusicFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);

        btnMusic = findViewById(R.id.btnMusic);
        btnVideos = findViewById(R.id.btnVideos);
        recyclerMusic = findViewById(R.id.recyclerMusic);
        recyclerVideo = findViewById(R.id.recyclerVideo);

        SharedPreferences sharedPreferences = getSharedPreferences("Account", MODE_PRIVATE);
        UserName = sharedPreferences.getString("userName", "");

        btnMusic.setOnClickListener(v -> showMusic());
        btnVideos.setOnClickListener(v -> showVideos());

        showMusic();
    }

    private void showMusic() {
        recyclerMusic.setVisibility(View.VISIBLE);
        recyclerVideo.setVisibility(View.GONE);

        btnMusic.setTextColor(Color.parseColor("#3F51B5"));
        btnVideos.setTextColor(Color.parseColor("#666666"));

        recyclerMusic.setLayoutManager(new LinearLayoutManager(this));

        List<File> musicFiles = getMusicFiles(UserName);

        if (musicFiles.isEmpty()) {
            Toast.makeText(this, "Hiç müzik bulunamadı.", Toast.LENGTH_SHORT).show();
        }

        MusicAdapter musicAdapter = new MusicAdapter(this, musicFiles, musicFile -> {
            // Burada çal / durdur mantığı
            if (mediaPlayer != null && mediaPlayer.isPlaying() && musicFile.equals(currentMusicFile)) {
                // Aynı müzik çalıyorsa durdur
                mediaPlayer.pause();
                Toast.makeText(this, "Müzik durduruldu", Toast.LENGTH_SHORT).show();
            } else {
                // Farklı müzik ya da durdurulmuş ise yeni müziği başlat
                try {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(musicFile.getAbsolutePath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    currentMusicFile = musicFile;


                    mediaPlayer.setOnCompletionListener(mp -> {
                        mediaPlayer.release();
                        mediaPlayer = null;
                        currentMusicFile = null;
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Müzik çalınamadı!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerMusic.setAdapter(musicAdapter);
    }

    private void showVideos() {
        recyclerVideo.setVisibility(View.VISIBLE);
        recyclerMusic.setVisibility(View.GONE);

        btnVideos.setTextColor(Color.parseColor("#3F51B5"));
        btnMusic.setTextColor(Color.parseColor("#666666"));

        recyclerVideo.setLayoutManager(new LinearLayoutManager(this));

        List<File> videoFiles = getVideoFiles(UserName);
        Log.d("DEBUG", "Video sayısı: " + videoFiles.size());

        if (videoAdapter == null) {
            videoAdapter = new VideoAdapter(this, videoFiles);
            recyclerVideo.setAdapter(videoAdapter);
        } else {
            videoAdapter.updateData(videoFiles);
        }

        if (videoFiles.isEmpty()) {
            Toast.makeText(this, "Hiç video bulunamadı.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoAdapter != null) {
            List<File> videoFiles = getVideoFiles(UserName);
            videoAdapter.updateData(videoFiles);
        }
    }

    private List<File> getMusicFiles(String userName) {
        List<File> musicFiles = new ArrayList<>();
        File musicDir = new File(getExternalFilesDir(null), "."+userName + "/mp3"); // Nokta kaldırıldı

        if (musicDir.exists() && musicDir.isDirectory()) {
            File[] files = musicDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && (file.getName().endsWith(".mp3") || file.getName().endsWith(".m4a") || file.getName().endsWith(".webm")) || file.getName().endsWith(".mp4")) {
                        musicFiles.add(file);
                    }
                }
            }
        }
        return musicFiles;
    }

    private List<File> getVideoFiles(String userName) {
        List<File> videoFiles = new ArrayList<>();
        File videoDir = new File(getExternalFilesDir(null),"."+userName + "/mp4"); // Nokta kaldırıldı

        if (videoDir.exists() && videoDir.isDirectory()) {
            File[] files = videoDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && (file.getName().endsWith(".mp4"))) {
                        videoFiles.add(file);
                    }
                }
            }
        }
        return videoFiles;
    }

    public void DownloderBtn(View view) {
        Intent page = new Intent(this, Downloader.class);
        startActivity(page);
    }

    public void ProfileBtn(View view) {
        Intent page = new Intent(this, Profile.class);
        startActivity(page);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
