package com.example.stayhome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class myAdapterFirmaNaracki extends RecyclerView.Adapter<myAdapterFirmaNaracki.ViewHolder>{


    private List<Naracka> myList;
    private int rowLayout;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtImeKupuvac, txtNaracka, txtIznos, txtAdresa,
                txtDatum, txtZabeleska, txtTelefon, txtStatusNaracka, txtVremeDostava;


        public ViewHolder(View itemView) {
            super(itemView);
            txtImeKupuvac = (TextView)itemView.findViewById(R.id.rw5txtImeKupuvac);
            txtNaracka = (TextView) itemView.findViewById(R.id.rw5txtNaracka);
            txtIznos = (TextView) itemView.findViewById(R.id.rw5txtIznos);
            txtAdresa = (TextView) itemView.findViewById(R.id.rw5txtAdresa);
            txtDatum = (TextView) itemView.findViewById(R.id.rw5txtDatum);
            txtZabeleska = (TextView) itemView.findViewById(R.id.rw5txtZabeleska);
            txtTelefon = (TextView) itemView.findViewById(R.id.rw5txtTelefon);
            txtStatusNaracka = (TextView) itemView.findViewById(R.id.rw5txtStatusNaracka);
            txtVremeDostava = (TextView) itemView.findViewById(R.id.rw5txtVremeDostava);
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
                viewHolder.txtTelefon.setText("Телефон за контакт: " + user.getTelefon());
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
        viewHolder.txtAdresa.setText("Адреса за достава: " + naracka.getAdresa());

        viewHolder.txtAdresa.setPaintFlags(viewHolder.txtAdresa.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        if(naracka.getPrifatenaNaracka().equals("За потврда")) {
            viewHolder.txtStatusNaracka.setText("Прифати");
        } else {
            viewHolder.txtStatusNaracka.setText("Доставено");
            viewHolder.txtVremeDostava.setText("Време за достава: " + naracka.getVremeDostava() + " мин.");
        }
        viewHolder.txtStatusNaracka.setPaintFlags(viewHolder.txtStatusNaracka.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        viewHolder.txtAdresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShowGoogleMapActivity.class);
                intent.putExtra("lat", naracka.getLatitude());
                intent.putExtra("lon", naracka.getLongitude());
                v.getContext().startActivity(intent);
            }
        });


        viewHolder.txtStatusNaracka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.txtStatusNaracka.getText().equals("Прифати")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                    alert.setTitle("Потврди нарачка");

                    LinearLayout layout = new LinearLayout(mContext);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText minuti = new EditText(mContext);
                    minuti.setHint("Внесете минути за достава");
                    layout.addView(minuti);

                    alert.setView(layout);

                    alert.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Поднеси</font>"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Map<String, Object> map = new HashMap();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("AktivniNaracki");
                            map.put(naracka.getNarackaId() + "/vremeDostava", minuti.getText().toString().trim());
                            map.put(naracka.getNarackaId() + "/prifatenaNaracka", "Активни");
                            databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(mContext, "Успешно прифатена нарачка!!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            dialog.dismiss();
                        }
                    });

                    alert.setNegativeButton(Html.fromHtml("<font color='#FFFFFF'>Исклучи</font>"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    builder.setTitle("Доставена нарачка");
                    builder.setMessage("Дали сте сигурни дека оваа нарачка е успешно доставена?");

                    builder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Да</font>"), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("AktivniNaracki").child(naracka.getNarackaId());
                            databaseReference.removeValue();
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
            }
        });



    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }
}
