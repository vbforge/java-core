package com.vbforge.oop_inheritance_and_polymorphism;

import com.vbforge.oop_inheritance_and_polymorphism.AbstractClassDemo.Adapter;
import com.vbforge.oop_inheritance_and_polymorphism.AbstractClassDemo.Archivable;
import com.vbforge.oop_inheritance_and_polymorphism.AbstractClassDemo.BulletExporter;
import com.vbforge.oop_inheritance_and_polymorphism.AbstractClassDemo.DataExporter;
import com.vbforge.oop_inheritance_and_polymorphism.AbstractClassDemo.Document;
import com.vbforge.oop_inheritance_and_polymorphism.AbstractClassDemo.HtmlNote;
import com.vbforge.oop_inheritance_and_polymorphism.AbstractClassDemo.IntRange;
import com.vbforge.oop_inheritance_and_polymorphism.AbstractClassDemo.JsonArrayExporter;
import com.vbforge.oop_inheritance_and_polymorphism.AbstractClassDemo.MarkupDocument;
import com.vbforge.oop_inheritance_and_polymorphism.AbstractClassDemo.Memo;
import com.vbforge.oop_inheritance_and_polymorphism.AbstractClassDemo.Printable;
import com.vbforge.oop_inheritance_and_polymorphism.AbstractClassDemo.TextDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * AbstractClassDemoTest — verifies every concept shown in {@link AbstractClassDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * how abstract classes behave.
 */
@DisplayName("AbstractClassDemo — API contracts")
class AbstractClassDemoTest {

    // ── 1. Abstract class basics ──────────────────────────────────────────────

    @Nested
    @DisplayName("1. Abstract class basics")
    class BasicsTest {

        @Test
        @DisplayName("the abstract type has the abstract modifier, the concrete subclass does not")
        void abstractModifier() {
            assertThat(AbstractClassDemo.isAbstract(Document.class)).isTrue();
            assertThat(AbstractClassDemo.isAbstract(TextDocument.class)).isFalse();
        }

        @Test
        @DisplayName("a concrete subclass supplies the abstract method")
        void concreteSubclass() {
            Document doc = new TextDocument("Notes", "hello");
            assertThat(doc.content()).isEqualTo("hello");
        }

        @Test
        @DisplayName("a concrete method of the abstract class calls the subclass's abstract-method implementation")
        void concreteMethodUsesAbstractOne() {
            Document doc = new TextDocument("Notes", "hello");
            assertThat(doc.render()).isEqualTo("Notes: hello");
        }

        @Test
        @DisplayName("an anonymous subclass can supply the missing method inline")
        void anonymousSubclass() {
            Document anonymous = new Document("Anon") {
                @Override
                String content() {
                    return "inline";
                }
            };
            assertThat(anonymous.render()).isEqualTo("Anon: inline");
            assertThat(anonymous).isInstanceOf(Document.class);
        }

        @Test
        @DisplayName("the abstract class declares exactly one abstract method")
        void oneAbstractMethod() {
            assertThat(AbstractClassDemo.abstractMethodCount(Document.class)).isEqualTo(1);
        }
    }

    // ── 2. Constructors and state ─────────────────────────────────────────────

    @Nested
    @DisplayName("2. Constructors and state")
    class ConstructorsAndStateTest {

        @Test
        @DisplayName("state stored by the abstract parent is available to every subclass")
        void sharedState() {
            assertThat(new TextDocument("Report", "Q3").title()).isEqualTo("Report");
            assertThat(new HtmlNote("Page", "x").title()).isEqualTo("Page");
        }

        @Test
        @DisplayName("validation in the abstract constructor rejects an empty title")
        void rejectsEmptyTitle() {
            assertThatThrownBy(() -> new TextDocument("", "body"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("title must not be empty");
        }

        @Test
        @DisplayName("validation in the abstract constructor rejects a null title")
        void rejectsNullTitle() {
            assertThatThrownBy(() -> new TextDocument(null, "body"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("the same validation applies to every subclass")
        void appliesToAllSubclasses() {
            assertThatThrownBy(() -> new HtmlNote("", "body"))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> new Memo(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ── 3. Partial implementation ─────────────────────────────────────────────

    @Nested
    @DisplayName("3. Partial implementation")
    class PartialImplementationTest {

        @Test
        @DisplayName("an abstract subclass may leave the inherited abstract method unimplemented")
        void stillAbstract() {
            assertThat(AbstractClassDemo.isAbstract(MarkupDocument.class)).isTrue();
            assertThat(AbstractClassDemo.abstractMethodCount(MarkupDocument.class)).isZero();
        }

        @Test
        @DisplayName("the first concrete class in the chain implements the rest")
        void firstConcreteClass() {
            assertThat(AbstractClassDemo.isAbstract(HtmlNote.class)).isFalse();
            assertThat(new HtmlNote("Hi", "yo").content()).isEqualTo("<p>yo</p>");
        }

        @Test
        @DisplayName("super.render() reuses the parent's logic")
        void superRender() {
            assertThat(new HtmlNote("Hi", "yo").render()).isEqualTo("<doc>Hi: <p>yo</p></doc>");
        }

        @Test
        @DisplayName("an HtmlNote is a MarkupDocument and a Document")
        void hierarchy() {
            Document note = new HtmlNote("Hi", "yo");
            assertThat(note).isInstanceOf(MarkupDocument.class).isInstanceOf(Document.class);
        }
    }

    // ── 4. Template Method pattern ────────────────────────────────────────────

    @Nested
    @DisplayName("4. Template Method pattern")
    class TemplateMethodTest {

        private final List<String> rows = Arrays.asList("a", "b", "c");

        @Test
        @DisplayName("a subclass that implements only the abstract step uses all the default hooks")
        void defaultHooks() {
            assertThat(new BulletExporter().export(rows)).isEqualTo("- a\n- b\n- c");
        }

        @Test
        @DisplayName("a subclass can override the hooks it cares about")
        void overriddenHooks() {
            assertThat(new JsonArrayExporter().export(rows)).isEqualTo("[\"a\",\"b\",\"c\"]");
        }

        @Test
        @DisplayName("empty input: only header and footer are produced")
        void emptyRows() {
            assertThat(new JsonArrayExporter().export(Collections.<String>emptyList())).isEqualTo("[]");
            assertThat(new BulletExporter().export(new ArrayList<String>())).isEmpty();
        }

        @Test
        @DisplayName("a single row has no separator")
        void singleRow() {
            assertThat(new JsonArrayExporter().export(Collections.singletonList("x"))).isEqualTo("[\"x\"]");
        }

        @Test
        @DisplayName("the template method itself is final")
        void templateIsFinal() {
            assertThat(AbstractClassDemo.isFinalExport()).isTrue();
        }

        @Test
        @DisplayName("exporters are used through the abstract type")
        void throughAbstractType() {
            DataExporter exporter = new BulletExporter();
            assertThat(exporter.export(Collections.singletonList("only"))).isEqualTo("- only");
        }
    }

    // ── 5. A JDK example: AbstractList ────────────────────────────────────────

    @Nested
    @DisplayName("5. A JDK example: AbstractList")
    class AbstractListTest {

        @Test
        @DisplayName("two methods (get, size) give a full List")
        void getAndSize() {
            IntRange range = new IntRange(3, 6);
            assertThat(range.size()).isEqualTo(3);
            assertThat(range.get(0)).isEqualTo(3);
            assertThat(range.get(2)).isEqualTo(5);
        }

        @Test
        @DisplayName("inherited toString, contains and indexOf work")
        void inheritedMethods() {
            IntRange range = new IntRange(3, 6);
            assertThat(range.toString()).isEqualTo("[3, 4, 5]");
            assertThat(range.contains(4)).isTrue();
            assertThat(range.contains(9)).isFalse();
            assertThat(range.indexOf(5)).isEqualTo(2);
        }

        @Test
        @DisplayName("inherited iteration and equals work against any List")
        void inheritedIterationAndEquals() {
            IntRange range = new IntRange(3, 6);
            List<Integer> copy = new ArrayList<>(range);
            assertThat(copy).containsExactly(3, 4, 5);
            assertThat(range.equals(Arrays.asList(3, 4, 5))).isTrue();
        }

        @Test
        @DisplayName("inherited methods are built on top of get()")
        void inheritedMethodsCallGet() {
            IntRange range = new IntRange(0, 10);
            assertThat(range.getCalls).isZero();
            range.contains(7);
            assertThat(range.getCalls).isPositive();
        }

        @Test
        @DisplayName("mutators are not implemented → UnsupportedOperationException")
        void readOnly() {
            IntRange range = new IntRange(3, 6);
            assertThatThrownBy(() -> range.add(9)).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("an empty range has size 0")
        void emptyRange() {
            assertThat(new IntRange(5, 5)).isEmpty();
            assertThat(new IntRange(7, 2)).isEmpty();
        }

        @Test
        @DisplayName("get() rejects an out-of-range index")
        void outOfRange() {
            IntRange range = new IntRange(3, 6);
            assertThatThrownBy(() -> range.get(3)).isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    // ── 6. Abstract class vs interface ────────────────────────────────────────

    @Nested
    @DisplayName("6. Abstract class vs interface")
    class AbstractVsInterfaceTest {

        @Test
        @DisplayName("a class has one superclass and may implement several interfaces")
        void oneSuperclassManyInterfaces() {
            Memo memo = new Memo("Budget");
            assertThat(memo).isInstanceOf(Document.class)
                            .isInstanceOf(Printable.class)
                            .isInstanceOf(Archivable.class);
            assertThat(Memo.class.getSuperclass()).isEqualTo(Document.class);
            assertThat(Memo.class.getInterfaces()).containsExactlyInAnyOrder(Printable.class, Archivable.class);
        }

        @Test
        @DisplayName("each contract is honoured")
        void contracts() {
            Memo memo = new Memo("Budget");
            assertThat(memo.print()).isEqualTo("printing Budget x1");
            assertThat(memo.archive()).isEqualTo("archived Budget");
            assertThat(memo.render()).isEqualTo("Budget: memo");
        }

        @Test
        @DisplayName("an interface field is a constant: static and final")
        void interfaceFieldIsConstant() {
            assertThat(AbstractClassDemo.isInterfaceConstantStaticFinal()).isTrue();
            assertThat(Printable.DEFAULT_COPIES).isEqualTo(1);
        }
    }

    // ── 7. Gotchas ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("7. Gotchas")
    class GotchasTest {

        @Test
        @DisplayName("an abstract class may have zero abstract methods")
        void abstractWithoutAbstractMethods() {
            assertThat(AbstractClassDemo.isAbstract(Adapter.class)).isTrue();
            assertThat(AbstractClassDemo.abstractMethodCount(Adapter.class)).isZero();
        }

        @Test
        @DisplayName("it is still usable through a subclass")
        void usableThroughSubclass() {
            Adapter adapter = new Adapter() { };
            assertThat(adapter.hello()).isEqualTo("hi");
        }

        @Test
        @DisplayName("an abstract class with a constructor is still abstract")
        void constructorDoesNotMakeItConcrete() {
            assertThat(Document.class.getDeclaredConstructors()).isNotEmpty();
            assertThat(AbstractClassDemo.isAbstract(Document.class)).isTrue();
        }

        @Test
        @DisplayName("counting abstract methods: Document has 1, a concrete subclass has 0")
        void abstractMethodCounts() {
            assertThat(AbstractClassDemo.abstractMethodCount(Document.class)).isEqualTo(1);
            assertThat(AbstractClassDemo.abstractMethodCount(TextDocument.class)).isZero();
        }
    }
}
