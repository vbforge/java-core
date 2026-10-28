package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * JsonAnnotationsTasks — implement every method below to make {@code JsonAnnotationsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code JsonAnnotationsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=JsonAnnotationsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p><b>This pair is different:</b> the lesson is the annotations themselves, so besides
 * the methods you must also <em>add Jackson annotations</em> to the three plain model
 * classes at the bottom of this file ({@link Employee}, {@link Credentials},
 * {@link GeoPoint}). Each task's Javadoc says which rules its model must follow.
 * Do not change method signatures or the constructors/getters/setters of the models.
 */
public class JsonAnnotationsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Serialises an {@link Employee} to JSON following these rules — apply them by
     * annotating {@link Employee}:
     * <ul>
     *   <li>{@code fullName} is written as {@code "full_name"}</li>
     *   <li>{@code internalNotes} is never written</li>
     *   <li>properties whose value is {@code null} are omitted</li>
     * </ul>
     *
     * <p>Examples:
     * <pre>
     *   Employee(1, "Ann Lee", "ann@x.io", null,    "vip")
     *       → {"id":1,"full_name":"Ann Lee","email":"ann@x.io"}
     *   Employee(2, "Bob",     "b@x.io",   "bobby", null)
     *       → {"id":2,"full_name":"Bob","email":"b@x.io","nickname":"bobby"}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Three annotations: one that renames, one that hides, one that controls
     *       null handling (it can sit on the class).</li>
     * </ul>
     *
     * @param employee the employee to write
     * @return JSON text (key order is not significant)
     * @throws JsonProcessingException if serialisation fails
     */
    public String employeeToJson(Employee employee) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement employeeToJson");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Parses JSON into an {@link Employee}, using the same {@code full_name} naming.
     * Additionally, {@code email} must also be accepted under the input name
     * {@code mail}, and {@code internalNotes} must never be read from the input —
     * if present it is skipped silently and the field stays {@code null}.
     *
     * <p>Examples:
     * <pre>
     *   {"id":2,"full_name":"Bob","email":"b@x.io"}
     *       → id=2, fullName=Bob, email=b@x.io
     *   {"id":3,"full_name":"Cy","mail":"c@x.io"}
     *       → id=3, fullName=Cy, email=c@x.io
     *   {"id":4,"full_name":"Di","internalNotes":"hack"}
     *       → id=4, fullName=Di, internalNotes=null
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>An annotation that adds <em>input-only</em> alternative names exists.</li>
     * </ul>
     *
     * @param json JSON text of an employee
     * @return the parsed employee
     * @throws JsonProcessingException if the text is malformed or has unknown fields
     */
    public Employee employeeFromJson(String json) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement employeeFromJson");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Serialises {@link Credentials} so that the {@code password} is <strong>never</strong>
     * written, while {@code username} is. (Apply the rule by annotating {@link Credentials}.)
     *
     * <p>Examples:
     * <pre>
     *   Credentials("ann", "s3cret")  → {"username":"ann"}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>See task 4 first: a plain "ignore" annotation will not be enough.</li>
     * </ul>
     *
     * @param credentials the credentials to write
     * @return JSON text without the password
     * @throws JsonProcessingException if serialisation fails
     */
    public String credentialsToJson(Credentials credentials) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement credentialsToJson");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Parses JSON into {@link Credentials}. The {@code password} <em>must still be read</em>
     * from the input, even though task 3 never writes it.
     *
     * <p>Examples:
     * <pre>
     *   {"username":"ann","password":"s3cret"}  → username=ann, password=s3cret
     *   {"username":"bob"}                      → username=bob, password=null
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One annotation attribute makes a property read-only-in-one-direction;
     *       look at {@code access} on {@code @JsonProperty}.</li>
     * </ul>
     *
     * @param json JSON text of credentials
     * @return the parsed credentials
     * @throws JsonProcessingException if the text is malformed
     */
    public Credentials credentialsFromJson(String json) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement credentialsFromJson");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Parses JSON into the <em>immutable</em> {@link GeoPoint} (final fields, no no-arg
     * constructor). Make it readable by annotating its constructor and parameters.
     *
     * <p>Examples:
     * <pre>
     *   {"lat":50.45,"lon":30.52}  → lat=50.45, lon=30.52
     *   {"lon":1.0,"lat":2.0}      → lat=2.0,   lon=1.0   (key order does not matter)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Mark the constructor as the creator, and name each parameter's JSON field.</li>
     * </ul>
     *
     * @param json JSON text of a point
     * @return the parsed point
     * @throws JsonProcessingException if the text is malformed
     */
    public GeoPoint geoPointFromJson(String json) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement geoPointFromJson");
    }

    // ── Models you must annotate ───────────────────────────────────────────────

    /** Employee — annotate for tasks 1 and 2. */
    public static class Employee {
        private int id;
        private String fullName;
        private String email;
        private String nickname;
        private String internalNotes;

        public Employee() { }

        public Employee(int id, String fullName, String email, String nickname, String internalNotes) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.nickname = nickname;
            this.internalNotes = internalNotes;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getInternalNotes() { return internalNotes; }
        public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }
    }

    /** Credentials — annotate for tasks 3 and 4. */
    public static class Credentials {
        private String username;
        private String password;

        public Credentials() { }

        public Credentials(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    /** GeoPoint — immutable; annotate for task 5. */
    public static class GeoPoint {
        private final double lat;
        private final double lon;

        public GeoPoint(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        public double getLat() { return lat; }
        public double getLon() { return lon; }
    }
}
