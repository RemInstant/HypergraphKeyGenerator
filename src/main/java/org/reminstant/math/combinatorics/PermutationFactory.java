package org.reminstant.math.combinatorics;

import org.reminstant.utils.ArrayUtils;
import org.reminstant.utils.sequence.Sequence;
import org.reminstant.utils.Lazy;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static org.reminstant.math.combinatorics.CombinatoricsUtil.validateCombinatoricsParam;

// TODO: tests
public class PermutationFactory implements DiscreteObjectFactory<int[]> {

  private final int n;
  private final Lazy<BigInteger> count;

  private PermutationFactory(int n) {
    this.n = n;
    this.count = Lazy.ofSupplier(() -> calculateCount(n));
  }

  public static PermutationFactory ofParams(int n) {
    validateCombinatoricsParam(n);
    return new PermutationFactory(n);
  }

  public BigInteger count() {
    return count.get();
  }

  public boolean isValid(int[] permutation) {
    return permutation.length == n &&
        !ArrayUtils.hasDuplicates(permutation) &&
        ArrayUtils.allMatch(permutation, x -> x >= 0 && x < n);
  }

  public int[] byOrdinal(BigInteger ordinal) {
    if (ordinal.compareTo(BigInteger.ZERO) < 0 || ordinal.compareTo(count.get()) >= 0) {
      throw new NoSuchElementException("Such permutation does not exist");
    }

    throw new UnsupportedOperationException("todo"); // TODO:
  }

  public BigInteger toOrdinal(int[] permutation) {
    throwIfInvalid(permutation);

    throw new UnsupportedOperationException("todo"); // TODO:
  }

  public int[] getNext(int[] permutation) {
    throwIfInvalid(permutation);
    return getNextInner(permutation);
  }

  public Sequence<int[]> sequence() {
    return Sequence.ofTransformation(
        this::getNextInner,
        IntStream.range(0, n).toArray());
  }

  

  private static BigInteger calculateCount(int n) {
    BigInteger res = BigInteger.ONE;
    for (int i = 2; i <= n; ++i) {
      res = res.multiply(BigInteger.valueOf(i));
    }
    return res;
  }

  private int[] getNextInner(int[] permutation) {
    int idx1 = -1;
    for (int i = n - 2; i >= 0 && idx1 == -1; --i) {
      if (permutation[i] < permutation[i+1]) {
        idx1 = i;
      }
    }
    if (idx1 == -1) {
      return null;
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

    ArrayUtils.reverseInPlace(permutation, idx1 + 1, n);
    return permutation;
  }

  private void throwIfInvalid(int[] permutation) {
    if (!isValid(permutation)) {
      throw new IllegalArgumentException("%s is not a permutation of a %d-element set"
          .formatted(Arrays.toString(permutation), n));
    }
  }
}
