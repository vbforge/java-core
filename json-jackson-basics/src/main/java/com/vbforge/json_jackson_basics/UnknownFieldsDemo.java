package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * UnknownFieldsDemo — what happens when the JSON and the Java class disagree.
 *
 * <p>Real APIs evolve: servers add fields, clients lag behind. Jackson's defaults favour
 * safety; this demo shows when and how to relax them.
 *
 * <p>Topics:
 * <ol>
 *   <li>Default behaviour — unknown properties fail</li>
 *   <li>Mapper-wide and per-reader leniency</li>
 *   <li>{@code @JsonIgnoreProperties(ignoreUnknown = true)} on a class</li>
 *   <li>{@code @JsonAnySetter} / {@code @JsonAnyGetter} — keep the extras</li>
 *   <li>Missing fields — defaults, and {@code FAIL_ON_MISSING_CREATOR_PROPERTIES}</li>
 * </ol>
 */
public class UnknownFieldsDemo {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ── Model types ───────────────────────────────────────────────────────────

    /** Strict settings class — no annotations. */
    public static class Settings {
        private String theme;
        private int fontSize;

        public Settings() { }

        public Settings(String theme, int fontSize) {
            this.theme = theme;
            this.fontSize = fontSize;
        }

        public String getTheme() { return theme; }
        public void setTheme(String theme) { this.theme = theme; }
        public int getFontSize() { return fontSize; }
        public void setFontSize(int fontSize) { this.fontSize = fontSize; }
    }

    /** Same fields, but tolerant of unknown properties via the class annotation. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LenientSettings {
        private String theme;
        private int fontSize;

        public String getTheme() { return theme; }
        public void setTheme(String theme) { this.theme = theme; }
        public int getFontSize() { return fontSize; }
        public void setFontSize(int fontSize) { this.fontSize = fontSize; }
    }

    /** Keeps every field it does not know in a map, and writes them back at the top level. */
    public static class TrackedEvent {
        private String type;
        private final Map<String, Object> extras = new LinkedHashMap<>();

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        @JsonAnySetter
        public void addExtra(String key, Object value) {
            extras.put(key, value);
        }

        @JsonAnyGetter
        public Map<String, Object> getExtras() {
            return extras;
        }
    }

    /** Record whose components are both required in spirit — see section 5. */
    public record Point(int x, int y) { }

    public static void main(String[] args) throws Exception {
        failOnUnknownDemo();
        System.out.println("---");
        lenientMapperDemo();
        System.out.println("---");
        ignoreUnknownAnnotationDemo();
        System.out.println("---");
        anySetterDemo();
        System.out.println("---");
        missingFieldsDemo();
    }

    // ── 1. Default: unknown properties fail ───────────────────────────────────

    /**
     * By default {@code DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES} is
     * <em>enabled</em>: a JSON key with no matching Java property throws
     * {@link UnrecognizedPropertyException}.
     *
     * <p>WHY it matters: it catches typos ({@code "colour"} vs {@code "color"}) early
     * instead of silently dropping data.
     *
     * <p>Complexity: none — the check happens during normal parsing.
     *
     * <p>Gotcha: the exception carries the offending name and the list of known names,
     * which is exactly what you need to fix the mismatch.
     */
    static void failOnUnknownDemo() throws Exception {
        System.out.println("=== Default: fail on unknown ===");

        try {
            MAPPER.readValue("{\"theme\":\"dark\",\"colour\":\"red\"}", Settings.class);
            System.out.println("parsed (unexpected)");
        } catch (UnrecognizedPropertyException e) {
            System.out.println("unknown property : " + e.getPropertyName());           // colour
            System.out.println("knows about      : " + e.getKnownPropertyIds().size()); // 2
        }
    }

    // ── 2. Lenient mapper / reader ────────────────────────────────────────────

