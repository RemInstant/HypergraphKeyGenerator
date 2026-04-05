package org.reminstant.math.combinatorics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.reminstant.junit.converter.CsvToIntArray;
import org.reminstant.utils.sequence.Sequence;

import java.math.BigInteger;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

class ArrangementWithRepetitionTest {

  @ParameterizedTest
  @CsvSource({
      "-1, 2",
      "2, -1",
      "-1, -1"
  })
  void test_initiation_negativeArguments(int n, int k) {
    assertThatThrownBy(() -> ArrangementWithRepetitionFactory.ofParams(n, k))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 0, 1",
      "5, 0, 1",
      "1, 1, 1",
      "1, 10, 1",
      "2, 10, 1024",
      "10, 5, 100000"
  })
  void test_count_happyPath(int n, int k, BigInteger expectedCount) {
    BigInteger count = ArrangementWithRepetitionFactory.ofParams(n, k).count();

    assertThat(count)
        .isEqualTo(expectedCount);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 0, 0,  '[]'",
      "1, 0, 0,  '[]'",
      "9, 0, 0,  '[]'",
      "1, 1, 0,  '[0]'",
      "5, 1, 0,  '[0]'",
      "5, 1, 4,  '[4]'",
      "5, 2, 0,  '[0, 0]'",
      "5, 2, 1,  '[0, 1]'",
      "5, 2, 5,  '[1, 0]'",
      "5, 2, 24, '[4, 4]'",
      "2, 5, 0,  '[0, 0, 0, 0, 0]'",
      "2, 5, 8,  '[0, 1, 0, 0, 0]'",
      "2, 5, 18, '[1, 0, 0, 1, 0]'",
      "2, 5, 31, '[1, 1, 1, 1, 1]'",
  })
  void test_byOrdinal_happyPath(int n, int k, BigInteger ordinal,
                                @CsvToIntArray int[] expectedObject) {
    int[] object = ArrangementWithRepetitionFactory.ofParams(n, k).byOrdinal(ordinal);

    assertThat(object)
        .isEqualTo(expectedObject);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 5, 0",
      "1, 1, 1",
      "5, 1, 5",
  })
  void test_byOrdinal_nonExistent(int n, int k, BigInteger ordinal) {
    ArrangementWithRepetitionFactory factory = ArrangementWithRepetitionFactory.ofParams(n, k);

    assertThatThrownBy(() -> factory.byOrdinal(ordinal))
        .isInstanceOf(NoSuchElementException.class);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 0, 0,  '[]'",
      "1, 0, 0,  '[]'",
      "9, 0, 0,  '[]'",
      "1, 1, 0,  '[0]'",
      "5, 1, 0,  '[0]'",
      "5, 1, 4,  '[4]'",
      "5, 2, 0,  '[0, 0]'",
      "5, 2, 1,  '[0, 1]'",
      "5, 2, 5,  '[1, 0]'",
      "5, 2, 24, '[4, 4]'",
      "2, 5, 0,  '[0, 0, 0, 0, 0]'",
      "2, 5, 8,  '[0, 1, 0, 0, 0]'",
      "2, 5, 18, '[1, 0, 0, 1, 0]'",
      "2, 5, 31, '[1, 1, 1, 1, 1]'",
  })
  void test_toOrdinal_happyPath(int n, int k, BigInteger expectedOrdinal,
                                @CsvToIntArray int[] object) {
    BigInteger ordinal = ArrangementWithRepetitionFactory.ofParams(n, k).toOrdinal(object);

    assertThat(ordinal)
        .isEqualTo(expectedOrdinal);
  }

  @Test
  void test_generator_elements() {
    Iterable<int[]> expectedArrangements = List.of(
        new int[]{ 0, 0, 0 }, new int[]{ 0, 0, 1 }, new int[]{ 0, 0, 2 }, new int[]{ 0, 0, 3 },
        new int[]{ 0, 1, 0 }, new int[]{ 0, 1, 1 }, new int[]{ 0, 1, 2 }, new int[]{ 0, 1, 3 },
        new int[]{ 0, 2, 0 }, new int[]{ 0, 2, 1 }, new int[]{ 0, 2, 2 }, new int[]{ 0, 2, 3 },
        new int[]{ 0, 3, 0 }, new int[]{ 0, 3, 1 }, new int[]{ 0, 3, 2 }, new int[]{ 0, 3, 3 },
        new int[]{ 1, 0, 0 }, new int[]{ 1, 0, 1 }, new int[]{ 1, 0, 2 }, new int[]{ 1, 0, 3 },
        new int[]{ 1, 1, 0 }, new int[]{ 1, 1, 1 }, new int[]{ 1, 1, 2 }, new int[]{ 1, 1, 3 },
        new int[]{ 1, 2, 0 }, new int[]{ 1, 2, 1 }, new int[]{ 1, 2, 2 }, new int[]{ 1, 2, 3 },
        new int[]{ 1, 3, 0 }, new int[]{ 1, 3, 1 }, new int[]{ 1, 3, 2 }, new int[]{ 1, 3, 3 },
        new int[]{ 2, 0, 0 }, new int[]{ 2, 0, 1 }, new int[]{ 2, 0, 2 }, new int[]{ 2, 0, 3 },
        new int[]{ 2, 1, 0 }, new int[]{ 2, 1, 1 }, new int[]{ 2, 1, 2 }, new int[]{ 2, 1, 3 },
        new int[]{ 2, 2, 0 }, new int[]{ 2, 2, 1 }, new int[]{ 2, 2, 2 }, new int[]{ 2, 2, 3 },
        new int[]{ 2, 3, 0 }, new int[]{ 2, 3, 1 }, new int[]{ 2, 3, 2 }, new int[]{ 2, 3, 3 },
        new int[]{ 3, 0, 0 }, new int[]{ 3, 0, 1 }, new int[]{ 3, 0, 2 }, new int[]{ 3, 0, 3 },
        new int[]{ 3, 1, 0 }, new int[]{ 3, 1, 1 }, new int[]{ 3, 1, 2 }, new int[]{ 3, 1, 3 },
        new int[]{ 3, 2, 0 }, new int[]{ 3, 2, 1 }, new int[]{ 3, 2, 2 }, new int[]{ 3, 2, 3 },
        new int[]{ 3, 3, 0 }, new int[]{ 3, 3, 1 }, new int[]{ 3, 3, 2 }, new int[]{ 3, 3, 3 }
    );

    Sequence<int[]> generator = ArrangementWithRepetitionFactory.ofParams(4, 3).sequence();

    assertThat(generator)
        .toIterable()
        .hasSameSizeAs(expectedArrangements)
        .containsExactlyElementsOf(expectedArrangements);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 0",
      "5, 0",
      "0, 1",
      "1, 1",
      "1, 5",
      "2, 1",
      "2, 2",
      "5, 5",
      "10, 5",
      "50, 3"
  })
  void test_generatorAndCountSync(int n, int k) {
    ArrangementWithRepetitionFactory factory = ArrangementWithRepetitionFactory.ofParams(n, k);

    BigInteger count = factory.count();
    Sequence<?> sequence = factory.sequence();

    assertThat(sequence)
        .toIterable()
        .hasSize(count.intValueExact());
  }
}
