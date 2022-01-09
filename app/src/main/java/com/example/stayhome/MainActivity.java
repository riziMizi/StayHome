package com.example.stayhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView txtRegister;
    private EditText editEmail, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editEmail = (EditText) findViewById(R.id.editLoginEmail);
        editPassword = (EditText) findViewById(R.id.editLoginPassword);

        txtRegister = (TextView) findViewById(R.id.txtRegister);
        txtRegister.setPaintFlags(txtRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public void GoToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void Login(View view) {
        String Email = editEmail.getText().toString().trim();
        String Password = editPassword.getText().toString().trim();

        Intent intentFirma = new Intent(this, FirmaActivity.class);
        Intent intentKupuvac = new Intent(this, KupuvacActivity.class);

        if(Email.equals("")) {
            editEmail.setError("Задолжително поле!");
            editEmail.requestFocus();
            return;
        } else {
            if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                editEmail.setError("Внесете валидна е-маил адреса!");
                editEmail.requestFocus();
                return;
            }
        }

        if(Password.equals("")) {
            editPassword.setError("Задолжително поле!");
            editPassword.requestFocus();
            return;
        } else {
            if(Password.length() < 6) {
                editPassword.setError("Внесете повеќе од 6 карактери!");
                editPassword.requestFocus();
                return;
            }
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                    String uid = user.getUid();

                    reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User korisnik = snapshot.getValue(User.class);

                            if(korisnik != null) {
                                String tip = korisnik.getTipUser();
                                if(tip.equals("Firma")) {
                                    startActivity(intentFirma);
                                } else if(tip.equals("Kupuvac")) {
                                    startActivity(intentKupuvac);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MainActivity.this, "Настана некоја грешка!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Неуспешна најава.Погрешен е-маил или лозинка!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}