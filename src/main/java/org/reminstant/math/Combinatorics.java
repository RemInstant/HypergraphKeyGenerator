package org.reminstant.math;

import org.reminstant.Utils;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public class Combinatorics {
  private Combinatorics() {}

  private static final int[] EMPTY_INT_ARRAY = new int[0];



  public static long factorial(int x) {
    long res = 1;
    for (int i = 2; i <= x; ++i) {
      res = Math.multiplyExact(res, i);
    }
    return res;
  }

  public static long arrangementWithRepetition(int n, int k) {
    long res = 1;
    for (int i = 0; i < k; ++i) {
      res = Math.multiplyExact(res, n);
    }
    return res;
  }

  public static long combination(int n, int k) {
    long res = 1;
    for (int i = Math.max(k, n - k) + 1; i <= n; ++i) {
      res = Math.multiplyExact(res, i);
    }
    for (int i = 2; i <= Math.min(k, n - k); ++i) {
      res = Math.divideExact(res, i);
    }
    return res;
  }


  private static class Generator implements Iterator<int[]> {
    private int[] data;
    private final UnaryOperator<int[]> getNext;

    Generator(int[] startData, UnaryOperator<int[]> getNext) {
      this.data = startData;
      this.getNext = getNext;
    }

    @Override
    public boolean hasNext() { return data.length > 0; }

    @Override
    public int[] next() {
      if (!hasNext()) throw new NoSuchElementException();
      int[] cur = data;
      data = getNext.apply(data);
      return cur;
    }
  }

  public static Iterator<int[]> permutationGenerator(int n) {
    validateCombinatoricsParam(n);
    return new Generator(
        IntStream.range(0, n).toArray(),
        data -> getNextPermutation(n, data));
  }

  public static Iterator<int[]> arrangementsWithRepetitionGenerator(int n, int k) {
    validateCombinatoricsWithRepetitionsParams(n, k);
    return new Generator(
        new int[k],
        data -> getNextArrangementWithRepetitions(n, k, data));
  }

  public static Iterator<int[]> combinationsGenerator(int n, int k) {
    validateCombinatoricsParams(n, k);
    return new Generator(
        IntStream.range(0, k).toArray(),
        data -> getNextCombination(n, k, data));
  }



  public static List<int[]> getPermutations(int n) {
    validateCombinatoricsParam(n);
    long cnt = factorial(n);

    List<int[]> permutations = new ArrayList<>();
    int[] permutation = IntStream.range(0, n).toArray();

    permutations.add(permutation);
    for (int i = 1; i < cnt; ++i) {
      permutation = getNextPermutation(n, permutation);
      permutations.add(permutation);
    }

    return permutations;
  }

  public static List<int[]> getArrangementsWithRepetition(int n, int k) {
    validateCombinatoricsWithRepetitionsParams(n, k);
    long cnt = arrangementWithRepetition(n, k);

    List<int[]> arrangements = new ArrayList<>();
    int[] arrangement = new int[k];

    arrangements.add(arrangement);
    for (int i = 1; i < cnt; ++i) {
      arrangement = getNextArrangementWithRepetitions(n, k, arrangement);
      arrangements.add(arrangement);
    }

    return arrangements;
  }

  public static List<int[]> getCombinations(int n, int k) {
    validateCombinatoricsParams(n, k);
    long cnt = combination(n, k);

    List<int[]> combinations = new ArrayList<>();
    int[] combination = IntStream.range(0, k).toArray();

    combinations.add(combination);
    for (int i = 1; i < cnt; ++i) {
      combination = getNextCombination(n, k, combination);
      combinations.add(combination);
    }

    return combinations;
  }



  private static int[] getNextPermutation(int n, int[] permutation) {
    int idx1 = -1;
    for (int i = n - 2; i >= 0 && idx1 == -1; --i) {
      if (permutation[i] < permutation[i+1]) {
        idx1 = i;
      }
    }
    if (idx1 == -1) {
      return EMPTY_INT_ARRAY;
    }

    int idx2 = idx1 + 1;
    for (int i = idx2 + 1; i < n; ++i) {
      if (permutation[i] < permutation[idx2] && permutation[i] > permutation[idx1]) {
        idx2 = i;
      }
    }

    permutation = Arrays.copyOf(permutation, n);
    int tmp = permutation[idx1];
    permutation[idx1] = permutation[idx2];
    permutation[idx2] = tmp;

    Utils.arrayReverse(permutation, idx1 + 1, n);
    return permutation;
  }

  private static int[] getNextArrangementWithRepetitions(int n, int k, int[] arrangement) {
    arrangement = Arrays.copyOf(arrangement, arrangement.length);
    arrangement[k - 1]++;
    boolean carry = arrangement[k - 1] == n;
    for (int j = k - 1; j >= 0 && carry; --j) {
      arrangement[j] = 0;
      if (j - 1 >= 0) {
        arrangement[j - 1]++;
        carry = arrangement[j - 1] == n;
      }
    }
    if (carry) {
      return EMPTY_INT_ARRAY;
    }
    return arrangement;
  }

  private static int[] getNextCombination(int n, int k, int[] combination) {
    int idx = k - 1;
    int rightElement = n;
    while (idx >= 0 && rightElement - combination[idx] < 2) {
      idx--;
      rightElement = combination[idx + 1];
    }
    if (idx < 0) {
      return EMPTY_INT_ARRAY;
    }

    combination = Arrays.copyOf(combination, combination.length);
    combination[idx]++;
    for (int i = idx + 1; i < k; ++i) {
      combination[i] = combination[i - 1] + 1;
    }

    return combination;
  }



  private static void validateCombinatoricsParam(int n) {
    if (n < 0) {
      throw new IllegalArgumentException("n must be non-negative");
    }
  }

  private static void validateCombinatoricsParams(int n, int k) {
    if (n < 0 || k < 0) {
      throw new IllegalArgumentException("n and k must be non-negative");
    }
    if (k > n) {
      throw new IllegalArgumentException("k must be not greater than n");
    }
  }

  private static void validateCombinatoricsWithRepetitionsParams(int n, int k) {
    if (n < 0 || k < 0) {
      throw new IllegalArgumentException("n and k must be non-negative");
    }
  }
}
