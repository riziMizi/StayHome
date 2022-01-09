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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class myAdapterAdmin extends RecyclerView.Adapter<myAdapterAdmin.ViewHolder>{

    private List<User> myList;
    private int rowLayout;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtImeFirma, txtTipFirma, txtVidiMeni, txtConfirm, txtDecline;


        public ViewHolder(View itemView) {
            super(itemView);
            txtImeFirma = (TextView) itemView.findViewById(R.id.rw1txtImeFirma);
            txtTipFirma = (TextView) itemView.findViewById(R.id.rw1txtTipFirma);
            txtVidiMeni = (TextView) itemView.findViewById(R.id.rw1txtVidiMeni);
            txtConfirm = (TextView) itemView.findViewById(R.id.rw1Confirm);
            txtDecline = (TextView) itemView.findViewById(R.id.rw1Decline);

        }
    }

    public myAdapterAdmin(List<User> myList, int rowLayout, Context context) {
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
    public void onBindViewHolder(myAdapterAdmin.ViewHolder viewHolder, int i) {
        User user = myList.get(i);
        viewHolder.txtImeFirma.setText(user.getIme());
        viewHolder.txtTipFirma.setText("Тип на фирмата:  " + user.getTipFirma());
        viewHolder.txtVidiMeni.setText("Преглед на менито");

        viewHolder.txtVidiMeni.setPaintFlags(viewHolder.txtVidiMeni.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        viewHolder.txtVidiMeni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,AdminMeniActivity.class);
                intent.putExtra("FirmaId", user.getFirmaId());
                mContext.startActivity(intent);
            }
        });

        viewHolder.txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Потврда фирма");
                builder.setMessage("Дали сигурно ја прифаќаш фирмата?");

                builder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Да</font>"), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, Object> map = new HashMap();
                        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users");
                        map.put(user.getFirmaId() + "/odobrenoOdAdmin", 1);

                        firebaseDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(mContext, "Фирмата е успешно прифатена!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "Настана некоја грешка!!", Toast.LENGTH_SHORT).show();
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

        viewHolder.txtDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Потврда фирма");
                builder.setMessage("Дали сигурно ја одбиваш фирмата?");

                builder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Да</font>"), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, Object> map = new HashMap();
                        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users");
                        map.put(user.getFirmaId() + "/odobrenoOdAdmin", 2);

                        firebaseDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(mContext, "Фирмата е успешно одбиена!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "Настана некоја грешка!!", Toast.LENGTH_SHORT).show();
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
