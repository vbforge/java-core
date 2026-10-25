# date-time-api

> **Phase 5 — I/O, Date-Time, Logging & HTTP** | Java 8 | Package: `com.vbforge.date_time_api` | Tags: `DATE-TIME`

## What's covered

| Topic | Key points |
|---|---|
| `LocalDate` / `LocalTime` / `LocalDateTime` | Human date-time **without a zone**; 1-based months; immutable (`plusDays` returns a new value); `plusMonths` clamps `Jan 31 → Feb 29`; `TemporalAdjusters` (`lastDayOfMonth`, `next`, `dayOfWeekInMonth`) |
| `Instant` & UTC timestamps | One point on the UTC timeline; `ofEpochSecond` / `ofEpochMilli` / `toEpochMilli`; `toString()` ends in `Z`; no months/years (`UnsupportedTemporalTypeException`); `Clock.fixed` for testable "now" |
| `ZoneId` / `ZoneOffset` / `ZonedDateTime` | Region + rules vs fixed shift; `withZoneSameInstant` vs `withZoneSameLocal`; DST gap (23-hour day) and overlap (25-hour day); `withLaterOffsetAtOverlap`; `OffsetDateTime` |
| `Period` / `Duration` | `Period` = calendar (years, months, days), `Duration` = exact seconds/nanos; `Period.between`, `Duration.between`, `ChronoUnit.X.between`; `getDays()` is only a component |
| Formatting & parsing | `DateTimeFormatter.ofPattern`, `ISO_*` constants; `mm` vs `MM`, `YYYY` vs `yyyy`; `ResolverStyle.SMART/STRICT/LENIENT`; `withZone` for `Instant`; always pass a `Locale` for names |
| Comparing & differences | `isBefore` / `isAfter` / `isEqual` vs `equals` / `compareTo`; zoned values compare by instant; sorting; closed vs half-open ranges and the overlap test |
| Converting | `Instant` ↔ `LocalDateTime` ↔ `ZonedDateTime` always needs a zone; `ofInstant`, `atZone`, `toInstant`, `toLocalDateTime`; lossy round trip in a DST overlap; `Date.from` / `Date.toInstant`; epoch day / second |

## Files

```
src/
├── main/java/com/vbforge/date_time_api/
│   ├── LocalDateTimeDemo.java          ← LocalDate / LocalTime / LocalDateTime / adjusters
│   ├── LocalDateTimeTasks.java         ← 🎯 your tasks — implement these methods
│   ├── InstantDemo.java                ← Instant, epoch numbers, UTC, Clock
│   ├── InstantTasks.java               ← 🎯 your tasks — implement these methods
│   ├── ZonedDateTimeDemo.java          ← zones, offsets, DST gap & overlap, OffsetDateTime
│   ├── ZonedDateTimeTasks.java         ← 🎯 your tasks — implement these methods
│   ├── PeriodDurationDemo.java         ← Period, Duration, ChronoUnit.between
│   ├── PeriodDurationTasks.java        ← 🎯 your tasks — implement these methods
│   ├── FormattingParsingDemo.java      ← DateTimeFormatter, ISO formats, resolver styles
│   ├── FormattingParsingTasks.java     ← 🎯 your tasks — implement these methods
│   ├── ComparisonDemo.java             ← ordering, equality, sorting, ranges
│   ├── ComparisonTasks.java            ← 🎯 your tasks — implement these methods
│   ├── ConversionDemo.java             ← Instant ↔ LocalDateTime ↔ ZonedDateTime, legacy Date
│   └── ConversionTasks.java            ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/date_time_api/
    ├── LocalDateTimeDemoTest.java      ← 🧪 tests for the demo (already green)
    ├── LocalDateTimeTasksTest.java     ← 🧪 tests for your tasks — all must pass
    ├── InstantDemoTest.java            ← 🧪 tests for the demo (already green)
    ├── InstantTasksTest.java           ← 🧪 tests for your tasks — all must pass
    ├── ZonedDateTimeDemoTest.java      ← 🧪 tests for the demo (already green)
    ├── ZonedDateTimeTasksTest.java     ← 🧪 tests for your tasks — all must pass
    ├── PeriodDurationDemoTest.java     ← 🧪 tests for the demo (already green)
    ├── PeriodDurationTasksTest.java    ← 🧪 tests for your tasks — all must pass
    ├── FormattingParsingDemoTest.java  ← 🧪 tests for the demo (already green)
    ├── FormattingParsingTasksTest.java ← 🧪 tests for your tasks — all must pass
    ├── ComparisonDemoTest.java         ← 🧪 tests for the demo (already green)
    ├── ComparisonTasksTest.java        ← 🧪 tests for your tasks — all must pass
    ├── ConversionDemoTest.java         ← 🧪 tests for the demo (already green)
    └── ConversionTasksTest.java        ← 🧪 tests for your tasks — all must pass
```

