package test.java.controller;

import servlet.Controller;
import servlet.WebRoute;
import test.java.model.Departement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class DepartementController {

    @WebRoute(url = "/departement/insert")
    public String showInsertForm(HttpServletRequest req, HttpServletResponse resp) {
        return "formulaireDepartement";  
    }

    @WebRoute(url = "/departement/save")
    public String saveDepartement(int id_departement, String nom, HttpServletRequest req, HttpServletResponse resp) {
        Departement dept = new Departement(id_departement, nom);
        dept.setId(id_departement);
        dept.setLibelle(nom);
        req.setAttribute("dept", dept);
        return "departement";  
    }

    @WebRoute(url = "/departement")
    public String getDepartement(HttpServletRequest req, HttpServletResponse resp) {
        Departement dept = new Departement(1, "IT Department");
        req.setAttribute("dept", dept);
        return "departement";
    }
}