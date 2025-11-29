package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontServlet extends HttpServlet {
    private List<RouteInfo> routes;

    @Override
    public void init() throws ServletException {
        super.init();
        List<String> packagesToScan = Arrays.asList("test.java.controller");
        routes = AnnotationScanner.scanRoutesForApp(packagesToScan);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String fullPath = req.getRequestURI().substring(req.getContextPath().length());
        if ("/".equals(fullPath) || fullPath.isEmpty()) {
            resp.getWriter().println("Racine de l'app (routes chargees : " + routes.size() + ")");
            return;
        }
        String path = fullPath.replaceAll("/+$", "");

        RouteInfo matchedRoute = null;
        Map<String, String> pathVars = null;
        for (RouteInfo route : routes) {
            Map<String, String> vars = matchPath(route.getUrl(), path);
            if (vars != null) {
                matchedRoute = route;
                pathVars = vars;
                break;
            }
        }

        resp.setContentType("text/html; charset=UTF-8");
        if (matchedRoute != null) {
            try {
                if (pathVars != null) {
                    for (Map.Entry<String, String> entry : pathVars.entrySet()) {
                        req.setAttribute(entry.getKey(), entry.getValue());
                    }
                }
                Object instance = matchedRoute.getControllerClass().getDeclaredConstructor().newInstance();
                Method method = matchedRoute.getMethod();
                Parameter[] params = method.getParameters();
                List<Object> invokeArgs = new ArrayList<>();
                Map<String, Object> injectedParams = new HashMap<>(); // Optionnel : pour afficher les params injectes
                for (int i = 0; i < params.length - 2; i++) {
                    Parameter p = params[i];
                    String paramName = p.getName();
                    String valStr = req.getParameter(paramName);
                    if (valStr == null) {
                        throw new IllegalArgumentException("Parametre manquant : " + paramName);
                    }
                    Class<?> type = p.getType();
                    Object val;
                    if (type == String.class) {
                        val = valStr;
                    } else if (type == int.class || type == Integer.class) {
                        val = Integer.parseInt(valStr);
                    } else {
                        throw new IllegalArgumentException("Type non supporte : " + type);
                    }
                    invokeArgs.add(val);
                    injectedParams.put(paramName, val);
                }
                invokeArgs.add(req);
                invokeArgs.add(resp);
                Object result = method.invoke(instance, invokeArgs.toArray());
                if (result instanceof String) {
                    String viewName = (String) result;
                    String jspPath = "/WEB-INF/views/" + viewName + ".jsp";
                    String realJspPath = getServletContext().getRealPath(jspPath);
                    resp.getWriter().println("<h1>Route supportee : " + path + "</h1>");
                    resp.getWriter().println("<p>Classe : " + matchedRoute.getControllerClass().getName() + "</p>");
                    resp.getWriter().println("<p>Methode : " + matchedRoute.getMethod().getName() + "</p>");
                    if (!injectedParams.isEmpty()) {
                        resp.getWriter()
                                .println("<p><strong>Parametres injectes : " + injectedParams + "</strong></p>");
                    }
                    if (pathVars != null && !pathVars.isEmpty()) {
                        resp.getWriter().println("<p><strong>Variables de chemin : " + pathVars + "</strong></p>");
                    }
                    resp.getWriter().println("<p><strong>Fichier JSP : " + jspPath + "</strong></p>");
                    if (realJspPath != null) {
                        File jspFile = new File(realJspPath);
                        if (jspFile.exists()) {
                            resp.getWriter().println("<hr><h2>Contenu du JSP :</h2>");
                            RequestDispatcher dispatcher = req.getRequestDispatcher(jspPath);
                            dispatcher.include(req, resp);
                            return;
                        } else {
                            resp.getWriter().println("<p><strong>(Fichier JSP NON TROUVe)</strong></p>");
                        }
                    } else {
                        resp.getWriter().println("<p><strong>(Chemin JSP NON DISPONIBLE)</strong></p>");
                    }
                }
            } catch (Exception e) {
                resp.getWriter().println("Erreur invocation : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            String cleanPath = path.substring(1);
            String jspPath = "/WEB-INF/views/" + cleanPath + ".jsp";
            String realJspPath = getServletContext().getRealPath(jspPath);
            if (realJspPath != null) {
                File jspFile = new File(realJspPath);
                if (jspFile.exists()) {
                    RequestDispatcher dispatcher = req.getRequestDispatcher(jspPath);
                    dispatcher.forward(req, resp);
                    return;
                }
            }
            chercherRessource(req, resp, cleanPath, path);
        }
    }

    private Map<String, String> matchPath(String pattern, String path) {
        if (!pattern.startsWith("/"))
            pattern = "/" + pattern;
        if (!path.startsWith("/"))
            path = "/" + path;
        pattern = pattern.replaceAll("/+$", "");
        path = path.replaceAll("/+$", "");

        if (path.equals(pattern)) {
            return new HashMap<>();
        }

        if (path.startsWith(pattern + "/")) {
            String rest = path.substring(pattern.length() + 1);
            if (!rest.contains("/") && !rest.isEmpty()) {
                Map<String, String> vars = new HashMap<>();
                vars.put("id", rest);
                return vars;
            }
        }
        return null;
    }

    private void chercherRessource(HttpServletRequest req, HttpServletResponse resp, String cleanPath,
            String originalPath)
            throws ServletException, IOException {
        String viewPath = "/WEB-INF/views/" + cleanPath;
        String realPath = getServletContext().getRealPath(viewPath);
        if (realPath != null) {
            File file = new File(realPath);
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
                return;
            }
        }
        resp.getWriter().println("<h1>Il n'y a pas de route pour l'URL : " + originalPath + "</h1>");
    }
}