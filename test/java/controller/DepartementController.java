package test.java.controller;

import servlet.Controller;
import servlet.WebRoute;
import servlet.RequestParam;
import servlet.PathVariable;
import test.java.model.Departement;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class DepartementController {

    @WebRoute(url = "/departement/insert", method = "GET")
    public String showInsertForm(HttpServletRequest req, HttpServletResponse resp) {
        return "formulaireDepartement";
    }

    @WebRoute(url = "/departement/insert-DeptEmploye", method = "GET") 
    public String showformulaireDeptEmploye(HttpServletRequest req, HttpServletResponse resp) {
        return "formulaireDeptEmploye";
    }

    @WebRoute(url = "/departement/save", method = "POST")
    public String saveDepartement(
            @RequestParam("id_departement") int idDept,
            @RequestParam("nom") String nomDept,
            HttpServletRequest req,
            HttpServletResponse resp) {

        Departement dept = new Departement(idDept, nomDept);
        req.setAttribute("dept", dept);
        req.setAttribute("message", "Departement cree avec succes !");
        return "departement";
    }

    @WebRoute(url = "/departement/{id}", method = "GET")
    public String getDepartementById(
            @PathVariable("id") int id,
            HttpServletRequest req,
            HttpServletResponse resp) {

        Departement dept = new Departement(id, "Departement #" + id);
        req.setAttribute("dept", dept);
        return "departement";
    }

    @WebRoute(url = "/departement/{id}", method = "POST")
    public String updateDepartementById(
            @PathVariable("id") int id,
            @RequestParam("nom") String nouveauNom,
            HttpServletRequest req,
            HttpServletResponse resp) {

        Departement dept = new Departement(id, nouveauNom);
        req.setAttribute("dept", dept);
        req.setAttribute("message", "Departement #" + id + " mis a jour !");
        return "departement";
    }

    @WebRoute(url = "/departement", method = "GET")
    public String getDepartement(HttpServletRequest req, HttpServletResponse resp) {
        Departement dept = new Departement(1, "IT Department");
        req.setAttribute("dept", dept);
        return "departement";
    }

    @WebRoute(url = "/departement/save/map", method = "POST")
    public String saveDepartementWithMap(
            Map<String, String> params,
            HttpServletRequest req,
            HttpServletResponse resp) {

        String nom = params.get("nom");
        String employes = params.get("employes");

        req.setAttribute("message", "Parametres reçus via Map :");
        req.setAttribute("params", params);
        req.setAttribute("nom", nom);
        req.setAttribute("employes", employes);

        return "ListeDeptEmp";
    }

}