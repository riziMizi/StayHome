package com.example.stayhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Trace;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NarackaActivity extends AppCompatActivity {

    private TextView txtIznosNaracka, txtNaracka, txtLokacija, txtIzberiLokacija;
    private EditText editZabeleska;
    private Button buttonNaracaj;

    private int Iznos;
    private String Naracka;

    private String FirmaId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naracka);

        Intent intent = getIntent();
        FirmaId = intent.getStringExtra("FirmaId");

        Iznos = 0;
        Naracka = "";

        buttonNaracaj = (Button) findViewById(R.id.buttonNaracaj);

        txtNaracka = (TextView) findViewById(R.id.txtVasaNaracka);
        txtIznosNaracka = (TextView) findViewById(R.id.txtIznosNaracka);
        txtLokacija = (TextView) findViewById(R.id.txtLokacija);
        txtIzberiLokacija = (TextView) findViewById(R.id.txtIzborLokacija);

        editZabeleska = (EditText) findViewById(R.id.editZabeleska);

        buttonNaracaj.setVisibility(View.INVISIBLE);
        txtLokacija.setVisibility(View.INVISIBLE);
        txtIzberiLokacija.setVisibility(View.INVISIBLE);
        editZabeleska.setVisibility(View.INVISIBLE);

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

    private void PostaviNaracka() {
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Naracki")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Naracka = "";
                Iznos = 0;
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Meni meni = dataSnapshot.getValue(Meni.class);
                        Naracka += String.valueOf(meni.getKolicina()) + "  " + meni.getArtikl() + "\n";
                        Iznos += meni.getKolicina() * meni.getCena();
                    }
                    txtNaracka.setText(Naracka);
                    txtIznosNaracka.setText("Вкупен износ: " + String.valueOf(Iznos) + " ден.");
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
    }
}