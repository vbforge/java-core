package com.vbforge.practice.practice_7_config_driven_invocation;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

/**
 * The class and the method to call come from configuration (key=value text), not from code:
 * Class.forName(data) -> instance -> find the method called "action" -> invoke.
 * The configuration is an in-memory string, so the demo needs no resource file.
 */
public class ConfigDrivenInvokeDemo {

    private static final String CONFIG =
            "data=com.vbforge.reflection_api.practice.practice_7_config_driven_invocation.Sportsmen\n"
                    + "action=jump\n";

    public static void main(String[] args) throws Exception {
        Properties properties = read();
        String data = properties.getProperty("data");
        String action = properties.getProperty("action");

        Class<?> dataClass = Class.forName(data);
        Object object = dataClass.getDeclaredConstructor().newInstance();

        Method[] methods = dataClass.getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(Method::getName));
        Method actionMethod = null;
        for (Method m : methods) {
            String nameMethod = m.getName();
            System.out.println(nameMethod);
            if (nameMethod.equals(action)) {
                actionMethod = m;
            }
        }
        if (actionMethod == null) {
            System.out.println("action not found");
        } else {
            actionMethod.invoke(object);
        }
    }

    private static Properties read() throws IOException {
        Properties prop = new Properties();
        prop.load(new StringReader(CONFIG));
        return prop;
    }
}
