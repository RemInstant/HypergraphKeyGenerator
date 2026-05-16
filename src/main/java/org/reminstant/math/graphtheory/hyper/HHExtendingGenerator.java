package org.reminstant.math.graphtheory.hyper;

import org.reminstant.math.combinatorics.CombinationFactory;
import org.reminstant.utils.BigIntGenerator;
import org.reminstant.utils.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.random.RandomGenerator;

public class HHExtendingGenerator implements Generator<HomogenousHypergraph> {

  private static final Logger log = LoggerFactory.getLogger(HHExtendingGenerator.class);
  private final int verticesCount;
  private final int edgeDimension;
  private final int edgeMaxCount;
  private final int treeEdgeCount;
  private final int additionalEdgeMinCount;
  private final RandomGenerator random;
  private final HyperTreeGenerator hyperTreeGenerator;
  private final BigInteger[] additionalEdgeDistribution;
  private final BigIntGenerator edgeWeightGenerator;
  private final BigIntGenerator additionalEdgeCountGenerator;

  public HHExtendingGenerator(int verticesCount, int edgeDimension, int edgeMinCount,
                              int edgeMaxCount, RandomGenerator random) {
    this.verticesCount = verticesCount;
    this.edgeDimension = edgeDimension;
    this.edgeMaxCount = CombinationFactory.ofParams(verticesCount, edgeDimension).count().intValueExact();
    this.treeEdgeCount = (verticesCount - 1) / (edgeDimension - 1);

    if (edgeMaxCount < edgeMinCount) {
      throw new IllegalArgumentException("edgeMinCount > edgeMaxCount");
    }
    if (edgeMinCount < treeEdgeCount) {
      throw new IllegalArgumentException("edgeMinCount < treeEdgeCount");
    }

    edgeMaxCount = Math.min(edgeMaxCount, this.edgeMaxCount);

    int additionalEdgeMaxCount = edgeMaxCount - treeEdgeCount;
    this.additionalEdgeMinCount = edgeMinCount - treeEdgeCount;
    int delta = additionalEdgeMaxCount - additionalEdgeMinCount + 1;

    this.random = random;
    this.hyperTreeGenerator = new HyperTreeGenerator(verticesCount, edgeDimension, random);
    this.additionalEdgeCountGenerator = new BigIntGenerator(BigInteger.valueOf(delta), random);
    this.edgeWeightGenerator = null;
    this.additionalEdgeDistribution = null;


//    this.additionalEdgeDistribution = new BigInteger[additionalEdgeMaxCount + 1];
//    for (int i = 0; i <= additionalEdgeMaxCount; ++i) {
//      log.info("CONSTRUCTOR NEXT {}/{}", i+1, additionalEdgeMaxCount);
//      BigInteger prev = i == 0 ? BigInteger.ZERO : additionalEdgeDistribution[i - 1];
//      additionalEdgeDistribution[i] = prev.add(CombinationFactory.ofParams(edgeMaxCount, t + i).count());
//    }
//
//    BigInteger maxWeight = additionalEdgeDistribution[additionalEdgeMaxCount];
//    this.edgeWeightGenerator = new BigIntGenerator(maxWeight, random);
  }

  public HHExtendingGenerator(int verticesCount, int edgeDimension, int edgeMinCount, int edgeMaxCount, long seed) {
    this(verticesCount, edgeDimension, edgeMinCount, edgeMaxCount, new Random(seed));
  }

  public HHExtendingGenerator(int verticesCount, int edgeDimension, int edgeMinCount, int edgeMaxCount) {
    this(verticesCount, edgeDimension, edgeMinCount, edgeMaxCount, getStrongRandom());
  }

  public HHExtendingGenerator(int verticesCount, int edgeDimension) {
    this(verticesCount, edgeDimension, 0, Integer.MAX_VALUE, getStrongRandom());
  }

  public HomogenousHypergraph next() {
    NavigableSet<Integer> treeEdgeIndices = new TreeSet<>();

    HomogenousHyperTree tree = hyperTreeGenerator.next();
//    HomogenousHyperTree tree = new HyperTreeGenerator(verticesCount, edgeDimension, 777).next();
    for (var edge : tree.getEdges()) {
      int edgeIndex = edge.getEdgeIndex(verticesCount);
      treeEdgeIndices.add(edgeIndex);
    }

    HomogenousHypergraph graph = HomogenousHypergraph.ofTree(tree);

//    BigInteger weight = edgeWeightGenerator.next();
//    int idx = Arrays.binarySearch(additionalEdgeDistribution, weight);
//    int additionalEdgeCount = idx < 0
//        ? -(idx + 1)
//        : idx + 1;

    int additionalEdgeCount = additionalEdgeMinCount + additionalEdgeCountGenerator.next().intValue();

//    var additionalEdgeIndicesFactory = CombinationFactory.ofParams(additionalEdgeMaxCount, additionalEdgeCount);
//    var g = new BigIntGenerator(additionalEdgeIndicesFactory.count(), random);

    for (int i = 0; i < additionalEdgeCount; ++i) {
      int additionalEdgeMaxCount = edgeMaxCount - treeEdgeCount - i;
      int edgeIndex = random.nextInt(additionalEdgeMaxCount);

      int indexShift;
      int shiftedIndex = edgeIndex;
      do {
        indexShift = treeEdgeIndices.headSet(shiftedIndex + 1).size();
        shiftedIndex = edgeIndex + indexShift;
      } while (indexShift < treeEdgeIndices.headSet(shiftedIndex + 1).size());

      HyperEdge e = HyperEdge.ofEdgeIndex(shiftedIndex, verticesCount, edgeDimension);
      treeEdgeIndices.add(shiftedIndex);
      graph.addEdge(e);
    }

//    int[] edgeIndices = additionalEdgeIndicesFactory.byOrdinal(g.next());

//    for (int edgeIndex : edgeIndices) {
//      int indexShift;
//      int shiftedIndex = edgeIndex;
//      do {
//        indexShift = treeEdgeIndices.headSet(shiftedIndex + 1).size();
//        shiftedIndex = edgeIndex + indexShift;
//      } while (indexShift < treeEdgeIndices.headSet(shiftedIndex + 1).size());
//
//      HyperEdge e = HyperEdge.ofEdgeIndex(shiftedIndex, verticesCount, edgeDimension);
//      graph.addEdge(e);
//    }
//    log.info("NEXT 6");

    if (tree.getEdges().size() + additionalEdgeCount != graph.getEdgeCount()) {
      throw new RuntimeException();
    }

    return graph;

//    for (int i = 0; i < edgeMaxCount; ++i) {
//      if (!edgeBitSet.get(i) && random.nextBoolean()) {
//        edgeBitSet.set(i);
//      }
//    }
//
//    return HomogenousHypergraph.ofEdgesBitset(verticesCount, edgeDimension, edgeBitSet);
  }


  private static RandomGenerator getStrongRandom() {
    try {
      return SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
