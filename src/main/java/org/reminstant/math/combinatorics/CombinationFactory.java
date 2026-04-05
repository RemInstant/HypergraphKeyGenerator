package org.reminstant.math.combinatorics;

import org.reminstant.math.Combinatorics;
import org.reminstant.utils.ArrayUtils;
import org.reminstant.utils.sequence.Sequence;
import org.reminstant.utils.Lazy;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static org.reminstant.math.combinatorics.CombinatoricsUtil.validateCombinatoricsParams;

public class CombinationFactory implements DiscreteObjectFactory<int[]> {

  private final int n;
  private final int k;
  private final Lazy<BigInteger> count;

  private CombinationFactory(int n, int k) {
    this.n = n;
    this.k = k;
    this.count = Lazy.ofSupplier(() -> calculateCount(n, k));
  }

  public static CombinationFactory ofParams(int n, int k) {
    validateCombinatoricsParams(n, k);
    return new CombinationFactory(n, k);
  }

  public BigInteger count() {
    return count.get();
  }

  public boolean isValid(int[] combination) {
    return combination.length == k &&
        !ArrayUtils.hasDuplicates(combination) &&
        ArrayUtils.allMatch(combination, x -> x >= 0 && x < n);
  }

  public int[] byOrdinal(BigInteger ordinal) {
    if (ordinal.compareTo(BigInteger.ZERO) < 0 || ordinal.compareTo(count.get()) >= 0) {
      throw new NoSuchElementException("Such combination does not exist");
    }

    int[] combination = new int[k];
    int copyN = n;
    int copyK = k;
    int idx = 0;
    int next = 0;
    while (copyK > 0) {
      BigInteger cc = CombinationFactory.ofParams(copyN - 1, copyK - 1).count();
      if (ordinal.compareTo(cc) < 0) {
        combination[idx++] = next;
        copyK -= 1;
      } else {
        ordinal = ordinal.subtract(cc);
      }
      copyN -= 1;
      next += 1;
    }
    return combination;
  }

  public BigInteger toOrdinal(int[] combination) {
    throwIfInvalid(combination);

    BigInteger reverseOrdinal = BigInteger.ZERO;
    for (int i = 0; i < k; ++i) {
      int v = combination[i];
      reverseOrdinal = reverseOrdinal.add(
          CombinationFactory.ofParams(n - v - 1, k - i).count());
    }

    return CombinationFactory.ofParams(n, k)
        .count()
        .subtract(BigInteger.ONE)
        .subtract(reverseOrdinal);
  }

  public int[] getNext(int[] combination) {
    throwIfInvalid(combination);
    return getNextInner(combination);
  }

  public Sequence<int[]> sequence() {
    return Sequence.ofTransformation(
        this::getNextInner,
        k <= n
            ? IntStream.range(0, k).toArray()
            : null);
  }

  

  private static BigInteger calculateCount(int n, int k) {
    if (k == 1) {
      return BigInteger.valueOf(n);
    }
    if (k == 0 || k == n) {
      return BigInteger.ONE;
    }
    if (k > n) {
      return BigInteger.ZERO;
    }

    BigInteger res = BigInteger.ONE;
    for (int i = Math.max(k, n - k) + 1; i <= n; ++i) {
      res = res.multiply(BigInteger.valueOf(i));
    }
    return res.divide(Combinatorics.factorial(Math.min(k, n - k)));
  }

  private int[] getNextInner(int[] combination) {
    int idx = k - 1;
    int rightElement = n;
    while (idx >= 0 && rightElement - combination[idx] < 2) {
      idx--;
      rightElement = combination[idx + 1];
    }
    if (idx < 0) {
      return null;
    }

    combination = Arrays.copyOf(combination, combination.length);
    combination[idx]++;
    for (int i = idx + 1; i < k; ++i) {
      combination[i] = combination[i - 1] + 1;
    }

    return combination;
  }

  private void throwIfInvalid(int[] combination) {
    if (!isValid(combination)) {
      throw new IllegalArgumentException("%s is not a %d-combination from a %d-element set"
          .formatted(Arrays.toString(combination), k, n));
    }
  }
}
