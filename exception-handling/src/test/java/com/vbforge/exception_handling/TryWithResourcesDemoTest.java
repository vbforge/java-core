package com.vbforge.exception_handling;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TryWithResourcesDemo — API contracts")
class TryWithResourcesDemoTest {

    private final List<String> log = new ArrayList<String>();

    @Nested
    @DisplayName("1. Manual close vs try-with-resources")
    class ManualVsAutomaticTest {

        @Test
        @DisplayName("manual try/finally opens, uses and closes")
        void manual() {
            TryWithResourcesDemo.manualCloseDemo(log);

            assertThat(log).containsExactly("open:r", "use:r", "close:r");
        }

        @Test
        @DisplayName("try-with-resources produces exactly the same events")
        void automatic() {
            TryWithResourcesDemo.tryWithResourcesDemo(log);

            assertThat(log).containsExactly("open:r", "use:r", "close:r");
        }
    }

    @Nested
    @DisplayName("2. Close order")
    class CloseOrderTest {

        @Test
        @DisplayName("resources are opened top-to-bottom and closed in reverse order")
        void reverseOrder() {
            TryWithResourcesDemo.closeOrderDemo(log);

            assertThat(log).containsExactly("open:a", "open:b", "use:a", "use:b", "close:b", "close:a");
        }
    }

    @Nested
    @DisplayName("3. Closing happens BEFORE catch and finally")
    class CloseBeforeCatchTest {

        @Test
        @DisplayName("close runs first, then catch, then finally")
        void order() {
            TryWithResourcesDemo.closeBeforeCatchDemo(log);

            assertThat(log).containsExactly(
                    "open:r", "use:r", "close:r", "catch:body failed", "finally");
        }
    }

    @Nested
    @DisplayName("4. Suppressed exceptions")
    class SuppressedTest {

        @Test
        @DisplayName("the body exception is the one that propagates")
        void bodyExceptionWins() {
            Exception e = TryWithResourcesDemo.suppressedDemo(log);

            assertThat(e).hasMessage("body failed");
        }

        @Test
        @DisplayName("the close failure is attached as a suppressed exception")
        void closeFailureSuppressed() {
            Exception e = TryWithResourcesDemo.suppressedDemo(log);

            assertThat(e.getSuppressed()).hasSize(1);
            assertThat(e.getSuppressed()[0]).hasMessage("close failed: r");
        }

        @Test
        @DisplayName("the resource was still closed")
        void stillClosed() {
            TryWithResourcesDemo.suppressedDemo(log);

            assertThat(log).containsExactly("open:r", "use:r", "close:r");
        }

        @Test
        @DisplayName("contrast: with try/finally the close exception REPLACES the body exception")
        void manualFinallyLosesBodyException() {
            Exception e = TryWithResourcesDemo.manualFinallyMasksDemo(log);

            assertThat(e).hasMessage("close failed: r");
            assertThat(e.getSuppressed()).isEmpty();
        }

        @Test
        @DisplayName("when only close() fails, that exception propagates")
        void onlyCloseFails() {
            assertThat(TryWithResourcesDemo.closeFailureDemo(log)).isEqualTo("close failed: r");
        }
    }

    @Nested
    @DisplayName("5. Partial initialisation")
    class PartialInitTest {

        @Test
        @DisplayName("the first resource is closed; the one that failed to open never existed")
        void firstClosedSecondNeverOpened() {
            TryWithResourcesDemo.partialInitDemo(log);

            assertThat(log).containsExactly("open:a", "close:a", "catch:open failed: b");
        }
    }

    @Nested
    @DisplayName("6. Null resources")
    class NullResourceTest {

        @Test
        @DisplayName("a null resource is skipped on close — no NullPointerException")
        void nullIsSkipped() {
            TryWithResourcesDemo.nullResourceDemo(log);

            assertThat(log).containsExactly("body-ran");
        }
    }
}
