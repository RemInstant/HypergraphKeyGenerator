package org.reminstant.math.combinatorics;

import org.reminstant.utils.sequence.Sequence;
import org.reminstant.utils.Lazy;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.reminstant.math.combinatorics.CombinatoricsUtil.validateCombinatoricsParams;

public class ArrangementWithRepetitionFactory implements DiscreteObjectFactory<int[]> {

  private final int n;
  private final int k;
  private final Lazy<BigInteger> count;

  private ArrangementWithRepetitionFactory(int n, int k) {
    this.n = n;
    this.k = k;
    this.count = Lazy.ofSupplier(() -> calculateCount(n, k));
  }

  public static ArrangementWithRepetitionFactory ofParams(int n, int k) {
    validateCombinatoricsParams(n, k);
    return new ArrangementWithRepetitionFactory(n, k);
  }

  public BigInteger count() {
    return count.get();
  }

  public boolean isValid(int[] arrangement) {
    if (arrangement.length != k) {
      return false;
    }
    for (int elem : arrangement) {
      if (elem < 0 || elem >= n) {
        return false;
      }
    }
    return true;
  }

  public int[] byOrdinal(BigInteger ordinal) {
    if (ordinal.compareTo(BigInteger.ZERO) < 0 || ordinal.compareTo(count.get()) >= 0) {
      throw new NoSuchElementException("Such arrangement does not exist");
    }

    BigInteger div = BigInteger.valueOf(n);
    int[] arrangement = new int[k];
    for (int i = k - 1; i >= 0 && ordinal.compareTo(BigInteger.ZERO) >= 0; --i) {
      BigInteger[] tmp = ordinal.divideAndRemainder(div);
      ordinal = tmp[0];
      arrangement[i] = tmp[1].intValue();
    }

    return arrangement;
  }

  public BigInteger toOrdinal(int[] arrangement) {
    throwIfInvalid(arrangement);

    BigInteger bigN = BigInteger.valueOf(n);
    BigInteger ordinal = BigInteger.ZERO;
    for (int i = 0; i < k; ++i) {
      ordinal = ordinal.multiply(bigN).add(BigInteger.valueOf(arrangement[i]));
    }

    return ordinal;
  }

  public int[] getNext(int[] arrangement) {
    throwIfInvalid(arrangement);
    return getNextInner(arrangement);
  }

  public Sequence<int[]> sequence() {
    return Sequence.ofTransformation(
        this::getNextInner,
        k == 0 || n > 0 ? new int[k] : null);
  }

  

  private static BigInteger calculateCount(int n, int k) {
    if (k == 0) {
      return BigInteger.ONE;
    }
    if (n == 0) {
      return BigInteger.ZERO;
    }
    BigInteger res = BigInteger.ONE;
    for (int i = 0; i < k; ++i) {
      res = res.multiply(BigInteger.valueOf(n));
    }
    return res;
  }

  private int[] getNextInner(int[] arrangement) {
    if (arrangement.length == 0) {
      return null;
    }
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
      return null;
    }
    return arrangement;
  }

  private void throwIfInvalid(int[] arrangement) {
    if (!isValid(arrangement)) {
      throw new IllegalArgumentException("%s is not a %d-arrangement with repetitions from a %d-element set"
          .formatted(Arrays.toString(arrangement), k, n));
    }
  }
}
