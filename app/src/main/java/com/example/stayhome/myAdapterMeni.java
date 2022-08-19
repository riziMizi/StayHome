package com.example.stayhome;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stayhome.classes.Meni;
import com.example.stayhome.classes.User;
import com.example.stayhome.firma.FirmaMeniActivity;
import com.example.stayhome.firma.ProfilFirmaActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.List;

public class myAdapterMeni extends RecyclerView.Adapter<myAdapterMeni.ViewHolder>{

    private List<Meni> myList;
    private int rowLayout;
    private Context mContext;
    private User userFirma;
    public ProgressBar progressBar;


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtDeleteArtikl, txtEditArtikl, txtSaveEdit;
        private EditText editArtikl, editArtiklSostav, editArtiklCena;
        private ImageView imageView;



        public ViewHolder(View itemView) {
            super(itemView);
            editArtikl = (EditText) itemView.findViewById(R.id.rweditMeniArtikl);
            editArtiklSostav = (EditText) itemView.findViewById(R.id.rweditMeniArtiklSostav);
            editArtiklCena = (EditText) itemView.findViewById(R.id.rweditMeniArtiklCena);
            txtDeleteArtikl = (TextView) itemView.findViewById(R.id.rwDelete);
            txtEditArtikl = (TextView) itemView.findViewById(R.id.rwEdit);
            txtSaveEdit = (TextView) itemView.findViewById(R.id.rwSaveEdit);
            imageView = (ImageView) itemView.findViewById(R.id.rwImageView);

            progressBar = itemView.findViewById(R.id.progressBarPredlogMeni);

            editArtikl.setEnabled(false);
            editArtiklSostav.setEnabled(false);
            editArtiklCena.setEnabled(false);

            txtDeleteArtikl.setVisibility(View.INVISIBLE);
            txtEditArtikl.setVisibility(View.INVISIBLE);
            txtSaveEdit.setVisibility(View.INVISIBLE);
            StaviDeleteVisible(txtDeleteArtikl, txtEditArtikl);
        }
    }


    public myAdapterMeni(List<Meni> myList, int rowLayout, Context context) {
        this.myList = myList;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
//        if(progressBar != null) {
//            progressBar.setVisibility(View.GONE);
//        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        Meni meni = myList.get(i);

        viewHolder.editArtikl.setText(meni.getArtikl());
        viewHolder.editArtiklSostav.setText(meni.getSostavArtikl());
        viewHolder.editArtiklCena.setText(String.valueOf(meni.getCena()));

        if(!meni.getSlika().equals("")) {
            Glide.with(mContext).load(myList.get(i).getSlika()).into(viewHolder.imageView);
        }

        viewHolder.txtEditArtikl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.editArtikl.setEnabled(true);
                viewHolder.editArtiklSostav.setEnabled(true);
                viewHolder.editArtiklCena.setEnabled(true);
                viewHolder.txtSaveEdit.setVisibility(View.VISIBLE);
            }
        });


        viewHolder.txtSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.editArtikl.setEnabled(false);
                viewHolder.editArtiklSostav.setEnabled(false);
                viewHolder.editArtiklCena.setEnabled(false);
                viewHolder.txtSaveEdit.setVisibility(View.INVISIBLE);

                String Artikl = viewHolder.editArtikl.getText().toString().trim();
                String ArtiklSostav = viewHolder.editArtiklSostav.getText().toString().trim();
                String ArtiklCena = viewHolder.editArtiklCena.getText().toString().trim();

                if(userFirma.getPostoiMeni() == 0) {
                    meni.setArtikl(Artikl);
                    meni.setSostavArtikl(ArtiklSostav);
                    meni.setCena(Integer.parseInt(ArtiklCena));
                    Toast.makeText(mContext, "Успешно направивте промена!", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                            child("Meni").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    databaseReference.child(meni.getArtiklId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Meni meni1 = snapshot.getValue(Meni.class);
                            meni1.setArtikl(Artikl);
                            meni1.setSostavArtikl(ArtiklSostav);
                            meni1.setCena(Integer.parseInt(ArtiklCena));
                            snapshot.getRef().setValue(meni1);
                            Toast.makeText(mContext, "Успешно направивте промена!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

        viewHolder.txtDeleteArtikl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.editArtikl.setEnabled(false);
                viewHolder.editArtiklSostav.setEnabled(false);
                viewHolder.editArtiklCena.setEnabled(false);
                viewHolder.txtSaveEdit.setVisibility(View.INVISIBLE);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Избриши артикл");
                builder.setMessage("Дали сигурно сакате да го избришете овој артикл од менито?");

                builder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Да</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(userFirma.getPostoiMeni() == 0) {
                            myList.remove(viewHolder.getAdapterPosition());
                            notifyDataSetChanged();
                        } else {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Meni").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            databaseReference.child(meni.getArtiklId()).removeValue();
                        }
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
            }
        });

    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }

    private void StaviDeleteVisible(TextView txtDelete, TextView txtEdit) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User korisnik = snapshot.getValue(User.class);
                if(korisnik.getTipUser().equals("Firma")) {
                    userFirma = korisnik;
                    txtDelete.setVisibility(View.VISIBLE);
                    txtEdit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Настана некоја грешка!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
