package test.java.controller;

import servlet.WebRoute;


import servlet.Controller;

@Controller  
public class UserController {

    @WebRoute(url = "/users")
    public String listUsers() {  
        return "Liste des utilisateurs (vue: users.html)";
    }

    @WebRoute(url = "/users/add")
    public String addUser() {
        return "Ajout d'utilisateur (vue: add.html)";
    }

    public String deleteUser() {  // Non annotée
        return "Suppression (non routée)";
    }
}
