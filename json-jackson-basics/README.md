# json-jackson-basics

> **Phase 5 — I/O, Date-Time, Logging & HTTP** | Java 8+ | Package: `com.vbforge.json_jackson_basics`
>
> Tags: `I/O`, `JSON` · Dependencies: `jackson-databind`, `jackson-datatype-jsr310` (versions managed by the parent pom)
>
> The `record` examples need Java 16+; the repo builds with the parent's Java 17, so nothing to configure.

## What's covered

| Topic | Key points |
|---|---|
| `ObjectMapper` basics | `writeValueAsString`, `readValue`; reuse one thread-safe mapper; untyped numbers → `Integer`/`Long`/`Double`; `TypeReference` beats the `List.class` erasure trap |
| Tree model | `readTree`, `JsonNode`; `get` (Java `null`) vs `path` (`MissingNode`) vs `at("/json/pointer")`; JSON `null` is a `NullNode`; build with `ObjectNode` |
| Errors | `JsonProcessingException` is **checked** (`IOException`); `JsonParseException` for bad syntax; `writerWithDefaultPrettyPrinter` |
| POJOs | getters write, no-arg constructor + setters read; `isActive()` → `"active"`; no default constructor → `InvalidDefinitionException` |
| Records | no annotations needed (Jackson 2.12+); immutable; missing component → default value |
| Conversion | `convertValue`, `valueToTree`, `treeToValue`; failures surface as `IllegalArgumentException` |
| Annotations | `@JsonProperty` rename, `@JsonPropertyOrder`, `@JsonIgnore`, `access = WRITE_ONLY`, `@JsonInclude`, `@JsonAlias`, `@JsonCreator` |
| Dates | `JavaTimeModule`; `WRITE_DATES_AS_TIMESTAMPS`; `@JsonFormat(pattern=…)`; `Instant` vs `LocalDateTime`; legacy `java.util.Date` |
| Unknown fields | `FAIL_ON_UNKNOWN_PROPERTIES`, `UnrecognizedPropertyException`, `@JsonIgnoreProperties(ignoreUnknown = true)`, `@JsonAnySetter`/`@JsonAnyGetter`, `FAIL_ON_MISSING_CREATOR_PROPERTIES` |

## Files

```
src/
├── main/java/com/vbforge/json_jackson_basics/
│   ├── JsonBasicsDemo.java           ← theory: ObjectMapper, TypeReference, tree model, errors
│   ├── JsonBasicsTasks.java          ← 🎯 your tasks — implement these methods
│   ├── PojosAndRecordsDemo.java      ← theory: POJOs, records, nested types, convertValue
│   ├── PojosAndRecordsTasks.java     ← 🎯 your tasks
│   ├── JsonAnnotationsDemo.java      ← theory: @JsonProperty/@JsonIgnore/@JsonInclude/@JsonAlias/@JsonCreator
│   ├── JsonAnnotationsTasks.java     ← 🎯 your tasks (you also annotate 3 model classes)
│   ├── JsonDatesDemo.java            ← theory: JavaTimeModule, ISO strings, @JsonFormat, Date
│   ├── JsonDatesTasks.java           ← 🎯 your tasks (you also annotate Invoice)
│   ├── UnknownFieldsDemo.java        ← theory: unknown + missing fields, any-setter
│   └── UnknownFieldsTasks.java       ← 🎯 your tasks (you also annotate Envelope)
└── test/java/com/vbforge/json_jackson_basics/
    ├── JsonBasicsDemoTest.java       ← tests for the demo (already green)
    ├── JsonBasicsTasksTest.java      ← 🧪 tests for your tasks — all must pass
    ├── PojosAndRecordsDemoTest.java  ← tests for the demo (already green)
    ├── PojosAndRecordsTasksTest.java ← 🧪 tests for your tasks
    ├── JsonAnnotationsDemoTest.java  ← tests for the demo (already green)
    ├── JsonAnnotationsTasksTest.java ← 🧪 tests for your tasks
    ├── JsonDatesDemoTest.java        ← tests for the demo (already green)
    ├── JsonDatesTasksTest.java       ← 🧪 tests for your tasks
    ├── UnknownFieldsDemoTest.java    ← tests for the demo (already green)
    └── UnknownFieldsTasksTest.java   ← 🧪 tests for your tasks
```

