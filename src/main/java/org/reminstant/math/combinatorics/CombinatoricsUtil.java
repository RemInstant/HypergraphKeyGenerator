package org.reminstant.math.combinatorics;

public class CombinatoricsUtil {
  private CombinatoricsUtil() { }

  static final int[] EMPTY_INT_ARRAY = new int[0];

  static void validateCombinatoricsParam(int n) {
    if (n < 0) {
      throw new IllegalArgumentException("n must be non-negative");
    }
  }

  static void validateCombinatoricsParams(int n, int k) {
    if (n < 0 || k < 0) {
      throw new IllegalArgumentException("n and k must be non-negative");
    }
  }
}
