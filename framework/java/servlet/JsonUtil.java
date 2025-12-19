package servlet;

import java.lang.reflect.Method;
import java.util.*;

public class JsonUtil {

    public static String toJson(Object obj) {
        if (obj == null)
            return "null";
        if (obj instanceof String)
            return quote((String) obj);
        if (obj instanceof Number || obj instanceof Boolean)
            return obj.toString();
        if (obj instanceof Map)
            return mapToJson((Map<?, ?>) obj);
        if (obj instanceof Collection)
            return collectionToJson((Collection<?>) obj);
        // Fallback for POJO
        return pojoToJson(obj);
    }

    private static String quote(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r") + "\"";
    }

    private static String mapToJson(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<?, ?> e : map.entrySet()) {
            if (!first)
                sb.append(",");
            first = false;
            sb.append(quote(String.valueOf(e.getKey()))).append(":").append(toJson(e.getValue()));
        }
        sb.append("}");
        return sb.toString();
    }

    private static String collectionToJson(Collection<?> coll) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (Object o : coll) {
            if (!first)
                sb.append(",");
            first = false;
            sb.append(toJson(o));
        }
        sb.append("]");
        return sb.toString();
    }

    private static String pojoToJson(Object obj) {
        try {
            Map<String, Object> props = new LinkedHashMap<>();
            Method[] methods = obj.getClass().getMethods();
            for (Method m : methods) {
                if (m.getParameterCount() == 0) {
                    String name = m.getName();
                    String propName = null;
                    if (name.startsWith("get") && name.length() > 3 && !name.equals("getClass")) {
                        propName = name.substring(3, 4).toLowerCase() + name.substring(4);
                    } else if (name.startsWith("is") && name.length() > 2) {
                        propName = name.substring(2, 3).toLowerCase() + name.substring(3);
                    }
                    if (propName != null) {
                        Object val = m.invoke(obj);
                        props.put(propName, val);
                    }
                }
            }
            return mapToJson(props);
        } catch (Exception e) {
            // Fallback to string
            return quote(obj.toString());
        }
    }
}
