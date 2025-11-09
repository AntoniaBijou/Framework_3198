package servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AnnotationScanner {

    public static Map<String, RouteInfo> scanRoutesForApp(List<String> packagesToScan) {
        Map<String, RouteInfo> routes = new HashMap<>();
        System.out.println("=== Scan des routes au demarrage (packages: " + packagesToScan + ") ===");

        for (String packageName : packagesToScan) {
            try {
                List<Class<?>> classes = findClassesInPackage(packageName);
                for (Class<?> clazz : classes) {
                    if (clazz.isAnnotationPresent(Controller.class)) {  //  VeRIFIE @Controller
                        System.out.println("Controleur trouve : " + clazz.getSimpleName());
                        for (Method method : clazz.getDeclaredMethods()) {
                            WebRoute annotation = method.getAnnotation(WebRoute.class);
                            if (annotation != null && method.getReturnType() == String.class) {  //  VeRIFIE retour String
                                String url = annotation.url();
                                if (!routes.containsKey(url)) {
                                    routes.put(url, new RouteInfo(clazz, method, url));
                                    System.out.println("  Route : " + url + " → " + method.getName());
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur scan package " + packageName + " : " + e.getMessage());
            }
        }

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
        if (!Files.isDirectory(directory)) return classes;

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
}