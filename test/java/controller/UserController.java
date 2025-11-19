package test.java.controller;

import servlet.WebRoute;


import servlet.Controller;

@Controller  
public class UserController {

    @WebRoute(url = "/users")
    public String listUsers() {  
        return "Liste des utilisateurs ";
    }

    @WebRoute(url = "/users/add")
    public String addUser() {
        return "Ajout d'utilisateur";
    }

    public String deleteUser() {  // Non annotée
        return "Suppression (non routée)";
    }
}
