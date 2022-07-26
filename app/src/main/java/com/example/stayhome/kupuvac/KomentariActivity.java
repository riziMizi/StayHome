package com.example.stayhome.kupuvac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayhome.MainActivity;
import com.example.stayhome.R;
import com.example.stayhome.classes.Komentar;
import com.example.stayhome.classes.User;
import com.example.stayhome.myAdapterKomentari;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KomentariActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private myAdapterKomentari mAdapter;

    private List<Komentar> list = new ArrayList<>();

    private String FirmaId, FirmaIme;

    private TextView txtImeFirma, txtKomentiraj;

    String ImeKupuvac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komentari);

        Intent intent = getIntent();
        FirmaId = intent.getStringExtra("FirmaId");
        FirmaIme = intent.getStringExtra("FirmaIme");

        txtImeFirma = findViewById(R.id.txtKomentariNaslov);
        txtKomentiraj = findViewById(R.id.txtKomentiraj);
        txtImeFirma.setText(FirmaIme);

        txtKomentiraj.setPaintFlags(txtKomentiraj.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        NapraviLista();
        ZemiImeKupuvac();

        mRecyclerView = (RecyclerView) findViewById(R.id.listKomentariKupuvac);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new myAdapterKomentari(list, R.layout.adapter_komentari, this);
        mRecyclerView.setAdapter(mAdapter);

        txtKomentiraj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(KomentariActivity.this);

                alert.setTitle("Коментирај");

                LinearLayout layout = new LinearLayout(KomentariActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText komentar = new EditText(KomentariActivity.this);
                komentar.setHint("Внесете коментар");
                layout.addView(komentar);

                alert.setView(layout);

                alert.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Поднеси</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(komentar.getText().toString().equals("")){
                            Toast.makeText(KomentariActivity.this, "Не внесовте никаков коментар!", Toast.LENGTH_SHORT).show();
                        } else {
                            Komentar kom = new Komentar(ImeKupuvac, komentar.getText().toString().trim(), FirmaId);
                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Komentari");
                            databaseReference1.child(UUID.randomUUID().toString()).setValue(kom).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(KomentariActivity.this, "Ви благодариме на коментарот!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(KomentariActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton(Html.fromHtml("<font color='#FFFFFF'>Исклучи</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

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
                        startActivity(new Intent(KomentariActivity.this, MainActivity.class));
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

    private void NapraviLista() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Komentari");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if(snapshot.exists()) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Komentar komentar = dataSnapshot.getValue(Komentar.class);
                        if(komentar.getFirmaId().equals(FirmaId)) {
                            list.add(komentar);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(KomentariActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ZemiImeKupuvac() {
        ImeKupuvac = "";
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                ImeKupuvac = user.getIme();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(KomentariActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}