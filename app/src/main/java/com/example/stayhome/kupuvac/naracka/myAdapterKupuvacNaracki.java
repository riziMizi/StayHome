package com.example.stayhome.kupuvac.naracka;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class myAdapterKupuvacNaracki extends RecyclerView.Adapter<myAdapterKupuvacNaracki.ViewHolder>{

    private List<Naracka> myList;
    private int rowLayout;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtImeFirma, txtNaracka, txtIznos, txtPotvrdenaNaracka, txtDatum, txtZabeleska,
                txtPotvrda, txtDostavuvacIme, txtDostavuvacTelefon;


        public ViewHolder(View itemView) {
            super(itemView);
            txtImeFirma = (TextView)itemView.findViewById(R.id.rw4txtImeFirma);
            txtNaracka = (TextView) itemView.findViewById(R.id.rw4txtNaracka);
            txtIznos = (TextView) itemView.findViewById(R.id.rw4txtIznos);
            txtPotvrdenaNaracka = (TextView) itemView.findViewById(R.id.rw4txtPotvrdenaNaracka);
            txtDatum = (TextView) itemView.findViewById(R.id.rw4txtDatum);
            txtZabeleska = (TextView) itemView.findViewById(R.id.rw4txtZabeleska);
            txtPotvrda = (TextView) itemView.findViewById(R.id.rw4txtPotvrdi);
            txtDostavuvacIme = (TextView) itemView.findViewById(R.id.rw4txtDostavuvacIme);
            txtDostavuvacTelefon = (TextView) itemView.findViewById(R.id.rw4txtDostavuvacTelefon);

            txtPotvrda.setPaintFlags(txtPotvrda.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            txtPotvrda.setVisibility(View.INVISIBLE);
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
        if(naracka.getZabeleska().equals("")) {
            viewHolder.txtZabeleska.setText("Забелешка: /");
        } else {
            viewHolder.txtZabeleska.setText("Забелешка: " + naracka.getZabeleska());
        }
        viewHolder.txtIznos.setText("Вкупен износ: " + naracka.getCena() + " ден.");
        if(naracka.getPrifatenaNaracka().equals("За потврда")) {
            viewHolder.txtPotvrdenaNaracka.setText("На чекање");
        } else if(naracka.getPrifatenaNaracka().equals("На чекање")){
            viewHolder.txtPotvrdenaNaracka.setText("Време за достава: " + naracka.getVremeDostava() + " мин.");
            viewHolder.txtPotvrda.setVisibility(View.VISIBLE);
        } else if(naracka.getPrifatenaNaracka().equals("Прифатена")) {
            viewHolder.txtPotvrda.setVisibility(View.INVISIBLE);
            viewHolder.txtPotvrdenaNaracka.setText("Време за достава: " + naracka.getVremeDostava() + " мин.");
            viewHolder.txtDostavuvacIme.setText("Име на доставувачот: " + naracka.getDostavuvacIme());
            viewHolder.txtDostavuvacTelefon.setText("Телефон за контакт: " + naracka.getDostavuvacTelefon());
        }
        viewHolder.txtDatum.setText("Датум на нарачката: " + naracka.getDatum());

        viewHolder.txtPotvrda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                alert.setTitle("Потврди нарачка");
                alert.setMessage("Доколку ви одговара времето за достава ве молиме да ја прифатите нарачката.");

                alert.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Прифати</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                            Map<String, Object> map = new HashMap();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("AktivniNaracki");
                            map.put(naracka.getNarackaId() + "/prifatenaNaracka", "Прифатена");
                            databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(mContext, "Нарачката ќе ви биде доставена!!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton(Html.fromHtml("<font color='#FFFFFF'>Откажи</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Map<String, Object> map = new HashMap();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("AktivniNaracki");
                        map.put(naracka.getNarackaId() + "/prifatenaNaracka", "Откажана");
                        databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(mContext, "Нарачката е успешно откажана!!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }

}
