package com.example.stayhome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stayhome.classes.Komentar;
import com.example.stayhome.classes.User;
import com.example.stayhome.firma.ProfilFirmaActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class myAdapterKomentari extends RecyclerView.Adapter<myAdapterKomentari.ViewHolder>{

    private List<Komentar> myList;
    private int rowLayout;
    private Context mContext;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtImeKupuvac, txtKomentar, txtDelete;


        public ViewHolder(View itemView) {
            super(itemView);
            txtImeKupuvac = (TextView) itemView.findViewById(R.id.rw6txtImeKupuvac);
            txtKomentar = (TextView) itemView.findViewById(R.id.rw6txtKomentar);
            txtDelete = (TextView) itemView.findViewById(R.id.rw6Delete);

            txtDelete.setVisibility(View.INVISIBLE);

        }
    }

    public myAdapterKomentari(List<Komentar> myList, int rowLayout, Context context) {
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
    public void onBindViewHolder(myAdapterKomentari.ViewHolder viewHolder, int i) {
        Komentar komentar = myList.get(i);

        if(uid.equals(komentar.getFirmaId())) {
            viewHolder.txtDelete.setVisibility(View.VISIBLE);
        }


        viewHolder.txtImeKupuvac.setText(komentar.getImeKupuvac());
        viewHolder.txtKomentar.setText(komentar.getKomentarKupuvac());

        viewHolder.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Избриши коментар");
                builder.setMessage("Дали сте сигурни дека сакате да го избришете овој коментар?");

                builder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Да</font>"), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Komentari");
                       databaseReference.child(komentar.getKomentarId()).removeValue();
                        Toast.makeText(mContext, "Успешно го избришавте коментарот!", Toast.LENGTH_SHORT).show();
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
}
