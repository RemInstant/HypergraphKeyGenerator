package org.reminstant.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.reminstant.junit.converter.CsvToIntArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CombinatoricsTest {
  private static final Logger log = LoggerFactory.getLogger(CombinatoricsTest.class);

  // region --- combinatorics objects count ---

  @ParameterizedTest
  @CsvSource({
      "0, 1",
      "1, 1",
      "2, 2",
      "3, 6",
      "5, 120",
      "50, 30414093201713378043612608166064768844377641568960512000000000000"
  })
  void test_permutationsCount_happyPath(int n, BigInteger expectedCount) {
    BigInteger count = Combinatorics.factorial(n);

    assertThat(count)
        .isEqualTo(expectedCount);
  }

  @ParameterizedTest
  @CsvSource({
      "-1",
      "-42"
  })
  void test_permutationsCount_negativeArguments(int n) {
    assertThatThrownBy(() -> Combinatorics.factorial(n))
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
  void test_arrangementWithRepetitionCount_happyPath(int n, int k, BigInteger expectedCount) {
    BigInteger count = Combinatorics.arrangementWithRepetitionCount(n, k);

    assertThat(count)
        .isEqualTo(expectedCount);
  }

  @ParameterizedTest
  @CsvSource({
      "-1, 2",
      "2, -1",
      "-1, -1"
  })
  void test_arrangementWithRepetitionCount_negativeArguments(int n, int k) {
    assertThatThrownBy(() -> Combinatorics.arrangementWithRepetitionCount(n, k))
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
  void test_combinationCount_happyPath(int n, int k, BigInteger expectedCount) {
    BigInteger count = Combinatorics.combinationCount(n, k);

    assertThat(count)
        .isEqualTo(expectedCount);
  }

  @ParameterizedTest
  @CsvSource({
      "-1, 2",
      "2, -1",
      "-1, -1"
  })
  void test_combinationCount_negativeArguments(int n, int k) {
    assertThatThrownBy(() -> Combinatorics.combinationCount(n, k))
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
  void test_setPartitionCount_happyPath(int n, int k, BigInteger expectedCount) {
    BigInteger count = Combinatorics.setPartitionCount(n, k);

    assertThat(count)
        .isEqualTo(expectedCount);
  }

  @ParameterizedTest
  @CsvSource({
      "-1, 2",
      "2, -1",
      "-1, -1"
  })
  void test_setPartitionCount_negativeArguments(int n, int k) {
    assertThatThrownBy(() -> Combinatorics.combinationCount(n, k))
        .isInstanceOf(IllegalArgumentException.class);
  }

  // endregion --- combinatorics objects count ---

  // region --- combinatorics objects ordinal ---

  @ParameterizedTest
  @CsvSource({
      "0, 0, 0,  '[]'",
      "6, 3, 0,  '[0, 1, 2]'",
      "6, 3, 4,  '[0, 2, 3]'",
      "6, 3, 13, '[1, 3, 4]'",
      "6, 3, 19, '[3, 4, 5]'",
      "6, 6, 0,  '[0, 1, 2, 3, 4, 5]'",
      "6, 6, 1,  '[]'",
      "6, 7, 0,  '[]'",
  })
  void test_getCombinationByOrdinal_happyPath(int n, int k, BigInteger ordinal,
                                              @CsvToIntArray int[] expectedObject) {
    int[] object = Combinatorics.getCombinationByOrdinal(n, k, ordinal);

    assertThat(object)
        .isEqualTo(expectedObject);
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
  void test_getCombinationOrdinal_happyPath(int n, int k, BigInteger expectedOrdinal,
                                            @CsvToIntArray int[] object) {
    BigInteger ordinal = Combinatorics.getCombinationOrdinal(n, k, object);

    assertThat(ordinal)
        .isEqualTo(expectedOrdinal);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 0, 0,  '[]'",
      "6, 0, 0,  '[]'",
      "6, 2, 0,  '[0, 1, 2, 3, 4, 5]'",
      "6, 3, 0,  '[0, 1, 2, 3, 4, 5]'",
      "6, 3, 1,  '[0, 1, 2, 4, 3, 5]'",
      "6, 3, 6,  '[0, 3, 1, 2, 4, 5]'",
      "6, 3, 10, '[0, 4, 1, 3, 2, 5]'",
      "6, 3, 14, '[0, 5, 1, 4, 2, 3]'",
      "6, 3, 15, '[]'",
      "6, 4, 0,  '[]'",
      "6, 6, 0,  '[0, 1, 2, 3, 4, 5]'",
      "6, 6, 1,  '[]'",
      "6, 7, 0,  '[]'"
  })
  void test_getSetPartitionByOrdinal_happyPath(int n, int k, BigInteger ordinal,
                                              @CsvToIntArray int[] expectedObject) {
    int[] object = Combinatorics.getSetPartitionByOrdinal(n, k, ordinal);

    assertThat(object)
        .isEqualTo(expectedObject);
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
  void test_getSetPartitionOrdinal_happyPath(int n, int k, BigInteger expectedOrdinal,
                                            @CsvToIntArray int[] object) {
    BigInteger ordinal = Combinatorics.getSetPartitionOrdinal(n, k, object);

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
  void test_getSetPartitionOrdinal_invalidArguments(int n, int k, @CsvToIntArray int[] object) {
    assertThatThrownBy(() -> Combinatorics.getSetPartitionOrdinal(n, k, object))
        .isInstanceOf(IllegalArgumentException.class);
  }

  // region --- combinatorics objects ordinal ---

  // region --- combinatorics objects generation ---

  @Test
  void test_permutations() {
    Iterable<int[]> expectedPermutations = List.of(
        new int[]{ 0, 1, 2, 3 }, new int[]{ 0, 1, 3, 2 }, new int[]{ 0, 2, 1, 3 },
        new int[]{ 0, 2, 3, 1 }, new int[]{ 0, 3, 1, 2 }, new int[]{ 0, 3, 2, 1 },
        new int[]{ 1, 0, 2, 3 }, new int[]{ 1, 0, 3, 2 }, new int[]{ 1, 2, 0, 3 },
        new int[]{ 1, 2, 3, 0 }, new int[]{ 1, 3, 0, 2 }, new int[]{ 1, 3, 2, 0 },
        new int[]{ 2, 0, 1, 3 }, new int[]{ 2, 0, 3, 1 }, new int[]{ 2, 1, 0, 3 },
        new int[]{ 2, 1, 3, 0 }, new int[]{ 2, 3, 0, 1 }, new int[]{ 2, 3, 1, 0 },
        new int[]{ 3, 0, 1, 2 }, new int[]{ 3, 0, 2, 1 }, new int[]{ 3, 1, 0, 2 },
        new int[]{ 3, 1, 2, 0 }, new int[]{ 3, 2, 0, 1 }, new int[]{ 3, 2, 1, 0 }
    );

    Iterable<int[]> permutations = Combinatorics.getPermutations(4);
    Iterator<int[]> permutationGenerator = Combinatorics.permutationGenerator(4);

    assertThat(permutations)
        .hasSameSizeAs(expectedPermutations)
        .containsExactlyElementsOf(expectedPermutations);
    assertThat(permutationGenerator)
        .toIterable()
        .hasSameSizeAs(expectedPermutations)
        .containsExactlyElementsOf(expectedPermutations);
  }

  @Test
  void test_arrangementsWithRepetition() {
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

    Iterable<int[]> arrangements = Combinatorics.getArrangementsWithRepetition(4, 3);
    Iterator<int[]> arrangementGenerator = Combinatorics.arrangementWithRepetitionGenerator(4, 3);

    assertThat(arrangements)
        .hasSameSizeAs(expectedArrangements)
        .containsExactlyElementsOf(expectedArrangements);
    assertThat(arrangementGenerator)
        .toIterable()
        .hasSameSizeAs(expectedArrangements)
        .containsExactlyElementsOf(expectedArrangements);
  }

  @Test
  void test_combinations() {
    Iterable<int[]> expectedCombinations = List.of(
        new int[]{ 0, 1, 2 }, new int[]{ 0, 1, 3 }, new int[]{ 0, 1, 4 }, new int[]{ 0, 1, 5 },
        new int[]{ 0, 2, 3 }, new int[]{ 0, 2, 4 }, new int[]{ 0, 2, 5 }, new int[]{ 0, 3, 4 },
        new int[]{ 0, 3, 5 }, new int[]{ 0, 4, 5 }, new int[]{ 1, 2, 3 }, new int[]{ 1, 2, 4 },
        new int[]{ 1, 2, 5 }, new int[]{ 1, 3, 4 }, new int[]{ 1, 3, 5 }, new int[]{ 1, 4, 5 },
        new int[]{ 2, 3, 4 }, new int[]{ 2, 3, 5 }, new int[]{ 2, 4, 5 }, new int[]{ 3, 4, 5 }
    );

    Iterable<int[]> combinations = Combinatorics.getCombinations(6, 3);
    Iterator<int[]> combinationGenerator = Combinatorics.combinationGenerator(6, 3);

    assertThat(combinations)
        .hasSameSizeAs(expectedCombinations)
        .containsExactlyElementsOf(expectedCombinations);
    assertThat(combinationGenerator)
        .toIterable()
        .hasSameSizeAs(expectedCombinations)
        .containsExactlyElementsOf(expectedCombinations);
  }

  @Test
  void test_setPartitions() {
    Iterable<int[]> expectedPartitions = List.of(
        new int[]{ 0, 1, 2, 3, 4, 5 }, new int[]{ 0, 1, 2, 4, 3, 5 }, new int[]{ 0, 1, 2, 5, 3, 4 },
        new int[]{ 0, 2, 1, 3, 4, 5 }, new int[]{ 0, 2, 1, 4, 3, 5 }, new int[]{ 0, 2, 1, 5, 3, 4 },
        new int[]{ 0, 3, 1, 2, 4, 5 }, new int[]{ 0, 3, 1, 4, 2, 5 }, new int[]{ 0, 3, 1, 5, 2, 4 },
        new int[]{ 0, 4, 1, 2, 3, 5 }, new int[]{ 0, 4, 1, 3, 2, 5 }, new int[]{ 0, 4, 1, 5, 2, 3 },
        new int[]{ 0, 5, 1, 2, 3, 4 }, new int[]{ 0, 5, 1, 3, 2, 4 }, new int[]{ 0, 5, 1, 4, 2, 3 }
    );

    Iterable<int[]> partitions = Combinatorics.getSetPartitions(6, 3);
    Iterator<int[]> partitionGenerator = Combinatorics.setPartitionGenerator(6, 3);

    assertThat(partitions)
        .hasSameSizeAs(expectedPartitions)
        .containsExactlyElementsOf(expectedPartitions);
    assertThat(partitionGenerator)
        .toIterable()
        .hasSameSizeAs(expectedPartitions)
        .containsExactlyElementsOf(expectedPartitions);
  }

  // endregion --- combinatorics objects generation ---

  // region --- combinatorics objects sync ---

  @ParameterizedTest
  @CsvSource({
      "0", "1", "2", "3", "4", "5"
  })
  void test_permutationsSync(int n) {
    BigInteger count = Combinatorics.factorial(n);
    Iterable<int[]> all = Combinatorics.getPermutations(n);
    Iterator<int[]> generator = Combinatorics.permutationGenerator(n);

    log.info("permutationsSync: n={}; count={}", n, count);
    assertThat(all)
        .hasSize(count.intValueExact());
    assertThat(generator)
        .toIterable()
        .hasSize(count.intValueExact())
        .containsExactlyElementsOf(all);
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
  void test_arrangementsWithRepetitionSync(int n, int k) {
    BigInteger count = Combinatorics.arrangementWithRepetitionCount(n, k);
    Iterable<int[]> all = Combinatorics.getArrangementsWithRepetition(n, k);
    Iterator<int[]> generator = Combinatorics.arrangementWithRepetitionGenerator(n, k);

    log.info("arrangementsWithRepetitionSync: n={}; k={}; count={}", n, k, count);
    assertThat(all)
        .hasSize(count.intValueExact());
    assertThat(generator)
        .toIterable()
        .hasSize(count.intValueExact())
        .containsExactlyElementsOf(all);
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
  void test_combinationsSync(int n, int k) {
    BigInteger count = Combinatorics.combinationCount(n, k);
    Iterable<int[]> all = Combinatorics.getCombinations(n, k);
    Iterator<int[]> generator = Combinatorics.combinationGenerator(n, k);

    log.info("combinationsSync: n={}; k={}; count={}", n, k, count);
    assertThat(all)
        .hasSize(count.intValueExact());
    assertThat(generator)
        .toIterable()
        .hasSize(count.intValueExact())
        .containsExactlyElementsOf(all);
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
  void test_setPartitionsSync(int n, int k) {
    BigInteger count = Combinatorics.setPartitionCount(n, k);
    Iterable<int[]> all = Combinatorics.getSetPartitions(n, k);
    Iterator<int[]> generator = Combinatorics.setPartitionGenerator(n, k);

    log.info("setPartitionsSync: n={}; k={}; count={}", n, k, count);
    assertThat(all)
        .hasSize(count.intValueExact());
    assertThat(generator)
        .toIterable()
        .hasSize(count.intValueExact())
        .containsExactlyElementsOf(all);
  }

  // endregion --- combinatorics objects sync ---

}
