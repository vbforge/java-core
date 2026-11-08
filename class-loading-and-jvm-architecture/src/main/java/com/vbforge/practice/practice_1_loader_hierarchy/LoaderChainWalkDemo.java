package com.vbforge.practice.practice_1_loader_hierarchy;

/**
 * Walk the parent chain of the application loader. Bootstrap counts as depth 0
 * (it is the null at the end of the chain), so the application loader is at depth 2:
 * application -> platform (Java 9+) / extension (Java 8) -> bootstrap.
 */
public class LoaderChainWalkDemo {

    public static void main(String[] args) {
        ClassLoader app = LoaderChainWalkDemo.class.getClassLoader();

        System.out.println("depth of the application loader = " + depthOf(app));
        System.out.println("depth of String's loader (null) = " + depthOf(String.class.getClassLoader()));

        int level = depthOf(app);
        for (ClassLoader loader = app; loader != null; loader = loader.getParent()) {
            System.out.println("level " + level + ": " + role(loader));
            level--;
        }
        System.out.println("level 0: <bootstrap> (null, not an object)");
    }

    /** Bootstrap (null) = 0; every parent step towards the leaf adds one. */
    static int depthOf(ClassLoader loader) {
        int depth = 0;
        for (ClassLoader l = loader; l != null; l = l.getParent()) {
            depth++;
        }
        return depth;
    }

    static String role(ClassLoader loader) {
        if (loader == ClassLoader.getSystemClassLoader()) {
            return "application (system) loader";
        }
        return "intermediate loader (platform on Java 9+, extension on Java 8)";
    }
}
