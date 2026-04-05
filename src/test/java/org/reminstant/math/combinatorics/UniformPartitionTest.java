package org.reminstant.math.combinatorics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.reminstant.junit.converter.CsvToIntArray;
import org.reminstant.math.Combinatorics;
import org.reminstant.utils.sequence.Sequence;

import java.math.BigInteger;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UniformPartitionTest {

  @ParameterizedTest
  @CsvSource({
      "-1, 2",
      "2, -1",
      "-1, -1"
  })
  void test_initiation_negativeArguments(int n, int k) {
    assertThatThrownBy(() -> UniformPartitionFactory.ofParams(n, k))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 5, 0",
      "4, 5, 0",
      "6, 1, 1",
      "6, 6, 1",
      "6, 2, 10",
      "6, 3, 15",
      "15, 5, 1401400"
  })
  void test_count_happyPath(int n, int k, BigInteger expectedCount) {
    BigInteger count = UniformPartitionFactory.ofParams(n, k).count();

    assertThat(count)
        .isEqualTo(expectedCount);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 0, 0,  '[]'",
      "6, 2, 0,  '[0, 1, 2, 3, 4, 5]'",
      "6, 3, 0,  '[0, 1, 2, 3, 4, 5]'",
      "6, 3, 1,  '[0, 1, 2, 4, 3, 5]'",
      "6, 3, 6,  '[0, 3, 1, 2, 4, 5]'",
      "6, 3, 10, '[0, 4, 1, 3, 2, 5]'",
      "6, 3, 14, '[0, 5, 1, 4, 2, 3]'",
      "6, 6, 0,  '[0, 1, 2, 3, 4, 5]'",
  })
  void test_byOrdinal_happyPath(int n, int k, BigInteger ordinal,
                                @CsvToIntArray int[] expectedObject) {
    int[] object = UniformPartitionFactory.ofParams(n, k).byOrdinal(ordinal);

    assertThat(object)
        .isEqualTo(expectedObject);
  }

  @ParameterizedTest
  @CsvSource({
      "6, 0, 0,  '[]'",
      "6, 3, 15, '[]'",
      "6, 4, 0,  '[]'",
      "6, 6, 1,  '[]'",
      "6, 7, 0,  '[]'",
  })
  void test_byOrdinal_nonExistent(int n, int k, BigInteger ordinal) {
    UniformPartitionFactory factory = UniformPartitionFactory.ofParams(n, k);

    assertThatThrownBy(() -> factory.byOrdinal(ordinal))
        .isInstanceOf(NoSuchElementException.class);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 0, 0,  '[]'",
      "0, 2, 0,  '[]'",
      "6, 2, 0,  '[0, 1, 2, 3, 4, 5]'",
      "6, 2, 9,  '[0, 4, 5, 1, 2, 3]'",
      "6, 3, 0,  '[0, 1, 2, 3, 4, 5]'",
      "6, 3, 1,  '[0, 1, 2, 4, 3, 5]'",
      "6, 3, 6,  '[0, 3, 1, 2, 4, 5]'",
      "6, 3, 10, '[0, 4, 1, 3, 2, 5]'",
      "6, 3, 14, '[0, 5, 1, 4, 2, 3]'",
      "6, 6, 0,  '[0, 1, 2, 3, 4, 5]'",
  })
  void test_toOrdinal_happyPath(int n, int k, BigInteger expectedOrdinal,
                                @CsvToIntArray int[] object) {
    BigInteger ordinal = UniformPartitionFactory.ofParams(n, k).toOrdinal(object);

    assertThat(ordinal)
        .isEqualTo(expectedOrdinal);
  }

  @ParameterizedTest
  @CsvSource({
      "6, 0, '[]'",
      "6, 0, '[0, 1, 2, 3, 4, 5]'",
      "6, 2, '[]'",
      "6, 2, '[0, 1, 2, 3, 4]'",
      "6, 4, '[0, 1, 2, 3, 4, 5]'",
      "6, 7, '[0, 1, 2, 3, 4, 5]'",
      "6, 2, '[1, 2, 3, 4, 5, 6]'",
//    "6, 2, '[0, 4, 5, | 1, 2, 3]'", VALID ONE
      "6, 2, '[5, 4, 0,   3, 2, 1]'",
      "6, 2, '[1, 2, 3,   0, 4, 5]'",
      "6, 2, '[3, 2, 1,   5, 4, 0]'"
  })
  void test_toOrdinal_invalidArguments(int n, int k, @CsvToIntArray int[] object) {
    assertThatThrownBy(() -> Combinatorics.getSetPartitionOrdinal(n, k, object))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void test_generator_elements() {
    Iterable<int[]> expectedPartitions = List.of(
        new int[]{ 0, 1, 2, 3, 4, 5 }, new int[]{ 0, 1, 2, 4, 3, 5 }, new int[]{ 0, 1, 2, 5, 3, 4 },
        new int[]{ 0, 2, 1, 3, 4, 5 }, new int[]{ 0, 2, 1, 4, 3, 5 }, new int[]{ 0, 2, 1, 5, 3, 4 },
        new int[]{ 0, 3, 1, 2, 4, 5 }, new int[]{ 0, 3, 1, 4, 2, 5 }, new int[]{ 0, 3, 1, 5, 2, 4 },
        new int[]{ 0, 4, 1, 2, 3, 5 }, new int[]{ 0, 4, 1, 3, 2, 5 }, new int[]{ 0, 4, 1, 5, 2, 3 },
        new int[]{ 0, 5, 1, 2, 3, 4 }, new int[]{ 0, 5, 1, 3, 2, 4 }, new int[]{ 0, 5, 1, 4, 2, 3 }
    );

    Sequence<int[]> generator = UniformPartitionFactory.ofParams(6, 3).sequence();

    assertThat(generator)
        .toIterable()
        .hasSameSizeAs(expectedPartitions)
        .containsExactlyElementsOf(expectedPartitions);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 0",
      "0, 1",
      "1, 1",
      "2, 1",
      "2, 2",
      "6, 1",
      "6, 2",
      "6, 3",
      "6, 6",
      "9, 3",
      "12, 2",
      "12, 3",
      "12, 4",
      "12, 6",
      "12, 12",
      "14, 7",
      "15, 3",
      "20, 2",
  })
  void test_generatorAndCountSync(int n, int k) {
    UniformPartitionFactory factory = UniformPartitionFactory.ofParams(n, k);

    BigInteger count = factory.count();
    Sequence<?> generator = factory.sequence();

    assertThat(generator)
        .toIterable()
        .hasSize(count.intValueExact());
  }
}
