package com.example.ytdownloader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Profile extends AppCompatActivity {
    EditText isim,sifre;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferences2;
    DbBrow dbPage=new DbBrow(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        sharedPreferences2=getSharedPreferences("Account",MODE_PRIVATE);
        isim=findViewById(R.id.etProfileName);
        sifre=findViewById(R.id.etProfilePassword);
        String isimVal=sharedPreferences2.getString("userName","Değer alınamadı").toString();
        String sifreVal=sharedPreferences2.getString("Password","Değer alınamadı").toString();
        isim.setText(isimVal);
        sifre.setText(sifreVal);
        sharedPreferences=getSharedPreferences("Memory",MODE_PRIVATE);
        sharedPreferences2=getSharedPreferences("Account",MODE_PRIVATE);
    }

    public void UpdateBtn(View view){
        isim=findViewById(R.id.etProfileName);
        sifre=findViewById(R.id.etProfilePassword);

        String isimVal=isim.getText().toString().trim();
        String sifreVal=sifre.getText().toString().trim();
        String mainisim=sharedPreferences2.getString("userName","");
        String[] isimCont={isimVal};
        String[] bilgi={isimVal,sifreVal,mainisim};
        if(!dbPage.kullaniciIsimControl(isimCont))
        {dbPage.Updater(bilgi); sharedPreferences2.edit().putString("userName",isimVal).apply();  }

    }
    public void DeleteBtn(View view){

      String[] isim={ sharedPreferences2.getString("userName","")};
        dbPage.Delete(isim);
        sharedPreferences.edit().putBoolean("Cookie",false).apply();
        finishAffinity();
    }

    public void goMainMenu(View view){
        Intent intent=new Intent(this,MainMenu.class);
        startActivity(intent);
    }
    public void QuickAccount(View view){
        sharedPreferences.edit().putBoolean("Cookie",false).apply();
        finishAffinity();
    }
}