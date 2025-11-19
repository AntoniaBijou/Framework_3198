package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontServlet extends HttpServlet {
    private Map<String, RouteInfo> routeMap;

    @Override
    public void init() throws ServletException {
        super.init();
        List<String> packagesToScan = Arrays.asList("test.java.controller");
        routeMap = AnnotationScanner.scanRoutesForApp(packagesToScan);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());

        if ("/".equals(path)) {
            resp.getWriter().println("Racine de l'app (routes chargees : " + routeMap.size() + ")");
            return;
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        path = path.replaceAll("/+$", "");

        RouteInfo route = routeMap.get("/" + path);
        resp.setContentType("text/html; charset=UTF-8");

        if (route != null) {
            try {
                Object instance = route.getControllerClass().getDeclaredConstructor().newInstance();
                Object result = route.getMethod().invoke(instance, req, resp);
                if (result instanceof String) {
                    String viewName = (String) result;
                    String jspPath = "/WEB-INF/views/" + viewName + ".jsp";
                    // Vérifiez si le chemin réel existe avant de créer File
                    String realJspPath = getServletContext().getRealPath(jspPath);
                    if (realJspPath != null) {
                        File jspFile = new File(realJspPath);
                        if (jspFile.exists()) {
                            RequestDispatcher dispatcher = req.getRequestDispatcher(jspPath);
                            dispatcher.forward(req, resp);
                            return;
                        } else {
                            // Affichage par défaut si pas de JSP
                            resp.getWriter().println("<h1>Route supportee : " + route.getUrl() + "</h1>");
                            resp.getWriter().println("<p>Classe : " + route.getControllerClass().getName() + "</p>");
                            resp.getWriter().println("<p>Methode : " + route.getMethod().getName() + "</p>");
                            resp.getWriter().println("<hr><h2>Resultat :</h2><p>" + viewName + "</p>");
                        }
                    } else {
                        // Gestion si getRealPath est null : Affichage simple (pas de fichier réel)
                        resp.getWriter().println("<h1>Route supportee : " + route.getUrl() + "</h1>");
                        resp.getWriter().println("<p>Classe : " + route.getControllerClass().getName() + "</p>");
                        resp.getWriter().println("<p>Methode : " + route.getMethod().getName() + "</p>");
                        resp.getWriter().println("<hr><h2>Resultat :</h2><p>" + viewName + "</p>");
                    }
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                    | NoSuchMethodException e) {
                resp.getWriter().println("Erreur invocation : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            String candidateView = "/WEB-INF/views/" + path;
            File file = new File(getServletContext().getRealPath(candidateView));
            if (file.exists() && file.isFile()) {
                if (candidateView.endsWith(".jsp")) {
                    RequestDispatcher dispatcher = req.getRequestDispatcher(candidateView);
                    dispatcher.forward(req, resp);
                } else {
                    chercherRessource(req, resp);
                }
                return;
            }
            resp.getWriter().println("<h1>Il n'y a pas de route pour l'URL : " + path + "</h1>");
        }
    }

    private void chercherRessource(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getRequestURI().substring(req.getContextPath().length());

        if ("/".equals(path)) {
            resp.getWriter().println("/");
            return;
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        String webInfPath = "/WEB-INF/views/";
        String viewPath;
        if (path.startsWith("WEB-INF/views/")) {
            viewPath = "/" + path;
        } else {
            viewPath = webInfPath + path;
        }

        File file = new File(getServletContext().getRealPath(viewPath));

        if (file.exists() && file.isFile()) {
            resp.setContentType("text/html; charset=UTF-8");
            try (FileInputStream fis = new FileInputStream(file);
                    OutputStream os = resp.getOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } else {
            resp.getWriter().println(path);
        }
    }

}