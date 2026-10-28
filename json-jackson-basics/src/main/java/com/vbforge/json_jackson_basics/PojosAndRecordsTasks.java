package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vbforge.json_jackson_basics.PojosAndRecordsDemo.PersonPojo;
import com.vbforge.json_jackson_basics.PojosAndRecordsDemo.PersonRecord;
import com.vbforge.json_jackson_basics.PojosAndRecordsDemo.Team;

import java.util.List;
import java.util.Map;

/**
 * PojosAndRecordsTasks — implement every method below to make {@code PojosAndRecordsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code PojosAndRecordsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=PojosAndRecordsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>The model types ({@code PersonRecord}, {@code PersonPojo}, {@code Team}) live in
 * {@code PojosAndRecordsDemo} and are already imported. Do not change method signatures.
 */
public class PojosAndRecordsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Serialises a {@link PersonRecord} to JSON. Property order is not significant.
     *
     * <p>Examples:
     * <pre>
     *   personToJson(PersonRecord[name=Ann, age=30])  → {"name":"Ann","age":30}
     *   personToJson(PersonRecord[name=null, age=0])  → {"name":null,"age":0}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Records need no annotations — the mapper already knows how to write them.</li>
     * </ul>
     *
     * @param person the record to write
     * @return JSON text
     * @throws JsonProcessingException if serialisation fails
     */
    public String personToJson(PersonRecord person) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement personToJson");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Parses a JSON object into a {@link PersonRecord}. A field missing from the JSON
     * takes its default value ({@code null} / {@code 0}).
     *
     * <p>Examples:
     * <pre>
     *   jsonToPerson({"name":"Ann","age":30})  → PersonRecord[name=Ann, age=30]
     *   jsonToPerson({"name":"Bob"})           → PersonRecord[name=Bob, age=0]
     *   jsonToPerson({})                       → PersonRecord[name=null, age=0]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Pass the target class to the mapper's read method.</li>
     * </ul>
     *
     * @param json JSON text of an object
     * @return the parsed record
     * @throws JsonProcessingException if the text is malformed
     */
    public PersonRecord jsonToPerson(String json) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement jsonToPerson");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Parses a JSON array of person objects into a {@link List} of {@link PersonRecord},
     * in document order.
     *
     * <p>Examples:
     * <pre>
     *   jsonToPeople([{"name":"Ann","age":30},{"name":"Bob","age":41}])
     *       → [PersonRecord[name=Ann, age=30], PersonRecord[name=Bob, age=41]]
     *   jsonToPeople([])  → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The element type must survive erasure — see
     *       {@link com.fasterxml.jackson.core.type.TypeReference}.</li>
     * </ul>
     *
     * @param json JSON text of an array
     * @return the parsed records
     * @throws JsonProcessingException if the text is malformed
     */
    public List<PersonRecord> jsonToPeople(String json) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement jsonToPeople");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Converts a {@link PersonRecord} to a {@code Map<String, Object>} <em>without</em>
     * going through JSON text. Numbers come back as {@link Integer}.
     *
     * <p>Examples:
     * <pre>
     *   personToMap(PersonRecord[name=Ann, age=30])  → {name=Ann, age=30}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Look for a mapper method that converts one Java type to another directly.</li>
     *   <li>The target is generic — use a type token.</li>
     * </ul>
     *
     * @param person the record to convert
     * @return a map with one entry per record component
     */
    public Map<String, Object> personToMap(PersonRecord person) {
        throw new UnsupportedOperationException("TODO: implement personToMap");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Parses a JSON object containing a team name and a list of members into a
     * {@link Team}.
     *
     * <p>Examples:
     * <pre>
     *   jsonToTeam({"name":"Core","members":[{"name":"Ann","age":30}]})
     *       → Team[name=Core, members=[PersonRecord[name=Ann, age=30]]]
     *   jsonToTeam({"name":"Empty","members":[]})
     *       → Team[name=Empty, members=[]]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Nested records need no extra configuration.</li>
     * </ul>
     *
     * @param json JSON text of a team
     * @return the parsed team
     * @throws JsonProcessingException if the text is malformed
     */
    public Team jsonToTeam(String json) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement jsonToTeam");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Parses a JSON object into a classic {@link PersonPojo} (no-arg constructor +
     * setters). A missing field keeps the POJO's default ({@code null} / {@code 0} /
     * {@code false}).
     *
     * <p>Examples:
     * <pre>
     *   jsonToPojo({"name":"Ann","age":30,"active":true})
     *       → name=Ann, age=30, active=true
     *   jsonToPojo({"name":"Bob"})
     *       → name=Bob, age=0, active=false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The JSON key is {@code active}, even though the Java getter is {@code isActive()}.</li>
     * </ul>
     *
     * @param json JSON text of an object
     * @return the populated POJO
     * @throws JsonProcessingException if the text is malformed
     */
    public PersonPojo jsonToPojo(String json) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement jsonToPojo");
    }
}
