package servlet;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {
        List<String> packagesToScan = Arrays.asList("test.java.controller");
        Map<String, RouteInfo> routes = AnnotationScanner.scanRoutesForApp(packagesToScan);

        System.out.println("\n=== Routes détectées ===");
        for (Map.Entry<String, RouteInfo> entry : routes.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}