package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * PojosAndRecordsDemo — mapping JSON to and from Java classes.
 *
 * <p>Read top-to-bottom. The model types ({@link PersonPojo}, {@link PersonRecord},
 * {@link Team}, {@link Point}, {@link PointRecord}) are public nested types because
 * {@code PojosAndRecordsTasks} reuses them.
 *
 * <p>Topics:
 * <ol>
 *   <li>POJOs — getters write, no-arg constructor + setters read</li>
 *   <li>Records — zero annotations needed (Jackson 2.12+, Java 16+)</li>
 *   <li>No default constructor — the classic {@link InvalidDefinitionException}</li>
 *   <li>Nested objects and collections</li>
 *   <li>{@code convertValue} / {@code valueToTree} — object ↔ Map/tree without text</li>
 * </ol>
 */
public class PojosAndRecordsDemo {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ── Model types ───────────────────────────────────────────────────────────

    /** Classic JavaBean: no-arg constructor, getters, setters. */
    public static class PersonPojo {
        private String name;
        private int age;
        private boolean active;
        private String secret = "hidden";   // no accessor → invisible to Jackson

        public PersonPojo() { }

        public PersonPojo(String name, int age, boolean active) {
            this.name = name;
            this.age = age;
            this.active = active;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public boolean isActive() { return active; }          // "is" prefix → property "active"
        public void setActive(boolean active) { this.active = active; }
        public String revealSecret() { return secret; }       // not a getter name → ignored
    }

    /** Immutable carrier — Jackson uses the canonical constructor, no annotations needed. */
    public record PersonRecord(String name, int age) { }

    /** A record that contains a list of records. */
    public record Team(String name, List<PersonRecord> members) { }

    /** Immutable class with NO default constructor and NO annotations — Jackson cannot read it. */
    public static class Point {
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() { return x; }
        public int getY() { return y; }
    }

    /** Same shape as {@link Point}, but as a record — readable out of the box. */
    public record PointRecord(int x, int y) { }

    public static void main(String[] args) throws Exception {
        pojoDemo();
        System.out.println("---");
        recordDemo();
        System.out.println("---");
        noDefaultConstructorDemo();
        System.out.println("---");
        nestedAndCollectionsDemo();
        System.out.println("---");
        convertValueDemo();
    }

    // ── 1. POJOs ──────────────────────────────────────────────────────────────

    /**
     * Jackson maps by <em>properties</em>: {@code getX()} / {@code isX()} define what is
     * written; a no-arg constructor plus {@code setX()} define what is read.
     *
     * <p>WHY it matters: knowing the rules explains every "why is my field missing?" bug.
     * A private field with no accessor is invisible; {@code isActive()} becomes
     * {@code "active"}, not {@code "isActive"}.
     *
     * <p>Complexity: O(n) per document; reflection metadata is cached per class.
     *
     * <p>Gotcha: names come from the <em>accessors</em>, not from the fields. A JSON key
     * that matches only a private field with no accessor is unknown on read and fails.
     */
    static void pojoDemo() throws JsonProcessingException {
        System.out.println("=== POJOs ===");

        PersonPojo ann = new PersonPojo("Ann", 30, true);
        System.out.println("serialised : " + MAPPER.writeValueAsString(ann));   // {"name":"Ann","age":30,"active":true}

        PersonPojo bob = MAPPER.readValue("{\"name\":\"Bob\",\"age\":41,\"active\":false}", PersonPojo.class);
        System.out.println("read name  : " + bob.getName());                    // Bob
        System.out.println("read age   : " + bob.getAge());                     // 41
        System.out.println("read active: " + bob.isActive());                   // false

        // The "secret" field was never written — no getter
        System.out.println("has secret : " + MAPPER.writeValueAsString(ann).contains("secret")); // false

        // ...and a "secret" key on input is unknown, because no accessor exposes the field
        try {
            MAPPER.readValue("{\"name\":\"Eve\",\"secret\":\"x\"}", PersonPojo.class);
            System.out.println("read secret: parsed (unexpected)");
        } catch (JsonMappingException e) {
            System.out.println("read secret: " + e.getClass().getSimpleName());   // UnrecognizedPropertyException
        }
    }

    // ── 2. Records ────────────────────────────────────────────────────────────

    /**
     * Records are the natural JSON carrier: immutable, one canonical constructor, accessors
     * named after the components. Jackson 2.12+ handles them with no annotations.
     *
     * <p>WHY it matters: a POJO needs ~15 lines of boilerplate for two fields; a record
     * needs one. Immutability also means a parsed value cannot be changed by accident.
     *
     * <p>Complexity: O(n) per document.
     *
     * <p>Gotcha: a field missing from the JSON does not fail — the component simply gets
     * its default ({@code null} for objects, {@code 0} for {@code int}).
     */
    static void recordDemo() throws JsonProcessingException {
        System.out.println("=== Records ===");

        PersonRecord ann = new PersonRecord("Ann", 30);
        String json = MAPPER.writeValueAsString(ann);
        System.out.println("serialised : " + json);                              // {"name":"Ann","age":30}

        PersonRecord back = MAPPER.readValue(json, PersonRecord.class);
        System.out.println("equals     : " + back.equals(ann));                  // true — records have value equality

        PersonRecord partial = MAPPER.readValue("{\"name\":\"Bob\"}", PersonRecord.class);
        System.out.println("missing age: " + partial.age());                     // 0
    }

