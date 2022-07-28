package com.example.stayhome.classes;

public class User {
    private String Ime;
    private String Telefon;
    private String Email;
    private String TipFirma;
    private String TipUser;

    //Firma
    private String Telefon2;
    private int OdobrenoOdAdmin;
    private int PostoiMeni;
    private String FirmaId;
    private int VkupnoOceni;
    private int ZbirOceni;
    private String RabotniDenovi;
    private String VremeOd;
    private String VremeDo;
    private String FirmaLogo;

    public User() {

    }

    //Kupuvac
    public User(String ime, String telefon, String email, String tipUser) {
        this.Ime = ime;
        this.Telefon = telefon;
        this.Email = email;
        this.TipUser = tipUser;
    }

    //Firma
    public User(String ime, String telefon, String telefon2, String email,String tipUser, String tipFirma, int odobrenoOdAdmin,
                int postoiMeni, int vkupnoOceni, int zbirOceni, String rabotniDenovi, String vremeOd, String vremeDo, String FirmaLogo) {
        this.Ime = ime;
        this.Telefon = telefon;
        this.Telefon2 = telefon2;
        this.Email = email;
        this.TipUser = tipUser;
        this.TipFirma = tipFirma;
        this.OdobrenoOdAdmin = odobrenoOdAdmin;
        this.PostoiMeni = postoiMeni;
        this.VkupnoOceni = vkupnoOceni;
        this.ZbirOceni = zbirOceni;
        this.RabotniDenovi = rabotniDenovi;
        this.VremeOd = vremeOd;
        this.VremeDo = vremeDo;
        this.FirmaLogo = FirmaLogo;
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

    public int getVkupnoOceni() {
        return VkupnoOceni;
    }

    public void setVkupnoOceni(int vkupnoOceni) {
        VkupnoOceni = vkupnoOceni;
    }

    public int getZbirOceni() {
        return ZbirOceni;
    }

    public void setZbirOceni(int zbirOceni) {
        ZbirOceni = zbirOceni;
    }

    public String getRabotniDenovi() {
        return RabotniDenovi;
    }

    public void setRabotniDenovi(String rabotniDenovi) {
        RabotniDenovi = rabotniDenovi;
    }

    public String getVremeOd() {
        return VremeOd;
    }

    public void setVremeOd(String vremeOd) {
        VremeOd = vremeOd;
    }

    public String getVremeDo() {
        return VremeDo;
    }

    public void setVremeDo(String vremeDo) {
        VremeDo = vremeDo;
    }

    public String getTelefon2() {
        return Telefon2;
    }

    public void setTelefon2(String telefon2) {
        Telefon2 = telefon2;
    }

    public String getFirmaLogo() {
        return FirmaLogo;
    }

    public void setFirmaLogo(String firmaLogo) {
        FirmaLogo = firmaLogo;
    }
}
