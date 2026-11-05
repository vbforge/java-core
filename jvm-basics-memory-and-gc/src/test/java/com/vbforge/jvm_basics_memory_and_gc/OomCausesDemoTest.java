package com.vbforge.jvm_basics_memory_and_gc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OomCausesDemo — API contracts")
class OomCausesDemoTest {

    @AfterEach
    void resetStaticState() {
        // the demo's static registry is a GC root; leave no residue between tests
        OomCausesDemo.clearLeakyRegistry();
    }

    @Nested
    @DisplayName("1. Reading the message")
    class Classification {

        @ParameterizedTest(name = "\"{0}\" -> {1}")
        @CsvSource({
                "Java heap space, HEAP",
                "GC overhead limit exceeded, GC_OVERHEAD",
                "Metaspace, METASPACE",
                "unable to create native thread: possibly out of memory or process/resource limits reached, NATIVE_THREAD",
                "Requested array size exceeds VM limit, ARRAY_LIMIT",
                "Direct buffer memory, DIRECT_BUFFER",
                "something else, UNKNOWN"
        })
        @DisplayName("classify maps each known message to its family")
        void mapping(String message, OomCausesDemo.OomKind expected) {
            assertThat(OomCausesDemo.classify(message)).isEqualTo(expected);
        }

        @Test
        @DisplayName("null is UNKNOWN and every kind has advice")
        void nullAndAdvice() {
            assertThat(OomCausesDemo.classify(null)).isEqualTo(OomCausesDemo.OomKind.UNKNOWN);
            for (OomCausesDemo.OomKind kind : OomCausesDemo.OomKind.values()) {
                assertThat(kind.advice()).isNotBlank();
            }
        }
    }

    @Nested
    @DisplayName("2. An allocation that can never succeed")
    class HugeArray {

        @Test
        @DisplayName("long[Integer.MAX_VALUE] throws OutOfMemoryError with a heap or array-limit message")
        void throwsOom() {
            String message = OomCausesDemo.provokeOutOfMemory();
            assertThat(OomCausesDemo.classify(message))
                    .isIn(OomCausesDemo.OomKind.HEAP, OomCausesDemo.OomKind.ARRAY_LIMIT);
        }
    }

    @Nested
    @DisplayName("3. Leak: unbounded static collection")
    class StaticLeak {

        @Test
        @DisplayName("the static registry retains every payload")
        void leakGrows() {
            assertThat(OomCausesDemo.leakyRegistrySizeAfter(1000)).isEqualTo(1000);
            assertThat(OomCausesDemo.leakyRegistrySizeAfter(500)).isEqualTo(1500);
        }

        @Test
        @DisplayName("clearing the registry releases the retained payloads")
        void clearReleases() {
            OomCausesDemo.leakyRegistrySizeAfter(10);
            OomCausesDemo.clearLeakyRegistry();
            assertThat(OomCausesDemo.leakyRegistrySizeAfter(0)).isZero();
        }

        @Test
        @DisplayName("a bounded LRU cache stays at its limit however many requests arrive")
        void boundedStaysBounded() {
            assertThat(OomCausesDemo.boundedCacheSizeAfter(1000, 100)).isEqualTo(100);
            assertThat(OomCausesDemo.boundedCacheSizeAfter(5, 100)).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("4. Leak: mutated hash key")
    class MutableKey {

        @Test
        @DisplayName("after mutating the key the entry is unfindable and unremovable but still held")
        void stuckEntry() {
            int[] result = OomCausesDemo.mutableKeyResult();
            assertThat(result[0]).as("found before mutation").isEqualTo(1);
            assertThat(result[1]).as("found after mutation").isZero();
            assertThat(result[2]).as("size after failed remove").isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("5. Leak: ThreadLocal in a thread pool")
    class ThreadLocalLeak {

        @Test
        @DisplayName("without remove() the next task on the same thread sees a stale value")
        void staleValue() throws Exception {
            assertThat(OomCausesDemo.threadLocalStaleValue()[0]).isEqualTo("user-1");
        }

        @Test
        @DisplayName("after remove() the next task sees no value")
        void removedValue() throws Exception {
            assertThat(OomCausesDemo.threadLocalStaleValue()[1]).isNull();
        }
    }

    @Nested
    @DisplayName("6. Evidence for the post-mortem")
    class Diagnostics {

        @Test
        @DisplayName("flags request a heap dump at the given path and exit on OOM")
        void flags() {
            assertThat(OomCausesDemo.oomDiagnosticFlags("/var/dumps/app.hprof"))
                    .isEqualTo("-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/dumps/app.hprof"
                            + " -XX:+ExitOnOutOfMemoryError");
        }
    }
}
