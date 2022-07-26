package com.example.stayhome.firma;

import androidx.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayhome.MainActivity;
import com.example.stayhome.R;
import com.example.stayhome.classes.User;
import com.example.stayhome.firma.naracki.FirmaNarackiActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirmaActivity extends AppCompatActivity {

    private Button buttonMeni, buttonNaracki, buttonKomentari;
    private TextView txtPrvaNajava;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProveriFirmaMeni();
        setContentView(R.layout.activity_firma);
        buttonMeni = (Button) findViewById(R.id.buttonMeni);
        buttonNaracki = (Button) findViewById(R.id.buttonNaracki);
        buttonKomentari = (Button) findViewById(R.id.buttonKomentari);

        txtPrvaNajava = (TextView) findViewById(R.id.txtPrvaNajava);

        buttonKomentari.setVisibility(View.INVISIBLE);
        buttonNaracki.setVisibility(View.INVISIBLE);

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));

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
                        startActivity(new Intent(FirmaActivity.this, MainActivity.class));
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

    private void ProveriFirmaMeni() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User korisnik = snapshot.getValue(User.class);
                if(korisnik.getOdobrenoOdAdmin() == 1) {
                    buttonKomentari.setVisibility(View.VISIBLE);
                    buttonNaracki.setVisibility(View.VISIBLE);
                    txtPrvaNajava.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FirmaActivity.this, "Настана некоја грешка!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void OtvoriMeni(View view) {
        Intent intent = new Intent(this, FirmaMeniActivity.class);
        startActivity(intent);
    }

    public void OtvoriNaracki(View view) {
        Intent intent = new Intent(this, FirmaNarackiActivity.class);
        startActivity(intent);
    }

    public void OtvoriKomentari(View view) {
        Intent intent = new Intent(this, KomentariFirmaActivity.class);
        startActivity(intent);
    }
}