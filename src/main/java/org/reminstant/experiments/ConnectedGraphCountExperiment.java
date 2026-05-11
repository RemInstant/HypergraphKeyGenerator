package org.reminstant.experiments;

import org.reminstant.math.Combinatorics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@SuppressWarnings("DuplicatedCode")
public class ConnectedGraphCountExperiment {
  private static final Logger log = LoggerFactory.getLogger(ConnectedGraphCountExperiment.class);

  public static void main(String[] args) {
    int n = 64;
    int k = 3;

    var a = getHypergraphCountByEdgeCount(n, k, 2 * n);
    var c = getConnectedHypergraphCountByEdgeCount(n, k, 2 * n);

//    BigInteger[] sums = new BigInteger[n + 1];
//    for (int i = 0; i <= n; ++i) {
//      sums[i] = q[i][0];
//      for (int j = 1; j < q[0].length; ++j) {
//        sums[i] = sums[i].add(q[i][j]);
//      }
//    }
//
//    for (int i = 0; i <= n; ++i) {
//      int indexLength = String.valueOf(i).length();
//      log.info("n={}: c1={}", i, connectedCount[i]);
//      log.info("  {}  c2={}", " ".repeat(indexLength), sums[i]);
//    }

    for (int i = 0; i < c[n].length; ++i) {
      if (c[n][i].compareTo(BigInteger.ZERO) == 0) continue;
      if (c[n][i].compareTo(a[n][i]) == 0) continue;
      String nn = " ".repeat(String.valueOf(n).length());
      String mm = " ".repeat(String.valueOf(i).length());
      double ratio = new BigDecimal(c[n][i]).divide(new BigDecimal(a[n][i]), 16, RoundingMode.CEILING).doubleValue();
      if (ratio > 0.75) break;
      log.info("n={} m={}: {}", n, i, ratio);
//      log.info("  {}   {}: {}", nn, mm, c[n][i]);
//      log.info("  {}   {}: {}", nn, mm, a[n][i]);
    }
  }

  public static BigInteger[] getConnectedHomogenousHypergraphCount(int n, int k) {
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
        BigInteger comb = Combinatorics.combinationCount(i - 1, j - 1);
        BigInteger subtractor = dp[j]
            .multiply(pows[i - j])
            .multiply(comb);
        dp[i] = dp[i].subtract(subtractor);
      }
    }

    return dp;
  }

  public static BigInteger[][] getHypergraphCountByEdgeCount(int n, int k, int mCap) {
    int m = (int) Combinatorics.Fast.combinationCount(n, k);
    m = Math.min(m, mCap);

    BigInteger[][] allCount = new BigInteger[n + 1][m + 1];
    for (int i = 0; i <= n; ++i) {
      allCount[i][0] = BigInteger.ONE;
      int q = (int) Combinatorics.Fast.combinationCount(i, k);
      for (int j = 1; j <= m; ++j) {
        allCount[i][j] = Combinatorics.combinationCount(q, j);
      }
    }

    return allCount;
  }

  public static BigInteger[][] getConnectedHypergraphCountByEdgeCount(int n, int k, int mCap) {
    int m = (int) Combinatorics.Fast.combinationCount(n, k);
    m = Math.min(m, mCap);

    BigInteger[][] allCount = getHypergraphCountByEdgeCount(n, k, m);

    BigInteger dp[][] = new BigInteger[n + 1][m + 1];
    for (int i = 0; i <= n; ++i) {
      for (int j = 0; j <= m; ++j) {
        dp[i][j] = BigInteger.ZERO;
      }
    }
    dp[0][0] = dp[1][0] = BigInteger.ONE;

    for (int i = k; i <= n; ++i) {
      int q = (int) Combinatorics.Fast.combinationCount(i, k);
      for (int j = 0; j <= Math.min(q, m); ++j) {
        dp[i][j] = allCount[i][j];

        for (int s = 1; s < i; ++s) {
          BigInteger mult = Combinatorics.combinationCount(i - 1, s - 1);
          for (int t = 0; t <= j; ++t) {
//            log.info("i={}, j={}, s={}, t={}", i, j, s, t);
            BigInteger subtractor = dp[s][t]
                .multiply(allCount[i - s][j - t])
                .multiply(mult);
            dp[i][j] = dp[i][j].subtract(subtractor);
          }
        }
      }
    }

    return dp;
  }


}
