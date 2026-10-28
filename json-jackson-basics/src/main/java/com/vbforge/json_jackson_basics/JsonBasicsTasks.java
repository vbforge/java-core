package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

/**
 * JsonBasicsTasks — implement every method below to make {@code JsonBasicsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code JsonBasicsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=JsonBasicsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures. Jackson is the only library you need.
 */
public class JsonBasicsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Converts a {@link Map} to a compact JSON string. Keys appear in the map's
     * iteration order.
     *
     * <p>Examples:
     * <pre>
     *   toJson({"id":1, "name":"Ann"})   → {"id":1,"name":"Ann"}
     *   toJson({"tags":["a","b"]})       → {"tags":["a","b"]}
     *   toJson({"note":null})            → {"note":null}
     *   toJson({})                       → {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link com.fasterxml.jackson.databind.ObjectMapper} has a single method that
     *       turns any object into a String.</li>
     *   <li>That method declares a checked exception — the signature already allows it.</li>
     * </ul>
     *
     * @param data the values to write (may be empty, values may be null or nested)
     * @return compact JSON text
     * @throws JsonProcessingException if serialisation fails
     */
    public String toJson(Map<String, Object> data) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement toJson");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Parses a JSON array of strings into a {@link List} of {@link String}.
     *
     * <p>Examples:
     * <pre>
     *   parseStringList(["a","b","c"])   → [a, b, c]
     *   parseStringList(["x"])           → [x]
     *   parseStringList([])              → []
     *   parseStringList({"a":1})         → throws JsonProcessingException (not an array)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Reading into {@code List.class} loses the element type — look at
     *       {@link com.fasterxml.jackson.core.type.TypeReference}.</li>
     * </ul>
     *
     * @param json JSON text expected to hold an array of strings
     * @return the strings in document order
     * @throws JsonProcessingException if the text is not an array of strings
     */
    public List<String> parseStringList(String json) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement parseStringList");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the text of the scalar value found at a JSON Pointer, or {@code null}
     * when there is nothing there (missing path, or the value is JSON {@code null}).
     * Numbers and booleans are returned as their text form. The pointer is
     * guaranteed to target either nothing or a scalar — never an object or array.
     *
     * <p>Examples:
     * <pre>
     *   textAtPointer({"user":{"name":"Ann"}},   "/user/name")   → "Ann"
     *   textAtPointer({"user":{"age":30}},       "/user/age")    → "30"
     *   textAtPointer({"tags":["a","b"]},        "/tags/1")      → "b"
     *   textAtPointer({"user":{"name":"Ann"}},   "/user/email")  → null
     *   textAtPointer({"user":{"email":null}},   "/user/email")  → null
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Parse into a tree, then look for a {@link com.fasterxml.jackson.databind.JsonNode}
     *       method that accepts a pointer string.</li>
     *   <li>Think about what a missing path and a JSON null each return — they are
     *       different node types.</li>
     * </ul>
     *
     * @param json    JSON text of an object
     * @param pointer a JSON Pointer such as {@code /user/name}
     * @return the scalar's text, or {@code null}
     * @throws JsonProcessingException if {@code json} is malformed
     */
    public String textAtPointer(String json, String pointer) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement textAtPointer");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if {@code json} is syntactically valid JSON, {@code false}
     * otherwise. Must never throw for non-null input. Any JSON value counts as valid,
     * including scalars such as {@code 42}, {@code true} or {@code "text"}.
     *
     * <p>Examples:
     * <pre>
     *   isValidJson({"a":1})    → true
     *   isValidJson([1,2,3])    → true
     *   isValidJson(42)         → true
     *   isValidJson({"a":      → false   (truncated)
     *   isValidJson({a:1})      → false   (unquoted field name)
     *   isValidJson({'a':1})    → false   (single quotes)
     *   isValidJson(not json)   → false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Try to parse; the parser tells you about bad input by throwing.</li>
     *   <li>Catch the most specific Jackson exception that covers every syntax error.</li>
     * </ul>
     *
     * @param json text to check (never null)
     * @return whether it parses as JSON
     */
    public boolean isValidJson(String json) {
        throw new UnsupportedOperationException("TODO: implement isValidJson");
    }
}
