package org.reminstant.math;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

public class CombinatoricsBenchmark {

  @State(Scope.Benchmark)
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.SECONDS)
  @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
  @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
  @Fork(value = 3, warmups = 2)
  @Threads(4)
  public static class CombinatoricsFactorialBenchmark {

    @Setup
    public void setup() {
      // no setup
    }

    @Benchmark
    public void testCombinatoricsFactorial(Blackhole blackhole) {
      for (int i = 0; i < 20; ++i) {
        blackhole.consume(Combinatorics.Fast.factorial(i));
      }
    }

    @Benchmark
    public void testBigIntCombinatoricsFactorial(Blackhole blackhole) {
      for (int i = 0; i < 20; ++i) {
        blackhole.consume(Combinatorics.factorial(i));
      }
    }
  }
}
