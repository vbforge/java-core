package com.vbforge.oop_inheritance_and_polymorphism;

import java.lang.reflect.Modifier;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

/**
 * AbstractClassDemo — annotated reference for abstract classes in Java.
 *
 * <p>Read top-to-bottom. Each section covers one facet of abstract classes with
 * concrete examples and a note explaining <em>why</em>.
 *
 * <p>Topics:
 * <ol>
 *   <li>Abstract class basics — {@code abstract} methods, no direct instances</li>
 *   <li>Constructors and state — what an abstract class can hold that an interface cannot</li>
 *   <li>Partial implementation — an abstract class extending an abstract class</li>
 *   <li>Template Method pattern — a fixed algorithm with abstract steps and optional hooks</li>
 *   <li>A JDK example — extending {@link AbstractList} by writing two methods</li>
 *   <li>Abstract class vs interface — one superclass, many interfaces</li>
 *   <li>Gotchas — what {@code abstract} can and cannot be combined with</li>
 * </ol>
 */
public class AbstractClassDemo {

    public static void main(String[] args) {
        basicsDemo();
        System.out.println("---");
        constructorsAndStateDemo();
        System.out.println("---");
        partialImplementationDemo();
        System.out.println("---");
        templateMethodDemo();
        System.out.println("---");
        abstractListDemo();
        System.out.println("---");
        abstractVsInterfaceDemo();
        System.out.println("---");
        gotchasDemo();
    }

    // ── Shared model for sections 1, 2, 3 and 6 ───────────────────────────────

    /**
     * Abstract base class: shared state ({@code title}), shared behaviour ({@code render}),
     * and one step that every subclass MUST supply ({@code content}).
     */
    abstract static class Document {
        private final String title;

        Document(String title) {
            if (title == null || title.isEmpty()) {
                throw new IllegalArgumentException("title must not be empty");   // validation lives ONCE, here
            }
            this.title = title;
        }

        final String title() {
            return title;
        }

        abstract String content();                          // no body — subclasses decide

        String render() {                                   // concrete method that uses the abstract one
            return title + ": " + content();
        }
    }

    static class TextDocument extends Document {
        private final String text;

        TextDocument(String title, String text) {
            super(title);
            this.text = text;
        }

        @Override
        String content() {
            return text;
        }
    }

    // ── 1. Abstract class basics ──────────────────────────────────────────────

    /**
     * An {@code abstract} class is an incomplete class: it may declare {@code abstract} methods
     * (a signature without a body) and it can NOT be instantiated directly.
     *
     * <p>WHY this matters: it lets a parent say "every Document has a title and can be rendered,
     * but only a concrete subclass knows what the content is". The compiler then FORCES every
     * concrete subclass to fill in the gap.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>{@code new Document("x")} does not compile — instantiate a concrete subclass instead</li>
     *   <li>A concrete subclass must implement ALL inherited abstract methods</li>
     *   <li>Concrete methods of the abstract class ({@code render()}) can call abstract ones —
     *       the call is dispatched to the subclass at runtime</li>
     *   <li>An anonymous subclass is a quick way to supply the missing methods inline</li>
     *   <li>You can still declare variables of the abstract type: {@code Document d = new TextDocument(...)}</li>
     * </ul>
     *
     * <p>Common gotcha: the abstract class is a real class in memory — it just has no direct
     * instances. {@code Modifier.isAbstract(Document.class.getModifiers())} is {@code true}.
     */
    static void basicsDemo() {
        System.out.println("=== Abstract class basics ===");

        // Document d = new Document("x");                  // ← compile error: Document is abstract
        Document doc = new TextDocument("Notes", "hello");  // abstract static type, concrete runtime type
        System.out.println("render()              : " + doc.render());                   // Notes: hello
        System.out.println("Document is abstract  : " + isAbstract(Document.class));    // true
        System.out.println("TextDocument abstract : " + isAbstract(TextDocument.class)); // false

        Document anonymous = new Document("Anon") {         // anonymous concrete subclass
            @Override
            String content() {
                return "from anonymous subclass";
            }
        };
        System.out.println("anonymous subclass    : " + anonymous.render());             // Anon: from anonymous subclass
    }

    static boolean isAbstract(Class<?> type) {
        return Modifier.isAbstract(type.getModifiers());
    }

