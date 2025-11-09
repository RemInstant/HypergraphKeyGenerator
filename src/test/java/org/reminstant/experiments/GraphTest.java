package org.reminstant.experiments;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reminstant.Utils;
import org.reminstant.math.Combinatorics;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

class GraphTest {

  @Test
  void graphEqualsTest1() {
    Graph graph1 = new Graph(4);
    Graph graph2 = new Graph(4);

    graph1.addEdge(0, 1);
    graph1.addEdge(1, 2);
    graph1.addEdge(2, 3);
    graph1.addEdge(3, 0);

    graph2.addEdge(0, 1);
    graph2.addEdge(1, 2);
    graph2.addEdge(2, 3);
    graph2.addEdge(3, 0);

    Assertions.assertEquals(graph1, graph2);
  }

  @Test
  void graphEqualsTest2() {
    Graph graph1 = new Graph(4);
    Graph graph2 = new Graph(4);

    graph1.addEdge(0, 1);
    graph1.addEdge(1, 2);
    graph1.addEdge(2, 3);
    graph1.addEdge(3, 0);

    graph2.addEdge(0, 1);
    graph2.addEdge(2, 1);
    graph2.addEdge(2, 3);
    graph2.addEdge(0, 3);

    Assertions.assertEquals(graph1, graph2);
  }

  @Test
  void graphEqualsTest3() {
    Graph graph1 = new Graph(5);
    Graph graph2 = new Graph(5);

    graph1.addEdge(0, 1);
    graph1.addEdge(1, 2);
    graph1.addEdge(2, 3);
    graph1.addEdge(3, 0);
    graph1.addEdge(0, 4);

    graph2.addEdge(0, 1);
    graph2.addEdge(2, 1);
    graph2.addEdge(2, 3);
    graph2.addEdge(0, 3);
    graph2.addEdge(3, 4);

    Assertions.assertNotEquals(graph1, graph2);
  }

  @Test
  void graphIsomorphismTest1() {
    Graph graph1 = new Graph(4);
    Graph graph2 = new Graph(4);

    graph1.addEdge(0, 1);
    graph1.addEdge(1, 2);
    graph1.addEdge(2, 3);
    graph1.addEdge(3, 0);

    graph2.addEdge(0, 1);
    graph2.addEdge(1, 2);
    graph2.addEdge(2, 3);
    graph2.addEdge(3, 0);

    Assertions.assertTrue(graph1.isomorphicTo(graph2));
  }

  @Test
  void graphIsomorphismTest2() {
    Graph graph1 = new Graph(4);
    Graph graph2 = new Graph(4);

    graph1.addEdge(0, 1);
    graph1.addEdge(1, 2);
    graph1.addEdge(2, 3);
    graph1.addEdge(3, 0);

    graph2.addEdge(0, 1);
    graph2.addEdge(2, 1);
    graph2.addEdge(2, 3);
    graph2.addEdge(0, 3);

    Assertions.assertTrue(graph1.isomorphicTo(graph2));
  }

  @Test
  void graphIsomorphismTest3() {
    Graph graph1 = new Graph(5);
    Graph graph2 = new Graph(5);

    graph1.addEdge(0, 1);
    graph1.addEdge(1, 2);
    graph1.addEdge(2, 3);
    graph1.addEdge(3, 0);
    graph1.addEdge(0, 4);

    graph2.addEdge(0, 1);
    graph2.addEdge(2, 1);
    graph2.addEdge(2, 3);
    graph2.addEdge(0, 3);
    graph2.addEdge(3, 4);

    Assertions.assertTrue(graph1.isomorphicTo(graph2));
  }

