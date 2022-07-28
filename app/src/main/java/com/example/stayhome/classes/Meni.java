package com.example.stayhome.classes;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Meni {
    private String Artikl;
    private String SostavArtikl;
    private int Cena;
    private String ArtiklId;
    private int Kolicina;
    private String Slika;
    private Uri SlikaUri;

    public Meni() {

    }

    public Meni(String artikl, String sostavArtikl, int cena, String slika) {
        this.Artikl = artikl;
        this.SostavArtikl = sostavArtikl;
        this.Cena = cena;
        this.Slika = slika;
    }

    public Meni(String artikl, String sostavArtikl, int cena, String slika, Uri slikaUri) {
        this.Artikl = artikl;
        this.SostavArtikl = sostavArtikl;
        this.Cena = cena;
        this.Slika = slika;
        this.SlikaUri = slikaUri;
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

    public int getKolicina() {
        return Kolicina;
    }

    public void setKolicina(int kolicina) {
        Kolicina = kolicina;
    }

    public String getSlika() {
        return Slika;
    }

    public void setSlika(String slika) {
        Slika = slika;
    }

    public Uri getSlikaUri() {
        return SlikaUri;
    }

    public void setSlikaUri(Uri slikaUri) {
        SlikaUri = slikaUri;
    }
}
