package com.example.stayhome;

public class User {
    private String Ime;
    private String Telefon;
    private String Email;
    private String TipFirma;
    private String TipUser;

    private int OdobrenoOdAdmin;
    private int PostoiMeni;
    private String FirmaId;

    public User() {

    }

    public User(String ime, String telefon, String email, String tipUser) {
        this.Ime = ime;
        this.Telefon = telefon;
        this.Email = email;
        this.TipUser = tipUser;
    }

    public User(String ime, String telefon, String email,String tipUser, String tipFirma, int odobrenoOdAdmin, int postoiMeni) {
        this.Ime = ime;
        this.Telefon = telefon;
        this.Email = email;
        this.TipUser = tipUser;
        this.TipFirma = tipFirma;
        this.OdobrenoOdAdmin = odobrenoOdAdmin;
        this.PostoiMeni = postoiMeni;
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

    public int getPostoiMeni() {
        return PostoiMeni;
    }

    public void setPostoiMeni(int postoiMeni) {
        PostoiMeni = postoiMeni;
    }

    public String getFirmaId() {
        return FirmaId;
    }

    public void setFirmaId(String firmaId) {
        FirmaId = firmaId;
    }
}
