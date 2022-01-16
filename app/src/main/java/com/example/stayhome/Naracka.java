package com.example.stayhome;

public class Naracka {
    private String Adresa;
    private String Telefon;
    private int Cena;
    private String FirmaId;
    private double Longitude;
    private double Latitude;
    private String Zabeleska;

    public Naracka(String adresa, String telefon, int cena, String firmaId, String zabeleska) {
        this.Adresa = adresa;
        this.Telefon = telefon;
        this.Cena = cena;
        this.FirmaId = firmaId;
        this.Zabeleska = zabeleska;
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
}