Suggested order: Basics → Pojos & records → Annotations → Dates → Unknown fields.

## 🎯 Tasks

Open each `*Tasks.java` and implement its methods. Each has a detailed Javadoc.
In **Annotations**, **Dates** and **Unknown fields** a few tasks also ask you to
*add annotations to a small model class* at the bottom of the file — that is the lesson.

| # | Method | Topic |
|---|---|---|
| **JsonBasicsTasks** | | |
| 1 | `toJson(map)` | `writeValueAsString` |
| 2 | `parseStringList(json)` | `TypeReference` for `List<String>` |
| 3 | `textAtPointer(json, pointer)` | `readTree` + `at()`, `MissingNode` vs `NullNode` |
| 4 | `isValidJson(json)` | catching `JsonProcessingException` |
| **PojosAndRecordsTasks** | | |
| 1 | `personToJson(person)` | record → JSON |
| 2 | `jsonToPerson(json)` | JSON → record, default for missing |
| 3 | `jsonToPeople(json)` | `TypeReference<List<PersonRecord>>` |
| 4 | `personToMap(person)` | `convertValue` |
| 5 | `jsonToTeam(json)` | nested records + list |
| 6 | `jsonToPojo(json)` | no-arg constructor + setters |
| **JsonAnnotationsTasks** | | |
| 1 | `employeeToJson(employee)` | `@JsonProperty`, `@JsonIgnore`, `@JsonInclude` |
| 2 | `employeeFromJson(json)` | `@JsonAlias`, ignored input |
| 3 | `credentialsToJson(credentials)` | hide a field on output |
| 4 | `credentialsFromJson(json)` | `access = WRITE_ONLY` still reads |
| 5 | `geoPointFromJson(json)` | `@JsonCreator` for an immutable class |
| **JsonDatesTasks** | | |
| 1 | `createDateMapper()` | `JavaTimeModule` + disable timestamps |
| 2 | `instantToJson(instant)` | `Instant` as ISO-8601 UTC |
| 3 | `jsonToLocalDate(json)` | reading a `LocalDate`, bad input |
| 4 | `invoiceToJson(invoice)` | `@JsonFormat(pattern = …)` on write |
| 5 | `jsonToInvoice(json)` | `@JsonFormat` on read |
| **UnknownFieldsTasks** | | |
| 1 | `parseLenient(json)` | `FAIL_ON_UNKNOWN_PROPERTIES` off |
| 2 | `findUnknownProperty(json)` | `UnrecognizedPropertyException.getPropertyName()` |
| 3 | `parseEnvelope(json)` | `@JsonAnySetter` |
| 4 | `envelopeToJson(envelope)` | `@JsonAnyGetter` |
| 5 | `parseRequiredPoint(json)` | `FAIL_ON_MISSING_CREATOR_PROPERTIES` |

```bash
mvn test -Dtest=JsonBasicsTasksTest
```

All 5 task groups must go **GREEN** ✅ (they ship `@Disabled` on every `@Nested` class — remove it as you go)

## Key things to remember

