package test.java.model;

public class Departement {
    private int id_departement;
    private String nom;

     public Departement() {
    }

    public Departement(int id_departement, String nom) {
        this.id_departement = id_departement;
        this.nom = nom;
    }

    public int getId_departement() {
        return id_departement;
    }

    public void setId_departement(int id_departement) {
        this.id_departement = id_departement;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getId() {
        return id_departement;
    }

    public void setId(int id_departement) {
        this.id_departement = id_departement;
    }

    public String getLibelle() {
        return nom;
    }

    public void setLibelle(String nom) {
        this.nom = nom;
    }
}