package servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AnnotationScanner {

    public static List<RouteInfo> scanRoutesForApp(List<String> packagesToScan) {
        List<RouteInfo> routes = new ArrayList<>();
        System.out.println("=== Scan des routes au demarrage (packages: " + packagesToScan + ") ===");
        for (String packageName : packagesToScan) {
            try {
                List<Class<?>> classes = findClassesInPackage(packageName);
                System.out.println("DEBUG : Classes trouvees dans '" + packageName + "' : " + classes.size());
                for (Class<?> clazz : classes) {
                    System.out.println(" -> " + clazz.getName());
                }
                for (Class<?> clazz : classes) {
                    if (clazz.isAnnotationPresent(Controller.class)) {
                        System.out.println("Contrôleur trouve : " + clazz.getSimpleName());
                        for (Method method : clazz.getDeclaredMethods()) {
                            WebRoute annotation = method.getAnnotation(WebRoute.class);
                            if (annotation != null) {
                                if (validateMethodSignature(method)) {
                                    String url = annotation.url();
                                    routes.add(new RouteInfo(clazz, method, url));
                                    System.out.println(" Route : " + url + " → " + method.getName());
                                } else {
                                    System.err.println(
                                            "Methode invalide : " + method.getName() + " (signature incorrecte)");
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur scan package " + packageName + " : " + e.getMessage());
                e.printStackTrace();
            }
        }
       
        routes.sort(Comparator.comparingInt((RouteInfo r) -> r.getUrl().length()).reversed());
        System.out.println("Total routes chargees : " + routes.size());
        return routes;
    }

    public static List<Class<?>> findClassesInPackage(String packageName)
            throws IOException, URISyntaxException, ClassNotFoundException {

        List<Class<?>> classes = new ArrayList<>();
        String packagePath = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(packagePath);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                Path path = Paths.get(resource.toURI());
                classes.addAll(scanDirectory(packageName, path));
            } else if (resource.getProtocol().equals("jar")) {
                JarURLConnection jarConn = (JarURLConnection) resource.openConnection();
                try (JarFile jarFile = jarConn.getJarFile()) {
                    classes.addAll(scanJar(packageName, jarFile));
                }
            }
        }
        return classes;
    }

    private static List<Class<?>> scanDirectory(String packageName, Path directory)
            throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!Files.isDirectory(directory))
            return classes;

        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".class")) {
                    String className = buildClassName(packageName, directory, file);
                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return classes;
    }

    private static List<Class<?>> scanJar(String packageName, JarFile jarFile) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        String packagePath = packageName.replace('.', '/');

        for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.startsWith(packagePath) && name.endsWith(".class") && !entry.isDirectory()) {
                String className = name.replace('/', '.').substring(0, name.length() - 6);
                classes.add(Class.forName(className));
            }
        }
        return classes;
    }

    private static String buildClassName(String packageName, Path root, Path file) {
        String relative = root.relativize(file).toString();
        return packageName + "." + relative.replace(File.separator, ".").substring(0, relative.length() - 6);
    }

    private static boolean validateMethodSignature(Method method) {
        if (method.getReturnType() != String.class)
            return false;
        Parameter[] params = method.getParameters();
        if (params.length < 2)
            return false;
        if (params[params.length - 2].getType() != HttpServletRequest.class ||
                params[params.length - 1].getType() != HttpServletResponse.class) {
            return false;
        }
        for (int i = 0; i < params.length - 2; i++) {
            Class<?> type = params[i].getType();
            if (type != String.class && type != int.class && type != Integer.class) {
                return false;
            }
        }
        return true;
    }
}