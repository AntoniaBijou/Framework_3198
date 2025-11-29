package servlet;

import java.lang.reflect.Method;

public class RouteInfo {
    private final Class<?> controllerClass;
    private final Method method;
    private final String url;
    private final String httpMethod; 

    public RouteInfo(Class<?> controllerClass, Method method, String url, String httpMethod) {
        this.controllerClass = controllerClass;
        this.method = method;
        this.url = url;
        this.httpMethod = httpMethod;
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
    
    public String getHttpMethod() { 
        return httpMethod; 
    }

    @Override
    public String toString() {
        return "RouteInfo{url='" + url + "', httpMethod='" + httpMethod + "', class=" + 
               controllerClass.getSimpleName() + ", method=" + method.getName() + "}";
    }
}