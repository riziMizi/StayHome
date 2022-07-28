package com.example.stayhome.kupuvac;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stayhome.R;
import com.example.stayhome.classes.User;
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

public class myAdapterFirmi  extends RecyclerView.Adapter<myAdapterFirmi.ViewHolder>{

    private List<User> myList;
    private int rowLayout;
    private Context mContext;

    private int GlobalOcena;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtImeFirma, txtTipFirma, txtTelefonFirma, txtTelefonFirma2, txtMeni, txtRabotnoVreme,
                txtKomentar, txtOcena;
        private RatingBar ratingBar;
        private ImageView imageView;


        public ViewHolder(View itemView) {
            super(itemView);
            txtImeFirma = (TextView) itemView.findViewById(R.id.rw2txtImeFirma);
            txtTipFirma = (TextView) itemView.findViewById(R.id.rw2txtTipFirma);
            txtTelefonFirma = (TextView) itemView.findViewById(R.id.rw2txtFirmaTelefon);
            txtTelefonFirma2 = (TextView) itemView.findViewById(R.id.rw2txtFirmaTelefon2);
            txtMeni = (TextView) itemView.findViewById(R.id.rw2txtMeni);
            txtRabotnoVreme = (TextView) itemView.findViewById(R.id.rw2txtRabotnoVreme);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rw2ratingBar);
            txtKomentar = (TextView) itemView.findViewById(R.id.rw2Komentari);
            txtOcena = (TextView) itemView.findViewById(R.id.rw2Ocena);
            imageView = (ImageView) itemView.findViewById(R.id.rw2imageView);
        }
    }

    public myAdapterFirmi(List<User> myList, int rowLayout, Context context) {
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
    public void onBindViewHolder(myAdapterFirmi.ViewHolder viewHolder, int i) {
        User user = myList.get(i);
        viewHolder.txtImeFirma.setText(user.getIme());
        viewHolder.txtTipFirma.setText(user.getTipFirma());
        viewHolder.txtTelefonFirma.setText("Телефон за контакт:  " + user.getTelefon());
        if(!user.getTelefon2().equals("")) {
            viewHolder.txtTelefonFirma2.setText("Телефон за контакт 2: " + user.getTelefon2());
        } else {
            viewHolder.txtTelefonFirma2.setText("Телефон за контакт 2: /" + user.getTelefon2());
        }

        viewHolder.txtRabotnoVreme.setText("Работно време:  " + user.getRabotniDenovi() + "   " +
                user.getVremeOd() + "-" + user.getVremeDo());
        if(!user.getFirmaLogo().equals("")) {
            Glide.with(mContext).load(myList.get(i).getFirmaLogo()).into(viewHolder.imageView);
        }

        viewHolder.txtMeni.setPaintFlags(viewHolder.txtMeni.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").
                child(user.getFirmaId());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                int ZbirOceni = user.getZbirOceni();
                int VkupnoOceni = user.getVkupnoOceni();
                if(VkupnoOceni != 0) {
                    float Ocena = (float) ZbirOceni / VkupnoOceni;
                    viewHolder.ratingBar.setRating(Ocena);
                } else {
                    viewHolder.ratingBar.setRating(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.txtMeni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, KupuvacMeniActivity.class);
                intent.putExtra("FirmaId", user.getFirmaId());
                mContext.startActivity(intent);
            }
        });

        viewHolder.txtKomentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, KomentariActivity.class);
                intent.putExtra("FirmaId", user.getFirmaId());
                intent.putExtra("FirmaIme", user.getIme());
                mContext.startActivity(intent);
            }
        });

        viewHolder.txtOcena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                alert.setTitle("Оцена");

                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);

                final TextView textView = new TextView(mContext);
                textView.setText("Оценете ја фирмата:");
                textView.setPadding(10,10,0,0);
                textView.setTextSize(16);
                layout.addView(textView);

                final RadioGroup radioGroup = new RadioGroup(mContext);
                RadioButton radioButton;
                for(int i = 1; i < 6; i++) {
                    radioButton = new RadioButton(mContext);
                    radioButton.setText(String.valueOf(i));
                    radioGroup.addView(radioButton);
                }
                radioGroup.setPadding(10, 10, 0, 0);

                RadioButton radioButton1 = (RadioButton) radioGroup.getChildAt(4);
                radioButton1.setChecked(true);

                radioGroup.setOrientation(LinearLayout.HORIZONTAL);

                layout.addView(radioGroup);

                alert.setView(layout);

                alert.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Поднеси</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        RadioButton radioButton1 = (RadioButton) layout.findViewById(radioGroup.getCheckedRadioButtonId());
                        int Ocena = Integer.parseInt(radioButton1.getText().toString());

                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Oceni")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(user.getFirmaId());
                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    SmeniOcena(user.getFirmaId(), Ocena);
                                } else {
                                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Oceni")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(user.getFirmaId());
                                    databaseReference2.setValue(Ocena).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                DodadiOcena(user.getFirmaId(), Ocena);
                                            } else {
                                                Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
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
            }
        });

    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }

    private void DodadiOcena(String id, int ocena) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                int VkupnoOceni = user.getVkupnoOceni() + 1;
                int ZbirOceni = user.getZbirOceni() + ocena;
                user.setVkupnoOceni(VkupnoOceni);
                user.setZbirOceni(ZbirOceni);
                snapshot.getRef().setValue(user);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SmeniOcena(String id, int NovaOcena) {

        GlobalOcena = 0;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Oceni")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int val = snapshot.getValue(Integer.class);
                GlobalOcena = val;
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Oceni")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Map<String, Object> map = new HashMap();
                map.put(id, NovaOcena);
                reference1.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()) {
                            Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Users").child(id);
                reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        int ZbirOceni = user.getZbirOceni() - GlobalOcena;
                        ZbirOceni += NovaOcena;
                        user.setZbirOceni(ZbirOceni);
                        snapshot.getRef().setValue(user);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