    /**
     * Disable the feature on the mapper to ignore unknown keys everywhere, or on a single
     * {@link ObjectReader} to relax just one call.
     *
     * <p>WHY it matters: a client that must survive the server adding fields (forward
     * compatibility) should not break on them. Prefer the narrowest scope that works.
     *
     * <p>Complexity: none.
     *
     * <p>Gotcha: configure a mapper <em>before</em> first use and never reconfigure it while
     * shared across threads. A reader is immutable and safe to derive at any time.
     */
    static void lenientMapperDemo() throws Exception {
        System.out.println("=== Lenient mapper / reader ===");

        String json = "{\"theme\":\"dark\",\"fontSize\":14,\"colour\":\"red\"}";

        ObjectMapper lenient = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Settings a = lenient.readValue(json, Settings.class);
        System.out.println("mapper-wide : " + a.getTheme() + " / " + a.getFontSize());   // dark / 14

        ObjectReader reader = MAPPER.readerFor(Settings.class).without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Settings b = reader.readValue(json);
        System.out.println("per-reader  : " + b.getTheme() + " / " + b.getFontSize());   // dark / 14

        try {
            MAPPER.readValue(json, Settings.class);   // the shared mapper is untouched
            System.out.println("shared mapper : parsed (unexpected)");
        } catch (UnrecognizedPropertyException e) {
            System.out.println("shared mapper : still strict");
        }
    }

    // ── 3. @JsonIgnoreProperties ──────────────────────────────────────────────

    /**
     * {@code @JsonIgnoreProperties(ignoreUnknown = true)} makes one <em>class</em> tolerant,
     * whatever mapper reads it.
     *
     * <p>WHY it matters: the tolerance travels with the type, so DTOs for third-party APIs
     * stay lenient while your own strict types keep catching typos.
     *
     * <p>Complexity: none.
     *
     * <p>Gotcha: the annotation hides mistakes in that class too — a misspelt key is
     * dropped without a trace.
     */
    static void ignoreUnknownAnnotationDemo() throws Exception {
        System.out.println("=== @JsonIgnoreProperties ===");

        LenientSettings s = MAPPER.readValue(
                "{\"theme\":\"light\",\"fontSize\":12,\"extra\":true}", LenientSettings.class);
        System.out.println("theme       : " + s.getTheme());       // light
        System.out.println("fontSize    : " + s.getFontSize());    // 12
    }

    // ── 4. Any setter / any getter ────────────────────────────────────────────

    /**
     * {@code @JsonAnySetter} receives every key the class does not declare;
     * {@code @JsonAnyGetter} writes a map back out as top-level keys.
     *
     * <p>WHY it matters: you can read a payload with a few known fields plus arbitrary
     * extras, and write it back without losing anything.
     *
     * <p>Complexity: O(1) per extra key.
     *
     * <p>Gotcha: without {@code @JsonAnyGetter} the map is written as a nested
     * {@code "extras":{...}} object and the round trip changes the shape.
     */
    static void anySetterDemo() throws Exception {
        System.out.println("=== @JsonAnySetter / @JsonAnyGetter ===");

        TrackedEvent e = MAPPER.readValue("{\"type\":\"click\",\"x\":10,\"y\":20}", TrackedEvent.class);
        System.out.println("type        : " + e.getType());        // click
        System.out.println("extras      : " + e.getExtras());      // {x=10, y=20}
        System.out.println("written     : " + MAPPER.writeValueAsString(e));   // {"type":"click","x":10,"y":20}
    }

    // ── 5. Missing fields ─────────────────────────────────────────────────────

    /**
     * The opposite problem: JSON <em>lacks</em> a field the class has. By default Jackson
     * leaves the default ({@code null} / {@code 0}) and does not complain.
     * {@code DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES} makes a missing
     * constructor/record parameter an error.
     *
     * <p>WHY it matters: a silent {@code 0} for a missing amount is a nasty bug. Turn the
     * feature on where absence must be reported.
     *
     * <p>Complexity: none.
     *
     * <p>Gotcha: the feature only covers <em>creator</em> properties (records, constructors).
     * A setter-based POJO such as {@link Settings} still gets defaults without error.
     */
    static void missingFieldsDemo() throws Exception {
        System.out.println("=== Missing fields ===");

        Settings s = MAPPER.readValue("{}", Settings.class);
        System.out.println("theme       : " + s.getTheme());       // null
        System.out.println("fontSize    : " + s.getFontSize());    // 0

        Point lenient = MAPPER.readValue("{\"x\":1}", Point.class);
        System.out.println("record      : " + lenient);            // Point[x=1, y=0]

        ObjectMapper strict = new ObjectMapper().enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);
        try {
            strict.readValue("{\"x\":1}", Point.class);
            System.out.println("strict      : parsed (unexpected)");
        } catch (JsonMappingException ex) {
            System.out.println("strict      : " + ex.getClass().getSimpleName());
        }
    }
}
