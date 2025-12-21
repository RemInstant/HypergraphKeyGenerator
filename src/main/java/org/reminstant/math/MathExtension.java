package org.reminstant.math;

public class MathExtension {
  private MathExtension() { }

  public static int gcd(int a, int b) {
    while (a > 0) {
      b %= a;
      int t = a;
      a = b;
      b = t;
    }
    return b;
  }

  public static long gcd(long a, long b) {
    while (a > 0) {
      b %= a;
      long t = a;
      a = b;
      b = t;
    }
    return b;
  }
}
