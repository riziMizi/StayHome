package com.example.stayhome.firma.naracki;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stayhome.R;
import com.example.stayhome.classes.Naracka;
import com.example.stayhome.classes.User;
import com.example.stayhome.googleMap.ShowGoogleMapActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class myAdapterFirmaNaracki extends RecyclerView.Adapter<myAdapterFirmaNaracki.ViewHolder>{


    private List<Naracka> myList;
    private int rowLayout;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtImeKupuvac, txtNaracka, txtIznos,
                txtDatum, txtZabeleska, txtTelefonDostavuvac, txtImeDostavuvac, txtStatusNapravena;


        public ViewHolder(View itemView) {
            super(itemView);
            txtImeKupuvac = (TextView)itemView.findViewById(R.id.rw5txtImeKupuvac);
            txtNaracka = (TextView) itemView.findViewById(R.id.rw5txtNaracka);
            txtIznos = (TextView) itemView.findViewById(R.id.rw5txtIznos);
            txtDatum = (TextView) itemView.findViewById(R.id.rw5txtDatum);
            txtZabeleska = (TextView) itemView.findViewById(R.id.rw5txtZabeleska);
            txtTelefonDostavuvac = (TextView) itemView.findViewById(R.id.rw5txtTelefonDostavuvac);
            txtImeDostavuvac = (TextView) itemView.findViewById(R.id.rw5txtImeDostavuvac);
            txtStatusNapravena = (TextView) itemView.findViewById(R.id.rw5txtStatusNapravena);

            txtStatusNapravena.setPaintFlags(txtStatusNapravena.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            txtStatusNapravena.setVisibility(View.INVISIBLE);
        }
    }


    public myAdapterFirmaNaracki(List<Naracka> myList, int rowLayout, Context context) {
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
    public void onBindViewHolder(myAdapterFirmaNaracki.ViewHolder viewHolder, int i) {
        Naracka naracka = myList.get(i);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(naracka.getKupuvacId());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                viewHolder.txtImeKupuvac.setText(user.getIme());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Настана некоја грешка!!", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.txtNaracka.setText(naracka.getNarackaHrana());
        viewHolder.txtIznos.setText("Вкупен износ:  " + String.valueOf(naracka.getCena()) + " ден.");
        viewHolder.txtDatum.setText("Датум на нарачката: " + naracka.getDatum());
        if(naracka.getZabeleska().equals("")) {
            viewHolder.txtZabeleska.setText("Забелешка: /");
        } else {
            viewHolder.txtZabeleska.setText("Забелешка: " + naracka.getZabeleska());
        }

        viewHolder.txtImeDostavuvac.setText("Име на доставувач: " + naracka.getDostavuvacIme());
        viewHolder.txtTelefonDostavuvac.setText("Телефон за контакт: " + naracka.getDostavuvacTelefon());

        if(naracka.getNapravena() == 0) {
            viewHolder.txtStatusNapravena.setVisibility(View.VISIBLE);
        } else {
            viewHolder.txtStatusNapravena.setVisibility(View.INVISIBLE);
        }

        viewHolder.txtStatusNapravena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Готова нарачка");
                builder.setMessage("Дали сте сигурни дека оваа нарачка е готова?");

                builder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Да</font>"), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, Object> map = new HashMap();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("AktivniNaracki");
                        map.put(naracka.getNarackaId() + "/napravena", 1);
                        databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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
