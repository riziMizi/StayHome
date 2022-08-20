package com.example.stayhome.dostavuvac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayhome.MainActivity;
import com.example.stayhome.R;
import com.example.stayhome.classes.User;
import com.example.stayhome.kupuvac.KupuvacProfilActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DostavuvacProfilActivity extends AppCompatActivity {

    private TextView txtSaveEdit, txtVerify, txtVerified;
    private EditText editIme, editTelefon, editEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dostavuvac_profil);

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        txtSaveEdit = findViewById(R.id.txtDostavuvacSaveEdit);
        txtVerify = findViewById(R.id.txtDostavuvacNotVerified);
        txtVerified = findViewById(R.id.txtDostavuvacVerified);
        editIme = findViewById(R.id.editTextDostavuvacIme);
        editTelefon = findViewById(R.id.editTextDostavuvacTelefon);
        editEmail = findViewById(R.id.editTextDostavuvacEmail);

        txtVerify.setEnabled(false);

        txtSaveEdit.setVisibility(View.INVISIBLE);

        PostaviProfil();
        CheckIfEmailIsVerified();
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
                        startActivity(new Intent(DostavuvacProfilActivity.this, MainActivity.class));
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

    private void PostaviProfil() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                editEmail.setText(user.getEmail());
                editIme.setText(user.getIme());
                editTelefon.setText(user.getTelefon());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DostavuvacProfilActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Edit(View view) {
        editTelefon.setEnabled(true);
        txtVerify.setEnabled(true);
        txtSaveEdit.setVisibility(View.VISIBLE);
        txtVerify.setPaintFlags(txtVerify.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public void SaveEdit(View view) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String Telefon = editTelefon.getText().toString().trim();
                String Ime = editIme.getText().toString().trim();
                user.setTelefon(Telefon);
                user.setIme(Ime);
                snapshot.getRef().setValue(user);
                Toast.makeText(DostavuvacProfilActivity.this, "Успешно направивте промена!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DostavuvacProfilActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });

        txtSaveEdit.setVisibility(View.INVISIBLE);
        editTelefon.setEnabled(false);
        editIme.setEnabled(false);
        txtVerify.setEnabled(false);
        txtVerify.setPaintFlags(0);
    }

    public void VerifyEmail(View view) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                txtVerify.setEnabled(false);
                txtVerify.setPaintFlags(0);
                Toast.makeText(DostavuvacProfilActivity.this, "Испратен е линк за верифиакција на вашата е-маил адреса!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("123","onFailure: Email not sent: " + e.getMessage());
            }
        });
    }

    private void CheckIfEmailIsVerified() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser.isEmailVerified()) {
            txtVerify.setVisibility(View.INVISIBLE);
        } else {
            txtVerified.setVisibility(View.INVISIBLE);
        }
    }
}