    // ── 3. No default constructor ─────────────────────────────────────────────

    /**
     * Without a no-arg constructor, a class needs a creator (constructor/factory) that
     * tells Jackson which JSON field feeds which parameter. A plain class gives no such
     * hint, because parameter names are not available at runtime.
     *
     * <p>WHY it matters: this is the most common Jackson error a beginner meets. The cure
     * is a record (names are part of the type), a no-arg constructor, or
     * {@code @JsonCreator} (see {@code JsonAnnotationsDemo}).
     *
     * <p>Gotcha: the failure happens when you <em>read</em>. Writing {@link Point} works
     * fine, so the bug hides until the first deserialisation.
     */
    static void noDefaultConstructorDemo() throws JsonProcessingException {
        System.out.println("=== No default constructor ===");

        String json = "{\"x\":1,\"y\":2}";
        System.out.println("write Point: " + MAPPER.writeValueAsString(new Point(1, 2)));  // works

        try {
            MAPPER.readValue(json, Point.class);
            System.out.println("read Point : parsed (unexpected)");
        } catch (InvalidDefinitionException e) {
            System.out.println("read Point : " + e.getClass().getSimpleName());           // InvalidDefinitionException
        }

        PointRecord p = MAPPER.readValue(json, PointRecord.class);
        System.out.println("PointRecord: " + p);                                          // PointRecord[x=1, y=2]
    }

    // ── 4. Nested objects and collections ─────────────────────────────────────

    /**
     * Nested objects and collections need no extra work — Jackson recurses. For a
     * <em>top-level</em> generic collection you still need a {@link TypeReference}.
     *
     * <p>WHY it matters: real payloads are trees of objects; one {@code readValue} call
     * should rebuild the whole thing.
     *
     * <p>Complexity: O(total size of the document).
     */
    static void nestedAndCollectionsDemo() throws JsonProcessingException {
        System.out.println("=== Nested and collections ===");

        Team team = new Team("Core", Arrays.asList(new PersonRecord("Ann", 30), new PersonRecord("Bob", 41)));
        String json = MAPPER.writeValueAsString(team);
        System.out.println("team json  : " + json);   // {"name":"Core","members":[{"name":"Ann","age":30},{"name":"Bob","age":41}]}

        Team back = MAPPER.readValue(json, Team.class);
        System.out.println("members    : " + back.members().size());             // 2
        System.out.println("first      : " + back.members().get(0).name());      // Ann

        List<PersonRecord> people = MAPPER.readValue(
                "[{\"name\":\"Cy\",\"age\":5}]", new TypeReference<List<PersonRecord>>() { });
        System.out.println("list       : " + people);                            // [PersonRecord[name=Cy, age=5]]

        Map<String, PersonRecord> byId = MAPPER.readValue(
                "{\"a\":{\"name\":\"Di\",\"age\":7}}", new TypeReference<Map<String, PersonRecord>>() { });
        System.out.println("map value  : " + byId.get("a").name());              // Di
    }

    // ── 5. convertValue / valueToTree ─────────────────────────────────────────

    /**
     * {@code convertValue} maps one Java representation to another <em>without</em>
     * producing JSON text; {@code valueToTree} / {@code treeToValue} do the same via a
     * {@link JsonNode}.
     *
     * <p>WHY it matters: it is the shortest path from an object to a {@code Map} (for
     * logging or generic processing) and back, and it applies exactly the same rules as
     * real serialisation.
     *
     * <p>Complexity: O(n); it is a serialise + deserialise through an in-memory buffer.
     *
     * <p>Gotcha: {@code convertValue} reports failures as an unchecked
     * {@link IllegalArgumentException}, not as a {@link JsonProcessingException} — and the
     * usual rules still apply, so an unknown key in the source Map fails the conversion.
     */
    static void convertValueDemo() throws JsonProcessingException {
        System.out.println("=== convertValue ===");

        PersonPojo ann = new PersonPojo("Ann", 30, true);
        Map<String, Object> map = MAPPER.convertValue(ann, new TypeReference<Map<String, Object>>() { });
        System.out.println("pojo -> map : " + map);                              // {name=Ann, age=30, active=true}

        PersonRecord rec = new PersonRecord("Bob", 41);
        Map<String, Object> recMap = MAPPER.convertValue(rec, new TypeReference<Map<String, Object>>() { });
        PersonRecord back = MAPPER.convertValue(recMap, PersonRecord.class);
        System.out.println("rec -> map -> rec equal : " + back.equals(rec));     // true

        // valueToTree / treeToValue — same idea, but the middle step is a JsonNode
        JsonNode tree = MAPPER.valueToTree(rec);
        System.out.println("tree        : " + tree);                             // {"name":"Bob","age":41}
        System.out.println("treeToValue : " + MAPPER.treeToValue(tree, PersonRecord.class));

        // ❌ the pojo's map has an "active" key that PersonRecord does not know
        try {
            MAPPER.convertValue(map, PersonRecord.class);
            System.out.println("pojo map -> record : converted (unexpected)");
        } catch (IllegalArgumentException e) {
            System.out.println("pojo map -> record : IllegalArgumentException");
        }
    }
}
