package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JsonBasicsDemo — annotated reference for the core Jackson workflow.
 *
 * <p>Read top-to-bottom. Each section covers one part of the {@link ObjectMapper} API
 * with concrete examples and a note explaining <em>why</em> it behaves that way.
 *
 * <p>Topics:
 * <ol>
 *   <li>ObjectMapper basics — {@code writeValueAsString}, {@code readValue}, scalars, number types</li>
 *   <li>Generic types — {@link TypeReference} and the type-erasure trap</li>
 *   <li>Tree model — {@link JsonNode}, {@code get} vs {@code path} vs {@code at}, building trees</li>
 *   <li>Pretty printing and errors — {@code writerWithDefaultPrettyPrinter}, {@link JsonProcessingException}</li>
 * </ol>
 */
public class JsonBasicsDemo {

    /**
     * ONE shared mapper for the whole class.
     *
     * <p>WHY: building an {@link ObjectMapper} is expensive (it scans classes and caches
     * serializers/deserializers). Once configured, it is thread-safe — so create it once
     * and reuse it. Creating a new mapper per call throws that cache away every time.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        objectMapperDemo();
        System.out.println("---");
        typeReferenceDemo();
        System.out.println("---");
        treeModelDemo();
        System.out.println("---");
        prettyPrintAndErrorsDemo();
    }

    /** Lets us write JSON with single quotes in Java source — far easier to read. */
    private static String q(String singleQuoted) {
        return singleQuoted.replace('\'', '"');
    }

    // ── 1. ObjectMapper basics ────────────────────────────────────────────────

    /**
     * {@link ObjectMapper} converts Java objects to JSON text (serialisation) and back
     * (deserialisation).
     *
     * <p>WHY it matters: JSON is the lingua franca of web APIs, config files and message
     * queues. Without a mapper you would parse strings by hand and escape quotes yourself.
     *
     * <p>Complexity: both directions are O(n) in the size of the document.
     *
     * <p>Gotcha: when you read into a raw {@code Map} or {@code Object}, Jackson picks the
     * number type by size — {@link Integer} if it fits, then {@link Long}, and
     * {@link Double} for decimals. Your code must not assume one type.
     */
    static void objectMapperDemo() throws JsonProcessingException {
        System.out.println("=== ObjectMapper basics ===");

        // Java -> JSON
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", 1);
        user.put("name", "Ann");
        user.put("admin", false);
        user.put("tags", Arrays.asList("java", "json"));

        String json = MAPPER.writeValueAsString(user);
        System.out.println("writeValueAsString : " + json);   // {"id":1,"name":"Ann","admin":false,"tags":["java","json"]}

        // JSON -> Java (untyped): objects become LinkedHashMap, arrays become ArrayList
        Map<?, ?> back = MAPPER.readValue(json, Map.class);
        System.out.println("readValue(Map)     : " + back);
        System.out.println("map class          : " + back.getClass().getSimpleName());          // LinkedHashMap
        System.out.println("tags class         : " + back.get("tags").getClass().getSimpleName()); // ArrayList

        // Number type depends on the size of the value
        Map<?, ?> numbers = MAPPER.readValue(q("{'small':1,'big':12345678901,'dec':1.5}"), Map.class);
        System.out.println("small              : " + numbers.get("small").getClass().getSimpleName()); // Integer
        System.out.println("big                : " + numbers.get("big").getClass().getSimpleName());   // Long
        System.out.println("dec                : " + numbers.get("dec").getClass().getSimpleName());   // Double

        // Scalars are valid JSON documents too — strings come out quoted
        System.out.println("string literal     : " + MAPPER.writeValueAsString("hi"));   // "hi"
        System.out.println("readValue(int)     : " + MAPPER.readValue("42", Integer.class)); // 42
        System.out.println("null literal       : " + MAPPER.writeValueAsString(null));   // null
    }

    // ── 2. Generic types ──────────────────────────────────────────────────────

    /**
     * Java erases generic type arguments at runtime, so {@code List.class} carries no
     * information about the element type.
     *
     * <p>WHY it matters: {@code readValue(json, List.class)} compiles and runs, but the
     * elements are {@code LinkedHashMap}s, not your type — the bug surfaces later as a
     * {@link ClassCastException}. A {@link TypeReference} captures the full generic type
     * through an anonymous subclass (note the trailing {@code {}}).
     *
     * <p>Complexity: O(n); the type is resolved once per call and cached by the mapper.
     */
    static void typeReferenceDemo() throws JsonProcessingException {
        System.out.println("=== TypeReference ===");

        String json = q("[{'id':1},{'id':2}]");

        // ❌ erasure: elements are LinkedHashMap, not Map<String,Integer> as you might hope
        List<?> raw = MAPPER.readValue(json, List.class);
        System.out.println("List.class element : " + raw.get(0).getClass().getSimpleName()); // LinkedHashMap

        // ✅ TypeReference keeps the generics
        List<Map<String, Integer>> typed =
                MAPPER.readValue(json, new TypeReference<List<Map<String, Integer>>>() { });
        System.out.println("typed get(1).id + 1: " + (typed.get(1).get("id") + 1));          // 3

        // Nested generics work the same way
        Map<String, List<Integer>> scores =
                MAPPER.readValue(q("{'ann':[1,2],'bob':[3]}"), new TypeReference<Map<String, List<Integer>>>() { });
        System.out.println("scores             : " + scores);                                // {ann=[1, 2], bob=[3]}

        // Arrays carry their element type, so no TypeReference is needed
        int[] numbers = MAPPER.readValue("[1,2,3]", int[].class);
        System.out.println("int[] length       : " + numbers.length);                        // 3

        // Programmatic alternative to TypeReference
        List<Integer> viaFactory = MAPPER.readValue("[4,5]",
                MAPPER.getTypeFactory().constructCollectionType(List.class, Integer.class));
        System.out.println("constructCollection: " + viaFactory);                            // [4, 5]
    }

