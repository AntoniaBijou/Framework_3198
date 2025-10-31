package servlet;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Main {
    
    @WebRoute(url = "/home")
    public static void accueil() {
        System.out.println("Méthode annotée exécutée : accueil()");
    }

    public static void autre() {
        System.out.println("Méthode non annotée : autre()");
    }

    public static void main(String[] args) throws Exception {
        for (Method m : Main.class.getDeclaredMethods()) {
            if (m.isAnnotationPresent(WebRoute.class)) {
                WebRoute ann = m.getAnnotation(WebRoute.class);
                System.out.println("Méthode annotée trouvée: " + m.getName());
                System.out.println("URL de l'annotation: " + ann.url());
                if (Modifier.isStatic(m.getModifiers())) {
                    m.invoke(null);
                } else {
                    m.invoke(Main.class.getDeclaredConstructor().newInstance());
                }
                break;
            }
        }
    }
}