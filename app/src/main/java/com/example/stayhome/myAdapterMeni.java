package com.example.stayhome;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class myAdapterMeni extends RecyclerView.Adapter<myAdapterMeni.ViewHolder>{

    private List<Meni> myList;
    private int rowLayout;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtArtikl, txtArtiklSostav, txtArtiklCena;


        public ViewHolder(View itemView) {
            super(itemView);
            txtArtikl = (TextView)itemView.findViewById(R.id.rwtxtMeniArtikl);
            txtArtiklSostav = (TextView) itemView.findViewById(R.id.rwtxtMeniArtiklSostav);
            txtArtiklCena = (TextView) itemView.findViewById(R.id.rwtxtMeniArtiklCena);
        }
    }


    public myAdapterMeni(List<Meni> myList, int rowLayout, Context context) {
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
        Meni meni = myList.get(i);

        viewHolder.txtArtikl.setText(meni.getArtikl());
        viewHolder.txtArtiklSostav.setText("Состав:\n" + meni.getSostavArtikl());
        viewHolder.txtArtiklCena.setText("Цена:  " + String.valueOf(meni.getCena() + " ден."));

    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }
}