    // ── 3. Tree model ─────────────────────────────────────────────────────────

    /**
     * {@link JsonNode} is an in-memory tree of the document — use it when you don't have
     * (or don't want) a Java class for the JSON.
     *
     * <p>WHY it matters: peeking at one field of a large payload, or handling JSON whose
     * shape varies, is far easier with a tree than with a class per variant.
     *
     * <p>Complexity: building the tree is O(n); {@code get}/{@code path} on an object are
     * O(1); {@code at(pointer)} is O(depth).
     *
     * <p>Gotcha: {@code get("missing")} returns Java {@code null} (NPE bait), while
     * {@code path("missing")} returns a {@code MissingNode} you can safely chain on.
     * A JSON {@code null} is neither — it is a {@code NullNode}.
     */
    static void treeModelDemo() throws JsonProcessingException {
        System.out.println("=== Tree model ===");

        JsonNode root = MAPPER.readTree(
                q("{'user':{'name':'Ann','age':30,'address':null},'tags':['a','b']}"));

        System.out.println("name               : " + root.get("user").get("name").asText());   // Ann
        System.out.println("age + 1            : " + (root.get("user").get("age").asInt() + 1)); // 31
        System.out.println("age isInt          : " + root.get("user").get("age").isInt());       // true

        // get vs path
        System.out.println("get(nope) == null  : " + (root.get("nope") == null));                // true
        System.out.println("path(nope) missing : " + root.path("nope").isMissingNode());         // true
        System.out.println("safe chain         : " + root.path("nope").path("deeper").asText("n/a")); // n/a

        // JSON null is a NullNode, not Java null
        JsonNode address = root.get("user").get("address");
        System.out.println("address isNull     : " + address.isNull());                          // true

        // JSON Pointer — one call instead of a chain
        System.out.println("at(/tags/1)        : " + root.at("/tags/1").asText());               // b
        System.out.println("at(/user/zip)      : " + root.at("/user/zip").isMissingNode());      // true

        // Iterate over field names
        Iterator<String> names = root.get("user").fieldNames();
        StringBuilder sb = new StringBuilder();
        while (names.hasNext()) {
            sb.append(names.next()).append(' ');
        }
        System.out.println("user fields        : " + sb.toString().trim());                      // name age address

        // Build a tree by hand
        ObjectNode out = MAPPER.createObjectNode();
        out.put("id", 7);
        out.put("ok", true);
        ArrayNode tags = out.putArray("tags");
        tags.add("x").add("y");
        out.set("child", MAPPER.createObjectNode().put("k", "v"));
        System.out.println("built tree         : " + out);   // {"id":7,"ok":true,"tags":["x","y"],"child":{"k":"v"}}
    }

    // ── 4. Pretty printing and errors ─────────────────────────────────────────

    /**
     * Output is compact by default; ask the writer for indentation when humans will read it.
     *
     * <p>WHY it matters: compact JSON saves bytes on the wire, pretty JSON saves your eyes
     * in logs and config files. Pick per use case — don't change the shared mapper.
     *
     * <p>Errors: malformed input throws {@link JsonProcessingException}, a <em>checked</em>
     * exception (it extends {@code IOException}), so you must catch it or declare it.
     *
     * <p>Gotcha: single quotes and unquoted field names are valid JavaScript but
     * <em>invalid JSON</em> — Jackson rejects both by default.
     */
    static void prettyPrintAndErrorsDemo() {
        System.out.println("=== Pretty printing and errors ===");

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("name", "Ann");
        data.put("tags", Arrays.asList("a", "b"));

        try {
            System.out.println("compact : " + MAPPER.writeValueAsString(data));
            System.out.println("pretty  :\n" + MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(data));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);   // cannot happen for a Map of plain values
        }

        String[] broken = {"{\"a\":", "{a:1}", "{'a':1}", "not json"};
        for (String bad : broken) {
            try {
                MAPPER.readTree(bad);
                System.out.println(bad + " -> parsed (unexpected)");
            } catch (JsonProcessingException e) {
                System.out.println(bad + " -> " + e.getClass().getSimpleName());
            }
        }
    }
}
