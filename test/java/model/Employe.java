package test.java.model;

public class Employe {
    private int id_employe;
    private String nom;
    private Departement departement;

    public Employe() {
    }

    public Employe(int id_employe, String nom, Departement departement) {
        this.id_employe = id_employe;
        this.nom = nom;
        this.departement = departement;
    }

    public int getId_employe() {
        return id_employe;
    }

    public void setId_employe(int id_employe) {
        this.id_employe = id_employe;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }
}