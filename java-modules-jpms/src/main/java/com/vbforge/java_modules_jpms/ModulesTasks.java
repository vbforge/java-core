package com.vbforge.java_modules_jpms;

import java.util.Set;

/**
 * ModulesTasks — implement every method below to make {@code ModulesTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code ModulesDemo.java} to see each API in action.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Remove {@code @Disabled} from the corresponding {@code @Nested} class in the test.</li>
 *   <li>Run {@code mvn test -Dtest=ModulesTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 */
public class ModulesTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if {@code clazz} belongs to a <em>named</em> module,
     * {@code false} if it belongs to the unnamed module.
     *
     * <p>Examples:
     * <pre>
     *   isNamedModule(String.class)                 → true    (java.base)
     *   isNamedModule(java.sql.Connection.class)    → true    (java.sql)
     *   isNamedModule(ModulesTasks.class)           → false   (test classpath)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Every {@code Class} has a {@code getModule()} method.</li>
     *   <li>{@code Module.isNamed()} is exactly the predicate you need — no
     *       string comparisons, no name checks.</li>
     * </ul>
     *
     * @param clazz the class to inspect (never null in the tests)
     * @return {@code true} if the class is in a named module
     */
    public boolean isNamedModule(Class<?> clazz) {
        throw new UnsupportedOperationException("TODO: implement isNamedModule");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the name of the module that {@code clazz} belongs to, or
     * {@code null} if the module is unnamed.
     *
     * <p>Examples:
     * <pre>
     *   moduleNameOf(String.class)                  → "java.base"
     *   moduleNameOf(java.sql.Connection.class)     → "java.sql"
     *   moduleNameOf(ModulesTasks.class)            → null   (unnamed module)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Module.getName()} already returns {@code null} for unnamed
     *       modules — no extra check is needed.</li>
     * </ul>
     *
     * @param clazz the class to inspect
     * @return the module name, or {@code null} for the unnamed module
     */
    public String moduleNameOf(Class<?> clazz) {
        throw new UnsupportedOperationException("TODO: implement moduleNameOf");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the set of module names that {@code moduleName} declares in its
     * {@code requires} directives. If no module with that name exists in the
     * boot {@code ModuleLayer}, returns an empty set.
     *
     * <p>Examples:
     * <pre>
     *   requiresOf("java.base")      → {}                                    (java.base requires nothing)
     *   requiresOf("java.sql")       → {"java.base", "java.logging", "java.xml", …}
     *   requiresOf("no.such.module") → {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Look up the module with {@code ModuleLayer.boot().findModule(name)}
     *       — it returns {@code Optional<Module>}.</li>
     *   <li>{@code Module.getDescriptor().requires()} returns
     *       {@code Set<ModuleDescriptor.Requires>}.</li>
     *   <li>Each {@code Requires} exposes {@code name()}.</li>
     * </ul>
     *
     * @param moduleName the module whose {@code requires} to read
     * @return set of required module names; empty if the module is absent
     */
    public Set<String> requiresOf(String moduleName) {
        throw new UnsupportedOperationException("TODO: implement requiresOf");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the set of package names that {@code moduleName} declares in its
     * {@code exports} directives. If no module with that name exists in the
     * boot {@code ModuleLayer}, returns an empty set.
     *
     * <p>Examples:
     * <pre>
     *   exportsOf("java.base")      → contains "java.lang", "java.util", "java.io", … (~50 packages)
     *   exportsOf("java.sql")       → contains "java.sql"
     *   exportsOf("no.such.module") → {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code ModuleDescriptor.exports()} returns
     *       {@code Set<ModuleDescriptor.Exports>}.</li>
     *   <li>Each {@code Exports} exposes {@code source()} — the package name.</li>
     * </ul>
     *
     * @param moduleName the module whose {@code exports} to read
     * @return set of exported package names; empty if the module is absent
     */
    public Set<String> exportsOf(String moduleName) {
        throw new UnsupportedOperationException("TODO: implement exportsOf");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the names of every module in the boot {@code ModuleLayer}.
     *
     * <p>Examples:
     * <pre>
     *   bootLayerModuleNames()   → {"java.base", "java.sql", "java.logging", "java.xml", …} (~70 modules)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code ModuleLayer.boot().modules()} returns {@code Set<Module>}.</li>
     *   <li>{@code Module.getName()} gives the name — never {@code null} for
     *       boot-layer modules.</li>
     * </ul>
     *
     * @return set of all boot-layer module names
     */
    public Set<String> bootLayerModuleNames() {
        throw new UnsupportedOperationException("TODO: implement bootLayerModuleNames");
    }
}