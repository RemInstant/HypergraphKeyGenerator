package org.reminstant.utils;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.random.RandomGenerator;

public class BigIntGenerator implements Generator<BigInteger> {

  private final BigInteger maxExclusive;
  private final RandomGenerator random;
  private final byte[] randomData;

  public BigIntGenerator(BigInteger maxExclusive, RandomGenerator random) {
    if (maxExclusive.compareTo(BigInteger.ZERO) <= 0) {
      throw new IllegalArgumentException("maxExclusive must be greater than 0");
    }

    this.maxExclusive = maxExclusive;
    this.random = random;

    byte[] maxIndexAsBytes = maxExclusive.subtract(BigInteger.ONE).toByteArray();
    if (maxIndexAsBytes[0] == 0) {
      maxIndexAsBytes = Arrays.copyOfRange(maxIndexAsBytes, 1, maxIndexAsBytes.length);
    }
    this.randomData = maxIndexAsBytes;
  }

  public BigIntGenerator(BigInteger maxExclusive, long seed) {
    this(maxExclusive, new Random(seed));
  }

  public BigIntGenerator(BigInteger maxExclusive) {
    this(maxExclusive, getStrongRandom());
  }


  @Override
  public BigInteger next() {
    BigInteger value;
    do {
      random.nextBytes(randomData);
      value = new BigInteger(1, randomData);
    } while (value.compareTo(maxExclusive) >= 0);

    return value;
  }


  private static RandomGenerator getStrongRandom() {
    try {
      return SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