    // ── 2. Constructors and state ─────────────────────────────────────────────

    /**
     * An abstract class CAN have constructors, instance fields and initialisers. The constructor
     * cannot be called with {@code new}, but every subclass constructor calls it through
     * {@code super(...)}.
     *
     * <p>WHY this matters: this is the main thing an abstract class offers that an interface does
     * not — shared STATE and shared construction logic (validation, defaults) written once.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>{@code title} is stored and validated in {@code Document}'s constructor, not in each subclass</li>
     *   <li>An invalid title fails fast, no matter which subclass was being built</li>
     *   <li>Construction order is the same as in {@link InheritanceDemo}: the abstract parent first</li>
     * </ul>
     *
     * <p>Common gotcha: do not call an abstract method from the abstract class's constructor — the
     * subclass has not been initialised yet (see {@code InheritanceDemo}, section 5).
     */
    static void constructorsAndStateDemo() {
        System.out.println("=== Constructors and state ===");

        Document doc = new TextDocument("Report", "Q3");
        System.out.println("title() from parent   : " + doc.title());                    // Report
        try {
            new TextDocument("", "no title");
        } catch (IllegalArgumentException e) {
            System.out.println("validation in parent  : " + e.getMessage());             // title must not be empty
        }
    }

    // ── 3. Partial implementation ─────────────────────────────────────────────

    /** Overrides {@code render()} but leaves {@code content()} abstract — so it stays abstract. */
    abstract static class MarkupDocument extends Document {
        MarkupDocument(String title) {
            super(title);
        }

        @Override
        String render() {
            return "<doc>" + super.render() + "</doc>";
        }
    }

    static class HtmlNote extends MarkupDocument {
        private final String body;

        HtmlNote(String title, String body) {
            super(title);
            this.body = body;
        }

        @Override
        String content() {
            return "<p>" + body + "</p>";
        }
    }

    /**
     * A subclass of an abstract class does not have to implement every abstract method — but then
     * it must be declared {@code abstract} itself.
     *
     * <p>WHY this matters: you can build a hierarchy in layers. {@code MarkupDocument} adds the
     * XML-like wrapper for the whole family, {@code HtmlNote} finally supplies the content.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>{@code MarkupDocument} implements nothing abstract, yet it overrides a concrete method and stays abstract</li>
     *   <li>Only the first concrete class in the chain has to implement the remaining abstract methods</li>
     *   <li>{@code super.render()} reuses the parent's logic, exactly as in a normal class hierarchy</li>
     * </ul>
     *
     * <p>Common gotcha: forgetting one abstract method in the first concrete subclass is a compile
     * error ("is not abstract and does not override abstract method").
     */
    static void partialImplementationDemo() {
        System.out.println("=== Partial implementation ===");

        Document note = new HtmlNote("Hi", "yo");
        System.out.println("render()              : " + note.render());                  // <doc>Hi: <p>yo</p></doc>
        System.out.println("MarkupDocument abstract: " + isAbstract(MarkupDocument.class)); // true
        System.out.println("HtmlNote abstract     : " + isAbstract(HtmlNote.class));     // false
    }

    // ── 4. Template Method pattern ────────────────────────────────────────────

    /**
     * The skeleton of an algorithm: {@code export} is {@code final} (nobody may change the order of
     * the steps), {@code format} is abstract (every exporter MUST provide it) and
     * {@code header}, {@code footer}, {@code separator} are HOOKS with a default that subclasses
     * MAY override.
     */
    abstract static class DataExporter {
        public final String export(List<String> rows) {
            StringBuilder out = new StringBuilder(header());
            for (int i = 0; i < rows.size(); i++) {
                if (i > 0) {
                    out.append(separator());
                }
                out.append(format(rows.get(i)));
            }
            return out.append(footer()).toString();
        }

        protected abstract String format(String row);       // required step

        protected String header() {                         // optional hook
            return "";
        }

        protected String footer() {                         // optional hook
            return "";
        }

        protected String separator() {                      // optional hook
            return "\n";
        }
    }

    static class BulletExporter extends DataExporter {
        @Override
        protected String format(String row) {
            return "- " + row;
        }
    }

    static class JsonArrayExporter extends DataExporter {
        @Override
        protected String format(String row) {
            return "\"" + row + "\"";
        }

