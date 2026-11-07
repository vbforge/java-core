package com.vbforge.practice.practice_8_override_throws_rules;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * What can an override throw?
 *
 * Parent:    void doWork() throws IOException
 * The overriding method may throw:
 *   - the same type:                IOException                  (good)
 *   - a narrower type:              FileNotFoundException        (good, subclass of IOException)
 *   - nothing (no throws):                                       (good)
 *   - any unchecked exception:      IllegalStateException        (good, RuntimeException is exempt)
 *   - an Error:                     InternalError                (good, Error is not an Exception)
 *
 * The overriding method may NOT throw:
 *   - a broader type:               Exception                    (broader than IOException)
 *   - an unrelated checked type:    InterruptedException
 *   - a new checked type next to a good one: IOException + SQLException
 */
public class OverrideThrowsRulesDemo {

    static class Parent {
        void doWork() throws IOException {
            System.out.println("  Parent.doWork (declares IOException)");
        }
    }

    static class SameChild extends Parent {
        @Override
        void doWork() throws IOException {                                  // good — same
            System.out.println("  SameChild.doWork (declares IOException)");
        }
    }

    static class NarrowerChild extends Parent {
        @Override
        void doWork() throws FileNotFoundException {                        // good — narrower
            System.out.println("  NarrowerChild.doWork (declares FileNotFoundException)");
        }
    }

    static class SilentChild extends Parent {
        @Override
        void doWork() {                                                     // good — no throws
            System.out.println("  SilentChild.doWork (no throws)");
        }
    }

    static class UncheckedChild extends Parent {
        @Override
        void doWork() throws IOException, IllegalStateException {           // good — unchecked is exempt
            System.out.println("  UncheckedChild.doWork (adds unchecked IllegalStateException)");
        }
    }

    static class ErrorChild extends Parent {
        @Override
        void doWork() throws FileNotFoundException, InternalError {         // good — Error is not an Exception
            System.out.println("  ErrorChild.doWork (adds InternalError)");
        }
    }

    // ILLEGAL (compile errors if uncommented):
    // static class BroaderChild extends Parent {
    //     @Override void doWork() throws Exception { }                      // broader than IOException
    // }
    // static class UnrelatedChild extends Parent {
    //     @Override void doWork() throws InterruptedException { }           // unrelated checked type
    // }
    // static class NewCheckedChild extends Parent {
    //     @Override void doWork() throws IOException, java.sql.SQLException { }   // SQLException is new
    // }

    public static void main(String[] args) throws IOException {
        // A caller holding a `Parent` reference must handle the declared IOException,
        // even if the actual instance is a NarrowerChild or SilentChild.
        Parent[] workers = {
                new Parent(), new SameChild(), new NarrowerChild(),
                new SilentChild(), new UncheckedChild(), new ErrorChild()
        };
        for (Parent worker : workers) {
            worker.doWork();
        }
    }
}
