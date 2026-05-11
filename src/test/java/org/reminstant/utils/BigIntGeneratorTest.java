package org.reminstant.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BigIntGeneratorTest {

  private static final Logger log = LoggerFactory.getLogger(BigIntGeneratorTest.class);

  @ParameterizedTest
  @CsvSource({
      "4,   10000, 0.05",
      "5,   10000, 0.05",
      "97, 500000, 0.05",
  })
  void testGenerationDistribution(int maxExclusive, int generationCount, double maxError) {
    BigIntGenerator generator = new BigIntGenerator(BigInteger.valueOf(maxExclusive));
    Map<Integer, Integer> counter = new HashMap<>();

    for (int i = 0; i < generationCount; ++i) {
      int value = generator.next().intValueExact();
      counter.merge(value, 1, Integer::sum);
    }

    long sum = counter.values().stream().reduce(Integer::sum).orElse(0);
    double mean = 1. * sum / maxExclusive;

    assertThat(counter.values()).hasSize(maxExclusive);

    log.info("Mean count: {}", mean);
    for (int i = 0; i < maxExclusive; ++i) {
      int count = counter.get(i);
      double error = Math.abs(1 - count / mean);
      log.info("Bucket {}: {} (error = {})", i, count, error);
      assertThat(error).isLessThan(maxError);
    }
  }

  @ParameterizedTest
  @CsvSource({
      "4,  100, 123",
      "5,  100, 555",
      "97, 100, 777",
  })
  void testSeedGeneration(int maxExclusive, int generationCount, long seed) {
    BigIntGenerator generator1 = new BigIntGenerator(BigInteger.valueOf(maxExclusive), seed);
    BigIntGenerator generator2 = new BigIntGenerator(BigInteger.valueOf(maxExclusive), seed);

    List<BigInteger> list1 = new ArrayList<>();
    List<BigInteger> list2 = new ArrayList<>();

    for (int i = 0; i < generationCount; ++i) {
      list1.add(generator1.next());
      list2.add(generator2.next());
    }

    assertThat(list1).containsExactlyElementsOf(list2);
  }
}
