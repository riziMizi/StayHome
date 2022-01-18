package com.example.stayhome;

public class Naracka {
    private String Adresa;
    private int Cena;
    private String FirmaId;
    private double Longitude;
    private double Latitude;
    private String Zabeleska;
    private String PrifatenaNaracka;
    private String VremeDostava;
    private String KupuvacId;
    private String NarackaHrana;
    private String Datum;
    private String NarackaId;

    public Naracka() {

    }

    public Naracka(String adresa, int cena, String firmaId, String zabeleska, double longitude,
                   double latitude, String prifatenaNaracka, String vremeDostava, String kupuvacId, String narackaHrana,
                   String datum) {
        this.Adresa = adresa;
        this.Cena = cena;
        this.FirmaId = firmaId;
        this.Zabeleska = zabeleska;
        this.Longitude = longitude;
        this.Latitude = latitude;
        this.PrifatenaNaracka = prifatenaNaracka;
        this.VremeDostava = vremeDostava;
        this.KupuvacId = kupuvacId;
        this.NarackaHrana = narackaHrana;
        this.Datum = datum;
    }

    public String getAdresa() {
        return Adresa;
    }

    public void setAdresa(String adresa) {
        Adresa = adresa;
    }

    public int getCena() {
        return Cena;
    }

    public void setCena(int cena) {
        Cena = cena;
    }

    public String getFirmaId() {
        return FirmaId;
    }

    public void setFirmaId(String firmaId) {
        FirmaId = firmaId;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public String getZabeleska() {
        return Zabeleska;
    }

    public void setZabeleska(String zabeleska) {
        Zabeleska = zabeleska;
    }

    public String getVremeDostava() {
        return VremeDostava;
    }

    public void setVremeDostava(String vremeDostava) {
        VremeDostava = vremeDostava;
    }

    public String getKupuvacId() {
        return KupuvacId;
    }

    public void setKupuvacId(String kupuvacId) {
        KupuvacId = kupuvacId;
    }

    public String getNarackaHrana() {
        return NarackaHrana;
    }

    public void setNarackaHrana(String narackaHrana) {
        NarackaHrana = narackaHrana;
    }

    public String getDatum() {
        return Datum;
    }

    public void setDatum(String datum) {
        Datum = datum;
    }

    public String getPrifatenaNaracka() {
        return PrifatenaNaracka;
    }

    public void setPrifatenaNaracka(String prifatenaNaracka) {
        PrifatenaNaracka = prifatenaNaracka;
    }

    public String getNarackaId() {
        return NarackaId;
    }

    public void setNarackaId(String narackaId) {
        NarackaId = narackaId;
    }
}
