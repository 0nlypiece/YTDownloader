package com.example.ytdownloader;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Downloader extends AppCompatActivity {

    EditText editTextLink;
    RadioButton radioButtonM;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_downloader);
         // ffmpeg binary'nin tam yolu

        // İzin kontrolü (Android 10 ve öncesi için)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE_PERMISSION);
            }
        }

        editTextLink = findViewById(R.id.etMediaLink);
        WebView webView = findViewById(R.id.WebViev);
        radioButtonM = findViewById(R.id.rbMusic);
        sharedPreferences = getSharedPreferences("Account", MODE_PRIVATE);
        progressBar = findViewById(R.id.progressBar);

        // WebView ayarları
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        // Youtube link değiştiğinde embed oynatma
        editTextLink.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String youtubeLink = s.toString().trim();
                String videoId = extractYouTubeVideoId(youtubeLink);
                if (videoId != null) {
                    String embedUrl = "https://www.youtube.com/embed/" + videoId;
                    String html = "<html><body style=\"margin:0;padding:0\">" +
                            "<iframe width=\"100%\" height=\"100%\" " +
                            "src=\"" + embedUrl + "\" frameborder=\"0\" allowfullscreen></iframe>" +
                            "</body></html>";
                    webView.loadData(html, "text/html", "utf-8");
                }
            }
        });

        // Python başlat
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }

    // Youtube video ID çıkarma metodu
    private String extractYouTubeVideoId(String url) {
        try {
            if (url.contains("youtu.be/")) {
                return url.substring(url.indexOf("youtu.be/") + 9);
            } else if (url.contains("watch?v=")) {
                String videoId = url.substring(url.indexOf("watch?v=") + 8);
                int ampersandPos = videoId.indexOf('&');
                return (ampersandPos != -1) ? videoId.substring(0, ampersandPos) : videoId;
            } else if (url.contains("youtube.com/shorts/")) {
                return url.substring(url.indexOf("shorts/") + 7);
            } else if (url.contains("m.youtube.com/watch?v=")) {
                String videoId = url.substring(url.indexOf("watch?v=") + 8);
                int ampersandPos = videoId.indexOf('&');
                return (ampersandPos != -1) ? videoId.substring(0, ampersandPos) : videoId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // İzin sonucu işleme
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Depolama izni gerekli!", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // İndirme butonuna basınca çağrılır
    public void indir(View view) {
        String youtubeUrl = editTextLink.getText().toString().trim();
        boolean isMp3 = radioButtonM.isChecked();
        String userName = sharedPreferences.getString("userName", "defaultUser");

        // Uygulama özel dizininde kullanıcı klasörleri
        File baseFolder = new File(getExternalFilesDir(null), "." + userName);
        if (!baseFolder.exists()) baseFolder.mkdirs();

        File tempFolder = new File(baseFolder, "temp");
        if (!tempFolder.exists()) tempFolder.mkdirs();
        String tempPath = tempFolder.getAbsolutePath();

        Python py = Python.getInstance();
        PyObject pyModule;
        PyObject result;
        String downloadStatus;

        try {
            if (isMp3) {
                pyModule = py.getModule("DownloaderMp3");

                result = pyModule.callAttr("download_audio_rename_mp3", youtubeUrl, tempPath);

            } else {
                pyModule = py.getModule("DownloaderMp4");
                result = pyModule.callAttr("download_mp4", youtubeUrl, tempPath);
            }
            downloadStatus = result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Python hatası: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("Downloader", e.getMessage());
            return;
        }

        if (!downloadStatus.equals("İndirme başarılı")) {
            Toast.makeText(this, "İndirme başarısız: " + downloadStatus, Toast.LENGTH_SHORT).show();
            Log.d("Downloaderas", downloadStatus);
            return;
        }

        takipEtVeGuncelleProgressBar();



        String downloadedFilePath = findLatestFileInFolder(tempPath);
        if (downloadedFilePath == null) {
            Toast.makeText(this, "Dosya bulunamadı!", Toast.LENGTH_SHORT).show();
            return;
        }

        File downloadedFile = new File(downloadedFilePath);

        if (isMp3) {
            File mp3Folder = new File(baseFolder, "mp3");
            if (!mp3Folder.exists()) mp3Folder.mkdirs();


            File mp3OutputFile = new File(mp3Folder, downloadedFile.getName());


            if (moveFile(downloadedFile, mp3OutputFile)) {
                Toast.makeText(this, "MP3 olarak indirildi: " + mp3OutputFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                Log.d("Mp3Cont", mp3OutputFile.getAbsolutePath());
            } else {
                Toast.makeText(this, "Dosya taşınamadı!", Toast.LENGTH_SHORT).show();
            }
        } else {
            File mp4Folder = new File(baseFolder, "mp4");
            if (!mp4Folder.exists()) mp4Folder.mkdirs();

            File destFile = new File(mp4Folder, downloadedFile.getName());

            if (moveFile(downloadedFile, destFile)) {
                Log.d("Downloaderasdas", "Taşınan MP4 dosyası: " + destFile.getAbsolutePath()); // <-- EKLENDİ
            } else {
                Toast.makeText(this, "Video taşıma başarısız!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    // En yeni dosyayı bul
    private String findLatestFileInFolder(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) return null;

        File latestFile = files[0];
        for (File file : files) {
            if (file.lastModified() > latestFile.lastModified()) {
                latestFile = file;
            }
        }
        return latestFile.getAbsolutePath();
    }

    // ProgressBar güncellemesi

    private void takipEtVeGuncelleProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        String progressFilePath = getExternalFilesDir(null) + "/progress.txt";
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                File file = new File(progressFilePath);
                if (file.exists()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String percentStr = br.readLine();
                        if (percentStr != null) {
                            int percent = Integer.parseInt(percentStr);
                            progressBar.setProgress(percent);
                            if (percent >= 100) {
                                progressBar.setVisibility(View.GONE);
                                return;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                handler.postDelayed(this, 500);
            }
        };
        handler.postDelayed(runnable, 500);
    }

    // Yeni eklenen metod: Dosya kopyala ve sonra sil
    private boolean moveFile(File src, File dst) {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.flush();

            // Kopyalama başarılı ise kaynak dosyayı sil
            return src.delete();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



}
