package org.reminstant.math.combinatorics;

import org.reminstant.math.Combinatorics;
import org.reminstant.utils.ArrayUtils;
import org.reminstant.utils.sequence.Sequence;
import org.reminstant.utils.Lazy;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.IntStream;

import static org.reminstant.math.combinatorics.CombinatoricsUtil.validateCombinatoricsParams;

public class UniformPartitionFactory implements DiscreteObjectFactory<int[]> {

  private final int n;
  private final int k;
  private final int blockLength;
  private final Lazy<BigInteger> count;

  private UniformPartitionFactory(int n, int k) {
    this.n = n;
    this.k = k;
    this.blockLength = k != 0 ? (n / k) : 0;
    this.count = Lazy.ofSupplier(() -> calculateCount(n, k));
  }

  public static UniformPartitionFactory ofParams(int n, int k) {
    validateCombinatoricsParams(n, k);
    return new UniformPartitionFactory(n, k);
  }

  public BigInteger count() {
    return count.get();
  }

  public boolean isValid(int[] partition) {
    if (partition.length != n || ArrayUtils.hasDuplicates(partition) ||
        ArrayUtils.anyMatch(partition, x -> x < 0 || x >= n)) {
      return false;
    }

    boolean isValid = n == k || (k > 0 && n % k == 0);
    for (int i = 1; i < partition.length && isValid; ++i) {
      isValid = (i % blockLength == 0) || (partition[i - 1] < partition[i]); // block elems are sorted
      if (i % blockLength == 0) {
        isValid &= partition[i - blockLength] < partition[i]; // blocks are sorted
      }
    }
    return isValid;
  }

  public int[] byOrdinal(BigInteger ordinal) {
    if (ordinal.compareTo(BigInteger.ZERO) < 0 || ordinal.compareTo(count.get()) >= 0) {
      throw new NoSuchElementException("Such uniform partition does not exist");
    }

    int[] partition = new int[n];
    boolean[] usedElements = new boolean[n];

    for (int i = 0; i < k; ++i) {
      BigInteger div = UniformPartitionFactory
          .ofParams(n - (i + 1) * blockLength, k - (i + 1)).count();
      if (div.equals(BigInteger.ZERO)) {
        div = BigInteger.ONE;
      }
      BigInteger[] tmp = ordinal.divideAndRemainder(div);
      BigInteger blockOrdinal = tmp[0];
      ordinal = tmp[1];
      int[] partitionBlock = CombinationFactory
          .ofParams(n - i * blockLength, blockLength).byOrdinal(blockOrdinal);

      for (int j = 0; j < blockLength; ++j) {
        int value = ArrayUtils.nthIndexOf(usedElements, false, partitionBlock[j] - j);
        partition[i * blockLength + j] = value;
        usedElements[value] = true;
      }
    }

    return partition;
  }

  public BigInteger toOrdinal(int[] partition) {
    throwIfInvalid(partition);

    List<Integer> processingElements = new ArrayList<>();
    NavigableSet<Integer> processedElements = new TreeSet<>();
    BigInteger ordinal = BigInteger.ZERO;
    int[] partitionBlock = new int[blockLength];

    for (int i = 0; i < k; ++i) {
      System.arraycopy(partition, blockLength * i, partitionBlock, 0, blockLength);
      for (int j = 0; j < blockLength; ++j) {
        processingElements.add(partitionBlock[j]);
        partitionBlock[j] -= processedElements.headSet(partitionBlock[j]).size();
      }
      processedElements.addAll(processingElements);
      processingElements.clear();

      BigInteger blockOrdinal = CombinationFactory
          .ofParams(n - i * blockLength, blockLength).toOrdinal(partitionBlock);
      BigInteger tailCount = UniformPartitionFactory
          .ofParams(n - (i + 1) * blockLength, k - (i + 1)).count();
      ordinal = ordinal.add(blockOrdinal.multiply(tailCount));
    }

    return ordinal;
  }

  public int[] getNext(int[] partition) {
    throwIfInvalid(partition);
    return getNextInner(partition);
  }

  public Sequence<int[]> sequence() {
    return Sequence.ofTransformation(
        this::getNextInner,
        n == k || (k > 0 && n % k == 0)
            ? IntStream.range(0, n).toArray()
            : null);
  }

  

  private static BigInteger calculateCount(int n, int k) {
    if (k == 1 || k == n) {
      return BigInteger.ONE;
    }
    if (k > n || k == 0 || n % k != 0) {
      return BigInteger.ZERO;
    }

    int blockLength = n / k;
    return Combinatorics.factorial(n)
        .divide(Combinatorics.factorial(k))
        .divide(Combinatorics.factorial(blockLength).pow(k));
  }

  private int[] getNextInner(int[] partition) {
    // TODO: MAAN, CONSTRUCT NORMAL ALGO, NOT THAT STUPIDITY...
    if (partition.length == 0) {
      return null;
    }
    BigInteger ordinal = toOrdinal(partition);
    BigInteger nextOrdinal = ordinal.add(BigInteger.ONE);
    if (nextOrdinal.compareTo(count()) >= 0) {
      return null;
    }
    return byOrdinal(nextOrdinal);
  }

  private void throwIfInvalid(int[] partition) {
    if (!isValid(partition)) {
      throw new IllegalArgumentException("%s is not a uniform %d-partition of a %d-element set"
          .formatted(Arrays.toString(partition), k, n));
    }
  }
}