  @Test
  void graphIsomorphismTest4() {
    Graph graph1 = new Graph(8);
    Graph graph2 = new Graph(8);

    graph1.addEdge(0, 4);
    graph1.addEdge(0, 5);
    graph1.addEdge(0, 6);
    graph1.addEdge(1, 4);
    graph1.addEdge(1, 5);
    graph1.addEdge(1, 7);
    graph1.addEdge(2, 4);
    graph1.addEdge(2, 6);
    graph1.addEdge(2, 7);
    graph1.addEdge(3, 5);
    graph1.addEdge(3, 6);
    graph1.addEdge(3, 7);

    graph2.addEdge(0, 1);
    graph2.addEdge(0, 3);
    graph2.addEdge(0, 4);
    graph2.addEdge(1, 2);
    graph2.addEdge(1, 5);
    graph2.addEdge(2, 3);
    graph2.addEdge(2, 6);
    graph2.addEdge(3, 7);
    graph2.addEdge(4, 5);
    graph2.addEdge(4, 7);
    graph2.addEdge(5, 6);
    graph2.addEdge(6, 7);

    Assertions.assertTrue(graph1.isomorphicTo(graph2));
  }

  @Test
  void graphIsomorphismTest5() {
    Graph graph1 = new Graph(6);
    Graph graph2 = new Graph(6);
    Graph graph3 = new Graph(6);

    graph1.addEdge(0, 1);
    graph1.addEdge(1, 2);
    graph1.addEdge(2, 3);
    graph1.addEdge(3, 4);
    graph1.addEdge(4, 5);
    graph1.addEdge(5, 0);
    graph1.addEdge(0, 2);

    graph2.addEdge(0, 1);
    graph2.addEdge(1, 2);
    graph2.addEdge(2, 3);
    graph2.addEdge(3, 4);
    graph2.addEdge(4, 5);
    graph2.addEdge(5, 0);
    graph2.addEdge(0, 3);

    graph3.addEdge(0, 1);
    graph3.addEdge(1, 2);
    graph3.addEdge(2, 3);
    graph3.addEdge(3, 4);
    graph3.addEdge(4, 5);
    graph3.addEdge(5, 0);
    graph3.addEdge(0, 4);

    Assertions.assertFalse(graph1.isomorphicTo(graph2));
    Assertions.assertTrue(graph1.isomorphicTo(graph3));
  }

  @Test
  void testDistribution() {
    int n = 5;
//    IsomorphicClassifier<Graph> isoClassifier = new IsomorphicClassifier<>();
    EqualityClassifier<Graph> eqClassifier = new EqualityClassifier<>();

    Iterator<int[]> pruferGenerator = Combinatorics.arrangementsWithRepetitionGenerator(n, n - 2);
    while (pruferGenerator.hasNext()) {
      int[] pruferCode = pruferGenerator.next();
      Graph graph = Graph.ofTree(Tree.ofPruferCode(pruferCode));

      List<Edge> additionalEdges = graph.complement().getEdges();
      int addEdgesCnt = additionalEdges.size();

      Iterator<int[]> filterGenerator = Combinatorics.arrangementsWithRepetitionGenerator(2, addEdgesCnt);

      while (filterGenerator.hasNext()) {
        int[] filter = filterGenerator.next();

        Graph g = Graph.ofEdges(graph.getEdges());
        for (int i = 0; i < addEdgesCnt; ++i) {
          if (filter[i] == 1) {
            Edge e = additionalEdges.get(i);
            g.addEdge(e.u(), e.v());
          }
        }

//        isoClassifier.add(g);
        eqClassifier.add(g);
      }
    }

    System.out.println("isomorphism");
//    System.out.println(isoClassifier.getClassCount());
//    System.out.println(isoClassifier.getClassSizes());
//    System.out.println(isoClassifier.getClassification().get(0).getFirst().getEdges());

    System.out.println("equality");
    System.out.println(eqClassifier.getClassCount());
    System.out.println(eqClassifier.getClassSizes().stream().sorted().toList());
//    System.out.println(eqClassifier.getClassification().get(7).getFirst().getEdges());
  }

