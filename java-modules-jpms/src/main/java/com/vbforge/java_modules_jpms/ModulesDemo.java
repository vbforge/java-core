package com.vbforge.java_modules_jpms;

import java.lang.module.ModuleDescriptor;
import java.sql.Connection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ModulesDemo — the Java Platform Module System, taught from the runtime side.
 *
 * <p>Read top-to-bottom. The four sections cover, in order:
 * <ol>
 *   <li>{@code module-info.java} — the declaration file, its directives, what each does</li>
 *   <li>The runtime {@link Module} API — {@code Class.getModule()}, {@link ModuleLayer#boot()}</li>
 *   <li>{@link ModuleDescriptor} — reading a real module's {@code requires} and {@code exports}</li>
 *   <li>Named vs unnamed modules — classpath vs module path</li>
 * </ol>
 *
 * <p><b>About the demo itself:</b> this demo is <em>not</em> packaged as a
 * named module — it has no {@code module-info.java} of its own. That is
 * deliberate: the module system is a packaging concern, and this demo teaches
 * the concept through the runtime API that works on any JVM 9+ regardless of
 * how the current application is launched. The full {@code module-info.java}
 * syntax a real project would ship is shown in {@link #MODULE_INFO_EXAMPLE}.
 */
public class ModulesDemo {

    /**
     * A complete, syntactically valid {@code module-info.java} for this module.
     *
     * <p>Each directive is annotated with its one-line meaning. To actually
     * ship this module as a named module, save this text to
     * {@code src/main/java/module-info.java} and rebuild — the module becomes
     * visible to other modular code via {@code requires com.vbforge.java_modules_jpms;}.
     *
     * <p>Note: adding a real {@code module-info.java} to this Maven submodule
     * would force {@code maven-surefire} onto the module path, and reflection-heavy
     * test frameworks (JUnit 5, AssertJ) then require additional
     * {@code --add-opens}/{@code --add-reads} configuration. That's why the
     * example lives here as a string rather than as a real file.
     */
    public static final String MODULE_INFO_EXAMPLE =
        "module com.vbforge.java_modules_jpms {\n" +
        "    requires java.sql;                          // compile-time dependency\n" +
        "    requires java.logging;\n" +
        "    exports com.vbforge.java_modules_jpms;      // public API, visible to other modules\n" +
        "    // opens com.vbforge.java_modules_jpms.internal;  // reflection-only access\n" +
        "    // uses com.example.Service;                      // this module consumes a service\n" +
        "    // provides com.example.Service with com.example.Impl; // this module supplies one\n" +
        "}\n";

    public static void main(String[] args) {
        moduleInfoDemo();
        System.out.println("---");
        moduleApiDemo();
        System.out.println("---");
        moduleDescriptorDemo();
        System.out.println("---");
        namedVsUnnamedDemo();
    }

    // ── 1. module-info.java ───────────────────────────────────────────────────

    /**
     * The module declaration file and its directives.
     *
     * <p><b>Why:</b> {@code module-info.java} is where a project declares which
     * modules it <em>requires</em>, which packages it <em>exports</em>, which
     * packages it <em>opens</em> to reflection, and which services it
     * <em>uses</em> or <em>provides</em>. It replaces the jar-hell problem with
     * a compile-time and runtime checkable dependency graph.
     *
     * <p><b>Directives:</b>
     * <ul>
     *   <li>{@code requires M} — this module depends on module M</li>
     *   <li>{@code requires transitive M} — any module that requires <em>this</em> one
     *       also implicitly requires M (API leak)</li>
     *   <li>{@code exports P} — other modules may read public types in package P</li>
     *   <li>{@code opens P} — other modules may reflect into package P at runtime</li>
     *   <li>{@code uses S} — this module consumes service interface S via {@link java.util.ServiceLoader}</li>
     *   <li>{@code provides S with I} — this module supplies I as an implementation of S</li>
     * </ul>
     *
     * <p><b>Gotcha:</b> a package that is neither {@code exports}'d nor
     * {@code opens}'d is completely inaccessible to other modules — even via
     * reflection. That is the default — most packages must be explicitly exported.
     */
    static void moduleInfoDemo() {
        System.out.println("=== module-info.java syntax ===");
        System.out.println(MODULE_INFO_EXAMPLE);
    }

    // ── 2. Module runtime API ────────────────────────────────────────────────

    /**
     * Inspecting modules at runtime.
     *
     * <p><b>Why:</b> the {@link Module} class represents a loaded module and
     * exposes its name, descriptor, and whether it is <em>named</em> or
     * <em>unnamed</em>. {@link ModuleLayer#boot()} is the layer that contains
     * every JDK module plus anything else placed on the module path at JVM
     * start-up.
     *
     * <p><b>Stability:</b> the JDK modules ({@code java.base}, {@code java.sql},
     * {@code java.logging}, {@code java.xml}) are present in every Java 9+
     * runtime — safe to assert against in tests.
     */
    static void moduleApiDemo() {
        System.out.println("=== Module runtime API ===");
        ModuleLayer boot = ModuleLayer.boot();

        Set<String> names = boot.modules().stream()
            .map(Module::getName)
            .collect(Collectors.toSet());

        System.out.println("boot layer size : " + names.size());
        System.out.println("java.base there : " + names.contains("java.base"));
        System.out.println("java.sql there  : " + names.contains("java.sql"));
        System.out.println("java.logging    : " + names.contains("java.logging"));
        System.out.println("findModule miss : " + boot.findModule("no.such.module").isEmpty());

        Module stringModule = String.class.getModule();
        System.out.println("String's module : " + stringModule.getName());
        System.out.println("String named    : " + stringModule.isNamed());
    }

    // ── 3. ModuleDescriptor ──────────────────────────────────────────────────

    /**
     * Reading a module's metadata via {@link ModuleDescriptor}.
     *
     * <p><b>Why:</b> the descriptor is a parsed, immutable view of the
     * {@code module-info.class} that produced the module. Tools that need to
     * reason about a module — dependency analysers, classloaders, build tools
     * — read from it.
     *
     * <p><b>Examples shown:</b>
     * <ul>
     *   <li>{@code java.base} requires <em>nothing</em> — it is the root</li>
     *   <li>{@code java.base} exports ~50 packages ({@code java.lang}, {@code java.util}, …)</li>
     *   <li>{@code java.sql} requires {@code java.base}, {@code java.logging}, {@code java.xml}, …</li>
     * </ul>
     *
     * <p><b>Gotcha:</b> {@link ModuleDescriptor} returns {@code null} from
     * {@code Module.getDescriptor()} for <em>unnamed</em> modules — always
     * null-check before dereferencing.
     */
    static void moduleDescriptorDemo() {
        System.out.println("=== ModuleDescriptor ===");

        ModuleDescriptor baseDescriptor = String.class.getModule().getDescriptor();
        System.out.println("name            : " + baseDescriptor.name());

        Set<String> baseRequires = baseDescriptor.requires().stream()
            .map(ModuleDescriptor.Requires::name)
            .collect(Collectors.toSet());
        System.out.println("requires count  : " + baseRequires.size());  // 0

        Set<String> baseExports = baseDescriptor.exports().stream()
            .map(ModuleDescriptor.Exports::source)
            .collect(Collectors.toSet());
        System.out.println("exports count   : " + baseExports.size());
        System.out.println("exports java.lang: " + baseExports.contains("java.lang"));
        System.out.println("exports java.util: " + baseExports.contains("java.util"));

        ModuleDescriptor sqlDescriptor = Connection.class.getModule().getDescriptor();
        Set<String> sqlRequires = sqlDescriptor.requires().stream()
            .map(ModuleDescriptor.Requires::name)
            .collect(Collectors.toSet());
        System.out.println("java.sql reqs   : " + sqlRequires);
    }

    // ── 4. Named vs unnamed ──────────────────────────────────────────────────

    /**
     * Named vs unnamed modules — the single most important distinction.
     *
     * <p><b>Named:</b> a module with an actual {@code module-info} declaration —
     * either a JDK module or a jar on the module path. Its {@link Module#isNamed()}
     * returns {@code true} and {@link Module#getName()} returns its name.
     *
     * <p><b>Unnamed:</b> a "catch-all" module that the JVM creates implicitly for
     * every class placed on the <em>classpath</em>. Its {@code isNamed()} returns
     * {@code false}, and {@code getName()} returns {@code null}. Every unnamed
     * module reads every named module — that's why classpath code still works
     * when JDK packages move into modules.
     *
     * <p><b>Gotcha:</b> unnamed modules are <em>not</em> a bug or a fallback —
     * they are the correct description of "loaded from the classpath". All
     * pre-existing Java libraries live in the unnamed module until their
     * maintainers add a {@code module-info.java}.
     */
    static void namedVsUnnamedDemo() {
        System.out.println("=== Named vs unnamed ===");

        Module stringModule = String.class.getModule();
        System.out.println("String.class    : named=" + stringModule.isNamed()
            + ", name=" + stringModule.getName()
            + ", descriptor=" + (stringModule.getDescriptor() != null));

        Module ours = ModulesDemo.class.getModule();
        System.out.println("ModulesDemo     : named=" + ours.isNamed()
            + ", name=" + ours.getName()
            + ", descriptor=" + (ours.getDescriptor() != null));
        // At `mvn test` and `mvn exec:java`, this class is loaded from the
        // classpath and therefore lives in the unnamed module.
    }
}