package com.example.stayhome;

import java.util.ArrayList;
import java.util.List;

public class Meni {
    private String Artikl;
    private String SostavArtikl;
    private int Cena;
    private String ArtiklId;

    public Meni() {

    }

    public Meni(String artikl, String sostavArtikl, int cena) {
        this.Artikl = artikl;
        this.SostavArtikl = sostavArtikl;
        this.Cena = cena;
    }

    public String getArtikl() {
        return Artikl;
    }

    public void setArtikl(String artikl) {
        Artikl = artikl;
    }

    public int getCena() {
        return Cena;
    }

    public void setCena(int cena) {
        Cena = cena;
    }

    public String getSostavArtikl() {
        return SostavArtikl;
    }

    public void setSostavArtikl(String sostavArtikl) {
        SostavArtikl = sostavArtikl;
    }

    public String getArtiklId() {
        return ArtiklId;
    }

    public void setArtiklId(String artiklId) {
        ArtiklId = artiklId;
    }
}