> Every test is independent of the machine's time zone and language: all zones are explicit
> and every pattern that prints names carries a `Locale`.

## 🎯 Tasks

Seven Demo / Tasks pairs, 38 task methods in total. Each has a detailed Javadoc.

### `LocalDateTimeTasks`

| # | Method | Topic |
|---|---|---|
| 1 | `lastDayOfMonth(date)` | `TemporalAdjusters`, leap years |
| 2 | `nextWorkingDay(date)` | `DayOfWeek` checks, skipping weekends |
| 3 | `quarterOf(date)` | 1-based month arithmetic |
| 4 | `floorToQuarterHour(dateTime)` | `truncatedTo` + minute remainder |
| 5 | `nthWeekdayOfMonth(year, month, dow, n)` | `dayOfWeekInMonth` adjuster |

### `InstantTasks`

| # | Method | Topic |
|---|---|---|
| 1 | `epochMillisToIso(epochMillis)` | `Instant.ofEpochMilli`, ISO text |
| 2 | `isExpired(issuedAt, ttl, now)` | `Instant.plus(Duration)`, strict `isAfter`, injected "now" |
| 3 | `latest(instants)` | `Comparable` on `Instant`, `Optional` |
| 4 | `addDays(instant, days)` | `ChronoUnit.DAYS` (supported) vs `MONTHS` (not) |
| 5 | `truncateToMinute(instant)` | `truncatedTo` |

### `ZonedDateTimeTasks`

| # | Method | Topic |
|---|---|---|
| 1 | `convertToZone(moment, zone)` | `withZoneSameInstant` |
| 2 | `meetingTimeFor(local, meetingZone, participantZone)` | local → zoned → other zone → local |
| 3 | `offsetAt(zone, instant)` | `ZoneRules.getOffset` |
| 4 | `hoursInDay(date, zone)` | `atStartOfDay(zone)`, 23/24/25-hour days |
| 5 | `isDaylightSavingTime(zone, instant)` | `ZoneRules.isDaylightSavings` |

### `PeriodDurationTasks`

| # | Method | Topic |
|---|---|---|
| 1 | `ageInYears(birth, today)` | `Period.between(...).getYears()` |
| 2 | `daysBetween(start, end)` | `ChronoUnit.DAYS.between` (not `Period.getDays`) |
| 3 | `formatDuration(duration)` | `HH:mm:ss` from total seconds (Java 8 style) |
| 4 | `describePeriod(period)` | `getYears` / `getMonths` / `getDays`, singular vs plural |
| 5 | `daysUntilNextBirthday(birth, today)` | `withYear`, rolling over to next year |

### `FormattingParsingTasks`

| # | Method | Topic |
|---|---|---|
| 1 | `toEuropeanDate(date)` | `ofPattern("dd.MM.yyyy")` |
| 2 | `parseUsDate(text)` | `ResolverStyle.STRICT` + `uuuu` |
| 3 | `isValidIsoDate(text)` | strict ISO parse, catching `DateTimeParseException`, `null` safety |
| 4 | `formatInstantUtc(instant, pattern)` | `withZone(ZoneOffset.UTC)` |
| 5 | `longDateEnglish(date)` | `EEEE, MMMM d, yyyy` + explicit `Locale` |
| 6 | `parseOffsetDateTime(text)` | `OffsetDateTime.parse(...).toInstant()` |

### `ComparisonTasks`

| # | Method | Topic |
|---|---|---|
| 1 | `isWithinRange(date, start, end)` | closed range from `isBefore` / `isAfter` |
| 2 | `dateRangesOverlap(s1, e1, s2, e2)` | overlap test for closed ranges |
| 3 | `sameMoment(a, b)` | `isEqual` vs `equals` on `ZonedDateTime` |
| 4 | `earliest(moments)` | natural order of `ZonedDateTime` across zones |
| 5 | `sortNewestFirst(dates)` | copy + `Comparator.reverseOrder()` |
| 6 | `isBusinessHours(dateTime)` | weekday check + half-open time range |

### `ConversionTasks`

| # | Method | Topic |
|---|---|---|
| 1 | `epochSecondsUtc(dateTime)` | `LocalDateTime.toEpochSecond(ZoneOffset.UTC)` |
| 2 | `toInstant(dateTime, zone)` | `atZone(zone).toInstant()` |
| 3 | `toLocalDateTime(instant, zone)` | `LocalDateTime.ofInstant` |
| 4 | `startOfDayInstant(date, zone)` | `atStartOfDay(zone)` |
| 5 | `toLegacyDate(zoned)` | `Date.from(Instant)` |
| 6 | `fromLegacyDate(date, zone)` | `Date.toInstant().atZone(zone)` |

