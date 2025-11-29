package servlet;

import java.lang.reflect.Method;

public class RouteInfo {
    private final Class<?> controllerClass;
    private final Method method;
    private final String url;

    public RouteInfo(Class<?> controllerClass, Method method, String url) {
        this.controllerClass = controllerClass;
        this.method = method;
        this.url = url;
    }

    public Class<?> getControllerClass() { 
        return controllerClass; 
    }
    
    public Method getMethod() { 
        return method; 
    }
    
    public String getUrl() { 
        return url; 
    }


    @Override
    public String toString() {
        return "RouteInfo{url='" + url + "', method=" + method.getName() + "}";
    }
}