package org.reminstant.math.graphtheory.hyper;

import org.reminstant.math.Combinatorics;
import org.reminstant.math.combinatorics.ArrangementWithRepetitionFactory;
import org.reminstant.math.combinatorics.CombinationFactory;
import org.reminstant.math.combinatorics.DiscreteObjectFactory;
import org.reminstant.math.combinatorics.UniformPartitionFactory;
import org.reminstant.structure.Pair;
import org.reminstant.utils.sequence.Sequence;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HHTreeCodeFactory implements DiscreteObjectFactory<HHTreeCode> {

  private final int verticesCount;
  private final int edgeDimension;
  private final int partitionLength;
  private final int blockLength;
  private final int blockCount;

  private final DiscreteObjectFactory<int[]> partitionFactory;
  private final List<DiscreteObjectFactory<int[]>> nonRootIndicesFactories;
  private final List<DiscreteObjectFactory<int[]>> conditionalCodeFactories;
  private final List<DiscreteObjectFactory<int[]>> jointsFactories;

  private BigInteger count;

  private HHTreeCodeFactory(int verticesCount, int edgeDimension) {
    this.verticesCount = verticesCount;
    this.edgeDimension = edgeDimension;
    this.partitionLength = verticesCount - 1;
    this.blockLength = edgeDimension - 1;
    this.blockCount = partitionLength / blockLength;

    List<DiscreteObjectFactory<int[]>> nonRootIndicesFactoriesTmp = new ArrayList<>();
    List<DiscreteObjectFactory<int[]>> conditionalCodeFactoriesTmp = new ArrayList<>();
    List<DiscreteObjectFactory<int[]>> jointsFactoriesTmp = new ArrayList<>();
    for (int nonRootCount = 0; nonRootCount < blockCount; ++nonRootCount) {
      nonRootIndicesFactoriesTmp.add(
          CombinationFactory.ofParams(blockCount - 1, nonRootCount));
      conditionalCodeFactoriesTmp.add(
          ArrangementWithRepetitionFactory.ofParams(blockCount, nonRootCount));
      jointsFactoriesTmp.add(
          ArrangementWithRepetitionFactory.ofParams(blockLength, nonRootCount));
    }

    this.partitionFactory = UniformPartitionFactory.ofParams(verticesCount - 1, blockCount);
    this.nonRootIndicesFactories = Collections.unmodifiableList(nonRootIndicesFactoriesTmp);
    this.conditionalCodeFactories = Collections.unmodifiableList(conditionalCodeFactoriesTmp);
    this.jointsFactories = Collections.unmodifiableList(jointsFactoriesTmp);

    this.count = null;
  }

  public static HHTreeCodeFactory ofParams(int verticesCount, int edgeDimension) {
    if (verticesCount < 1) {
      throw new IllegalArgumentException("Violated condition: verticesCount >= 1");
    }
    if (edgeDimension < 2) {
      throw new IllegalArgumentException("Violated condition: edgeDimension >= 2");
    }
    if ((verticesCount - 1) % (edgeDimension - 1) != 0) {
      throw new IllegalArgumentException("Violated condition: (verticesCount-1) % (edgeDimension-1) = 0");
    }
    return new HHTreeCodeFactory(verticesCount, edgeDimension);
  }

  public BigInteger count() {
    if (count == null) {
      // n = verticesCount
      // k = edgeDimension
      // t = (n-1)/(k-1)
      // P(n, k)    - partition count
      // Q(n, k, x) - code with X maximums count
      // R(n, k, x) - joints count for code with X maximums
      // P(n, k)      = n! / (k! * ((n/k)!)^n)
      // P(n-1, t)    = (n-1)! / (t! * ((k-1)!)^(n-1)) (set partition for [n-1, t])
      // Q(n, k, x)   = C(t-1, x) * t^(t-1-x)
      // R(n, k, x)   = (k-1)^(t-1-x)
      // Q*R(n, k, x) = C(t-1, x) * (n-1)^(t-1-x)
      // RESULT(n, k) = P(n-1, t) * sum(i:0..t){Q(n,k,i)R(n,k,i)}

      BigInteger partitionCount = Combinatorics.setPartitionCount(partitionLength, blockCount);
      BigInteger codeJointsCount = getCodeJointsCount();
      count = partitionCount.multiply(codeJointsCount);
    }
    return count;
  }

  public boolean isValid(HHTreeCode combination) {
//    return combination.length == k &&
//        !ArrayUtils.hasDuplicates(combination) &&
//        ArrayUtils.allMatch(combination, x -> x >= 0 && x < n);
    throw new UnsupportedOperationException("todo"); // TODO:
  }

  public HHTreeCode byOrdinal(BigInteger ordinal) {
    if (ordinal.compareTo(BigInteger.ZERO) < 0 || ordinal.compareTo(count()) >= 0) {
      throw new NoSuchElementException("Such hypertree code does not exist");
    }

    BigInteger partitionDiv = getCodeJointsCount();
    BigInteger[] tmp = ordinal.divideAndRemainder(partitionDiv);
    BigInteger partitionOrdinal = tmp[0];
    BigInteger codeJointsOrdinal = tmp[1];

    int[] partition = partitionFactory.byOrdinal(partitionOrdinal);
    Pair<int[], int[]> codeJoints = getCodeJointsByOrdinal(codeJointsOrdinal);

    return new HHTreeCode(partition, codeJoints.first(), codeJoints.second());
  }

  public BigInteger toOrdinal(HHTreeCode combination) {
    throwIfInvalid(combination);

    throw new UnsupportedOperationException("todo"); // TODO:
  }

  public HHTreeCode getNext(HHTreeCode combination) {
    throwIfInvalid(combination);
    return getNextInner(combination);
  }

  public Sequence<HHTreeCode> sequence() {
    Sequence<Integer> nonRootCountGenerator = Sequence
        .ofSource(IntStream.range(0, blockCount).map(x -> blockCount - 1 - x).toArray());

    Function<Integer, Sequence<Pair<int[], int[]>>> codeJointsGeneratorFunction = nonRootCount ->
        nonRootIndicesFactories.get(nonRootCount).sequence()
            .combine(conditionalCodeFactories.get(nonRootCount).sequence())
            .map(x -> constructCodeFromConditional(x.second(), x.first()))
            .combine(jointsFactories.get(nonRootCount).sequence());

    var codeJointsGenerator = nonRootCountGenerator
        .combine(codeJointsGeneratorFunction)
        .map(Pair::second);

    return partitionFactory.sequence()
        .combine(codeJointsGenerator)
        .map(p -> {
          int[] iterPartition = p.first();
          int[] iterCode = p.second().first();
          int[] iterJoints = p.second().second();
          return new HHTreeCode(iterPartition, iterCode, iterJoints);
        });
  }



  public BigInteger getCodeJointsCount() {
    BigInteger codeJointsCount = BigInteger.ZERO;
    for (int nonRootCount = 0; nonRootCount < blockCount; ++nonRootCount) {
      codeJointsCount = codeJointsCount.add(getCodeJointsCount(nonRootCount));
    }
    return codeJointsCount;
  }

  public BigInteger getCodeJointsCount(int nontRootCount) {
    BigInteger partitionLengthBig = BigInteger.valueOf(partitionLength);
    return Combinatorics
        .combinationCount(blockCount - 1, blockCount - 1 - nontRootCount)
        .multiply(partitionLengthBig.pow(nontRootCount));
  }

  // TODO: private
  public Pair<int[], int[]> getCodeJointsByOrdinal(BigInteger ordinal) {
    BigInteger previousCount;
    BigInteger updatedPreviousCount = BigInteger.ZERO;
    int nonRootCount = blockCount;

    do {
      previousCount = updatedPreviousCount;
      nonRootCount -= 1;
      BigInteger conditionalCodeJointsCount = getCodeJointsCount(nonRootCount);
      updatedPreviousCount = previousCount.add(conditionalCodeJointsCount);
    } while (updatedPreviousCount.compareTo(ordinal) <= 0);

    ordinal = ordinal.subtract(previousCount);

    BigInteger conditionalCodeCount = conditionalCodeFactories.get(nonRootCount).count();
    BigInteger jointsCount = jointsFactories.get(nonRootCount).count();

    BigInteger div = conditionalCodeCount.multiply(jointsCount);
    BigInteger[] tmp = ordinal.divideAndRemainder(div);
    BigInteger nonRootOrdinal = tmp[0];
    ordinal = tmp[1];

    tmp = ordinal.divideAndRemainder(jointsCount);
    BigInteger conditionalCodeOrdinal = tmp[0];
    BigInteger jointsOrdinal = tmp[1];

    int[] nonRootIndices = nonRootIndicesFactories.get(nonRootCount).byOrdinal(nonRootOrdinal);
    int[] conditionalCode = conditionalCodeFactories.get(nonRootCount).byOrdinal(conditionalCodeOrdinal);

    int[] code = constructCodeFromConditional(conditionalCode, nonRootIndices);
    int[] joints = jointsFactories.get(nonRootCount).byOrdinal(jointsOrdinal);

    return Pair.of(code, joints);
  }

  private int[] constructCodeFromConditional(int[] conditionalCode, int[] nonRootIndices) {
    int[] code = new int[blockCount - 1];
    Set<Integer> nonRootIndicesSet = Arrays.stream(nonRootIndices).boxed().collect(Collectors.toSet());
    int conditionalCodeIter = 0;
    for (int i = 0; i < code.length; ++i) {
      if (nonRootIndicesSet.contains(i)) {
        code[i] = conditionalCode[conditionalCodeIter];
        conditionalCodeIter++;
      } else {
        code[i] = blockCount;
      }
    }
    return code;
  }

  private HHTreeCode getNextInner(HHTreeCode combination) {
    throw new UnsupportedOperationException("todo"); // TODO:
  }

  private void throwIfInvalid(HHTreeCode combination) {
    if (!isValid(combination)) {
      throw new IllegalArgumentException("%s is not a code of %d-homogenous hypertree with %d vertices"
          .formatted(combination, edgeDimension, verticesCount));
    }
  }
}
