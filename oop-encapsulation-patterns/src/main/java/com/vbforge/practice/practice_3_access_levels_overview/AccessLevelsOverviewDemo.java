package com.vbforge.practice.practice_3_access_levels_overview;

/**
 * The four Java access levels, seen from inside the declaring class.
 *
 * Modifier    | Class | Package | Subclass | World
 * ------------|-------|---------|----------|-------
 * private     |  yes  |   no    |   no     |  no
 * (package)   |  yes  |   yes   |   no*    |  no     <- default, no keyword
 * protected   |  yes  |   yes   |   yes    |  no
 * public      |  yes  |   yes   |   yes    |  yes
 *
 * (*) a subclass in the SAME package sees it through the package rule.
 *
 * Rules of thumb:
 *   - Fields: almost always private
 *   - Methods: as restrictive as possible; public only if part of the API
 *   - Prefer private over package-private for implementation details
 *   - protected: use sparingly, it couples you to every subclass
 */
public class AccessLevelsOverviewDemo {

    // private: this class only
    private String secretKey = "xK9#mP2$";

    // package-private: same package only
    String packageNote = "visible inside com.vbforge.oop_encapsulation_patterns.practice.practice_3_access_levels_overview";

    // protected: same package + subclasses
    protected int protectedCounter = 0;

    // public: everywhere
    public String publicLabel = "I am public";

    private String computeHash() {
        return Integer.toHexString(secretKey.hashCode());
    }

    /** Public API delegates to a private implementation step. */
    public String getHash() {
        return computeHash();
    }

    /** Hook for subclasses. */
    protected void onProcess() {
        System.out.println("Base processing (protectedCounter=" + protectedCounter + ")");
    }

    public void process() {
        protectedCounter++;
        onProcess();
    }

    public static void main(String[] args) {
        System.out.println("=== Access levels overview ===\n");

        AccessLevelsOverviewDemo obj = new AccessLevelsOverviewDemo();

        System.out.println("-- Everything is visible from inside the same class --");
        System.out.println("private field:         " + obj.secretKey);
        System.out.println("package-private field: " + obj.packageNote);
        System.out.println("protected field:       " + obj.protectedCounter);
        System.out.println("public field:          " + obj.publicLabel);

        System.out.println("\n-- Public API hides the private implementation --");
        System.out.println("getHash() (public): " + obj.getHash());

        System.out.println("\n-- Subclass using the protected hook --");
        AuditedProcessor ap = new AuditedProcessor();
        ap.process();
        ap.process();

        System.out.println("\n-- Summary --");
        System.out.println("private     -> this class only");
        System.out.println("(package)   -> this package only");
        System.out.println("protected   -> this package + subclasses");
        System.out.println("public      -> everywhere");
    }
}