```bash
mvn test -Dtest=LocalDateTimeTasksTest
mvn test -Dtest=InstantTasksTest
mvn test -Dtest=ZonedDateTimeTasksTest
mvn test -Dtest=PeriodDurationTasksTest
mvn test -Dtest=FormattingParsingTasksTest
mvn test -Dtest=ComparisonTasksTest
mvn test -Dtest=ConversionTasksTest
```

All 38 task groups must go **GREEN** ✅

## Key things to remember

```java
// java.time objects are IMMUTABLE — the result must be kept
LocalDate d = LocalDate.of(2024, 3, 15);
d.plusDays(1);                    // ❌ result thrown away, d is unchanged
d = d.plusDays(1);                // ✅

// Pick the type by the question you are answering
LocalDate / LocalDateTime         // calendar fields, NO zone  — birthdays, schedules
Instant                           // a moment on the UTC timeline — logs, timestamps, expiry
ZonedDateTime                     // moment + region + DST rules — user-facing calendar times
OffsetDateTime                    // moment + fixed offset — JSON / database boundaries

// Converting between zones: same MOMENT or same WALL CLOCK?
zdt.withZoneSameInstant(tokyo);   // ✅ 12:00 Paris → 20:00 Tokyo (same moment)
zdt.withZoneSameLocal(tokyo);     // ❌ almost always a bug: 12:00 Paris → 12:00 Tokyo (different moment)

// Instant has no calendar — months/years need a zone first
instant.plus(1, ChronoUnit.MONTHS);                                  // ❌ UnsupportedTemporalTypeException
instant.atZone(ZoneOffset.UTC).plusMonths(1).toInstant();           // ✅

// Period = calendar amount, Duration = exact amount
zdt.plus(Period.ofDays(1));       // ✅ "same time tomorrow" — stays 12:00 across DST
zdt.plus(Duration.ofDays(1));     // exactly 24 hours — wall clock can shift by 1 hour across DST
Period.between(a, b).getDays();   // ❌ only the days COMPONENT (P2M14D → 14)
ChronoUnit.DAYS.between(a, b);    // ✅ total number of days (74)

// Month arithmetic clamps — and order matters
LocalDate.of(2024, 1, 31).plusMonths(1);                  // 2024-02-29 (clamped, no exception)
LocalDate.of(2024, 1, 31).plusMonths(1).plusMonths(1);    // 2024-03-29 ≠ plusMonths(2) = 2024-03-31

// Formatter patterns are case-sensitive
ofPattern("mm");                  // ❌ minutes, not month → use "MM"
ofPattern("hh:mm");               // ❌ 12-hour clock without AM/PM → use "HH:mm" or add " a"
ofPattern("YYYY-MM-dd");          // ❌ week-based year: 2024-12-30 prints 2025-12-30 → use "yyyy"

// Default parsing is forgiving — validate with STRICT (and uuuu, not yyyy)
LocalDate.parse("2024-02-31", ofPattern("uuuu-MM-dd"));  // ❌ silently becomes 2024-02-29
LocalDate.parse("2024-02-31",
    ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT));  // ✅ DateTimeParseException

// Names depend on the JVM's default locale — pin it
ofPattern("EEEE, MMMM d", Locale.US);     // ✅ same text on every machine

// Never hide "now" inside logic — inject a Clock (or the value) so tests are deterministic
Instant.now(Clock.fixed(moment, ZoneOffset.UTC));         // ✅ testable
Instant.now();                                            // ❌ hard to test

// ZonedDateTime: equals() compares zone too; isEqual() compares the moment
paris12.equals(newYork06);        // ❌ false (different zone and fields)
paris12.isEqual(newYork06);       // ✅ true (same instant)

// A LocalDateTime cannot always find its way back to the moment (DST overlap)
// Store Instant (or OffsetDateTime), convert to LocalDateTime only for display.

// Ranges: prefer half-open [start, end) — back-to-back slots do not overlap
startA.isBefore(endB) && startB.isBefore(endA);           // ✅ overlap test for half-open ranges
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest='*DemoTest'

# Task tests only
mvn test -Dtest='*TasksTest'

# Run a main demo (one per concept)
mvn exec:java -Dexec.mainClass="com.vbforge.date_time_api.LocalDateTimeDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.date_time_api.InstantDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.date_time_api.ZonedDateTimeDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.date_time_api.PeriodDurationDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.date_time_api.FormattingParsingDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.date_time_api.ComparisonDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.date_time_api.ConversionDemo"
```
