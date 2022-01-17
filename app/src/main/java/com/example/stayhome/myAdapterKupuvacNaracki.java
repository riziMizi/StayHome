package com.example.stayhome;

import android.content.Context;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class myAdapterKupuvacNaracki extends RecyclerView.Adapter<myAdapterKupuvacNaracki.ViewHolder>{

    private List<Naracka> myList;
    private int rowLayout;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtImeFirma, txtNaracka, txtIznos, txtPotvrdenaNaracka, txtDatum;


        public ViewHolder(View itemView) {
            super(itemView);
            txtImeFirma = (TextView)itemView.findViewById(R.id.rw4txtImeFirma);
            txtNaracka = (TextView) itemView.findViewById(R.id.rw4txtNaracka);
            txtIznos = (TextView) itemView.findViewById(R.id.rw4txtIznos);
            txtPotvrdenaNaracka = (TextView) itemView.findViewById(R.id.rw4txtPotvrdenaNaracka);
            txtDatum = (TextView) itemView.findViewById(R.id.rw4txtDatum);
        }
    }


    public myAdapterKupuvacNaracki(List<Naracka> myList, int rowLayout, Context context) {
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
    public void onBindViewHolder(myAdapterKupuvacNaracki.ViewHolder viewHolder, int i) {
        Naracka naracka = myList.get(i);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.getKey().equals(naracka.getFirmaId())) {
                        User korisnik = dataSnapshot.getValue(User.class);
                        viewHolder.txtImeFirma.setText(korisnik.getIme());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Настана некоја грешка!!", Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.txtNaracka.setText(naracka.getNarackaHrana());
        viewHolder.txtIznos.setText("Вкупен износ: " + naracka.getCena() + " ден.");
        if(naracka.getPrifatenaNaracka() == 0) {
            viewHolder.txtPotvrdenaNaracka.setText("На чекање");
        } else {
            viewHolder.txtPotvrdenaNaracka.setText("Време за достава: " + naracka.getVremeDostava() + " мин.");
        }
        viewHolder.txtDatum.setText("Датум на нарачката: " + naracka.getDatum());

    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }

}
