package org.reminstant.math.graphtheory.hyper;

import org.reminstant.utils.Generator;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.random.RandomGenerator;

public class HHOverlappingGenerator implements Generator<HomogenousHypergraph> {

  private final int verticesCount;
  private final int edgeDimension;
  private final RandomGenerator random;
  private final HyperTreeGenerator hyperTreeGenerator;
  private final int treeCount;

  public HHOverlappingGenerator(int verticesCount, int edgeDimension,
                                int treeCount, RandomGenerator random) {
    this.verticesCount = verticesCount;
    this.edgeDimension = edgeDimension;
    this.random = random;
    this.hyperTreeGenerator = new HyperTreeGenerator(verticesCount, edgeDimension, random);
    this.treeCount = treeCount;
  }

  public HHOverlappingGenerator(int verticesCount, int edgeDimension, int treeCount, long seed) {
    this(verticesCount, edgeDimension, treeCount, new Random(seed));
  }

  public HHOverlappingGenerator(int verticesCount, int edgeDimension, int treeCount) {
    this(verticesCount, edgeDimension, treeCount, getStrongRandom());
  }

  public HomogenousHypergraph next() {
    HomogenousHypergraph graph = new HomogenousHypergraph(verticesCount, edgeDimension);

//    do {
//      HomogenousHyperTree tree = hyperTreeGenerator.next();
//      graph.unionInPlace(HomogenousHypergraph.ofTree(tree));
//    } while(random.nextInt() % 8 != 7);

    for (int i = 0; i < treeCount; ++i) {
      HomogenousHyperTree tree = hyperTreeGenerator.next();
      graph.unionInPlace(HomogenousHypergraph.ofTree(tree));
    }
    return graph;
  }


  private static RandomGenerator getStrongRandom() {
    try {
      return SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
