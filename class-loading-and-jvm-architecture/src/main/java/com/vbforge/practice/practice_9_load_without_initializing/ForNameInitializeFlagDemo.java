package com.vbforge.practice.practice_9_load_without_initializing;

/**
 * Class.forName(name) initialises the class (static blocks run NOW).
 * Class.forName(name, false, loader) only loads it; initialisation waits for the
 * first active use. A class literal never initialises anything.
 */
public class ForNameInitializeFlagDemo {

    public static void main(String[] args) throws Exception {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        String loud = LoudPlugin.class.getName();
        String eager = EagerPlugin.class.getName();

        System.out.println("1. Class.forName(LoudPlugin, false, loader)");
        Class<?> loaded = Class.forName(loud, false, loader);
        System.out.println("   loaded " + loaded.getSimpleName() + ", no static initializer output above");

        System.out.println("2. Class.forName(EagerPlugin)  (single argument = initialize true)");
        Class.forName(eager);

        System.out.println("3. first active use of LoudPlugin: LoudPlugin.touch()");
        LoudPlugin.touch();

        System.out.println("4. Class.forName(LoudPlugin) again: already initialised, nothing runs twice");
        Class.forName(loud);
        System.out.println("   done");
    }
}
