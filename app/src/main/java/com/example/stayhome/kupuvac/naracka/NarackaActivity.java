package com.example.stayhome.kupuvac.naracka;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayhome.MainActivity;
import com.example.stayhome.R;
import com.example.stayhome.classes.Meni;
import com.example.stayhome.classes.Naracka;
import com.example.stayhome.classes.User;
import com.example.stayhome.googleMap.GoogleMapActivity;
import com.example.stayhome.kupuvac.KupuvacActivity;
import com.example.stayhome.kupuvac.KupuvacAktivniNaracki;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class NarackaActivity extends AppCompatActivity {

    private TextView txtIznosNaracka, txtNaracka, txtLokacija, txtIzberiLokacija, txtIznosNarackaSoPopust;
    private EditText editZabeleska;
    private Button buttonNaracaj;

    private int Iznos, IznosSoPopust;
    private String Naracka;

    private String FirmaId = "";

    private static final int REQ_CODE = 123;

    private double Lat,Log = 0;
    private String Address = "";

    private int ZnameTipKorisnik = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naracka);

        Intent intent = getIntent();
        FirmaId = intent.getStringExtra("FirmaId");

        Iznos = 0;
        IznosSoPopust = 0;

        Naracka = "";

        buttonNaracaj = (Button) findViewById(R.id.buttonNaracaj);

        txtNaracka = (TextView) findViewById(R.id.txtVasaNaracka);
        txtIznosNaracka = (TextView) findViewById(R.id.txtIznosNaracka);
        txtLokacija = (TextView) findViewById(R.id.txtLokacija);
        txtIzberiLokacija = (TextView) findViewById(R.id.txtIzborLokacija);
        txtIznosNarackaSoPopust = (TextView) findViewById(R.id.txtIznosNarackaSoPopust);

        editZabeleska = (EditText) findViewById(R.id.editZabeleska);

        buttonNaracaj.setVisibility(View.INVISIBLE);
        txtLokacija.setVisibility(View.INVISIBLE);
        txtIzberiLokacija.setVisibility(View.INVISIBLE);
        editZabeleska.setVisibility(View.INVISIBLE);

        ProveriTipKorisnik();
        PostaviNaracka();

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));

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
                        startActivity(new Intent(NarackaActivity.this, MainActivity.class));
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
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("FirmaId", FirmaId);
                setResult(RESULT_OK, intent);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if (requestCode == REQ_CODE) {
                Lat = data.getDoubleExtra("latitude", 0);
                Log = data.getDoubleExtra("longitude", 0);
                Address = data.getStringExtra("address");
                txtIzberiLokacija.setText(Address);
            }
        }
    }

    private void PostaviNaracka() {
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Naracki")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Naracka = "";
                Iznos = 0;
                IznosSoPopust = 0;
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Meni meni = dataSnapshot.getValue(Meni.class);
                        Naracka += String.valueOf(meni.getKolicina()) + "  " + meni.getArtikl() + "\n";
                        Iznos += meni.getKolicina() * meni.getCena();
                    }
                    txtNaracka.setText(Naracka);
                    txtIznosNaracka.setText("Вкупен износ: " + String.valueOf(Iznos) + " ден. ");
                    if(ZnameTipKorisnik == 1 && Iznos > 1000) {
                        IznosSoPopust =(int) (Iznos * 0.85);
                        txtIznosNarackaSoPopust.setText("/ " + String.valueOf(IznosSoPopust) + " ден.");
                    }
                }
                if(!txtNaracka.getText().toString().trim().equals("")) {
                    buttonNaracaj.setVisibility(View.VISIBLE);
                    txtLokacija.setVisibility(View.VISIBLE);
                    txtIzberiLokacija.setVisibility(View.VISIBLE);
                    editZabeleska.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NarackaActivity.this, "Настана некоја грешка!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void SelectLocation(View view) {
        Intent intent = new Intent(this, GoogleMapActivity.class);
        startActivityForResult(intent, REQ_CODE);
    }

    public void IspratiNaracka(View view) {
        String Adresa = txtIzberiLokacija.getText().toString().trim();
        String Zabeleska = editZabeleska.getText().toString().trim();
        String Naracka = txtNaracka.getText().toString().trim();
        SimpleDateFormat formatter= new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date(System.currentTimeMillis());
        String Datum = formatter.format(date);
        Intent intent = new Intent(this, KupuvacAktivniNaracki.class);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        if(Adresa.equals("")) {
            txtIzberiLokacija.setError("Задолжително поле!");
            txtIzberiLokacija.requestFocus();
            return;
        } else {
            txtIzberiLokacija.setError(null);
        }


        com.example.stayhome.classes.Naracka naracka = new Naracka(Adresa, Iznos, FirmaId, Zabeleska, Log, Lat, "За потврда", "", uid, Naracka, Datum);
        if(IznosSoPopust > 0) {
            naracka.setCena(IznosSoPopust);
        }

        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("AktivniNaracki")
                .child(UUID.randomUUID().toString());
        firebaseDatabase.setValue(naracka).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(NarackaActivity.this, "Успешно ја испративте нарачката!!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                } else {
                    Toast.makeText(NarackaActivity.this, "Настана некоја грешка!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ProveriTipKorisnik() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User korisnik = snapshot.getValue(User.class);
                if(korisnik.getTipUser().equals("Firma")) {
                    ZnameTipKorisnik = 1;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NarackaActivity.this, "Настана некоја грешка!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}