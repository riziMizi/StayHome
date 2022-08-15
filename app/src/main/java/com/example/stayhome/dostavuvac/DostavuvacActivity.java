package com.example.stayhome.dostavuvac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.stayhome.MainActivity;
import com.example.stayhome.R;
import com.example.stayhome.classes.Naracka;
import com.example.stayhome.classes.User;
import com.example.stayhome.firma.FirmaActivity;
import com.example.stayhome.firma.ProfilFirmaActivity;
import com.example.stayhome.kupuvac.KupuvacActivity;
import com.example.stayhome.kupuvac.KupuvacAktivniNaracki;
import com.example.stayhome.kupuvac.KupuvacProfilActivity;
import com.example.stayhome.kupuvac.myAdapterFirmi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DostavuvacActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private myAdapterDostavaNaracki mAdapter;

    private List<Naracka> list = new ArrayList<>();
    private ArrayList<String> listaOpstini = new ArrayList<>();

    FirebaseUser currentUser =FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dostavuvac);

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        ZemiOpstini();
        NapraviLista();

        mRecyclerView = (RecyclerView) findViewById(R.id.listNarackiDostava);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new myAdapterDostavaNaracki(list, R.layout.adapter_dostava_naracki, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.actionbar_firma, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_profile:
                startActivity(new Intent(DostavuvacActivity.this, KupuvacProfilActivity.class));
                return true;
            case R.id.action_signout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Одјави се");
                builder.setMessage("Дали сте сигурни дека сакате да се одјавите?");

                builder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Да</font>"), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(DostavuvacActivity.this, MainActivity.class));
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

    public void OtvoriOpstini(View view) {
        startActivity(new Intent(DostavuvacActivity.this, OpstiniDostavaActivity.class));
    }

    private void NapraviLista() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AktivniNaracki");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if(snapshot.exists()) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Naracka naracka = dataSnapshot.getValue(Naracka.class);
                        if(listaOpstini != null) {
                            if(listaOpstini.contains(naracka.getOpstina()) && naracka.getPrifatenaNaracka().equals("За потврда")) {
                                naracka.setNarackaId(dataSnapshot.getKey());
                                list.add(naracka);
                            }
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DostavuvacActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ZemiOpstini() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                listaOpstini = user.getOpstiniDostavuvac();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DostavuvacActivity.this, "Настана грешка. Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void OtvoriNarackiZaDostava(View view) {
        startActivity(new Intent(DostavuvacActivity.this, NarackiDostavaActivity.class));

    }
}