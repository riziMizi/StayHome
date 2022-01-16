package com.example.stayhome;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class myAdapterFirmi  extends RecyclerView.Adapter<myAdapterFirmi.ViewHolder>{

    private List<User> myList;
    private int rowLayout;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtImeFirma, txtTipFirma, txtTelefonFirma, txtMeni, txtRabotnoVreme;
        private RatingBar ratingBar;


        public ViewHolder(View itemView) {
            super(itemView);
            txtImeFirma = (TextView) itemView.findViewById(R.id.rw2txtImeFirma);
            txtTipFirma = (TextView) itemView.findViewById(R.id.rw2txtTipFirma);
            txtTelefonFirma = (TextView) itemView.findViewById(R.id.rw2txtFirmaTelefon);
            txtMeni = (TextView) itemView.findViewById(R.id.rw2txtMeni);
            txtRabotnoVreme = (TextView) itemView.findViewById(R.id.rw2txtRabotnoVreme);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rw2ratingBar);
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
        viewHolder.ratingBar.setRating(Float.parseFloat("3.0"));
        viewHolder.txtRabotnoVreme.setText("Работно време:  " + user.getRabotniDenovi() + "   " +
                user.getVremeOd() + "-" + user.getVremeDo());

        viewHolder.txtMeni.setPaintFlags(viewHolder.txtMeni.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        viewHolder.txtMeni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,KupuvacMeniActivity.class);
                intent.putExtra("FirmaId", user.getFirmaId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }

}
