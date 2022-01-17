package com.example.stayhome;

public class Naracka {
    private String Adresa;
    private String Telefon;
    private int Cena;
    private String FirmaId;
    private double Longitude;
    private double Latitude;
    private String Zabeleska;
    private int PrifatenaNaracka;
    private String VremeDostava;
    private String KupuvacId;
    private String NarackaHrana;
    private String Datum;

    public Naracka() {

    }

    public Naracka(String adresa, String telefon, int cena, String firmaId, String zabeleska, double longitude,
                   double latitude, int prifatenaNaracka, String vremeDostava, String kupuvacId, String narackaHrana, String datum) {
        this.Adresa = adresa;
        this.Telefon = telefon;
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

    public String getTelefon() {
        return Telefon;
    }

    public void setTelefon(String telefon) {
        Telefon = telefon;
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

    public int getPrifatenaNaracka() {
        return PrifatenaNaracka;
    }

    public void setPrifatenaNaracka(int prifatenaNaracka) {
        PrifatenaNaracka = prifatenaNaracka;
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
}