        @Override
        protected String header() {
            return "[";
        }

        @Override
        protected String footer() {
            return "]";
        }

        @Override
        protected String separator() {
            return ",";
        }
    }

    /**
     * Template Method: the abstract class owns the ALGORITHM, subclasses own the STEPS.
     *
     * <p>WHY this matters: the invariant part (loop, separators, ordering) is written once and
     * cannot be broken by a subclass because {@code export} is {@code final}. This is exactly how
     * frameworks such as {@code HttpServlet} or Spring's {@code JdbcTemplate} callbacks work.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>abstract step — forced: {@code format}</li>
     *   <li>hook — optional with a default: {@code header}, {@code footer}, {@code separator}</li>
     *   <li>{@code final} template method — protects the algorithm from being overridden</li>
     * </ul>
     *
     * <p>Common gotcha: too many abstract steps make subclasses tedious to write — prefer hooks
     * with sensible defaults for anything most subclasses will not customise.
     */
    static void templateMethodDemo() {
        System.out.println("=== Template Method ===");

        List<String> rows = Arrays.asList("a", "b", "c");
        System.out.println("bullets:\n" + new BulletExporter().export(rows));            // - a\n- b\n- c
        System.out.println("json                  : " + new JsonArrayExporter().export(rows)); // ["a","b","c"]
        System.out.println("export() is final     : " + isFinalExport());                // true
    }

