package com.vbforge.practice.practice_13_jmh_benchmark_skeleton;

/**
 * This module has no JMH dependency, so the benchmark is printed as source instead of being run.
 * Each JMH annotation is mapped to the thing you did by hand in practice_6_mini_harness.
 * To run it for real: add jmh-core + jmh-generator-annprocess (see the module README) in your own project.
 */
public class JmhSkeletonDemo {

    private static final String SOURCE = """
            @State(Scope.Thread)
            @BenchmarkMode(Mode.AverageTime)
            @OutputTimeUnit(TimeUnit.NANOSECONDS)
            @Warmup(iterations = 5, time = 1)          // = warm-up rounds, results discarded
            @Measurement(iterations = 5, time = 1)     // = measured rounds
            @Fork(2)                                   // = fresh JVM per fork: no profile pollution
            public class StringBuildBenchmark {

                @Param({"10", "100"})                  // = run the benchmark for several inputs
                int size;

                @Benchmark
                public String withBuilder() {          // returned value = consumed, no dead-code elimination
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < size; i++) sb.append(i);
                    return sb.toString();
                }

                @Benchmark
                public void withBlackhole(Blackhole bh) {   // Blackhole = the "sink" you wrote by hand
                    bh.consume(size * 31);
                }
            }
            """;

    public static void main(String[] args) {
        System.out.print(SOURCE);
        System.out.println("---");
        System.out.println("Run: mvn package, then java -jar target/benchmarks.jar");
    }
}