  @Test
  void testDistributionWithAdjustableEdgeCnt() {
    int n = 4;
    IsomorphicClassifier<Graph> isoClassifier = new IsomorphicClassifier<>();
    EqualityClassifier<Graph> eqClassifier = new EqualityClassifier<>();
    Map<Integer, Integer> graphCntGroupByAddEdgeCnt = new HashMap<>();

    Iterator<int[]> pruferGenerator = Combinatorics.arrangementsWithRepetitionGenerator(n, n - 2);
    Instant st = Instant.now();
    int i = 0;
    long cnt = Combinatorics.arrangementWithRepetition(n, n - 2);
    while (pruferGenerator.hasNext()) {
      if (i % 50 == 0) {
        System.out.printf("%d/%d (%ss) - ", i, cnt, Duration.between(st, Instant.now()).toSeconds());
        System.out.printf("%d%n", eqClassifier.getClassSizes().stream().sorted().distinct().toList().size());
      }
      i++;
      int[] pruferCode = pruferGenerator.next();
      Graph graph = Graph.ofTree(Tree.ofPruferCode(pruferCode));

      List<Edge> additionalEdges = graph.complement().getEdges();
      int maxAddEdgesCnt = additionalEdges.size();


      IntUnaryOperator getCnt = x -> {
        double c = (Math.pow(cnt, 1 - 2. * x / (n - 1) / (n - 2)));
        c *= Combinatorics.combination(n - 1, x);
        c /= Combinatorics.combination(n, (int) Math.round(1. * x * n / (n - 1)));
//        c = 1;
        return (int) Math.round(c);
      };

      int[] addEdgesCntArr = IntStream
          .rangeClosed(0, maxAddEdgesCnt)
          .mapToObj(x -> new Utils.Pair<>(x, getCnt.applyAsInt(x)))
          .flatMapToInt(p -> IntStream
              .range(0, p.second())
              .map(x -> p.first())
          )
          .toArray();

//      for (int q : addEdgesCntArr) {
//        System.out.print(q + " ");
//        System.out.println((int) (Math.pow(cnt, 1 - 2. * q / (n - 1) / (n - 2))));
//      }
//      System.out.println();

      for (int addEdgesCnt : addEdgesCntArr) {
        if (addEdgesCnt == 0) {
          isoClassifier.add(graph); // for no additional edges
          eqClassifier.add(graph); // for no additional edges
          graphCntGroupByAddEdgeCnt.merge(0, 1, Integer::sum);
          continue;
        }

        Iterator<int[]> edgesCombinationGenerator = Combinatorics.combinationsGenerator(maxAddEdgesCnt, addEdgesCnt);
        while (edgesCombinationGenerator.hasNext()) {
          int[] edgesIndices = edgesCombinationGenerator.next();
          graphCntGroupByAddEdgeCnt.merge(addEdgesCnt, 1, Integer::sum);

          Graph g = Graph.ofEdges(graph.getEdges());
          for (int idx : edgesIndices) {
            Edge e = additionalEdges.get(idx);
            g.addEdge(e.u(), e.v());
          }

          isoClassifier.add(g);
          eqClassifier.add(g);
        }
      }
    }

    System.out.println(graphCntGroupByAddEdgeCnt);
    System.out.println(graphCntGroupByAddEdgeCnt.values().stream().map(x -> x / cnt).toList());
    System.out.println("isomorphism");
    System.out.println(isoClassifier.getClassCount());
    System.out.println(isoClassifier.getClassSizes().stream().sorted().toList());
    System.out.println(isoClassifier.getClassification().get(0).getFirst().getEdges());

    var eqClassSizes = eqClassifier.getClassSizes();
    int eqMinClassSize = eqClassSizes.stream().min(Integer::compare).orElseThrow();
    int eqMaxClassSize = eqClassSizes.stream().max(Integer::compare).orElseThrow();
    double ratio = 1.0 * eqMaxClassSize / eqMinClassSize;

    System.out.println("equality");
    System.out.println(eqClassifier.getClassCount());
//    System.out.println(eqClassSizes.stream().sorted().toList());
    System.out.println(eqClassSizes.stream().sorted().distinct().toList().size());
    System.out.println(eqClassSizes.stream().sorted().distinct().toList());
    System.out.printf("min=%d max=%d ratio=%f%n", eqMinClassSize, eqMaxClassSize, ratio);
//    System.out.println(eqClassifier.getClassification().get(7).getFirst().getEdges());
  }
}
