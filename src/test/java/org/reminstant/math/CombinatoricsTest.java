package org.reminstant.math;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CombinatoricsTest {

  @Test
  void permutationsTest() {
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
    Iterator<int[]> permutationsGenerator = Combinatorics.permutationGenerator(4);

    assertThat(permutations)
        .hasSameSizeAs(expectedPermutations)
        .containsExactlyElementsOf(expectedPermutations);
    assertThat(permutationsGenerator)
        .toIterable()
        .hasSameSizeAs(expectedPermutations)
        .containsExactlyElementsOf(expectedPermutations);
  }

  @Test
  void arrangementsWithRepetitionTest() {
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
    Iterator<int[]> arrangementsGenerator = Combinatorics.arrangementsWithRepetitionGenerator(4, 3);

    assertThat(arrangements)
        .hasSameSizeAs(expectedArrangements)
        .containsExactlyElementsOf(expectedArrangements);
    assertThat(arrangementsGenerator)
        .toIterable()
        .hasSameSizeAs(expectedArrangements)
        .containsExactlyElementsOf(expectedArrangements);
  }



  @Test
  void testCombinationByOrdinal() {
    for (int i = -10; i < 10; ++i) {
      System.out.println(Arrays.toString(Combinatorics.getCombinationByOrdinal(4, 3, i)));
    }
  }

  @Test
  void testCombinationToOrdinal() {
    List<Integer> expectedOrdinals = List.of(
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19
    );

    List<int[]> combinations = List.of(
        new int[]{ 0, 1, 2 }, new int[]{ 0, 1, 3 }, new int[]{ 0, 1, 4 }, new int[]{ 0, 1, 5 },
        new int[]{ 0, 2, 3 }, new int[]{ 0, 2, 4 }, new int[]{ 0, 2, 5 }, new int[]{ 0, 3, 4 },
        new int[]{ 0, 3, 5 }, new int[]{ 0, 4, 5 }, new int[]{ 1, 2, 3 }, new int[]{ 1, 2, 4 },
        new int[]{ 1, 2, 5 }, new int[]{ 1, 3, 4 }, new int[]{ 1, 3, 5 }, new int[]{ 1, 4, 5 },
        new int[]{ 2, 3, 4 }, new int[]{ 2, 3, 5 }, new int[]{ 2, 4, 5 }, new int[]{ 3, 4, 5 }
    );

    List<Integer> ordinals = combinations.stream()
        .map(c -> Combinatorics.getCombinationOrdinal(6, c))
        .map(BigInteger::intValueExact)
        .toList();

    assertThat(ordinals)
        .hasSameSizeAs(expectedOrdinals)
        .containsExactlyElementsOf(expectedOrdinals);
  }

  @Test
  void combinationsTest() {
    Iterable<int[]> expectedCombinations = List.of(
        new int[]{ 0, 1, 2 }, new int[]{ 0, 1, 3 }, new int[]{ 0, 1, 4 }, new int[]{ 0, 1, 5 },
        new int[]{ 0, 2, 3 }, new int[]{ 0, 2, 4 }, new int[]{ 0, 2, 5 }, new int[]{ 0, 3, 4 },
        new int[]{ 0, 3, 5 }, new int[]{ 0, 4, 5 }, new int[]{ 1, 2, 3 }, new int[]{ 1, 2, 4 },
        new int[]{ 1, 2, 5 }, new int[]{ 1, 3, 4 }, new int[]{ 1, 3, 5 }, new int[]{ 1, 4, 5 },
        new int[]{ 2, 3, 4 }, new int[]{ 2, 3, 5 }, new int[]{ 2, 4, 5 }, new int[]{ 3, 4, 5 }
    );

    Iterable<int[]> combinations = Combinatorics.getCombinations(6, 3);
    Iterator<int[]> combinationsGenerator = Combinatorics.combinationsGenerator(6, 3);

    assertThat(combinations)
        .hasSameSizeAs(expectedCombinations)
        .containsExactlyElementsOf(expectedCombinations);
    assertThat(combinationsGenerator)
        .toIterable()
        .hasSameSizeAs(expectedCombinations)
        .containsExactlyElementsOf(expectedCombinations);
  }
}
