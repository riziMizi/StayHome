package com.example.stayhome.dostavuvac;

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
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.stayhome.MainActivity;
import com.example.stayhome.R;
import com.example.stayhome.classes.User;
import com.example.stayhome.firma.FirmaMeniActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;

public class OpstiniDostavaActivity extends AppCompatActivity {

    private CheckBox checkBoxAerodrom, checkBoxButel, checkBoxGaziBaba, checkBoxGorcePetrov, checkBoxKarpos,
    checkBoxKiselaVoda, checkBoxSaraj, checkBoxCentar, checkBoxCair, checkBoxSutoOrizari;

    FirebaseUser currentUser =FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opstini_dostava);

        checkBoxAerodrom = findViewById(R.id.checkboxAerodrom);
        checkBoxButel = findViewById(R.id.checkboxButel);
        checkBoxGaziBaba = findViewById(R.id.checkboxGaziBaba);
        checkBoxGorcePetrov = findViewById(R.id.checkboxGorcePetrov);
        checkBoxKarpos = findViewById(R.id.checkboxKarpos);
        checkBoxKiselaVoda = findViewById(R.id.checkboxKiselaVoda);
        checkBoxSaraj = findViewById(R.id.checkboxSaraj);
        checkBoxCentar = findViewById(R.id.checkboxCentar);
        checkBoxCair = findViewById(R.id.checkboxCair);
        checkBoxSutoOrizari = findViewById(R.id.checkboxSutoOrizari);

        PostaviOpstini();

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
                        startActivity(new Intent(OpstiniDostavaActivity.this, MainActivity.class));
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

    public void ZacuvajOpstini(View view) {
        LinkedList<String> lista = new LinkedList<>();

        if(checkBoxAerodrom.isChecked()) {
            lista.add("Aerodrom");
        }
        if(checkBoxButel.isChecked()) {
            lista.add("Butel");
        }
        if(checkBoxGaziBaba.isChecked()) {
            lista.add("GaziBaba");
        }
        if(checkBoxGorcePetrov.isChecked()) {
            lista.add("GorcePetrov");
        }
        if(checkBoxKarpos.isChecked()) {
            lista.add("Karpos");
        }
        if(checkBoxKiselaVoda.isChecked()) {
            lista.add("KiselaVoda");
        }
        if(checkBoxSaraj.isChecked()) {
            lista.add("Saraj");
        }
        if(checkBoxCentar.isChecked()) {
            lista.add("Centar");
        }
        if(checkBoxCair.isChecked()) {
            lista.add("Cair");
        }
        if(checkBoxSutoOrizari.isChecked()) {
            lista.add("SutoOrizari");
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());

        databaseReference.child("opstiniDostavuvac").setValue(lista).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(OpstiniDostavaActivity.this, "Успешно направивте промена.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OpstiniDostavaActivity.this, "Настана грешка. Обидете се повторно!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void PostaviOpstini() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                ArrayList<String> list = user.getOpstiniDostavuvac();
                if(list != null) {
                    if(list.contains("Aerodrom")) {
                        checkBoxAerodrom.setChecked(true);
                    }
                    if(list.contains("Butel")) {
                        checkBoxButel.setChecked(true);
                    }
                    if(list.contains("GaziBaba")) {
                        checkBoxGaziBaba.setChecked(true);
                    }
                    if(list.contains("GorcePetrov")) {
                        checkBoxGorcePetrov.setChecked(true);
                    }
                    if(list.contains("Karpos")) {
                        checkBoxKarpos.setChecked(true);
                    }
                    if(list.contains("KiselaVoda")) {
                        checkBoxKiselaVoda.setChecked(true);
                    }
                    if(list.contains("Saraj")) {
                        checkBoxSaraj.setChecked(true);
                    }
                    if(list.contains("Centar")) {
                        checkBoxCentar.setChecked(true);
                    }
                    if(list.contains("Cair")) {
                        checkBoxCair.setChecked(true);
                    }
                    if(list.contains("SutoOrizari")) {
                        checkBoxSutoOrizari.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OpstiniDostavaActivity.this, "Настана грешка. Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}