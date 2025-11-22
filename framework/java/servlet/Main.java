package servlet;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        List<String> packagesToScan = Arrays.asList("test.java.controller");
        List<RouteInfo> routes = AnnotationScanner.scanRoutesForApp(packagesToScan);

        System.out.println("\n=== Routes detectees ===");
        for (RouteInfo route : routes) {
            System.out.println(route);
        }
    }
}