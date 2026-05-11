package org.reminstant.experiments;

import org.reminstant.math.combinatorics.CombinationFactory;
import org.reminstant.math.graphtheory.hyper.HHTreeCode;
import org.reminstant.math.graphtheory.hyper.HHTreeCodeFactory;
import org.reminstant.math.graphtheory.hyper.HomogenousHypergraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SuppressWarnings("DuplicatedCode")
public class SpanningHypertreeCountExperiment {
  private static final Logger log = LoggerFactory.getLogger(SpanningHypertreeCountExperiment.class);

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    int n = 6;
    int k = 2;
    int mMax = CombinationFactory.ofParams(n, k).count().intValueExact();

    var treeBitsets = HHTreeCodeFactory.ofParams(n, k)
        .sequence()
        .map(HHTreeCode::toTree)
        .map(HomogenousHypergraph::ofTree)
        .map(HomogenousHypergraph::getEdgesBitset)
        .getRemaining();

    int parallelism = 6;

    long[][] tMin = new long[parallelism][mMax + 1];
    long[][] tMax = new long[parallelism][mMax + 1];

    try (var executor = Executors.newFixedThreadPool(parallelism)) {
      List<Future> futures = new ArrayList<>();
      for (int i = 0; i < parallelism; ++i) {
        int j = i;
        futures.add(executor.submit(() -> calc(n, k, tMin[j], tMax[j], treeBitsets, j, parallelism)));
      }

      for (int i = 0; i < parallelism; ++i) {
        futures.get(i).get();
      }

      for (int i = 1; i < parallelism; ++i) {
        for (int m = 0; m <= mMax; ++m) {
          tMin[0][m] = Math.min(tMin[0][m], tMin[i][m]);
          tMax[0][m] = Math.max(tMax[0][m], tMax[i][m]);
        }
      }
    }

    double maxD = 0;
    for (int i = n - 1; i <= mMax; ++i) {
      double d = 1. * tMax[0][i] / tMin[0][i];
      maxD = Math.max(maxD, d);
      log.info("m={}: tMin={} tMax={}, d={}", i, tMin[0][i], tMax[0][i], d);
    }
    log.info("maxD={}", maxD);
  }

  public static void calc(int n, int k, long[] tMin, long[] tMax,
                          List<BitSet> treeBitsets, int shift, int step) {
    int mMax = CombinationFactory.ofParams(n, k).count().intValueExact();
    long hhCount = BigInteger.TWO.pow(mMax).longValueExact();
    for (int i = 0; i < tMin.length; ++i) {
      tMin[i] = Long.MAX_VALUE;
    }

    BitSet bs = new BitSet(tMin.length);
    for (int i = 0; i < shift; ++i) {
      bitSetNext(bs);
    }

    for (int i = shift; i < hhCount; i += step) {
      int m = bs.cardinality();
      if (m >= n - 1) {
        long t = treeBitsets.stream().filter(treeBs -> bitSetContains(bs, treeBs)).count();
        if (t != 0) {
          tMin[m] = Math.min(tMin[m], t);
          tMax[m] = Math.max(tMax[m], t);
        }
      }
      for (int j = 0; j < step; ++j) {
        bitSetNext(bs);
      }
    }
  }

  public static void bitSetNext(BitSet bs) {
    int firstFalse = bs.nextClearBit(0);
    bs.set(firstFalse);
    if (firstFalse > 0) {
      bs.clear(0, firstFalse);
    }
  }

  public static boolean bitSetContains(BitSet a, BitSet b) {
    for (int i = b.nextSetBit(0); i >= 0; i = b.nextSetBit(i + 1)) {
      if (!a.get(i)) {
        return false;
      }
    }
    return true;
  }



}