    static boolean isFinalExport() {
        try {
            return Modifier.isFinal(DataExporter.class.getDeclaredMethod("export", List.class).getModifiers());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    // ── 5. A JDK example: AbstractList ────────────────────────────────────────

    /** A read-only list of the ints {@code [start, end)} — only {@code get} and {@code size} are written. */
    static class IntRange extends AbstractList<Integer> {
        private final int start;
        private final int end;
        int getCalls;                                       // counts how often get() is invoked

        IntRange(int startInclusive, int endExclusive) {
            this.start = startInclusive;
            this.end = endExclusive;
        }

        @Override
        public Integer get(int index) {
            if (index < 0 || index >= size()) {
                throw new IndexOutOfBoundsException("index " + index + ", size " + size());
            }
            getCalls++;
            return start + index;
        }

        @Override
        public int size() {
            return Math.max(0, end - start);
        }
    }

    /**
     * {@link AbstractList} is the JDK's own abstract class: you implement ONE read method and the
     * size, and it gives you {@code iterator}, {@code contains}, {@code indexOf}, {@code equals},
     * {@code hashCode}, {@code toString}, {@code subList} and more.
     *
     * <p>WHY this matters: it shows the abstract-class idea at scale. The JDK ships dozens of
     * {@code Abstract*} skeletons ({@code AbstractMap}, {@code AbstractSet}, ...) so that writing
     * a new collection costs two methods, not thirty.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>The inherited {@code contains}/{@code indexOf} are built on top of YOUR {@code get} —
     *       the call counter proves it</li>
     *   <li>Mutators such as {@code add} are not implemented by the skeleton and throw
     *       {@link UnsupportedOperationException} — that is how you get a read-only list</li>
     *   <li>Inherited {@code equals}/{@code toString} work against any other {@code List}</li>
     * </ul>
     *
     * <p>Common gotcha: the inherited methods are generic, not fast. {@code contains} walks the
     * list with {@code get(i)}, so it is O(n); override it when you know a cheaper way.
     */
    static void abstractListDemo() {
        System.out.println("=== AbstractList ===");

        IntRange range = new IntRange(3, 6);
        System.out.println("range                 : " + range);                          // [3, 4, 5]
        System.out.println("contains(4)           : " + range.contains(4));              // true
        System.out.println("indexOf(5)            : " + range.indexOf(5));               // 2
        System.out.println("equals Arrays.asList  : " + range.equals(Arrays.asList(3, 4, 5))); // true
        System.out.println("get() calls so far    : " + (range.getCalls > 0));           // true — inherited methods use get()
        try {
            range.add(9);
        } catch (UnsupportedOperationException e) {
            System.out.println("add(9)                : UnsupportedOperationException");
        }
    }

    // ── 6. Abstract class vs interface ────────────────────────────────────────

    interface Printable {
        int DEFAULT_COPIES = 1;                             // interface fields are implicitly public static final

        String print();
    }

    interface Archivable {
        String archive();
    }

    /** One superclass (Document) + two interfaces. Interface methods must be {@code public}. */
    static class Memo extends Document implements Printable, Archivable {
        Memo(String title) {
            super(title);
        }

        @Override
        String content() {
            return "memo";
        }

        @Override
        public String print() {
            return "printing " + title() + " x" + DEFAULT_COPIES;
        }

        @Override
        public String archive() {
            return "archived " + title();
        }
    }

    /**
     * Both let you declare a contract, but they answer different questions: an abstract class says
     * "what you ARE and share" (state, constructors, partial code), an interface says "what you CAN
     * DO".
     *
     * <p>WHY this matters: choosing the wrong one is a design mistake that is hard to undo.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>A class extends at most ONE class (abstract or not) but may implement MANY interfaces</li>
     *   <li>An interface cannot hold instance state — a field declared in it is a constant
     *       ({@code public static final})</li>
     *   <li>An abstract class can have constructors, private fields and protected helpers; an interface cannot</li>
     * </ul>
     *
     * <p>Rule of thumb: start with an interface for the contract; add an abstract class only when
     * implementations share real state or code. Interfaces (default/static methods, multiple
     * inheritance of type) are the topic of the next module, {@code oop-interfaces-and-abstraction}.
     *
     * <p>Common gotcha: implementing an interface method without {@code public} does not compile —
     * the same "access may be widened, never narrowed" rule from {@link InheritanceDemo}, section 2.
     */
    static void abstractVsInterfaceDemo() {
        System.out.println("=== Abstract class vs interface ===");

        Memo memo = new Memo("Budget");
        System.out.println("print()               : " + memo.print());                   // printing Budget x1
        System.out.println("archive()             : " + memo.archive());                 // archived Budget
        System.out.println("is a Document         : " + (memo instanceof Document));     // true
        System.out.println("is Printable          : " + (memo instanceof Printable));    // true
        System.out.println("is Archivable         : " + (memo instanceof Archivable));   // true
        System.out.println("constant is static    : " + isInterfaceConstantStaticFinal()); // true
    }

    static boolean isInterfaceConstantStaticFinal() {
        try {
            int modifiers = Printable.class.getField("DEFAULT_COPIES").getModifiers();
            return Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    // ── 7. Gotchas ────────────────────────────────────────────────────────────

    /** Abstract, but with ZERO abstract methods — a common "adapter" style base class. */
    abstract static class Adapter {
        String hello() {
            return "hi";
        }
    }

    /**
     * What {@code abstract} can and cannot do.
     *
     * <p>WHY this matters: the rules are small, but each one is a favourite interview question.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>An abstract class does NOT need abstract methods — {@code abstract} alone forbids
     *       {@code new}. It is still usable through a subclass (below: an anonymous one)</li>
     *   <li>{@code abstract} cannot be combined with {@code final} (a final method cannot be
     *       overridden, an abstract one must be), {@code private} (a private method is invisible
     *       to subclasses) or {@code static} (static methods are not overridden) — these are compile errors</li>
     *   <li>A class that declares an abstract method must itself be declared {@code abstract}</li>
     * </ul>
     *
     * <p>Common gotcha: an {@code abstract} class that has a public constructor is still not
     * instantiable — the constructor exists only for subclasses to call.
     */
    static void gotchasDemo() {
        System.out.println("=== Gotchas ===");

        Adapter adapter = new Adapter() { };                // legal: anonymous concrete subclass, nothing to implement
        System.out.println("no abstract methods   : " + adapter.hello());                 // hi
        System.out.println("Adapter is abstract   : " + isAbstract(Adapter.class));       // true
        System.out.println("abstract methods in Adapter: " + abstractMethodCount(Adapter.class)); // 0
        System.out.println("abstract methods in Document: " + abstractMethodCount(Document.class)); // 1

        // abstract final void f();    // ← compile error: illegal combination of modifiers
        // abstract private void f();  // ← compile error: illegal combination of modifiers
        // abstract static void f();   // ← compile error: illegal combination of modifiers
    }

    static int abstractMethodCount(Class<?> type) {
        int count = 0;
        for (java.lang.reflect.Method method : type.getDeclaredMethods()) {
            if (Modifier.isAbstract(method.getModifiers())) {
                count++;
            }
        }
        return count;
    }
}
