package com.example.stayhome.firma;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayhome.MainActivity;
import com.example.stayhome.R;
import com.example.stayhome.classes.Meni;
import com.example.stayhome.classes.User;
import com.example.stayhome.myAdapterMeni;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private ConstraintLayout constraintLayoutDodadiMeni, constraintLayoutMeni;

    private EditText editTextArtikl, editTextArtiklSostav, editTextArtiklCena;

    private TextView txtNepotvrdenoMeni, txtError;

    private Button buttonIspratiMeni, buttonIspratiMeniPovtorno;

    private List<Meni> listaIspratiMeni = new ArrayList<>();
    private List<Meni> listaMeni = new ArrayList<>();

    private ImageView imageView;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private Uri imageUri;

    private ProgressBar progressBar;

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

        progressBar = findViewById(R.id.progressBarPredlogMeni);
        progressBar.setVisibility(View.INVISIBLE);

        imageView = findViewById(R.id.imageViewPredlogMeni);

        buttonIspratiMeni = (Button) findViewById(R.id.buttonIspratiMeni);
        buttonIspratiMeni.setVisibility(View.INVISIBLE);

        buttonIspratiMeniPovtorno = (Button) findViewById(R.id.buttonIspratiMeniPovtorno);
        buttonIspratiMeniPovtorno.setVisibility(View.INVISIBLE);

        txtNepotvrdenoMeni = (TextView) findViewById(R.id.txtNepotvrdenoMeni);
        txtError = (TextView) findViewById(R.id.txtError);
        txtError.setVisibility(View.INVISIBLE);

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

        if(imageUri == null) {
            txtError.setVisibility(View.VISIBLE);
            return;
        }

        Meni meni = new Meni(Artikl, ArtiklSostav, ArtiklCena, imageUri.toString(), imageUri);
        listaIspratiMeni.add(meni);

        mAdapterIspratimeni.notifyDataSetChanged();

        IsprazniPolinja();
        buttonIspratiMeni.setVisibility(View.VISIBLE);
        txtError.setVisibility(View.INVISIBLE);

    }

    private void IsprazniPolinja() {
        editTextArtikl.setText("");
        editTextArtiklSostav.setText("");
        editTextArtiklCena.setText("");
        imageUri = null;
        String uri = "@drawable/ic_add_photo";
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);
        imageView.setImageDrawable(res);
    }


    public void IspratiMeni(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Испрати мени");
        builder.setMessage("Дали сигурно сакате да го испратите менито до админот?");

        builder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Да</font>"), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                for(Meni meni : listaIspratiMeni) {
                    StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtention(meni.getSlikaUri()));
                    fileRef.putFile(meni.getSlikaUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Meni meni1 = new Meni(meni.getArtikl(), meni.getSostavArtikl(), meni.getCena(), meni.getSlika());
                                    FirebaseDatabase.getInstance().getReference("Meni")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(UUID.randomUUID().toString()).
                                            setValue(meni1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(!task.isSuccessful()) {
                                                Toast.makeText(FirmaMeniActivity.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(FirmaMeniActivity.this, "Настана некоја грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
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