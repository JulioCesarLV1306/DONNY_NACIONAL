package util;

import java.util.HashMap;
import java.util.Map;

public class Memory {
    private Memory() {
    }

    private static Map<String, Object> datos;
    static {
        datos = new HashMap<>();
    }

    /*public static void putClean(String key, Object value) {
        datos.clear();
        datos.put(key, value);
    }*/
    
    public static void put(String key, Object value) {
        datos.put(key, value);
    }

    public static Object get(String key) {
        return datos.get(key);
    }

    public static boolean containsKey(String key){
        return datos.containsKey(key);
    }

    public static void remove(String key){
        datos.remove(key);
    }
}
