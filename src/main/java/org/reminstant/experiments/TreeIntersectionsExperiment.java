package org.reminstant.experiments;

import org.reminstant.math.Combinatorics;
import org.reminstant.math.graphtheory.ordinary.Edge;
import org.reminstant.math.graphtheory.ordinary.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TreeIntersectionsExperiment {
  private static final Logger log = LoggerFactory.getLogger(TreeIntersectionsExperiment.class);

  public static void main(String[] args) {
    int n = 6; // verticesCount

    List<List<Integer>> edgeCombinations = getAllEdgeCombinations(n);
    List<List<Integer>> intersections = getIntersectionsList(edgeCombinations);
    Map<List<Integer>, Integer> groupedIntersections = groupIntersections(intersections);
    Map<Integer, Long> wayCount = getWayCount(n, groupedIntersections);
    Map<Integer, Double> probabilities = getProbabilities(n, wayCount);
    Double mean = getMean(n, wayCount);

    log.info("COUNT = {}", edgeCombinations.size());
    log.info("COMBINATIONS = {}", edgeCombinations);
    log.info("INTERSECTIONS = {}", intersections);
    printIntersectionTable(n, edgeCombinations, intersections, true);
    printMap("groupedIntersections", groupedIntersections, true);
    printMap("wayCount", wayCount, true);
    printMap("probabilities", probabilities, true);
    System.out.printf("MEAN = %f%n", mean);
  }

  private static List<List<Integer>> getAllEdgeCombinations(int n) {
    int edgeCount = Combinatorics.combinationCount(n, 2).intValueExact();
    Map<Integer, Edge> edgeByIndex = IntStream
        .range(0, edgeCount)
        .boxed()
        .collect(Collectors.toMap(
            Function.identity(),
            i -> {
              int[] e = Combinatorics.getCombinationByOrdinal(n, 2, BigInteger.valueOf(i));
              return new Edge(e[0], e[1]);
            }
        ));

    List<List<Integer>> edgeCombinationsList = new ArrayList<>();
    Iterator<int[]> edgeIndicesGenerator = Combinatorics.combinationGenerator(edgeCount, n - 1);
    Tree.Builder builder = Tree.builder();
    while (edgeIndicesGenerator.hasNext()) {
      int[] edgeIndices = edgeIndicesGenerator.next();
      for (int edgeIndex : edgeIndices) {
        builder.addEdgeUnvalidated(edgeByIndex.get(edgeIndex));
      }
      if (builder.canBuild()) {
        edgeCombinationsList.add(IntStream.of(edgeIndices).boxed().toList());
      }
      builder.clear();
    }

    return edgeCombinationsList;
  }

  private static List<List<Integer>> getIntersectionsList(List<List<Integer>> edgeCombinations) {
    List<List<Integer>> intersections = new ArrayList<>();
    for (int i = 0; i < edgeCombinations.size(); ++i) {
      Map<Integer, Integer> map = new HashMap<>();
      List<Integer> c1 = edgeCombinations.get(i);
      for (List<Integer> c2 : edgeCombinations) {
        int count = (int) c2.stream().filter(c1::contains).count();
        map.merge(count, 1, Integer::sum);
      }
      List<Integer> l = new ArrayList<>();
      for (int j = 0; j <= c1.size(); ++j) {
        l.add(map.getOrDefault(j, 0));
      }
      intersections.add(l);
    }
    return intersections;
  }

  private static Map<List<Integer>, Integer> groupIntersections(List<List<Integer>> intersections) {
    Map<List<Integer>, Integer> countMap = new HashMap<>();
    for (List<Integer> i : intersections) {
      countMap.merge(i, 1, Integer::sum);
    }
    return countMap;
  }

  private static Map<Integer, Long> getWayCount(int n, Map<List<Integer>, Integer> groupedIntersections) {
    SortedMap<Integer, Long> wayCount = new TreeMap<>();
    for (int i = 0; i < n; ++i) {
      long c = 0;
      for (var entry : groupedIntersections.entrySet()) {
        long c1 = entry.getKey().get(i);
        long c2 = entry.getValue();
        c += c1 * c2;
      }
      wayCount.put(2 * n - 2 - i, c);
    }
    return wayCount;
  }

  private static Map<Integer, Double> getProbabilities(int n, Map<Integer, Long> wayCount) {
    long div = Combinatorics.arrangementWithRepetitionCount(n, n - 2).longValueExact();
    SortedMap<Integer, Double> probabilites = new TreeMap<>();
    for (var entry : wayCount.entrySet()) {
      probabilites.put(entry.getKey(), 1. * entry.getValue() / div / div);
    }
    return probabilites;
  }

  private static double getMean(int n, Map<Integer, Long> wayCount) {
    long div = Combinatorics.arrangementWithRepetitionCount(n, n - 2).longValueExact();
    double val = 0;
    for (var entry : wayCount.entrySet()) {
      val += entry.getKey() * entry.getValue();
    }
    return val / div / div;
  }

  private static void printIntersectionTable(int n, List<List<Integer>> edgeCombinations,
                                             List<List<Integer>> intersections, boolean newLine) {
    int len1 = 0;
    int len2 = 0;
    for (int i = 0; i < edgeCombinations.size(); ++i) {
      len1 = Math.max(len1, edgeCombinations.get(i).toString().length());
      len2 = Math.max(len2, intersections.get(i).toString().length());
    }
    String sepString = "-".repeat(len1 + len2 + 7);
    for (int i = 0; i < edgeCombinations.size(); ++i) {
      if (i % n == 0) System.out.println(sepString);
      String s1 = edgeCombinations.get(i).toString();
      String s2 = intersections.get(i).toString();
      s1 = " ".repeat(len1 - s1.length()) + s1;
      s2 = " ".repeat(len2 - s2.length()) + s2;
      System.out.println("| " + s1 + " | " + s2 + " |");
    }
    System.out.println(sepString);
    if (newLine) {
      System.out.println();
    }
  }

  private static void printMap(String title, Map<?, ?> groupedIntersections, boolean newLine) {
    int len1 = 0;
    int len2 = 0;
    for (var entry : groupedIntersections.entrySet()) {
      len1 = Math.max(len1, entry.getKey().toString().length());
      len2 = Math.max(len2, entry.getValue().toString().length());
    }
    System.out.println(title);
    for (var entry : groupedIntersections.entrySet()) {
      String s1 = entry.getKey().toString();
      String s2 = entry.getValue().toString();
      s1 = " ".repeat(len1 - s1.length()) + s1;
      s2 = " ".repeat(len2 - s2.length()) + s2;
      System.out.println("| " + s1 + " | " + s2 + " |");
    }
    if (newLine) {
      System.out.println();
    }
  }
}
