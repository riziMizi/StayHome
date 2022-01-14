package com.example.stayhome;

import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class myAdapterMeni extends RecyclerView.Adapter<myAdapterMeni.ViewHolder>{

    private List<Meni> myList;
    private int rowLayout;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtArtikl, txtArtiklSostav, txtArtiklCena, txtDeleteArtikl;


        public ViewHolder(View itemView) {
            super(itemView);
            txtArtikl = (TextView)itemView.findViewById(R.id.rwtxtMeniArtikl);
            txtArtiklSostav = (TextView) itemView.findViewById(R.id.rwtxtMeniArtiklSostav);
            txtArtiklCena = (TextView) itemView.findViewById(R.id.rwtxtMeniArtiklCena);
            txtDeleteArtikl = (TextView) itemView.findViewById(R.id.rwDelete);

            txtDeleteArtikl.setVisibility(View.INVISIBLE);
            StaviDeleteVisible(txtDeleteArtikl);

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
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Meni meni = myList.get(i);

        viewHolder.txtArtikl.setText(meni.getArtikl());
        viewHolder.txtArtiklSostav.setText("Состав:\n" + meni.getSostavArtikl());
        viewHolder.txtArtiklCena.setText("Цена:  " + String.valueOf(meni.getCena() + " ден."));

        viewHolder.txtDeleteArtikl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Избриши артикл");
                builder.setMessage("Дали сигурно сакате да го избришете овој артикл од менито?");

                builder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Да</font>"), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Meni").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        databaseReference.child(meni.getArtiklId()).removeValue();
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

    private void StaviDeleteVisible(TextView textView) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User korisnik = snapshot.getValue(User.class);
                if(korisnik.getPostoiMeni() == 1 || korisnik.getPostoiMeni() == 2) {
                    textView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Настана некоја грешка!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
