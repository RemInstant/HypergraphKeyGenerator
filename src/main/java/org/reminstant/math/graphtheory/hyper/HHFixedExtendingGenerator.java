package org.reminstant.math.graphtheory.hyper;

import org.reminstant.math.combinatorics.CombinationFactory;
import org.reminstant.utils.BigIntGenerator;
import org.reminstant.utils.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.NavigableSet;
import java.util.Random;
import java.util.TreeSet;
import java.util.random.RandomGenerator;

public class HHFixedExtendingGenerator implements Generator<HomogenousHypergraph> {

  private static final Logger log = LoggerFactory.getLogger(HHFixedExtendingGenerator.class);
  private final int verticesCount;
  private final int edgeDimension;
  private final int edgeMaxCount;
  private final int treeEdgeCount;
  private final int additionalEdgeCount;
  private final RandomGenerator random;
  private final HyperTreeGenerator hyperTreeGenerator;
  private final BigInteger[] additionalEdgeDistribution;
  private final BigIntGenerator edgeWeightGenerator;

  public HHFixedExtendingGenerator(int verticesCount, int edgeDimension,
                                   int edgeCount, RandomGenerator random) {
    this.verticesCount = verticesCount;
    this.edgeDimension = edgeDimension;
    this.edgeMaxCount = CombinationFactory.ofParams(verticesCount, edgeDimension).count().intValueExact();
    this.treeEdgeCount = (verticesCount - 1) / (edgeDimension - 1);

    int additionalEdgeMaxCount = edgeMaxCount - treeEdgeCount;
    this.additionalEdgeCount = edgeCount - treeEdgeCount;
    if (additionalEdgeCount < 0 || additionalEdgeCount > additionalEdgeMaxCount) {
      throw new IllegalArgumentException("invalid edge count");
    }

    this.random = random;
    this.hyperTreeGenerator = new HyperTreeGenerator(verticesCount, edgeDimension, random);
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

  public HHFixedExtendingGenerator(int verticesCount, int edgeDimension, int edgeCount, long seed) {
    this(verticesCount, edgeDimension, edgeCount, new Random(seed));
  }

  public HHFixedExtendingGenerator(int verticesCount, int edgeDimension, int edgeCount) {
    this(verticesCount, edgeDimension, edgeCount, getStrongRandom());
  }

  public HomogenousHypergraph next() {
    NavigableSet<Integer> treeEdgeIndices = new TreeSet<>();

    HomogenousHyperTree tree = hyperTreeGenerator.next();
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
