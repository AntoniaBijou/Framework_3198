package test.java.model;

public class Departement {
    private int id;
    private String libelle;

    public Departement(int id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    public int getId() {
        return id;
    }

    public String getLibelle() {
        return libelle;
    }
}
