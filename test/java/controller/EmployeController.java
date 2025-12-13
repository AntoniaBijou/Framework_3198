package test.java.controller;

import servlet.Controller;
import servlet.WebRoute;
import servlet.ModelAttribute;
import test.java.model.Employe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class EmployeController {

    private static List<Employe> employes = new ArrayList<>();

    @WebRoute(url = "/employe/insert", method = "GET")
    public String showInsertForm(HttpServletRequest req, HttpServletResponse resp) {
        return "formulaireEmploye";
    }

    @WebRoute(url = "/employe/save", method = "POST")
    public String saveEmploye(
            @ModelAttribute Employe employe,
            HttpServletRequest req,
            HttpServletResponse resp) {
        
        employes.add(employe);
        req.setAttribute("employes", employes);
        req.setAttribute("message", "Employe ajoute avec succes !");
        return "listeEmployes";
    }

    @WebRoute(url = "/employe/liste", method = "GET")
    public String listEmployes(HttpServletRequest req, HttpServletResponse resp) {
        req.setAttribute("employes", employes);
        return "listeEmployes";
    }
}