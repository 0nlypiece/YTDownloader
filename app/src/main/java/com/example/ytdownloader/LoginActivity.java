package com.example.ytdownloader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ytdownloader.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferences2;
    Intent intent;
    DbBrow dbr=new DbBrow(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbr.DBtanimla();
        dbr.KullaniciKontrol();
        sharedPreferences=getSharedPreferences("Memory",MODE_PRIVATE);
        sharedPreferences2=getSharedPreferences("Account",MODE_PRIVATE);

        boolean Control=sharedPreferences.getBoolean("Cookie",false);
        if(Control) {intent=new Intent(this, MainMenu.class); startActivity(intent);}
    }
    public void Login(View view){
        String useName=binding.etUsername.getText().toString();
        String passworD=binding.etPassword.getText().toString();
        boolean check=binding.cbRememberMe.isChecked();
        if(check){sharedPreferences.edit().putBoolean("Cookie",true).apply();   sharedPreferences2.edit().putString("userName",useName).apply();   sharedPreferences2.edit().putString("Password",passworD).apply(); dbr.KullaniciGirisControl(useName,passworD);
           }
        else {sharedPreferences.edit().putBoolean("Cookie",false).apply();   sharedPreferences2.edit().putString("userName",useName).apply();   sharedPreferences2.edit().putString("Password",passworD).apply(); dbr.KullaniciGirisControl(useName,passworD);}
    }
}