package test.java.controller;

import servlet.WebRoute;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.Controller;

@Controller
public class UserController {

    @WebRoute(url = "/users")
    public String listUsers(HttpServletRequest req, HttpServletResponse resp) { 
        return "Liste des utilisateurs (vue: users.html)";
    }

    @WebRoute(url = "/users/add")
    public String addUser(HttpServletRequest req, HttpServletResponse resp) { 
        return "Ajout d'utilisateur (vue: add.html)";
    }

    public String deleteUser() { // Non annotée
        return "Suppression (non routée)";
    }

}
