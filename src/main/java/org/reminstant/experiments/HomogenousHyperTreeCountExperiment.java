package org.reminstant.experiments;

import org.reminstant.math.graphtheory.hyper.HomogenousHyperTree;
import org.reminstant.math.Combinatorics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class HomogenousHyperTreeCountExperiment {
  private static final Logger log = LoggerFactory.getLogger(HomogenousHyperTreeCountExperiment.class);

  public static void main(String[] args) {
    int verticesCount = 7;
    int edgeDimension = 3;

    long count = calculateCount(verticesCount, edgeDimension);
    log.info("COUNT = {}", count);
  }

  private static long calculateCount(int verticesCount, int edgeDimension) {
    if (edgeDimension < 2 || (verticesCount - 1) % (edgeDimension - 1) != 0) {
      log.warn("Invalid parameters");
      return 0;
    }
    if (verticesCount == 0) {
      return 0;
    }

    long count = 0;
    int edgeCount = (verticesCount - 1) / (edgeDimension - 1);
    List<int[]> edgeVertices = Combinatorics.getCombinations(verticesCount, edgeDimension);
    Iterator<int[]> edgeIndicesGenerator = Combinatorics.combinationGenerator(edgeVertices.size(), edgeCount);
    var builder = HomogenousHyperTree.builder();

    while (edgeIndicesGenerator.hasNext()) {
      int[] edgeIndices = edgeIndicesGenerator.next();
      for (int edgeIndex : edgeIndices) {
        builder.addEdgeUnvalidated(edgeVertices.get(edgeIndex));
      }
      if (builder.canBuild()) {
        count++;
      }
      builder.clear();
    }

    return count;
  }
}
