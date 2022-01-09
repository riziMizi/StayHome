package com.example.stayhome;

public class User {
    private String Ime;
    private String Telefon;
    private String Email;
    private String TipFirma;
    private String TipUser;
    private int OdobrenoOdAdmin;

    public User() {

    }

    public User(String ime, String telefon, String email, String tipUser) {
        this.Ime = ime;
        this.Telefon = telefon;
        this.Email = email;
        this.TipUser = tipUser;
    }

    public User(String ime, String telefon, String email,String tipUser, String tipFirma, int odobrenoOdAdmin) {
        this.Ime = ime;
        this.Telefon = telefon;
        this.Email = email;
        this.TipUser = tipUser;
        this.TipFirma = tipFirma;
        this.OdobrenoOdAdmin = odobrenoOdAdmin;
    }

    public String getIme() {
        return Ime;
    }

    public void setIme(String ime) {
        Ime = ime;
    }

    public String getTelefon() {
        return Telefon;
    }

    public void setTelefon(String telefon) {
        Telefon = telefon;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getTipFirma() {
        return TipFirma;
    }

    public void setTipFirma(String tipFirma) {
        TipFirma = tipFirma;
    }

    public String getTipUser() {
        return TipUser;
    }

    public void setTipUser(String tipUser) {
        TipUser = tipUser;
    }

    public int getOdobrenoOdAdmin() {
        return OdobrenoOdAdmin;
    }

    public void setOdobrenoOdAdmin(int odobrenoOdAdmin) {
        OdobrenoOdAdmin = odobrenoOdAdmin;
    }
}
