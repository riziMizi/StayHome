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

public class myAdapterKomentari extends RecyclerView.Adapter<myAdapterKomentari.ViewHolder>{

    private List<Komentar> myList;
    private int rowLayout;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtImeKupuvac, txtKomentar;


        public ViewHolder(View itemView) {
            super(itemView);
            txtImeKupuvac = (TextView) itemView.findViewById(R.id.rw6txtImeKupuvac);
            txtKomentar = (TextView) itemView.findViewById(R.id.rw6txtKomentar);
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

        viewHolder.txtImeKupuvac.setText(komentar.getImeKupuvac());
        viewHolder.txtKomentar.setText(komentar.getKomentarKupuvac());

    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }
}
