package servlet;

public class Main {
    
    public static void main(String[] args) throws Exception {
        String packageToScan = "test.java.controller";
        AnnotationScanner.printWebRouteAnnotations(packageToScan);
    }
}