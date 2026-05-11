package org.reminstant.experiments;

import org.reminstant.math.Combinatorics;
import org.reminstant.math.combinatorics.CombinationFactory;
import org.reminstant.math.graphtheory.hyper.HHExtendingGenerator;
import org.reminstant.math.graphtheory.hyper.HHOverlappingGenerator;
import org.reminstant.math.graphtheory.hyper.HomogenousHypergraph;
import org.reminstant.math.graphtheory.hyper.HyperEdge;
import org.reminstant.utils.BigIntGenerator;
import org.reminstant.utils.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class DegreeVectorPrinter {
  private static final Logger log = LoggerFactory.getLogger(DegreeVectorPrinter.class);

  public static void main(String[] args) throws InterruptedException {
    int n = 34;
    int k = 4;
    int addCap = 50000;
    int treeCount = 40;
    int graphCount = 10;
    boolean plusOneEdge = false;

//    var g = new HHOverlappingGenerator(n, k, treeCount);
    var g = new HHExtendingGenerator(n, k, 0, addCap);
    var Random = new SecureRandom();
    var plusEdgeFactory = CombinationFactory.ofParams(n, k - 1);

    try (PrintWriter writer = new PrintWriter("mathew.txt")) {
      for (int i = 0; i < graphCount; ++i) {
        Instant start = Instant.now();
        var graph = generate(g, plusEdgeFactory, Random, plusOneEdge);
        log.info("Generated #{} in {}ms", i, Duration.between(start, Instant.now()).toMillis());

        start = Instant.now();
        List<Integer> degrees = graph.getDegreesList();
        log.info("Converted #{} to vector in {}ms", i, Duration.between(start, Instant.now()).toMillis());

        start = Instant.now();
        Collections.shuffle(degrees);
        log.info("Shuffled #{} in {}ms", i, Duration.between(start, Instant.now()).toMillis());

        int checkSum = 0;
        int minDegree = Integer.MAX_VALUE;
        int maxDegree = Integer.MIN_VALUE;
        start = Instant.now();
        for (var degree : degrees) {
          checkSum += degree;
          minDegree = Math.min(minDegree, degree);
          maxDegree = Math.max(maxDegree, degree);
          writer.print(degree);
          writer.print(' ');
        }
        log.info("Printed #{} in {}ms", i, Duration.between(start, Instant.now()).toMillis());

        writer.println();
        log.info("graph #{}: checksum = {}, min = {}, max = {}, dist = {}",
            i, checkSum, minDegree, maxDegree, maxDegree - minDegree);
      }
      writer.flush();
    } catch (IOException e) {
      log.error("", e);
    }
  }

  private static HomogenousHypergraph generate(Generator<HomogenousHypergraph> generator,
                                               CombinationFactory plusEdgeFactory,
                                               Random random,
                                               boolean plusOne) {
    HomogenousHypergraph graph = generator.next();

    if (plusOne) {
      int n = graph.getVerticesCount();
      int k = graph.getEdgeDimension();
      int s = 0;

      var plusGraph = new HomogenousHypergraph(n + 1, k);
      graph.getEdges().forEach(plusGraph::addEdge);
      graph = plusGraph;

      for (var q : graph.getDegreesList()) {
        s += q;
      }

      s /= graph.getVerticesCount();

//      SortedSet<Integer> treeEdgeIndices = new TreeSet<>();
//      for (int i = 0; i < s; ++i) {
//        start = Instant.now();
//        int additionalEdgeMaxCount = plusEdgeFactory.count().intValue() - i;
//        int edgeIndex = random.nextInt(additionalEdgeMaxCount);
//
//        int indexShift;
//        int shiftedIndex = edgeIndex;
//        do {
//          indexShift = treeEdgeIndices.headSet(shiftedIndex + 1).size();
//          shiftedIndex = edgeIndex + indexShift;
//        } while (indexShift < treeEdgeIndices.headSet(shiftedIndex + 1).size());
//
//        int[] edge = plusEdgeFactory.byOrdinal(shiftedIndex);
//        edge = Arrays.copyOf(edge, k);
//        edge[k - 1] = n;
//
//        treeEdgeIndices.add(shiftedIndex);
//        graph.addEdge(HyperEdge.of(edge));
//
//        log.info("N 3 {} - {}ms", i, Duration.between(start, Instant.now()).toMillis());
//      }

      var plusEdgeIndicesFactory = CombinationFactory.ofParams(plusEdgeFactory.count().intValue(), s);
      BigInteger ordinal = new BigIntGenerator(plusEdgeIndicesFactory.count()).next();
      int[] indices = plusEdgeIndicesFactory.byOrdinal(ordinal);

      for (var index : indices) {
        int[] edge = plusEdgeFactory.byOrdinal(index);
        edge = Arrays.copyOf(edge, k);
        edge[k - 1] = n;
        graph.addEdge(HyperEdge.of(edge));
      }
    }

    return graph;
  }
}
