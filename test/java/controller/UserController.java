package test.java.controller;

import servlet.WebRoute;

public class UserController {

    @WebRoute(url = "/users")
    public void listUsers() {
        System.out.println("Liste des utilisateurs");
    }

    @WebRoute(url = "/users/add")
    public void addUser() {
        System.out.println("Ajout d'utilisateur");
    }

    public void deleteUser() {
        System.out.println("Suppression (non annotée)");
    }
}
