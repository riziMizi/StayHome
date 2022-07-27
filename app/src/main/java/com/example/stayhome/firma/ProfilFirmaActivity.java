package com.example.stayhome.firma;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayhome.MainActivity;
import com.example.stayhome.R;
import com.example.stayhome.classes.Komentar;
import com.example.stayhome.classes.User;
import com.example.stayhome.myAdapterKomentari;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PKCS12Attribute;
import java.util.ArrayList;
import java.util.List;

public class ProfilFirmaActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private myAdapterKomentari mAdapter;

    private List<Komentar> list = new ArrayList<>();

    private RatingBar ratingBar;

    private EditText editTextFirmaEmail, editTextFirmaTelefon, editTextFirmaTelefon2;
    private TextView txtChooseImage, txtSaveEdit;

    private ImageView imageViewFirmaLogo;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_firma);

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTextFirmaEmail = findViewById(R.id.editTextFirmaEmail);
        editTextFirmaTelefon = findViewById(R.id.editTextFirmaTelefon);
        editTextFirmaTelefon2 = findViewById(R.id.editTextFirmaTelefon2);

        txtChooseImage = findViewById(R.id.txtChooseImage);
        txtChooseImage.setVisibility(View.INVISIBLE);
        txtSaveEdit = findViewById(R.id.txtSaveEdit);
        txtSaveEdit.setVisibility(View.INVISIBLE);

        editTextFirmaEmail.setEnabled(false);
        editTextFirmaTelefon.setEnabled(false);
        editTextFirmaTelefon2.setEnabled(false);

        ratingBar = findViewById(R.id.ratinBarFirma);

        imageViewFirmaLogo = findViewById(R.id.imageViewFirmaLogo);

        NapraviLista();
        PostaviProfil();

        mRecyclerView = (RecyclerView) findViewById(R.id.listKomentariFirma);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new myAdapterKomentari(list, R.layout.adapter_komentari, this);
        mRecyclerView.setAdapter(mAdapter);
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
                        startActivity(new Intent(ProfilFirmaActivity.this, MainActivity.class));
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
                        if(komentar.getFirmaId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            list.add(komentar);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfilFirmaActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void PostaviProfil() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                int ZbirOceni = user.getZbirOceni();
                int VkupnoOceni = user.getVkupnoOceni();

                if(VkupnoOceni != 0) {
                    float Ocena = (float) ZbirOceni / VkupnoOceni;
                    ratingBar.setRating(Ocena);
                } else {
                    ratingBar.setRating(0);
                }
                editTextFirmaEmail.setText(user.getEmail());
                editTextFirmaTelefon.setText(user.getTelefon());
                editTextFirmaTelefon2.setText(user.getTelefon2());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfilFirmaActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Edit(View view) {
        editTextFirmaEmail.setEnabled(true);
        editTextFirmaTelefon.setEnabled(true);
        editTextFirmaTelefon2.setEnabled(true);
        txtChooseImage.setVisibility(View.VISIBLE);
        txtSaveEdit.setVisibility(View.VISIBLE);
    }

    public void ChooseImage(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String [] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_CODE);

            } else {
                pickImageFromGallery();
            }
        } else {
            pickImageFromGallery();
        }

    }

    public void SaveEdit(View view) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String Email = editTextFirmaEmail.getText().toString().trim();
                String Telefon = editTextFirmaTelefon.getText().toString().trim();
                String Telefon2 = editTextFirmaTelefon2.getText().toString().trim();
                user.setEmail(Email);
                user.setTelefon(Telefon);
                user.setTelefon2(Telefon2);
                snapshot.getRef().setValue(user);
                Toast.makeText(ProfilFirmaActivity.this, "Успешно направивте промена!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfilFirmaActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });

        txtSaveEdit.setVisibility(View.INVISIBLE);
        txtChooseImage.setVisibility(View.INVISIBLE);
        editTextFirmaEmail.setEnabled(false);
        editTextFirmaTelefon.setEnabled(false);
        editTextFirmaTelefon2.setEnabled(false);

    }

    public void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                } else {
                    Toast.makeText(this, "Недозволен пристап!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageUri = data.getData();
            imageViewFirmaLogo.setImageURI(imageUri);
        }
    }
}