package servlet;

public class Main {
    
    public static void main(String[] args) throws Exception {
        // Package à scanner (doit exister dans le classpath du projet test)
        String packageToScan = "test.java.controller";

        // Appel du scanner
        AnnotationScanner.printWebRouteAnnotations(packageToScan);
    }
}