package org.reminstant.math.combinatorics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.reminstant.junit.converter.CsvToIntArray;
import org.reminstant.utils.sequence.Sequence;

import java.math.BigInteger;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CombinationTest {

  @ParameterizedTest
  @CsvSource({
      "-1, 2",
      "2, -1",
      "-1, -1"
  })
  void test_initiation_negativeArguments(int n, int k) {
    assertThatThrownBy(() -> CombinationFactory.ofParams(n, k))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 0, 1",
      "5, 0, 1",
      "0, 5, 0",
      "6, 5, 6",
      "6, 1, 6",
      "10, 5, 252",
      "100, 50, 100891344545564193334812497256"
  })
  void test_count_happyPath(int n, int k, BigInteger expectedCount) {
    BigInteger count = CombinationFactory.ofParams(n, k).count();

    assertThat(count)
        .isEqualTo(expectedCount);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 0, 0,  '[]'",
      "6, 3, 0,  '[0, 1, 2]'",
      "6, 3, 4,  '[0, 2, 3]'",
      "6, 3, 13, '[1, 3, 4]'",
      "6, 3, 19, '[3, 4, 5]'",
      "6, 6, 0,  '[0, 1, 2, 3, 4, 5]'",
  })
  void test_byOrdinal_happyPath(int n, int k, BigInteger ordinal,
                                @CsvToIntArray int[] expectedObject) {
    int[] object = CombinationFactory.ofParams(n, k).byOrdinal(ordinal);

    assertThat(object)
        .isEqualTo(expectedObject);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 0, 1,  '[]'",
      "0, 1, 1,  '[]'",
      "6, 6, 1,  '[]'",
      "6, 7, 0,  '[]'",
  })
  void test_byOrdinal_nonExistent(int n, int k, BigInteger ordinal) {
    CombinationFactory factory = CombinationFactory.ofParams(n, k);

    assertThatThrownBy(() -> factory.byOrdinal(ordinal))
        .isInstanceOf(NoSuchElementException.class);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 0, 0,  '[]'",
      "6, 3, 0,  '[0, 1, 2]'",
      "6, 3, 4,  '[0, 2, 3]'",
      "6, 3, 13, '[1, 3, 4]'",
      "6, 3, 19, '[3, 4, 5]'",
      "6, 6, 0,  '[0, 1, 2, 3, 4, 5]'",
  })
  void test_toOrdinal_happyPath(int n, int k, BigInteger expectedOrdinal,
                                @CsvToIntArray int[] object) {
    BigInteger ordinal = CombinationFactory.ofParams(n, k).toOrdinal(object);

    assertThat(ordinal)
        .isEqualTo(expectedOrdinal);
  }

  @Test
  void test_generator_elements() {
    Iterable<int[]> expectedCombinations = List.of(
        new int[]{ 0, 1, 2 }, new int[]{ 0, 1, 3 }, new int[]{ 0, 1, 4 }, new int[]{ 0, 1, 5 },
        new int[]{ 0, 2, 3 }, new int[]{ 0, 2, 4 }, new int[]{ 0, 2, 5 }, new int[]{ 0, 3, 4 },
        new int[]{ 0, 3, 5 }, new int[]{ 0, 4, 5 }, new int[]{ 1, 2, 3 }, new int[]{ 1, 2, 4 },
        new int[]{ 1, 2, 5 }, new int[]{ 1, 3, 4 }, new int[]{ 1, 3, 5 }, new int[]{ 1, 4, 5 },
        new int[]{ 2, 3, 4 }, new int[]{ 2, 3, 5 }, new int[]{ 2, 4, 5 }, new int[]{ 3, 4, 5 }
    );

    Sequence<int[]> generator = CombinationFactory.ofParams(6, 3).sequence();

    assertThat(generator)
        .toIterable()
        .hasSameSizeAs(expectedCombinations)
        .containsExactlyElementsOf(expectedCombinations);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 0",
      "5, 0",
      "0, 1",
      "1, 1",
      "5, 1",
      "5, 5",
      "10, 2",
      "10, 5",
      "10, 8",
      "20, 10"
  })
  void test_generatorAndCountSync(int n, int k) {
    CombinationFactory factory = CombinationFactory.ofParams(n, k);

    BigInteger count = factory.count();
    Sequence<?> generator = factory.sequence();

    assertThat(generator)
        .toIterable()
        .hasSize(count.intValueExact());
  }
}
