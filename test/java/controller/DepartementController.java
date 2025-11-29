package test.java.controller;

import servlet.Controller;
import servlet.RequestParam;
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
    public String saveDepartement(
            @RequestParam("id_departement") int idDept,
            @RequestParam("nom") String nomDept,
            HttpServletRequest req,
            HttpServletResponse resp) {

        Departement dept = new Departement(idDept, nomDept);
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