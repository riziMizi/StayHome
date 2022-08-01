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
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.stayhome.MainActivity;
import com.example.stayhome.R;
import com.example.stayhome.classes.Komentar;
import com.example.stayhome.classes.User;
import com.example.stayhome.myAdapterKomentari;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.PKCS12Attribute;
import java.util.ArrayList;
import java.util.List;

public class ProfilFirmaActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private myAdapterKomentari mAdapter;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private List<Komentar> list = new ArrayList<>();

    private RatingBar ratingBar;
    private ProgressBar progressBar;

    private EditText editTextFirmaEmail, editTextFirmaTelefon, editTextFirmaTelefon2;
    private TextView txtChooseImage, txtSaveEdit, txtVerify, txtVerified;

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

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        editTextFirmaEmail = findViewById(R.id.editTextFirmaEmail);
        editTextFirmaTelefon = findViewById(R.id.editTextFirmaTelefon);
        editTextFirmaTelefon2 = findViewById(R.id.editTextFirmaTelefon2);

        txtChooseImage = findViewById(R.id.txtChooseImage);
        txtChooseImage.setVisibility(View.INVISIBLE);
        txtSaveEdit = findViewById(R.id.txtSaveEdit);
        txtSaveEdit.setVisibility(View.INVISIBLE);
        txtVerify = findViewById(R.id.txtNotVerified);
        txtVerify.setEnabled(false);
        txtVerified = findViewById(R.id.txtVerified);


        editTextFirmaEmail.setEnabled(false);
        editTextFirmaTelefon.setEnabled(false);
        editTextFirmaTelefon2.setEnabled(false);

        ratingBar = findViewById(R.id.ratinBarFirma);

        imageViewFirmaLogo = findViewById(R.id.imageViewFirmaLogo);

        NapraviLista();
        PostaviProfil();
        CheckIfEmailIsVerified();

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
                if(!user.getFirmaLogo().equals("")) {
                    Glide.with(ProfilFirmaActivity.this).load(user.getFirmaLogo()).into(imageViewFirmaLogo);
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

    private void UploadImageToFirebase(Uri uri) {
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtention(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").
                                child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                String Telefon = editTextFirmaTelefon.getText().toString().trim();
                                String Telefon2 = editTextFirmaTelefon2.getText().toString().trim();
                                user.setTelefon(Telefon);
                                user.setTelefon2(Telefon2);
                                user.setFirmaLogo(uri.toString());
                                snapshot.getRef().setValue(user);
                                Toast.makeText(ProfilFirmaActivity.this, "Успешно направивте промена!", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(ProfilFirmaActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ProfilFirmaActivity.this, "Настана некоја грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void Edit(View view) {
        editTextFirmaTelefon.setEnabled(true);
        editTextFirmaTelefon2.setEnabled(true);
        txtVerify.setEnabled(true);
        txtChooseImage.setVisibility(View.VISIBLE);
        txtSaveEdit.setVisibility(View.VISIBLE);
        txtVerify.setPaintFlags(txtVerify.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
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

        if(imageUri != null) {
            UploadImageToFirebase(imageUri);
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    String Telefon = editTextFirmaTelefon.getText().toString().trim();
                    String Telefon2 = editTextFirmaTelefon2.getText().toString().trim();
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
        }

        txtSaveEdit.setVisibility(View.INVISIBLE);
        txtChooseImage.setVisibility(View.INVISIBLE);
        editTextFirmaTelefon.setEnabled(false);
        editTextFirmaTelefon2.setEnabled(false);
        txtVerify.setEnabled(false);
        txtVerify.setPaintFlags(0);
        imageUri = null;

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

    public void VerifyEmail(View view) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                txtVerify.setEnabled(false);
                txtVerify.setPaintFlags(0);
                Toast.makeText(ProfilFirmaActivity.this, "Испратен е линк за верифиакција на вашата е-маил адреса!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("123","onFailure: Email not sent: " + e.getMessage());
            }
        });
    }

    private void CheckIfEmailIsVerified() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser.isEmailVerified()) {
            txtVerify.setVisibility(View.INVISIBLE);

        } else {
            txtVerified.setVisibility(View.INVISIBLE);
        }
    }
}