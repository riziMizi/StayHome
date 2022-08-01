package com.example.stayhome.kupuvac.naracka;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.stayhome.R;
import com.example.stayhome.classes.Meni;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class myAdapterNarackaMeni extends RecyclerView.Adapter<myAdapterNarackaMeni.ViewHolder>{

    private List<Meni> myList;
    private int rowLayout;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtArtikl, txtArtiklSostav, txtArtiklCena, txtKolicina, txtDodadi, txtOdzemi;
        private ImageView imageView;


        public ViewHolder(View itemView) {
            super(itemView);
            txtArtikl = (TextView)itemView.findViewById(R.id.rw3txtMeniArtikl);
            txtArtiklSostav = (TextView) itemView.findViewById(R.id.rw3txtMeniArtiklSostav);
            txtArtiklCena = (TextView) itemView.findViewById(R.id.rw3txtMeniArtiklCena);
            txtKolicina = (TextView) itemView.findViewById(R.id.rw3txtKolicina);
            txtDodadi = (TextView) itemView.findViewById(R.id.rw3Dodadi);
            txtOdzemi = (TextView) itemView.findViewById(R.id.rw3Odzemi);
            imageView = (ImageView) itemView.findViewById(R.id.rw3imageViewKupuvacNaracka);
        }
    }


    public myAdapterNarackaMeni(List<Meni> myList, int rowLayout, Context context) {
        this.myList = myList;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(myAdapterNarackaMeni.ViewHolder viewHolder, int i) {
        Meni meni = myList.get(i);

        viewHolder.txtArtikl.setText(meni.getArtikl());
        viewHolder.txtArtiklSostav.setText("Состав:\n" + meni.getSostavArtikl());
        viewHolder.txtArtiklCena.setText("Цена:  " + String.valueOf(meni.getCena() + " ден."));

        if(!meni.getSlika().equals("")) {
            Glide.with(mContext).load(myList.get(i).getSlika()).into(viewHolder.imageView);
        }

        viewHolder.txtKolicina.setText(String.valueOf(meni.getKolicina()));

        viewHolder.txtDodadi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap();
                meni.setKolicina(meni.getKolicina() + 1);
                viewHolder.txtKolicina.setText(String.valueOf(meni.getKolicina()));
                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Naracki")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(meni.getArtiklId());

                firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            map.put("kolicina", meni.getKolicina());
                            firebaseDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()) {
                                        Toast.makeText(mContext, "Настана некоја грешка!!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            firebaseDatabase.setValue(meni).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()) {
                                        Toast.makeText(mContext, "Настана некоја грешка!!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(mContext, "Настана некоја грешка!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        viewHolder.txtOdzemi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int Kolicina = meni.getKolicina();
                if(Kolicina > 0) {
                    Map<String, Object> map = new HashMap();
                    meni.setKolicina(Kolicina - 1);
                    viewHolder.txtKolicina.setText(String.valueOf(meni.getKolicina()));
                    DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Naracki")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(meni.getArtiklId());
                    if(meni.getKolicina() == 0) {
                        firebaseDatabase.removeValue();
                    } else {
                        map.put("kolicina", meni.getKolicina());
                        firebaseDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(mContext, "Настана некоја грешка!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }

}
