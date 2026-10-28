package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vbforge.json_jackson_basics.UnknownFieldsDemo.Point;
import com.vbforge.json_jackson_basics.UnknownFieldsDemo.Settings;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * UnknownFieldsTasks — implement every method below to make {@code UnknownFieldsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code UnknownFieldsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=UnknownFieldsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Tasks 3 and 4 also require you to <em>add annotations</em> to the {@link Envelope}
 * model at the bottom of this file. {@code Settings} and {@code Point} come from
 * {@code UnknownFieldsDemo}. Do not change method signatures.
 */
public class UnknownFieldsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Parses JSON into {@link Settings}, <em>ignoring</em> any keys that {@code Settings}
     * does not declare. Do not modify the {@code Settings} class.
     *
     * <p>Examples:
     * <pre>
     *   {"theme":"dark","fontSize":14}                 → theme=dark, fontSize=14
     *   {"theme":"dark","fontSize":14,"colour":"red"}  → theme=dark, fontSize=14
     *   {"unrelated":1}                                → theme=null, fontSize=0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Configure the mapper (or a reader) via
     *       {@link com.fasterxml.jackson.databind.DeserializationFeature}.</li>
     * </ul>
     *
     * @param json JSON text of an object
     * @return the parsed settings
     * @throws JsonProcessingException if the text is malformed
     */
    public Settings parseLenient(String json) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement parseLenient");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Reads {@code json} <em>strictly</em> as {@link Settings} and returns the name of the
     * first property that {@code Settings} does not know, or {@code null} if every key is
     * known.
     *
     * <p>Examples:
     * <pre>
     *   {"theme":"dark","fontSize":14}                → null
     *   {"theme":"dark","colour":"red"}               → "colour"
     *   {"a":1,"b":2}                                 → "a"
     *   {}                                            → null
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A default mapper already fails on unknown keys — with a very specific exception
     *       subclass that exposes the property name.</li>
     * </ul>
     *
     * @param json JSON text of an object
     * @return the first unknown property name, or {@code null}
     * @throws JsonProcessingException for any other parsing problem
     */
    public String findUnknownProperty(String json) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement findUnknownProperty");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Parses JSON into an {@link Envelope}: {@code type} fills the {@code type} property,
     * and <em>every other key</em> is stored in the envelope's extras map. (Apply this by
     * annotating {@link Envelope#addExtra}.)
     *
     * <p>Examples:
     * <pre>
     *   {"type":"click","x":10,"y":20}  → type=click, extras={x=10, y=20}
     *   {"type":"ping"}                 → type=ping,  extras={}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One annotation marks a two-argument method as the catch-all for unknown keys.</li>
     * </ul>
     *
     * @param json JSON text of an object
     * @return the parsed envelope
     * @throws JsonProcessingException if the text is malformed
     */
    public Envelope parseEnvelope(String json) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement parseEnvelope");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Serialises an {@link Envelope} so that its extras are written as <em>top-level</em>
     * keys next to {@code type} — not nested under an {@code "extras"} key. (Apply this by
     * annotating {@link Envelope#getExtras}.)
     *
     * <p>Examples:
     * <pre>
     *   Envelope(type=click, extras={x=10, y=20})  → {"type":"click","x":10,"y":20}
     *   Envelope(type=ping,  extras={})            → {"type":"ping"}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The counterpart of the annotation from task 3, for the getter side.</li>
     * </ul>
     *
     * @param envelope the envelope to write
     * @return JSON text (key order is not significant)
     * @throws JsonProcessingException if serialisation fails
     */
    public String envelopeToJson(Envelope envelope) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement envelopeToJson");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Parses JSON into a {@link Point}, but <em>fails</em> when {@code x} or {@code y} is
     * missing instead of silently using {@code 0}.
     *
     * <p>Examples:
     * <pre>
     *   {"x":1,"y":2}  → Point[x=1, y=2]
     *   {"y":2,"x":1}  → Point[x=1, y=2]
     *   {"y":2}        → throws JsonMappingException   (x missing)
     *   {"x":1}        → throws JsonMappingException   (y missing)
     *   {}             → throws JsonMappingException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>There is a {@link com.fasterxml.jackson.databind.DeserializationFeature} about
     *       missing <em>creator</em> properties.</li>
     * </ul>
     *
     * @param json JSON text of an object
     * @return the parsed point
     * @throws JsonProcessingException if the text is malformed or a coordinate is missing
     */
    public Point parseRequiredPoint(String json) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement parseRequiredPoint");
    }

    // ── Model you must annotate ────────────────────────────────────────────────

    /** Envelope — annotate {@code addExtra} (task 3) and {@code getExtras} (task 4). */
    public static class Envelope {
        private String type;
        private final Map<String, Object> extras = new LinkedHashMap<>();

        public Envelope() { }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public void addExtra(String key, Object value) {
            extras.put(key, value);
        }

        public Map<String, Object> getExtras() {
            return extras;
        }
    }
}
