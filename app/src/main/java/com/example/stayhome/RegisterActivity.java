package com.example.stayhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.stayhome.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private Spinner spinnerTipFirma, spinnerRabotnoVreme, spinnerOsptini;
    private EditText editIme, editTelefon, editEmail, editPassword, editPassword2;
    private RadioGroup radioGroup;
    private TextView txtSaatOd, txtSaatDo, txtRabotnoVreme, txtOpstini;
    private ProgressBar progressBar;

    private int saat, minuti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editIme = (EditText) findViewById(R.id.editRegisterIme);
        editTelefon = (EditText) findViewById(R.id.editRegisterTelefon);
        editEmail = (EditText) findViewById(R.id.editRegisterEmail);
        editPassword = (EditText) findViewById(R.id.editRegisterPassword);
        editPassword2 = (EditText) findViewById(R.id.editRegisterPassword2);

        txtRabotnoVreme = (TextView) findViewById(R.id.txtRabotnoVreme);
        txtSaatOd = (TextView) findViewById(R.id.txtSaatOd);
        txtSaatDo = (TextView) findViewById(R.id.txtSaatDo);
        txtOpstini = (TextView) findViewById(R.id.txtOpstina);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroupTip);

        spinnerTipFirma = (Spinner) findViewById(R.id.spinnerTipFirma);
        spinnerRabotnoVreme = (Spinner) findViewById(R.id.spinnerRabotniDenovi);
        spinnerOsptini = (Spinner) findViewById(R.id.spinnerOpstini);

        spinnerTipFirma.setVisibility(View.INVISIBLE);
        spinnerRabotnoVreme.setVisibility(View.INVISIBLE);
        spinnerOsptini.setVisibility(View.INVISIBLE);
        txtRabotnoVreme.setVisibility(View.INVISIBLE);
        txtSaatOd.setVisibility(View.INVISIBLE);
        txtSaatDo.setVisibility(View.INVISIBLE);
        txtOpstini.setVisibility(View.INVISIBLE);

        progressBar = findViewById(R.id.progressBarRegister);
        progressBar.setVisibility(View.INVISIBLE);
    }


    public void IzberiTipKorisnik(View view) {

        int ID = radioGroup.getCheckedRadioButtonId();

        if(R.id.radioFirma == ID) {
            spinnerTipFirma.setVisibility(View.VISIBLE);
            spinnerRabotnoVreme.setVisibility(View.VISIBLE);
            spinnerOsptini.setVisibility(View.VISIBLE);
            txtRabotnoVreme.setVisibility(View.VISIBLE);
            txtSaatOd.setVisibility(View.VISIBLE);
            txtSaatDo.setVisibility(View.VISIBLE);
            txtOpstini.setVisibility(View.VISIBLE);
            editIme.setHint("Име на фирмата");
        } else {
            spinnerTipFirma.setVisibility(View.INVISIBLE);
            spinnerRabotnoVreme.setVisibility(View.INVISIBLE);
            spinnerOsptini.setVisibility(View.INVISIBLE);
            txtRabotnoVreme.setVisibility(View.INVISIBLE);
            txtSaatOd.setVisibility(View.INVISIBLE);
            txtSaatDo.setVisibility(View.INVISIBLE);
            txtOpstini.setVisibility(View.INVISIBLE);
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
        String Opstina = spinnerOsptini.getSelectedItem().toString();
        String RabotniDenovi = spinnerRabotnoVreme.getSelectedItem().toString();
        String VremeOd = txtSaatOd.getText().toString().trim();
        String VremeDo = txtSaatDo.getText().toString().trim();
        String TipUser = "Kupuvac";

        int ID = radioGroup.getCheckedRadioButtonId();

        if(R.id.radioFirma == ID) {
            TipUser = "Firma";

            if(VremeOd.equals("")) {
                txtSaatOd.setError("Задолжително поле!");
                txtSaatOd.requestFocus();
                return;
            } else {
                txtSaatOd.setError(null);
            }

            if(VremeDo.equals("")) {
                txtSaatDo.setError("Задолжително поле!");
                txtSaatDo.requestFocus();
                return;
            } else {
                txtSaatDo.setError(null);
            }
        } else if(R.id.radioDostavuvac == ID) {
            TipUser = "Dostavuvac";
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

        String finalTipUser = TipUser;
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {

                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("123","onFailure: Email not sent: " + e.getMessage());
                        }
                    });


                    User user;
                    if(finalTipUser.equals("Firma")) {
                        user = new User(Ime, Telefon,"", Email, finalTipUser, TipFirma, 0, 0, 0, 0, RabotniDenovi, VremeOd, VremeDo, "", Opstina);
                    } else if(finalTipUser.equals("Kupuvac")){
                        user = new User(Ime, Telefon, Email, finalTipUser);
                    } else {
                        ArrayList<String> lista = new ArrayList<String>();
                        user = new User(Ime, Telefon, Email, finalTipUser, lista);
                    }

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task1) {
                                    if(task1.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Успешно се регистриравте!", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                        progressBar.setVisibility(View.INVISIBLE);
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Неуспешна регистрација.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "Постои корисник со оваа е-маил адреса!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void SelectTime(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(RegisterActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view1, int hourOfDay, int minute) {

                saat = hourOfDay;
                minuti = minute;

                String vreme = saat + ":" + minuti;
                SimpleDateFormat format24 = new SimpleDateFormat("HH:mm");
                try {
                    Date date = format24.parse(vreme);

                    if(view.getId() == txtSaatOd.getId()) {
                        txtSaatOd.setText(format24.format(date));
                    } else if (view.getId() == txtSaatDo.getId()) {
                        txtSaatDo.setText(format24.format(date));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },24,0,true);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.updateTime(saat, minuti);
        timePickerDialog.show();
    }
}