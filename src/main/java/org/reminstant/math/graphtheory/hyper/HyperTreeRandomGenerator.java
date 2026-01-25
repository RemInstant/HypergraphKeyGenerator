package org.reminstant.math.graphtheory.hyper;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.random.RandomGenerator;

public class HyperTreeRandomGenerator {

  private final int verticesCount;
  private final int edgeDimension;
  private final int treeCount;
  private final HHTreeCodeFactory factory;
  private final RandomGenerator random;
  private final byte[] randomData;

  public HyperTreeRandomGenerator(int verticesCount, int edgeDimension,
                                  int treeCount, RandomGenerator random) {
    this.verticesCount = verticesCount;
    this.edgeDimension = edgeDimension;
    this.treeCount = treeCount;
    this.factory = HHTreeCodeFactory.ofParams(verticesCount, edgeDimension);
    this.random = random;

    BigInteger maxIndex = factory.count().subtract(BigInteger.ONE);
    byte[] maxIndexAsBytes = maxIndex.toByteArray();
    if (maxIndexAsBytes[0] == 0) {
      maxIndexAsBytes = Arrays.copyOfRange(maxIndexAsBytes, 1, maxIndexAsBytes.length);
    }
    this.randomData = maxIndexAsBytes;
  }

  public HyperTreeRandomGenerator(int verticesCount, int edgeDimension, int treeCount, long seed) {
    this(verticesCount, edgeDimension, treeCount, new Random(seed));
  }

  public HyperTreeRandomGenerator(int verticesCount, int edgeDimension, int treeCount) {
    this(verticesCount, edgeDimension, treeCount, getStrongRandom());
  }

  public HomogenousHypergraph next() {
    HomogenousHypergraph graph = new HomogenousHypergraph(verticesCount, edgeDimension);
    for (int i = 0; i < treeCount; ++i) {
      HomogenousHyperTree tree = nextTree();
      graph.unionInPlace(HomogenousHypergraph.ofTree(tree));
    }
    return graph;
  }

  private HomogenousHyperTree nextTree() {
    BigInteger ordinal;
    do {
      random.nextBytes(randomData);
      ordinal = new BigInteger(1, randomData);
    } while (ordinal.compareTo(factory.count()) >= 0);

    return factory.byOrdinal(ordinal).toTree();
  }

  private static RandomGenerator getStrongRandom() {
    try {
      return SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
