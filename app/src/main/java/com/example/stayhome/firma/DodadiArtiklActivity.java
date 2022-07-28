package com.example.stayhome.firma;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayhome.MainActivity;
import com.example.stayhome.R;
import com.example.stayhome.classes.Meni;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class DodadiArtiklActivity extends AppCompatActivity {

    private EditText editDodadiArtikl, editDodadiArtiklSostav, editDodadiArtiklCena;
    private TextView txtError;

    private ImageView imageView;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private Uri imageUri;

    private ProgressBar progressBar;

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodadi_artikl);

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        txtError = (TextView) findViewById(R.id.txtErrorDodadiArtikl);
        txtError.setVisibility(View.INVISIBLE);

        progressBar = findViewById(R.id.progressBarDodadiArtikl);
        progressBar.setVisibility(View.INVISIBLE);

        imageView = findViewById(R.id.imageViewSlikaArtikl);

        editDodadiArtikl = (EditText) findViewById(R.id.editDodadiMeniArtikl);
        editDodadiArtiklSostav = (EditText) findViewById(R.id.editDodadiMeniArtiklSostav);
        editDodadiArtiklCena = (EditText) findViewById(R.id.editDodadiMeniArtiklCena);
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
                        startActivity(new Intent(DodadiArtiklActivity.this, MainActivity.class));
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
        String Artikl = editDodadiArtikl.getText().toString().trim();
        String ArtiklSostav = editDodadiArtiklSostav.getText().toString().trim();
        String artiklCena = editDodadiArtiklCena.getText().toString().trim();
        int ArtiklCena = 0;

        if(Artikl.equals("")) {
            editDodadiArtikl.setError("Задолжително поле!");
            editDodadiArtikl.requestFocus();
            return;
        }

        if(ArtiklSostav.equals("")) {
            editDodadiArtiklSostav.setError("Задолжително поле!");
            editDodadiArtiklSostav.requestFocus();
            return;
        }

        if(artiklCena.equals("")) {
            editDodadiArtiklCena.setError("Задолжително поле!");
            editDodadiArtiklCena.requestFocus();
            return;
        }

        try {
            ArtiklCena = Integer.parseInt(artiklCena);

        } catch (Exception e) {
            editDodadiArtiklCena.setError("Ова поле мора да содржи број!");
            editDodadiArtiklCena.requestFocus();
            return;
        }

        if(imageUri == null) {
            txtError.setVisibility(View.VISIBLE);
            return;
        }

        Meni meni = new Meni(Artikl, ArtiklSostav, ArtiklCena,  imageUri.toString(), imageUri);

        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtention(meni.getSlikaUri()));
        fileRef.putFile(meni.getSlikaUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Meni meni1 = new Meni(meni.getArtikl(), meni.getSostavArtikl(), meni.getCena(), meni.getSlika());
                        FirebaseDatabase.getInstance().getReference("Meni")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(UUID.randomUUID().toString())
                                .setValue(meni1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(DodadiArtiklActivity.this, "Успешно го дадодовте артиклот во вашето мени!", Toast.LENGTH_SHORT).show();
                                    IsprazniPolinja();
                                } else {
                                    Toast.makeText(DodadiArtiklActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                                }
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
                Toast.makeText(DodadiArtiklActivity.this, "Настана некоја грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });

        txtError.setVisibility(View.INVISIBLE);
    }

    private void IsprazniPolinja() {
        editDodadiArtikl.setText("");
        editDodadiArtiklSostav.setText("");
        editDodadiArtiklCena.setText("");
        imageUri = null;
        String uri = "@drawable/ic_add_photo";
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);
        imageView.setImageDrawable(res);
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

    public void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
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
            imageView.setImageURI(imageUri);
            txtError.setVisibility(View.INVISIBLE);
        }
    }
}