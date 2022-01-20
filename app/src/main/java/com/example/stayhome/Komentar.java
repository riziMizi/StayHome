package com.example.stayhome;

public class Komentar {
    private String ImeKupuvac;
    private String KomentarKupuvac;
    private String FirmaId;

    public Komentar() {

    }

    public Komentar(String imeKupuvac, String komentarKupuvac, String firmaId) {
        this.ImeKupuvac = imeKupuvac;
        this.KomentarKupuvac = komentarKupuvac;
        this.FirmaId = firmaId;
    }

    public String getImeKupuvac() {
        return ImeKupuvac;
    }

    public void setImeKupuvac(String imeKupuvac) {
        ImeKupuvac = imeKupuvac;
    }

    public String getKomentarKupuvac() {
        return KomentarKupuvac;
    }

    public void setKomentarKupuvac(String komentarKupuvac) {
        KomentarKupuvac = komentarKupuvac;
    }

    public String getFirmaId() {
        return FirmaId;
    }

    public void setFirmaId(String firmaId) {
        FirmaId = firmaId;
    }
}
