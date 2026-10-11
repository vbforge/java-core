# maven-recap

> **Phase 1 — Basics** | Java 17+ | Package: `com.vbforge.maven_recap`

## What's covered

| Topic | Key points |
|---|---|
| `pom.xml` | Project Object Model; `modelVersion` always `4.0.0`; convention over configuration |
| Coordinates | `groupId` / `artifactId` / `version` / `packaging`; `groupId:artifactId:version` string format |
| Dependency scopes | `compile`, `provided`, `runtime`, `test`, `system`; only `compile` and `runtime` are transitive |
| Lifecycle | `validate → compile → test → package → verify → install → deploy`; each phase implies all upstream |
| Plugins | Goals bound to phases; `compiler`, `surefire`, `jar`, `install`, `deploy` default bindings |
| Wrapper | `mvnw` / `mvnw.cmd` + `.mvn/wrapper/maven-wrapper.properties`; pinned Maven per repo |

## Files

```
src/
├── main/java/com/vbforge/maven_recap/
│   ├── MavenDemo.java        ← theory + annotated examples
│   └── MavenTasks.java       ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/maven_recap/
    ├── MavenDemoTest.java     ← tests for the demo (already green)
    └── MavenTasksTest.java    ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Open `MavenTasks.java` and implement all 6 methods. Each has a detailed Javadoc.

| # | Method | Topic |
|---|---|---|
| 1 | `toCoordinateString(groupId, artifactId, version)` | Coordinate string format with validation |
| 2 | `isValidArtifactId(id)` | Name rules — lowercase, kebab-case, starts with a letter |
| 3 | `isTransitiveScope(scope)` | Scope semantics — which scopes propagate downstream |
| 4 | `nextPhase(current)` | Lifecycle progression — next phase or `null` |
| 5 | `defaultLifecycle()` | The 7 default-lifecycle phases in order |
| 6 | `distributionUrl(mavenVersion)` | Wrapper `distributionUrl` for a given Maven version |

```bash
mvn test -Dtest=MavenTasksTest
```

All 6 task groups must go **GREEN** ✅

## Key things to remember

```java
// Coordinate string — used in commands like mvn dependency:get
// groupId:artifactId:version
"com.vbforge:java-core:1.0.0"

// ArtifactId rules — lowercase, kebab-case, starts with a letter
"java-core"     // ✅
"Java-Core"     // ❌ uppercase
"java core"     // ❌ space
"1-java"        // ❌ starts with digit

// Scope transitivity — only compile and runtime propagate to consumers
compile   // ✅ transitive
runtime   // ✅ transitive
provided  // ❌ container-provided, not shipped
test      // ❌ test-only
system    // ❌ path-provided

// Lifecycle — running a phase runs all phases before it
mvn install   // also compiles, tests, packages
mvn package   // also compiles and tests, but doesn't install

// Wrapper — pin the Maven version in the repo
./mvnw clean install                         // ✅ uses repo-pinned Maven
mvn clean install                            // ⚠️  uses whatever is on PATH
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest=MavenDemoTest

# Task tests only
mvn test -Dtest=MavenTasksTest

# Run main demo
mvn exec:java -Dexec.mainClass="com.vbforge.maven_recap.MavenDemo"
```

---

A few topics that are missing, so considering adding them later as a `maven-mastery-lab` and creating a separate repo on github where include both (basic from here and advance as plan):
- **Dependency management:** `dependencyManagement`, parent POM inheritance and BOMs (you already rely on these in your parent pom).
- **Transitive resolution:** "nearest wins", exclusions and `mvn dependency:tree` for diagnosing conflicts.
- **Repositories:** the local `~/.m2`, Maven Central and `settings.xml`.
- **Properties and profiles:** `-P`, plus the difference between `SNAPSHOT` and release versions.
- **Multi-module projects:** the reactor and aggregator versus parent.
- **Common commands:** `mvn clean verify`, `-DskipTests`, `-pl`/`-am`, and the difference between `verify` and `install`.



