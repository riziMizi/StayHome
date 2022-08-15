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
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayhome.MainActivity;
import com.example.stayhome.R;
import com.example.stayhome.classes.User;
import com.example.stayhome.firma.FirmaActivity;
import com.example.stayhome.firma.FirmaMeniActivity;
import com.example.stayhome.firma.ProfilFirmaActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class KupuvacActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private myAdapterFirmi mAdapter;

     private TextView txtViewPopust;

    private List<User> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kupuvac);

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));


        txtViewPopust = (TextView) findViewById(R.id.txtPopust);
        txtViewPopust.setVisibility(View.INVISIBLE);

        ProveriTipKorisnik();
        NapraviLista();
        IzbrisiNaracki();

        mRecyclerView = (RecyclerView) findViewById(R.id.listFirmi);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new myAdapterFirmi(list, R.layout.adapter_firmi, this);
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
                startActivity(new Intent(KupuvacActivity.this, KupuvacProfilActivity.class));
                return true;
            case R.id.action_signout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Одјави се");
                builder.setMessage("Дали сте сигурни дека сакате да се одјавите?");

                builder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Да</font>"), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(KupuvacActivity.this, MainActivity.class));
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
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user1.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setFirmaId(dataSnapshot.getKey());
                    if(user.getTipUser().equals("Firma") && user.getOdobrenoOdAdmin() == 1) {
                        if(!uid.equals(user.getFirmaId())) {
                            list.add(user);
                        }
                    }
                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(KupuvacActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void IzbrisiNaracki() {
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Naracki");
        firebaseDatabase.removeValue();
    }

    public void AktivniNaracki(View view) {
        Intent intent = new Intent(this, KupuvacAktivniNaracki.class);
        startActivity(intent);
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
                    txtViewPopust.setVisibility(View.VISIBLE);
                    ActionBar actionBar = getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setDisplayHomeAsUpEnabled(true);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(KupuvacActivity.this, "Настана некоја грешка!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}