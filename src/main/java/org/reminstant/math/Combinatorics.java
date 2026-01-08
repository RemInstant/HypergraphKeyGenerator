package org.reminstant.math;

import org.reminstant.utils.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public class Combinatorics {
  private static final Logger log = LoggerFactory.getLogger(Combinatorics.class);

  private Combinatorics() {}

  private static final int[] EMPTY_INT_ARRAY = new int[0];

  public static BigInteger factorial(int x) {
    validateCombinatoricsParam(x);
    BigInteger res = BigInteger.ONE;
    for (int i = 2; i <= x; ++i) {
      res = res.multiply(BigInteger.valueOf(i));
    }
    return res;
  }

  public static BigInteger arrangementWithRepetitionCount(int n, int k) {
    validateCombinatoricsParams(n, k);
    if (n == 0) {
      return BigInteger.ZERO;
    }
    BigInteger res = BigInteger.ONE;
    for (int i = 0; i < k; ++i) {
      res = res.multiply(BigInteger.valueOf(n));
    }
    return res;
  }

  public static BigInteger combinationCount(int n, int k) {
    validateCombinatoricsParams(n, k);
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
    return res.divide(factorial(Math.min(k, n - k)));
  }

  public static BigInteger setPartitionCount(int n, int k) {
    validateCombinatoricsParams(n, k);
    if (k == 1 || k == n) {
      return BigInteger.ONE;
    }
    if (k > n || k == 0 || n % k != 0) {
      return BigInteger.ZERO;
    }

    int blockLength = n / k;
    BigInteger res = BigInteger.ONE;

    for (int i = 0; i + 1 < k; ++i) {
      res = res.multiply(combinationCount(n - i * blockLength, blockLength));
    }
    return res.divide(factorial(k));
  }

  public static int[] getCombinationByOrdinal(int n, int k, long ordinal) {
    return getCombinationByOrdinal(n, k, BigInteger.valueOf(ordinal));
  }

  public static int[] getCombinationByOrdinal(int n, int k, BigInteger ordinal) {
    validateCombinatoricsParams(n, k);
    if (ordinal.compareTo(BigInteger.ZERO) < 0 || ordinal.compareTo(combinationCount(n, k)) >= 0) {
      return EMPTY_INT_ARRAY;
    }

    int[] combination = new int[k];
    int idx = 0;
    int next = 0;
    while (k > 0) {
      BigInteger cc = combinationCount(n - 1, k - 1);
      if (ordinal.compareTo(cc) < 0) {
        combination[idx++] = next;
        k -= 1;
      } else {
        ordinal = ordinal.subtract(cc);
      }
      n -= 1;
      next += 1;
    }
    return combination;
  }

  public static BigInteger getCombinationOrdinal(int n, int k, int[] combination) {
    validateCombinatoricsParams(n, k);
    int[] originCombination = combination;
    combination = Arrays.copyOf(combination, k);
    Arrays.sort(combination);

    boolean isValid = k <= n && originCombination.length == k;
    if (originCombination.length > 0) {
      isValid &= combination[k - 1] < n;
    }
    for (int i = 1; i < k && isValid; ++i) {
      isValid = combination[i - 1] != combination[i];
    }
    if (!isValid) {
      throw new IllegalArgumentException("%s is not a %d-combination from a %d-element set"
          .formatted(Arrays.toString(originCombination), k, n));
    }

    BigInteger reverseOrdinal = BigInteger.ZERO;
    for (int i = 0; i < k; ++i) {
      int v = combination[i];
      reverseOrdinal = reverseOrdinal.add(combinationCount(n - v - 1, k - i));
    }

    return combinationCount(n, k).subtract(BigInteger.ONE).subtract(reverseOrdinal);
  }

  public static int[] getSetPartitionByOrdinal(int n, int k, long ordinal) {
    return getSetPartitionByOrdinal(n, k, BigInteger.valueOf(ordinal));
  }

  public static int[] getSetPartitionByOrdinal(int n, int k, BigInteger ordinal) {
    validateCombinatoricsParams(n, k);
    if (ordinal.compareTo(BigInteger.ZERO) < 0 || ordinal.compareTo(setPartitionCount(n, k)) >= 0 || k == 0) {
      return EMPTY_INT_ARRAY;
    }

    int[] partition = new int[n];
    boolean[] usedElements = new boolean[n];
    int blockLength = n / k;

    for (int i = 0; i < k; ++i) {
      BigInteger div = setPartitionCount(n - (i + 1) * blockLength, k - (i + 1));
      if (div.equals(BigInteger.ZERO)) {
        div = BigInteger.ONE;
      }
      BigInteger[] tmp = ordinal.divideAndRemainder(div);
      BigInteger blockOrdinal = tmp[0];
      ordinal = tmp[1];
      int[] partitionBlock = getCombinationByOrdinal(n - i * blockLength, blockLength, blockOrdinal);

      for (int j = 0; j < blockLength; ++j) {
        int value = ArrayUtils.nthIndexOf(usedElements, false, partitionBlock[j] - j);
        partition[i * blockLength + j] = value;
        usedElements[value] = true;
      }
    }

    return partition;
  }

  public static BigInteger getSetPartitionOrdinal(int n, int k, int[] partition) {
    validateCombinatoricsParams(n, k);
    if (n == 0 && partition.length == 0) {
      return BigInteger.ZERO;
    }

    int blockLength = k != 0 ? n / k : 1;
    int[] originPartition = partition;
    partition = Arrays.copyOf(partition, n);
    Arrays.sort(partition);

    boolean isValid = (n > 0) && (k > 0) && (n % k == 0);
    isValid &= originPartition.length == n && partition[n - 1] < n;

    for (int i = 1; i < n && isValid; ++i) {
      isValid = partition[i - 1] != partition[i]; // all elements unique
      isValid &= (i % blockLength == 0) || (originPartition[i - 1] < originPartition[i]); // block elems are sorted
      if (i % blockLength == 0) {
        isValid &= originPartition[i - blockLength] < originPartition[i]; // blocks are sorted
      }
    }
    if (!isValid) {
      throw new IllegalArgumentException("%s is not a partition of %d-element set into %d subsets of the same size"
          .formatted(Arrays.toString(originPartition), n, k));
    }

    partition = originPartition;
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

      BigInteger blockOrdinal = getCombinationOrdinal(n - i * blockLength, blockLength, partitionBlock);
      BigInteger tailCount = setPartitionCount(n - (i + 1) * blockLength, k - (i + 1));
      ordinal = ordinal.add(blockOrdinal.multiply(tailCount));
    }

    return ordinal;
  }



  public static class Fast {
    private Fast() { }

    public static long factorial(int x) {
      long res = 1;
      for (int i = 2; i <= x; ++i) {
        res = Math.multiplyExact(res, i);
      }
      return res;
    }

    public static long arrangementWithRepetitionCount(int n, int k) {
      if (n == 0) {
        return 0;
      }

      long res = 1;
      for (int i = 0; i < k; ++i) {
        res = Math.multiplyExact(res, n);
      }
      return res;
    }

    public static long combinationCount(int n, int k) {
      if (k > n) {
        return 0;
      }

      long res = 1;
      for (int i = Math.max(k, n - k) + 1; i <= n; ++i) {
        res = Math.multiplyExact(res, i);
      }
      for (int i = 2; i <= Math.min(k, n - k); ++i) {
        res = Math.divideExact(res, i);
      }
      return res;
    }

    public static long setPartitionCount(int n, int k) {
      if (k > n || k == 0 || n % k != 0) {
        return 0;
      }

      int blockLength = n / k;
      long res = 1;

      for (int i = 0; i + 1 < k; ++i) {
        res = Math.multiplyExact(res, Fast.combinationCount(n - i * blockLength, blockLength));
      }
      res = Math.divideExact(res, Fast.factorial(k));
      return res;
    }
  }


  private static class Generator implements Iterator<int[]> {
    private int[] data;
    private final UnaryOperator<int[]> getNext;

    Generator(int[] startData, UnaryOperator<int[]> getNext) {
      this.data = startData;
      this.getNext = getNext;
    }

    @Override
    public boolean hasNext() { return data != null; }

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

  public static Iterator<int[]> arrangementWithRepetitionGenerator(int n, int k) {
    validateCombinatoricsParams(n, k);
    return new Generator(
        n > 0 ? new int[k] : null,
        data -> getNextArrangementWithRepetitions(n, k, data));
  }

  public static Iterator<int[]> combinationGenerator(int n, int k) {
    validateCombinatoricsParams(n, k);
    return new Generator(
        k <= n
            ? IntStream.range(0, k).toArray()
            : null,
        data -> getNextCombination(n, k, data));
  }

  public static Iterator<int[]> setPartitionGenerator(int n, int k) {
    validateCombinatoricsParams(n, k);
    return new Generator(
        n == k || (k > 0 && n % k == 0)
            ? IntStream.range(0, n).toArray()
            : null,
        data -> getNextSetPartition(n, k, data));
  }



  public static List<int[]> getPermutations(int n) {
    validateCombinatoricsParam(n);
    long cnt = Fast.factorial(n);

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
    validateCombinatoricsParams(n, k);
    long cnt = Fast.arrangementWithRepetitionCount(n, k);

    List<int[]> arrangements = new ArrayList<>();
    int[] arrangement = new int[k];

    if (cnt > 0) {
      arrangements.add(arrangement);
    }
    for (int i = 1; i < cnt; ++i) {
      arrangement = getNextArrangementWithRepetitions(n, k, arrangement);
      arrangements.add(arrangement);
    }

    return arrangements;
  }

  public static List<int[]> getCombinations(int n, int k) {
    validateCombinatoricsParams(n, k);
    long cnt = Fast.combinationCount(n, k);

    List<int[]> combinations = new ArrayList<>();
    int[] combination = IntStream.range(0, k).toArray();

    if (cnt > 0) {
      combinations.add(combination);
    }
    for (int i = 1; i < cnt; ++i) {
      combination = getNextCombination(n, k, combination);
      combinations.add(combination);
    }

    return combinations;
  }

  public static List<int[]> getSetPartitions(int n, int k) {
    validateCombinatoricsParams(n, k);
    long cnt = Fast.setPartitionCount(n, k);

    List<int[]> partitions = new ArrayList<>();
    int[] partition = IntStream.range(0, n).toArray();

    partitions.add(partition);
    for (int i = 1; i < cnt; ++i) {
      partition = getNextSetPartition(n, k, partition);
      partitions.add(partition);
    }

    return partitions;
  }



  private static int[] getNextPermutation(int n, int[] permutation) {
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

  private static int[] getNextArrangementWithRepetitions(int n, int k, int[] arrangement) {
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

  private static int[] getNextCombination(int n, int k, int[] combination) {
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

  private static int[] getNextSetPartition(int n, int k, int[] partition) {
    // TODO: MAAN, CONSTRUCT NORMAL ALGO, NOT THAT STUPIDITY...
    if (partition.length == 0) {
      return null;
    }
    BigInteger ordinal = getSetPartitionOrdinal(n, k, partition);
    BigInteger nextOrdinal = ordinal.add(BigInteger.ONE);
    if (nextOrdinal.compareTo(setPartitionCount(n, k)) >= 0) {
      return null;
    }
    return getSetPartitionByOrdinal(n, k, nextOrdinal);
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
  }
}
