package com.example.ytdownloader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.widget.Toast;

import java.util.ArrayList;

public class DbBrow {

    Context context;
    SQLiteDatabase db;

    public DbBrow(Context context) {
        this.context = context;

    }

    public void DBtanimla(){
        db = context.openOrCreateDatabase("Database",Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Account (id INTEGER PRIMARY KEY,name VARCHAR,email VARCHAR,password VARCHAR)");

    }

    public void KullaniciKayit(String Name,String eMail,String Password){
        try {
            db = context.openOrCreateDatabase("Database",Context.MODE_PRIVATE,null);
            String kayit ="INSERT INTO Account(name,email,password) VALUES( ?,?,? )";
            SQLiteStatement sqLiteStatement=db.compileStatement(kayit);
            sqLiteStatement.bindString(1,Name);
            sqLiteStatement.bindString(2,eMail);
            sqLiteStatement.bindString(3,Password);
            sqLiteStatement.execute();
        }
        catch (Exception e){
            Toast.makeText(context, "Eklenemedi", Toast.LENGTH_SHORT).show();
        }
    }
    public void Updater(String[] bilgi){
        db = context.openOrCreateDatabase("Database", Context.MODE_PRIVATE, null);
        db.execSQL("UPDATE Account SET name=? , password=? Where name=?",bilgi);
        Toast.makeText(context, "Bilgiler Güncellendi", Toast.LENGTH_SHORT).show();
    }
    public void Delete(String[] isim){
        db = context.openOrCreateDatabase("Database", Context.MODE_PRIVATE, null);
        db.execSQL("DELETE FROM Account WHERE name=?", isim);
    }

    public void KullaniciKontrol(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        ArrayList<Integer> arrayList = new ArrayList<>();

        db = context.openOrCreateDatabase("Database", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM Account", null);
        int idIndex = cursor.getColumnIndex("id");

        if (cursor.getCount() > 0 && idIndex != -1) {
            while (cursor.moveToNext()) {
                arrayList.add(cursor.getInt(idIndex));
            }
        } else {
            alertDialog.setTitle("Kullanıcı Yok");
            alertDialog.setMessage("Herhangi bir kullanıcı bulunamadı.\nKayıt olmak zorundasınız.");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                }
            });
            alertDialog.show();
        }
        cursor.close();

    }
    public boolean kullaniciIsimControl(String[] isim){
        db = context.openOrCreateDatabase("Database", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM Account WHERE name=?", isim);
        int idIndex = cursor.getColumnIndex("id");

        if (cursor.getCount() > 0 && idIndex != -1) {
            Toast.makeText(context, "Bu kullanıcı Adı alınmıştır", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {return false;}

    }
    public void KullaniciGirisControl(String Name,String password){
        db = context.openOrCreateDatabase("Database", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM Account WHERE name=? AND password=?", new String[]{Name, password});
        int idIndex = cursor.getColumnIndex("id");
        if(cursor.getCount()>0 && idIndex !=-1){
             Intent intent=new Intent(context, MainMenu.class);
             context.startActivity(intent);
        }
        else {
            Toast.makeText(context, "Böyle bir kullanıcı bulunamadı", Toast.LENGTH_SHORT).show();
        }

    }

    boolean EmailGit=false;
    public void KullaniciEmailKontrol(String[] email ){
        db = context.openOrCreateDatabase("Database", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM Account Where email=?", email);
        int idIndex = cursor.getColumnIndex("id");

        if (cursor.getCount() > 0 && idIndex != -1) {
            while (cursor.moveToNext()) {

                Toast.makeText(context, "Sistemimizde bu email vardır lüften \n başka email kullanınız", Toast.LENGTH_SHORT).show();


            }
            EmailGit=true;
        } else {
          EmailGit=false;
        }
        cursor.close();

    }

}
