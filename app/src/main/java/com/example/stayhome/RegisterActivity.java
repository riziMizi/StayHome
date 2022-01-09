package com.example.stayhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Spinner spinnerTipFirma;
    private EditText editIme, editTelefon, editEmail, editPassword, editPassword2;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editIme = (EditText) findViewById(R.id.editRegisterIme);
        editTelefon = (EditText) findViewById(R.id.editRegisterTelefon);
        editEmail = (EditText) findViewById(R.id.editRegisterEmail);
        editPassword = (EditText) findViewById(R.id.editRegisterPassword);
        editPassword2 = (EditText) findViewById(R.id.editRegisterPassword2);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroupTip);

        spinnerTipFirma = (Spinner) findViewById(R.id.spinnerTipFirma);

        spinnerTipFirma.setVisibility(View.INVISIBLE);
    }


    public void IzberiTipKorisnik(View view) {

        int ID = radioGroup.getCheckedRadioButtonId();

        if(R.id.radioFirma == ID) {
            spinnerTipFirma.setVisibility(View.VISIBLE);
            editIme.setHint("Име на фирмата");
        } else {
            spinnerTipFirma.setVisibility(View.INVISIBLE);
            editIme.setHint("Име и презиме");
        }

    }

    public void Register(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        String Ime = editIme.getText().toString().trim();
        String Email = editEmail.getText().toString().trim();
        String Telefon = editTelefon.getText().toString().trim();
        String Password = editPassword.getText().toString().trim();
        String Password2 = editPassword2.getText().toString().trim();
        String TipFirma = spinnerTipFirma.getSelectedItem().toString();
        String TipUser = "Kupuvac";

        int ZnameDaliFirma = 0;

        int ID = radioGroup.getCheckedRadioButtonId();

        if(R.id.radioFirma == ID) {
            ZnameDaliFirma = 1;
            TipUser = "Firma";
        }


        if(Ime.equals("")) {
            editIme.setError("Задолжително поле!");
            editIme.requestFocus();
            return;
        }

        if(Telefon.equals("")) {
            editTelefon.setError("Задолжително поле!");
            editTelefon.requestFocus();
            return;
        } else {
            if(!Patterns.PHONE.matcher(Telefon).matches()) {
                editTelefon.setError("Внесете валиден телефонски број!");
                editTelefon.requestFocus();
                return;
            }
        }

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

        if(Password2.equals("")) {
            editPassword2.setError("Задолжително поле!");
            editPassword2.requestFocus();
            return;
        }

        if(!Password.equals(Password2)) {
            editPassword2.setError("Лозинктие не се поклопуваат!");
            editPassword2.requestFocus();
            return;
        }

        int finalZnameDaliFirma = ZnameDaliFirma;
        String finalTipUser = TipUser;
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    User user;
                    if(finalZnameDaliFirma == 1) {
                        user = new User(Ime, Telefon, Email, finalTipUser, TipFirma, 0);
                    } else {
                        user = new User(Ime, Telefon, Email, finalTipUser);
                    }

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task1) {
                                    if(task1.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Успешно се регистриравте!", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Неуспешна регистрација.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "Неуспешна регистрација.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}