```java
// 1. ObjectMapper is expensive to build and thread-safe once configured
private static final ObjectMapper MAPPER = new ObjectMapper();   // ✅ one shared instance
new ObjectMapper().readValue(json, Foo.class);                   // ❌ in a hot path: rebuilds caches every call

// 2. Generics are erased — List.class loses the element type
List<?> raw = MAPPER.readValue(json, List.class);                // ❌ elements are LinkedHashMap
List<Foo> ok = MAPPER.readValue(json, new TypeReference<List<Foo>>() { });  // ✅ note the trailing {}

// 3. JsonNode: get() can return Java null, path() never does
root.get("missing").asText();                                    // ❌ NullPointerException
root.path("missing").asText("n/a");                              // ✅ MissingNode is safe to chain
root.at("/user/address/city");                                   // ✅ JSON Pointer, one call
// JSON null is a NullNode — isNull() is true, but the node itself is not Java null

// 4. JsonProcessingException is CHECKED — catch it or declare it
String json = MAPPER.writeValueAsString(obj);                    // throws JsonProcessingException

// 5. A class with no no-arg constructor cannot be read without a creator
class Point { Point(int x, int y) {...} }                        // ❌ InvalidDefinitionException on read
record PointRecord(int x, int y) { }                             // ✅ records work out of the box
// or: @JsonCreator Point(@JsonProperty("x") int x, @JsonProperty("y") int y)   ✅

// 6. @JsonIgnore hides in BOTH directions — passwords need WRITE_ONLY
@JsonIgnore private String password;                             // ❌ can't be read from a login request
@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
private String password;                                         // ✅ read yes, written never

// 7. @JsonProperty rename is exclusive — the old name stops working on input
@JsonProperty("user_name") private String userName;              // "userName" in input → unknown property

// 8. java.time needs a module, and dates are numbers by default
new ObjectMapper().writeValueAsString(LocalDate.now());          // ❌ InvalidDefinitionException
new ObjectMapper().registerModule(new JavaTimeModule());         // ⚠️ works, but writes [2024,1,15]
mapper.registerModule(new JavaTimeModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // ✅ "2024-01-15"

// 9. Date pattern letters are case-sensitive
@JsonFormat(pattern = "dd/MM/yyyy")                              // ✅ MM = month
@JsonFormat(pattern = "dd/mm/yyyy")                              // ❌ mm = minute

// 10. Unknown fields fail by default — loosen at the narrowest scope that works
MAPPER.readerFor(Foo.class)
      .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .readValue(json);                                          // ✅ one call, shared mapper untouched
@JsonIgnoreProperties(ignoreUnknown = true) class Foo { ... }    // ✅ per class
mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // ⚠️ everywhere — hides typos

// 11. Missing fields do NOT fail by default — a silent 0 can be a nasty bug
MAPPER.readValue("{}", PointRecord.class);                       // PointRecord[x=0, y=0]
// enable FAIL_ON_MISSING_CREATOR_PROPERTIES where absence must be reported
// (it only covers records/constructors, not setter-based POJOs)

// 12. @JsonAnyGetter is the other half of @JsonAnySetter
@JsonAnySetter void put(String k, Object v) { extras.put(k, v); }   // collects unknown keys
@JsonAnyGetter Map<String, Object> extras() { return extras; }      // ✅ written back flat, not under "extras"
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest=JsonBasicsDemoTest

# Task tests only
mvn test -Dtest=JsonBasicsTasksTest

# Run main demo
mvn exec:java -Dexec.mainClass="com.vbforge.json_jackson_basics.JsonBasicsDemo"
```

Same pattern for the other four pairs — swap the class name:

| Concept | Demo class | Demo test | Task test |
|---|---|---|---|
| ObjectMapper basics | `JsonBasicsDemo` | `JsonBasicsDemoTest` | `JsonBasicsTasksTest` |
| POJOs & records | `PojosAndRecordsDemo` | `PojosAndRecordsDemoTest` | `PojosAndRecordsTasksTest` |
| Annotations | `JsonAnnotationsDemo` | `JsonAnnotationsDemoTest` | `JsonAnnotationsTasksTest` |
| Dates | `JsonDatesDemo` | `JsonDatesDemoTest` | `JsonDatesTasksTest` |
| Unknown fields | `UnknownFieldsDemo` | `UnknownFieldsDemoTest` | `UnknownFieldsTasksTest` |

```bash
# Every task test in the module
mvn test -Dtest='*TasksTest'
```
