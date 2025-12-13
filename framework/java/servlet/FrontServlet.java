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
        String requestMethod = req.getMethod().toUpperCase();

        RouteInfo matchedRoute = null;
        Map<String, String> pathVars = null;
        for (RouteInfo route : routes) {
            Map<String, String> vars = matchPath(route.getUrl(), path);
            if (vars != null && route.getHttpMethod().equals(requestMethod)) {
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
                Map<String, Object> injectedParams = new HashMap<>();
                // sprint-8
                for (int i = 0; i < params.length - 2; i++) {
                    Parameter p = params[i];
                    if (p.getType() == Map.class) {
                        Map<String, String> paramsMap = new HashMap<>();
                        Enumeration<String> parameterNames = req.getParameterNames();
                        while (parameterNames.hasMoreElements()) {
                            String paramName = parameterNames.nextElement();
                            String[] paramValues = req.getParameterValues(paramName);
                            if (paramValues != null) {
                                if (paramValues.length == 1) {
                                    paramsMap.put(paramName, paramValues[0]);
                                } else {
                                    paramsMap.put(paramName, String.join(", ", paramValues));
                                }
                            }
                        }

                        invokeArgs.add(paramsMap);
                        injectedParams.put("Map<params>", paramsMap);

                        continue;
                    }

                    // ===== SPRINT 8-bis : Détection de @ModelAttribute =====
                    ModelAttribute modelAttr = p.getAnnotation(ModelAttribute.class);
                    if (modelAttr != null) {
                        Class<?> modelClass = p.getType();

                        try {
                            // Créer une instance de l'objet
                            Object modelInstance = modelClass.getDeclaredConstructor().newInstance();

                            // Récupérer tous les paramètres de la requête
                            Enumeration<String> parameterNames = req.getParameterNames();

                            while (parameterNames.hasMoreElements()) {
                                String fullParamName = parameterNames.nextElement();
                                String paramValue = req.getParameter(fullParamName);

                                // Gérer la notation pointée : departement.nom, departement.id_departement
                                if (fullParamName.contains(".")) {
                                    String[] parts = fullParamName.split("\\.", 2);
                                    String objectName = parts[0]; // "departement"
                                    String fieldName = parts[1]; // "nom" ou "id_departement"

                                    // Chercher le getter de l'objet imbriqué
                                    String getterName = "get" + capitalize(objectName);
                                    String setterName = "set" + capitalize(objectName);

                                    try {
                                        Method getter = modelClass.getMethod(getterName);
                                        Object nestedObject = getter.invoke(modelInstance);

                                        // Si l'objet imbriqué est null, le créer
                                        if (nestedObject == null) {
                                            Class<?> nestedClass = getter.getReturnType();
                                            nestedObject = nestedClass.getDeclaredConstructor().newInstance();

                                            // Setter l'objet imbriqué
                                            Method setter = modelClass.getMethod(setterName, nestedClass);
                                            setter.invoke(modelInstance, nestedObject);
                                        }

                                        // Maintenant, set la propriété de l'objet imbriqué
                                        setProperty(nestedObject, fieldName, paramValue);

                                    } catch (Exception e) {
                                        System.err.println("Erreur lors du traitement de " + fullParamName + ": "
                                                + e.getMessage());
                                    }
                                } else {
                                    // Propriété simple : nom, id_employe
                                    setProperty(modelInstance, fullParamName, paramValue);
                                }
                            }

                            invokeArgs.add(modelInstance);
                            injectedParams.put(modelClass.getSimpleName(), modelInstance);

                        } catch (Exception e) {
                            throw new RuntimeException("Erreur lors de la création du modèle " + modelClass.getName(),
                                    e);
                        }

                        continue;
                    }

                    String paramName;
                    Object val = null;
                    boolean isPathVariable = false;

                    PathVariable pathVariable = p.getAnnotation(PathVariable.class);
                    if (pathVariable != null) {
                        paramName = pathVariable.value();
                        isPathVariable = true;

                        // Recuperer depuis pathVars
                        if (pathVars != null && pathVars.containsKey(paramName)) {
                            String valStr = pathVars.get(paramName);
                            Class<?> type = p.getType();

                            if (type == String.class) {
                                val = valStr;
                            } else if (type == int.class || type == Integer.class) {
                                val = Integer.parseInt(valStr);
                            } else {
                                throw new IllegalArgumentException("Type non supporte pour @PathVariable : " + type);
                            }
                        } else {
                            throw new IllegalArgumentException("Variable de chemin manquante : " + paramName);
                        }
                    } else {
                        // Verifier si c'est un @RequestParam
                        RequestParam requestParam = p.getAnnotation(RequestParam.class);
                        if (requestParam != null) {
                            paramName = requestParam.value();
                        } else {
                            paramName = p.getName();
                        }
                        // Recuperer depuis les paramètres de requête
                        String valStr = req.getParameter(paramName);
                        if (valStr == null) {
                            throw new IllegalArgumentException("Parametre manquant : " + paramName);
                        }
                        Class<?> type = p.getType();
                        if (type == String.class) {
                            val = valStr;
                        } else if (type == int.class || type == Integer.class) {
                            val = Integer.parseInt(valStr);
                        } else {
                            throw new IllegalArgumentException("Type non supporte : " + type);
                        }
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
                    resp.getWriter().println("<p>HTTP Method : " + matchedRoute.getHttpMethod() + "</p>");
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

        // Extraire les noms des variables de pattern (ex: {id}, {nom})
        List<String> varNames = new ArrayList<>();
        String regex = pattern;

        // Remplacer {variable} par un groupe de capture
        java.util.regex.Pattern varPattern = java.util.regex.Pattern.compile("\\{([^}]+)\\}");
        java.util.regex.Matcher varMatcher = varPattern.matcher(pattern);

        while (varMatcher.find()) {
            varNames.add(varMatcher.group(1)); // Stocker le nom de la variable
        }

        // Convertir le pattern en regex : /departement/{id} -> /departement/([^/]+)
        regex = regex.replaceAll("\\{[^}]+\\}", "([^/]+)");
        regex = "^" + regex + "$";

        java.util.regex.Pattern pathPattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher pathMatcher = pathPattern.matcher(path);

        if (pathMatcher.matches()) {
            Map<String, String> vars = new HashMap<>();
            for (int i = 0; i < varNames.size(); i++) {
                vars.put(varNames.get(i), pathMatcher.group(i + 1));
            }
            return vars;
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

    private static String capitalize(String str) {
        if (str == null || str.isEmpty())
            return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static void setProperty(Object obj, String fieldName, String value) throws Exception {
        // Construire le nom du setter : setNom, setId_employe
        String setterName = "set" + capitalize(fieldName);
        // Chercher le setter avec différents types
        Method setter = null;
        Class<?> paramType = null;
        // Essayer avec String
        try {
            setter = obj.getClass().getMethod(setterName, String.class);
            paramType = String.class;
        } catch (NoSuchMethodException e) {
            // Essayer avec int
            try {
                setter = obj.getClass().getMethod(setterName, int.class);
                paramType = int.class;
            } catch (NoSuchMethodException e2) {
                // Essayer avec Integer
                try {
                    setter = obj.getClass().getMethod(setterName, Integer.class);
                    paramType = Integer.class;
                } catch (NoSuchMethodException e3) {
                    System.err.println("Aucun setter trouvé pour : " + fieldName);
                    return;
                }
            }
        }
        if (paramType == String.class) {
            setter.invoke(obj, value);
        } else if (paramType == int.class || paramType == Integer.class) {
            setter.invoke(obj, Integer.parseInt(value));
        }
    }
}