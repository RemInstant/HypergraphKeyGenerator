package org.reminstant.math.combinatorics;

import org.reminstant.utils.Generator;

import java.math.BigInteger;

public interface DiscreteObjectFactory<T> {

  BigInteger count();

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  boolean isValid(T object);

  T byOrdinal(BigInteger ordinal);

  default T byOrdinal(long ordinal) {
    return byOrdinal(BigInteger.valueOf(ordinal));
  }

  BigInteger toOrdinal(T object);

  T getNext(T object);

  Generator<T> generator();
}
