package com.example.stayhome.dostavuvac;

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
import com.example.stayhome.classes.Komentar;
import com.example.stayhome.classes.Naracka;
import com.example.stayhome.classes.User;
import com.example.stayhome.googleMap.ShowGoogleMapActivity;
import com.example.stayhome.myAdapterKomentari;
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

public class myAdapterDostavaNaracki extends RecyclerView.Adapter<myAdapterDostavaNaracki.ViewHolder> {

    private List<Naracka> myList;
    private int rowLayout;
    private Context mContext;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    private String DostavuvacIme = "";
    private String DostavuvacTelefon = "";

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtImeFirma, txtNaracka, txtZabeleska, txtIznos, txtDatum,
                txtImeKupuvac, txtAdresa, txtTelefon, txtStatus, txtVremeDostava, txtStatusPotvrda, txtDelete;


        public ViewHolder(View itemView) {
            super(itemView);

            txtImeFirma = (TextView) itemView.findViewById(R.id.rw7ImeFirma);
            txtNaracka = (TextView) itemView.findViewById(R.id.rw7txtNaracka);
            txtZabeleska = (TextView) itemView.findViewById(R.id.rw7txtZabeleska);
            txtIznos = (TextView) itemView.findViewById(R.id.rw7txtIznos);
            txtDatum = (TextView) itemView.findViewById(R.id.rw7txtDatum);
            txtImeKupuvac = (TextView) itemView.findViewById(R.id.rw7txtImeKupuvac);
            txtAdresa = (TextView) itemView.findViewById(R.id.rw7txtAdresa);
            txtTelefon = (TextView) itemView.findViewById(R.id.rw7txtTelefon);
            txtStatus = (TextView) itemView.findViewById(R.id.rw7txtStatusNaracka);
            txtVremeDostava = (TextView) itemView.findViewById(R.id.rw7txtVremeDostava);
            txtStatusPotvrda = (TextView) itemView.findViewById(R.id.rw7txtStatusPotvrda);
            txtDelete = (TextView) itemView.findViewById(R.id.rw7txtDelete);

            ZemiPodatociZaDostavuvac();

            txtDelete.setVisibility(View.INVISIBLE);


        }
    }

    public myAdapterDostavaNaracki(List<Naracka> myList, int rowLayout, Context context) {
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
        Naracka naracka = myList.get(i);
        System.out.println(naracka.getNarackaId());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(naracka.getKupuvacId());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                viewHolder.txtImeKupuvac.setText("Име на купувачот: " + user.getIme());
                viewHolder.txtTelefon.setText("Телефон за контакт: " + user.getTelefon());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Настана некоја грешка!!", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.txtImeFirma.setText(naracka.getImeFirma());

        viewHolder.txtNaracka.setText(naracka.getNarackaHrana());
        if(naracka.getZabeleska().equals("")) {
            viewHolder.txtZabeleska.setText("Забелешка: /");
        } else {
            viewHolder.txtZabeleska.setText("Забелешка: " + naracka.getZabeleska());
        }
        viewHolder.txtIznos.setText("Вкупен износ:  " + String.valueOf(naracka.getCena()) + " ден.");
        viewHolder.txtDatum.setText("Датум на нарачката: " + naracka.getDatum());

        viewHolder.txtAdresa.setText("Адреса за достава: " + naracka.getAdresa());

        viewHolder.txtAdresa.setPaintFlags(viewHolder.txtAdresa.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        viewHolder.txtAdresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShowGoogleMapActivity.class);
                intent.putExtra("lat", naracka.getLatitude());
                intent.putExtra("lon", naracka.getLongitude());
                v.getContext().startActivity(intent);
            }
        });

        if(naracka.getPrifatenaNaracka().equals("За потврда")) {
            viewHolder.txtStatus.setText("Прифати");
            viewHolder.txtStatus.setPaintFlags(viewHolder.txtStatus.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        } else if(naracka.getPrifatenaNaracka().equals("На чекање")) {
            viewHolder.txtVremeDostava.setText("Време за достава: " + naracka.getVremeDostava() + " мин.");
            viewHolder.txtStatusPotvrda.setText("Непотврдено");
        }else if(naracka.getPrifatenaNaracka().equals("Прифатена")) {
            viewHolder.txtVremeDostava.setText("Време за достава: " + naracka.getVremeDostava() + " мин.");
            viewHolder.txtStatusPotvrda.setText("Потврдена");
            viewHolder.txtStatus.setText("Доставена");
            viewHolder.txtStatus.setPaintFlags(viewHolder.txtStatus.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        } else if(naracka.getPrifatenaNaracka().equals("Откажана")) {
            viewHolder.txtVremeDostava.setText("Време за достава: " + naracka.getVremeDostava() + " мин.");
            viewHolder.txtStatusPotvrda.setText("Откажана");
            viewHolder.txtDelete.setVisibility(View.VISIBLE);
        }

        viewHolder.txtStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.txtStatus.getText().equals("Прифати")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                    alert.setTitle("Потврди нарачка");

                    LinearLayout layout = new LinearLayout(mContext);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText minuti = new EditText(mContext);
                    minuti.setHint("Внесете време за достава.(минути)");
                    minuti.setInputType(InputType.TYPE_CLASS_NUMBER);
                    minuti.setImeOptions(EditorInfo.IME_ACTION_NONE);
                    layout.addView(minuti);

                    alert.setView(layout);

                    alert.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Поднеси</font>"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if(minuti.getText().toString().equals("")){
                                Toast.makeText(mContext, "При прифаќање мора да се внесе време за достава!", Toast.LENGTH_SHORT).show();
                            } else {
                                Map<String, Object> map = new HashMap();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("AktivniNaracki");
                                map.put(naracka.getNarackaId() + "/vremeDostava", minuti.getText().toString().trim());
                                map.put(naracka.getNarackaId() + "/prifatenaNaracka", "На чекање");
                                map.put(naracka.getNarackaId() + "/dostavuvacId", uid);
                                map.put(naracka.getNarackaId() + "/dostavuvacIme", DostavuvacIme);
                                map.put(naracka.getNarackaId() + "/dostavuvacTelefon", DostavuvacTelefon);
                                databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(mContext, "Успешно прифатена нарачка!!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

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

        viewHolder.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Отстрани нарачка");
                builder.setMessage("Дали сте сигурни дека сакате оваа нарачка да ја избришете?");

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
        });
    }


    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }

    private void ZemiPodatociZaDostavuvac() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                DostavuvacIme = user.getIme();
                DostavuvacTelefon = user.getTelefon();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Настана грешка. Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
