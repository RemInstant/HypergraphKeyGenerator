package org.reminstant.utils;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.random.RandomGenerator;

public class BigIntGenerator implements Generator<BigInteger> {

  private final BigInteger maxExclusive;
  private final RandomGenerator random;
  private final byte[] randomData;
  private final int numBits;
  private final int leadingZeros;
  private final byte firstByteMask;

  public BigIntGenerator(BigInteger maxExclusive, RandomGenerator random) {
    if (maxExclusive.compareTo(BigInteger.ZERO) <= 0) {
      throw new IllegalArgumentException("maxExclusive must be greater than 0");
    }

    this.maxExclusive = maxExclusive;
    this.random = random;
    this.numBits = maxExclusive.subtract(BigInteger.ONE).bitLength();

    int numBytes = (numBits + 7) / 8;
    this.randomData = new byte[numBytes];
    this.leadingZeros = numBytes * 8 - numBits;
    this.firstByteMask = (byte) ((1 << (8 - leadingZeros)) - 1);
  }

  public BigIntGenerator(BigInteger maxExclusive, long seed) {
    this(maxExclusive, new Random(seed));
  }

  public BigIntGenerator(BigInteger maxExclusive) {
    this(maxExclusive, getStrongRandom());
  }


  @Override
  public BigInteger next() {
    if (numBits == 0) {
      return BigInteger.ZERO;
    }
    BigInteger value;
    do {
      random.nextBytes(randomData);
      randomData[0] &= firstByteMask;
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
