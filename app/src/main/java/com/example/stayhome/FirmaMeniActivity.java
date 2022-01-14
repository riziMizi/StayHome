package com.example.stayhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FirmaMeniActivity extends AppCompatActivity {

    private RecyclerView mRecyclerViewIspratiMeni;
    private myAdapterMeni mAdapterIspratimeni;

    private RecyclerView mRecyclerViewMeni;
    private myAdapterMeni mAdapterMeni;

    private ConstraintLayout constraintLayoutDodadiMeni, constraintLayoutMeni;

    private EditText editTextArtikl, editTextArtiklSostav, editTextArtiklCena;

    private TextView txtNepotvrdenoMeni;

    private Button buttonIspratiMeni, buttonIspratiMeniPovtorno;

    private List<Meni> listaIspratiMeni = new ArrayList<>();
    private List<Meni> listaMeni = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProveriMeni();
        setContentView(R.layout.activity_firma_meni);

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        buttonIspratiMeni = (Button) findViewById(R.id.buttonIspratiMeni);
        buttonIspratiMeni.setVisibility(View.INVISIBLE);

        buttonIspratiMeniPovtorno = (Button) findViewById(R.id.buttonIspratiMeniPovtorno);
        buttonIspratiMeniPovtorno.setVisibility(View.INVISIBLE);

        txtNepotvrdenoMeni = (TextView) findViewById(R.id.txtNepotvrdenoMeni);

        editTextArtikl = (EditText) findViewById(R.id.editMeniArtikl);
        editTextArtiklSostav = (EditText) findViewById(R.id.editMeniArtiklSostav);
        editTextArtiklCena = (EditText) findViewById(R.id.editMeniArtiklCena);

        constraintLayoutDodadiMeni = (ConstraintLayout) findViewById(R.id.layoutDodadiMeni);
        constraintLayoutMeni = (ConstraintLayout) findViewById(R.id.layoutMeni);

        mRecyclerViewIspratiMeni = (RecyclerView) findViewById(R.id.listNapraviMeni);
        mRecyclerViewIspratiMeni.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewIspratiMeni.setItemAnimator(new DefaultItemAnimator());
        mAdapterIspratimeni = new myAdapterMeni(listaIspratiMeni, R.layout.adapter_meni, this);
        mRecyclerViewIspratiMeni.setAdapter(mAdapterIspratimeni);

        mRecyclerViewMeni = (RecyclerView) findViewById(R.id.listMeni);
        mRecyclerViewMeni.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewMeni.setItemAnimator(new DefaultItemAnimator());
        mAdapterMeni = new myAdapterMeni(listaMeni, R.layout.adapter_meni, this);
        mRecyclerViewMeni.setAdapter(mAdapterMeni);

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
                        startActivity(new Intent(FirmaMeniActivity.this, MainActivity.class));
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
        String Artikl = editTextArtikl.getText().toString().trim();
        String ArtiklSostav = editTextArtiklSostav.getText().toString().trim();
        String artiklCena = editTextArtiklCena.getText().toString().trim();
        int ArtiklCena = 0;

        if(Artikl.equals("")) {
            editTextArtikl.setError("Задолжително поле!");
            editTextArtikl.requestFocus();
            return;
        }

        if(ArtiklSostav.equals("")) {
            editTextArtiklSostav.setError("Задолжително поле!");
            editTextArtiklSostav.requestFocus();
            return;
        }

        if(artiklCena.equals("")) {
            editTextArtiklCena.setError("Задолжително поле!");
            editTextArtiklCena.requestFocus();
            return;
        }

        try {
            ArtiklCena = Integer.parseInt(artiklCena);

        } catch (Exception e) {
            editTextArtiklCena.setError("Ова поле мора да содржи број!");
            editTextArtiklCena.requestFocus();
            return;
        }

        Meni meni = new Meni(Artikl, ArtiklSostav, ArtiklCena);
        listaIspratiMeni.add(meni);

        mAdapterIspratimeni.notifyDataSetChanged();

        IsprazniPolinja();
        buttonIspratiMeni.setVisibility(View.VISIBLE);

    }

    private void IsprazniPolinja() {
        editTextArtikl.setText("");
        editTextArtiklSostav.setText("");
        editTextArtiklCena.setText("");
    }


    public void IspratiMeni(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Испрати мени");
        builder.setMessage("Дали сигурно сакате да го испратите менито до админот?");

        builder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Да</font>"), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                for(Meni meni : listaIspratiMeni) {
                    FirebaseDatabase.getInstance().getReference("Meni")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(UUID.randomUUID().toString()).setValue(meni).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(!task.isSuccessful()) {
                                Toast.makeText(FirmaMeniActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        }
                    });
                }
                Toast.makeText(FirmaMeniActivity.this, "Успешно го ипсративте вашето мени.", Toast.LENGTH_LONG).show();
                listaIspratiMeni.clear();
                Map<String, Object> map = new HashMap();
                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users");
                map.put(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/postoiMeni", 1);

                firebaseDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()) {
                            Toast.makeText(FirmaMeniActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
                Intent intent = new Intent(FirmaMeniActivity.this, FirmaMeniActivity.class);
                startActivity(intent);
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
    }

    private void ProveriMeni() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User korisnik = snapshot.getValue(User.class);
                if(korisnik.getPostoiMeni() == 0) {
                    constraintLayoutMeni.setVisibility(View.INVISIBLE);
                } else {
                    constraintLayoutDodadiMeni.setVisibility(View.INVISIBLE);
                    NapraviListaMeni();
                    if(korisnik.getOdobrenoOdAdmin() == 0 || korisnik.getOdobrenoOdAdmin() == 2) {
                        txtNepotvrdenoMeni.setVisibility(View.VISIBLE);
                        txtNepotvrdenoMeni.setText("Непотврдено!!");
                        if(korisnik.getOdobrenoOdAdmin() == 2) {
                            txtNepotvrdenoMeni.setText("Одбиено. Испратете ново мени!!");
                            buttonIspratiMeniPovtorno.setVisibility(View.VISIBLE);
                        }

                    } else {
                        txtNepotvrdenoMeni.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FirmaMeniActivity.this, "Настана некоја грешка!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void IspratiMeniPovtorno(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Испрати мени");
        builder.setMessage("Дали сигурно сакате да го испратите менито до админот?");

        builder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Да</font>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Map<String, Object> map = new HashMap();
                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users");
                map.put(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/odobrenoOdAdmin", 0);
                firebaseDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(FirmaMeniActivity.this, "Успешно го ипсративте вашето мени.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(FirmaMeniActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
                Intent intent = new Intent(FirmaMeniActivity.this, FirmaMeniActivity.class);
                startActivity(intent);
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
    }

    public void DodadiArtiklMeni(View view) {
        Intent intent = new Intent(this, DodadiArtiklActivity.class);
        startActivity(intent);
    }


    private void NapraviListaMeni() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Meni")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaMeni.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Meni meni = dataSnapshot.getValue(Meni.class);
                    meni.setArtiklId(dataSnapshot.getKey());
                    listaMeni.add(meni);
                }
                mAdapterMeni.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FirmaMeniActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}