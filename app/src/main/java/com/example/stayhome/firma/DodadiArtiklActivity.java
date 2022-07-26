package com.example.stayhome.firma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stayhome.MainActivity;
import com.example.stayhome.R;
import com.example.stayhome.classes.Meni;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class DodadiArtiklActivity extends AppCompatActivity {

    private EditText editDodadiArtikl, editDodadiArtiklSostav, editDodadiArtiklCena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodadi_artikl);

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editDodadiArtikl = (EditText) findViewById(R.id.editDodadiMeniArtikl);
        editDodadiArtiklSostav = (EditText) findViewById(R.id.editDodadiMeniArtiklSostav);
        editDodadiArtiklCena = (EditText) findViewById(R.id.editDodadiMeniArtiklCena);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_signout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Одјави се");
                builder.setMessage("Дали сте сигурни дека сакате да се одјавите?");

                builder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Да</font>"), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(DodadiArtiklActivity.this, MainActivity.class));
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton(Html.fromHtml("<font color='#FFFFFF'>Не</font>"), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void DodadiArtikl(View view) {
        String Artikl = editDodadiArtikl.getText().toString().trim();
        String ArtiklSostav = editDodadiArtiklSostav.getText().toString().trim();
        String artiklCena = editDodadiArtiklCena.getText().toString().trim();
        int ArtiklCena = 0;

        if(Artikl.equals("")) {
            editDodadiArtikl.setError("Задолжително поле!");
            editDodadiArtikl.requestFocus();
            return;
        }

        if(ArtiklSostav.equals("")) {
            editDodadiArtiklSostav.setError("Задолжително поле!");
            editDodadiArtiklSostav.requestFocus();
            return;
        }

        if(artiklCena.equals("")) {
            editDodadiArtiklCena.setError("Задолжително поле!");
            editDodadiArtiklCena.requestFocus();
            return;
        }

        try {
            ArtiklCena = Integer.parseInt(artiklCena);

        } catch (Exception e) {
            editDodadiArtiklCena.setError("Ова поле мора да содржи број!");
            editDodadiArtiklCena.requestFocus();
            return;
        }

        Meni meni = new Meni(Artikl, ArtiklSostav, ArtiklCena);

        FirebaseDatabase.getInstance().getReference("Meni")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(UUID.randomUUID().toString())
                .setValue(meni).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(DodadiArtiklActivity.this, "Успешно го дадодовте артиклот во вашето мени!", Toast.LENGTH_SHORT).show();
                    IsprazniPolinja();
                } else {
                    Toast.makeText(DodadiArtiklActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void IsprazniPolinja() {
        editDodadiArtikl.setText("");
        editDodadiArtiklSostav.setText("");
        editDodadiArtiklCena.setText("");
    }
}