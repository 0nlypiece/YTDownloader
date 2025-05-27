package com.example.ytdownloader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.ytdownloader.databinding.ActivityMainBinding;

import java.io.Console;
import java.io.File;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    String GidenKod="   ";
    SendMail sendMail=new SendMail(this);
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferences2;
    private String mail;
    Intent intent;
    DbBrow dbPage=new DbBrow(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }




        sharedPreferences=getSharedPreferences("Account",MODE_PRIVATE);
        sharedPreferences2=getSharedPreferences("Memory",MODE_PRIVATE);
        dbPage.DBtanimla();
        boolean Control=sharedPreferences2.getBoolean("Cookie",false);
       if(Control) {intent=new Intent(this, MainMenu.class); startActivity(intent);}
    }

    public void Register(View view){

        String UserName=binding.etName.getText().toString().trim();
        String Password=binding.etPassword.getText().toString().trim();
        String gmail=binding.etEmail.getText().toString().trim();
        String gmailCode=binding.etEmailCode.getText().toString().trim();
        if(!UserName.matches("") && !Password.matches("") && Password.length() > 6 && !gmail.matches("") && gmailCode.matches(GidenKod)){
            String[] isimCont={UserName};
            if(!dbPage.kullaniciIsimControl(isimCont)) {
                Dosya(UserName);
                dbPage.KullaniciKayit(UserName, gmail, Password);
                sharedPreferences.edit().putString("userName", UserName).apply();
                sharedPreferences.edit().putString("Password", Password).apply();
                sharedPreferences.edit().putString("Email", gmail).apply();


                Intent intent = new Intent(this, MainMenu.class);
                startActivity(intent);
                finish();
            }
        }
        else {
            Toast.makeText(this, "Boş geçmeyin Veya \n Şifreniz Kısa", Toast.LENGTH_SHORT).show();
        }
    }
    public void SendCode(View view){
        GidenKod.trim();
        GidenKod= random("");
        String mail= binding.etEmail.getText().toString().trim();

        boolean gecerli= sendMail.isValidGmail(mail);
        if(gecerli==true) {

            String emailCont[] = {mail};

            dbPage.KullaniciEmailKontrol(emailCont);
            if (dbPage.EmailGit) {
                return;
            } else {
                ExecutorService executorService = Executors.newSingleThreadExecutor();


                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            sendMail.sendEmail(mail, GidenKod);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                executorService.shutdown();
            }
        }
    }
    public void GoLogin(View view){
        Intent intent=new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public String random(String random){
        String characters="ABCDEFKGHIJKLMNOPRSTUWVYZabcdefghijklmnoprstuwvyz123467890";
        Random rnd=new Random();
        for(int i=0;i<6;i++){
            int index=  rnd.nextInt(characters.length());
            random+=characters.charAt(index);
        }
        return random;
    }

    public void Dosya(String isim) {
        // Kullanıcıya özel klasör yolu: /storage/emulated/0/Android/data/your.package.name/files/.username
        File baseFolder = new File(getExternalFilesDir(null), "." + isim);

        if (!baseFolder.exists()) {
            boolean created = baseFolder.mkdirs();
            if (!created) {
                Log.e("Dosya", "Ana klasör oluşturulamadı!");
                return;
            }
        }

        // mp4 klasörünü oluştur
        File mp4Folder = new File(baseFolder, "mp4");
        if (!mp4Folder.exists()) {
            boolean created = mp4Folder.mkdirs();
            if (!created) {
                Log.e("Dosya", "mp4 klasörü oluşturulamadı!");
            }
        }

        // mp3 klasörünü oluştur
        File mp3Folder = new File(baseFolder, "mp3");
        if (!mp3Folder.exists()) {
            boolean created = mp3Folder.mkdirs();
            if (!created) {
                Log.e("Dosya", "mp3 klasörü oluşturulamadı!");
            }
        }
    }


}