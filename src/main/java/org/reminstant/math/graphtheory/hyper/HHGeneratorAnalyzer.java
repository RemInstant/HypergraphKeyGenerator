package org.reminstant.math.graphtheory.hyper;

import org.reminstant.math.Combinatorics;
import org.reminstant.math.combinatorics.CombinationFactory;
import org.reminstant.utils.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class HHGeneratorAnalyzer {

  private static final Logger log = LoggerFactory.getLogger(HHGeneratorAnalyzer.class);

  private final Generator<HomogenousHypergraph> generator;
  private final int generationCount;
  private final Map<HomogenousHypergraph, Integer> counter;
  private final Map<Integer, Integer> counterByEdgeCount;

  public HHGeneratorAnalyzer(Generator<HomogenousHypergraph> generator, int generationCount) {
    this.generator = generator;
    this.generationCount = generationCount;
    this.counter = new HashMap<>();
    this.counterByEdgeCount = new HashMap<>();
  }

  public void analyze(Path outputPath, boolean writeExtraInfo) {
    HomogenousHypergraph hypergraph = generator.next();
    counter.merge(hypergraph, 1, Integer::sum);
    counterByEdgeCount.merge(hypergraph.getEdgeCount(), 1, Integer::sum);

    int verticesCount = hypergraph.getVerticesCount();
    int edgeDimension = hypergraph.getEdgeDimension();
    BigInteger connectedCount = getConnectedHomogenousHypergraphCount(verticesCount, edgeDimension);

    if (connectedCount.compareTo(BigInteger.ZERO) == 0) {
      return;
    }

    for (int i = 1; i < generationCount; ++i) {
      hypergraph = generator.next();
      counter.merge(hypergraph, 1, Integer::sum);
      counterByEdgeCount.merge(hypergraph.getEdgeCount(), 1, Integer::sum);
    }

    BigInteger sum = BigInteger.ZERO;
    for (int value : counter.values()) {
      sum = sum.add(BigInteger.valueOf(value));
    }

    double mean = new BigDecimal(sum)
        .divide(new BigDecimal(connectedCount), 16, RoundingMode.FLOOR)
        .doubleValue();
    double maxError = 0;

    int uniqueMin = Integer.MAX_VALUE;
    int uniqueMax = 0;
    for (int count : counter.values()) {
      double error = Math.abs(1 - count / mean);
//      log.info("Bucket #: {} (error = {})", count, error);
      maxError = Math.max(maxError, error);
      uniqueMin = Math.min(uniqueMin, count);
      uniqueMax = Math.max(uniqueMax, count);
    }

    log.info("Generated {} unique hypergraphs of {} possible", counter.size(), connectedCount);
    log.info("Unique hypergraph counter min: {}", uniqueMin);
    log.info("Unique hypergraph counter max: {}", uniqueMax);
    log.info("Mean count: {}", mean);
    log.info("Max error: {}", maxError);

    if (outputPath == null) {
      return;
    }

    Map<Integer, Integer> uniqueGraphCountBySize = new HashMap<>();
    for (var key : counter.keySet()) {
      uniqueGraphCountBySize.merge(key.getEdgeCount(), 1, Integer::sum);
    }

    try (var writer = Files.newBufferedWriter(outputPath,
        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
      for (var entry : counter.entrySet()) {
        writer.write(String.valueOf(entry.getValue()));
//        writer.write(", ");
//        writer.write(String.valueOf(entry.getKey().getEdgeCount()));
        writer.newLine();
      }
      if (writeExtraInfo) {
        writer.newLine();
        writer.write("Graph entries by size");
        writer.newLine();
        for (var entry : counterByEdgeCount.entrySet()) {
          writer.write(String.valueOf(entry.getKey()));
          writer.write(": ");
          writer.write(String.valueOf(entry.getValue()));
          writer.newLine();
        }
        writer.newLine();
        writer.write("Unique graph count by size");
        writer.newLine();
        for (var entry : uniqueGraphCountBySize.entrySet()) {
          writer.write(String.valueOf(entry.getKey()));
          writer.write(": ");
          writer.write(String.valueOf(entry.getValue()));
          writer.newLine();
        }
        writer.newLine();
        writer.write("Mean entries by size");
        writer.newLine();
        for (var entry : uniqueGraphCountBySize.entrySet()) {
          int total = counterByEdgeCount.get(entry.getKey());
          int count = entry.getValue();
          writer.write(String.valueOf(entry.getKey()));
          writer.write(": ");
          writer.write(String.valueOf(1. * total / count));
          writer.newLine();
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static BigInteger getConnectedHomogenousHypergraphCount(int n, int k) {
    BigInteger[] pows = new BigInteger[n + 1];
    BigInteger[] dp = new BigInteger[n + 1];
    for (int i = 0; i < k; ++i) {
      pows[i] = BigInteger.ONE;
      dp[i] = BigInteger.ZERO;
    }
    dp[0] = dp[1] = BigInteger.ONE;

    for (int i = k; i <= n; ++i) {
      pows[i] = BigInteger.TWO.pow(Math.toIntExact(Combinatorics.Fast.combinationCount(i, k)));
      dp[i] = pows[i];

      for (int j = 1; j < i; ++j) {
        BigInteger comb = CombinationFactory.ofParams(i - 1, j - 1).count();
        BigInteger subtractor = dp[j]
            .multiply(pows[i - j])
            .multiply(comb);
        dp[i] = dp[i].subtract(subtractor);
      }
    }

    return dp[n];
  }
}
