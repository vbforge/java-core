package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JsonAnnotationsDemo — controlling the JSON shape with Jackson annotations.
 *
 * <p>Read top-to-bottom. All model classes are public nested types so the tests can use them.
 *
 * <p>Topics:
 * <ol>
 *   <li>{@code @JsonProperty} and {@code @JsonPropertyOrder} — rename and order</li>
 *   <li>{@code @JsonIgnore} vs {@code access = WRITE_ONLY} — hiding data</li>
 *   <li>{@code @JsonInclude} — omitting null / empty values</li>
 *   <li>{@code @JsonAlias} — accepting several input names</li>
 *   <li>{@code @JsonCreator} — reading immutable classes</li>
 * </ol>
 */
public class JsonAnnotationsDemo {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ── Model types ───────────────────────────────────────────────────────────

    /** A user as seen by an external API (snake_case names, hidden internals). */
    @JsonPropertyOrder({"id", "user_name", "email"})
    public static class Member {
        private int id;
        @JsonProperty("user_name")
        private String userName;
        @JsonAlias({"mail", "email_address"})
        private String email;
        private String nickname;
        @JsonIgnore
        private String internalNote;
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        private String password;

        public Member() { }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getInternalNote() { return internalNote; }
        public void setInternalNote(String internalNote) { this.internalNote = internalNote; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    /** Write-only model demonstrating per-property {@code @JsonInclude}. */
    public static class Profile {
        private final String city;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private final String bio;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private final List<String> tags;

        public Profile(String city, String bio, List<String> tags) {
            this.city = city;
            this.bio = bio;
            this.tags = tags;
        }

        public String getCity() { return city; }
        public String getBio() { return bio; }
        public List<String> getTags() { return tags; }
    }

    /** Immutable class that Jackson can read thanks to {@code @JsonCreator}. */
    public static class Coordinates {
        private final double lat;
        private final double lon;

        @JsonCreator
        public Coordinates(@JsonProperty("lat") double lat, @JsonProperty("lon") double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        public double getLat() { return lat; }
        public double getLon() { return lon; }
    }

    private static Member member(int id, String userName, String email) {
        Member m = new Member();
        m.setId(id);
        m.setUserName(userName);
        m.setEmail(email);
        return m;
    }

    public static void main(String[] args) throws Exception {
        renameDemo();
        System.out.println("---");
        ignoreDemo();
        System.out.println("---");
        includeDemo();
        System.out.println("---");
        aliasDemo();
        System.out.println("---");
        creatorDemo();
    }

    // ── 1. Rename and order ───────────────────────────────────────────────────

    /**
     * {@code @JsonProperty("name")} sets the JSON name of a property;
     * {@code @JsonPropertyOrder} fixes the order of keys in the output.
     *
     * <p>WHY it matters: Java uses {@code camelCase}, many APIs use {@code snake_case}.
     * Annotations let the Java class keep idiomatic names while the wire format stays
     * what the other side expects.
     *
     * <p>Complexity: no runtime cost beyond the one-time class analysis.
     *
     * <p>Gotcha: renaming is two-way and exclusive — after the rename, the old name
     * ({@code userName}) is <em>not</em> accepted on input any more.
     */
    static void renameDemo() throws Exception {
        System.out.println("=== @JsonProperty / @JsonPropertyOrder ===");

        Member m = member(1, "ann", "ann@example.com");
        System.out.println("serialised : " + MAPPER.writeValueAsString(m));
        // {"id":1,"user_name":"ann","email":"ann@example.com","nickname":null}

        Member back = MAPPER.readValue("{\"id\":2,\"user_name\":\"bob\"}", Member.class);
        System.out.println("read       : " + back.getUserName());                  // bob

        try {
            MAPPER.readValue("{\"userName\":\"bob\"}", Member.class);
            System.out.println("old name   : accepted (unexpected)");
        } catch (UnrecognizedPropertyException e) {
            System.out.println("old name   : rejected, unknown = " + e.getPropertyName());   // userName
        }
    }

    // ── 2. Hiding data ────────────────────────────────────────────────────────

    /**
     * {@code @JsonIgnore} removes a property from <em>both</em> directions.
     * {@code @JsonProperty(access = WRITE_ONLY)} keeps it for <em>reading</em> only.
     *
     * <p>WHY it matters: secrets must not leak into responses or logs, yet a password has
     * to be readable when a client submits it. {@code @JsonIgnore} would break that.
     *
     * <p>Complexity: none at runtime.
     *
     * <p>Gotcha: an ignored property that shows up in the input is skipped silently —
     * it does not raise an "unknown property" error.
     */
    static void ignoreDemo() throws Exception {
        System.out.println("=== @JsonIgnore vs WRITE_ONLY ===");

        Member m = member(1, "ann", "ann@example.com");
        m.setInternalNote("vip");
        m.setPassword("s3cret");
        String json = MAPPER.writeValueAsString(m);
        System.out.println("has internalNote : " + json.contains("internalNote"));    // false
        System.out.println("has password    : " + json.contains("password"));         // false

        Member in = MAPPER.readValue(
                "{\"id\":3,\"user_name\":\"cy\",\"password\":\"pw\",\"internalNote\":\"hack\"}", Member.class);
        System.out.println("password read   : " + in.getPassword());                  // pw   (WRITE_ONLY still reads)
        System.out.println("note read       : " + in.getInternalNote());              // null (@JsonIgnore skips it)
    }

    // ── 3. Include rules ──────────────────────────────────────────────────────

    /**
     * {@code @JsonInclude} decides when a value is written. {@code NON_NULL} skips
     * {@code null}; {@code NON_EMPTY} also skips empty strings, collections and arrays.
     *
     * <p>WHY it matters: smaller payloads, and APIs that treat "absent" and "null"
     * differently. You can set the rule per property, per class, or mapper-wide with
     * {@code setSerializationInclusion}.
     *
     * <p>Complexity: one extra check per property while writing.
     *
     * <p>Gotcha: by default Jackson writes {@code null} explicitly ({@code "city":null}).
     * And {@code NON_NULL} keeps empty strings — only {@code NON_EMPTY} drops them.
     */
    static void includeDemo() throws Exception {
        System.out.println("=== @JsonInclude ===");

        Profile sparse = new Profile(null, null, Collections.<String>emptyList());
        System.out.println("sparse     : " + MAPPER.writeValueAsString(sparse));      // {"city":null}

        Profile full = new Profile(null, "", new ArrayList<>(Collections.singletonList("a")));
        System.out.println("full       : " + MAPPER.writeValueAsString(full));        // {"city":null,"bio":"","tags":["a"]}

        // Mapper-wide rule instead of per-property annotations (own mapper: never mutate the shared one)
        ObjectMapper nonNull = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        System.out.println("mapper-wide: " + nonNull.writeValueAsString(member(1, "ann", "ann@example.com")));
        // {"id":1,"user_name":"ann","email":"ann@example.com"}
    }

    // ── 4. Aliases ────────────────────────────────────────────────────────────

    /**
     * {@code @JsonAlias} adds extra <em>input</em> names for a property; output always
     * uses the primary name.
     *
     * <p>WHY it matters: it lets you accept an old and a new field name during a migration
     * without breaking either client.
     *
     * <p>Complexity: none at runtime.
     *
     * <p>Gotcha: aliases only affect reading — serialisation never emits an alias.
     */
    static void aliasDemo() throws Exception {
        System.out.println("=== @JsonAlias ===");

        for (String key : new String[]{"email", "mail", "email_address"}) {
            Member m = MAPPER.readValue("{\"" + key + "\":\"a@x.io\"}", Member.class);
            System.out.println(key + " -> " + m.getEmail());                          // a@x.io each time
        }
        System.out.println("written as : " + MAPPER.writeValueAsString(member(1, "ann", "a@x.io")).contains("\"email\":"));
    }

    // ── 5. @JsonCreator ───────────────────────────────────────────────────────

    /**
     * {@code @JsonCreator} marks the constructor (or static factory) Jackson must use, and
     * {@code @JsonProperty} on each parameter names the JSON field that feeds it.
     *
     * <p>WHY it matters: it is how you keep a class immutable (final fields, no setters)
     * and still deserialise it. Records give you this for free from Java 16.
     *
     * <p>Complexity: none beyond normal construction.
     *
     * <p>Gotcha: forget the {@code @JsonProperty} on a parameter and the build still
     * compiles — the failure only appears at runtime.
     */
    static void creatorDemo() throws Exception {
        System.out.println("=== @JsonCreator ===");

        Coordinates c = MAPPER.readValue("{\"lon\":30.52,\"lat\":50.45}", Coordinates.class);
        System.out.println("lat        : " + c.getLat());                             // 50.45
        System.out.println("lon        : " + c.getLon());                             // 30.52
        System.out.println("written    : " + MAPPER.writeValueAsString(c));           // {"lat":50.45,"lon":30.52}
    }
}
