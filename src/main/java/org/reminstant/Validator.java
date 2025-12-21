package org.reminstant;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Objects;

public class Validator {
  private Validator() { }

  public static void requireNonNull(Object obj, String objName) {
    Objects.requireNonNull(obj, objName + " cannot be null");
  }

  public static void requireNonEmpty(Collection<?> collection, String collectionName) {
    if (collection.isEmpty()) {
      throw new IllegalArgumentException(collectionName + " must be non-empty");
    }
  }

  public static <T> void requireEquals(T value, T valueToCompareWith, String valueName) {
    if (!value.equals(valueToCompareWith)) {
      throw new IllegalArgumentException(valueName + " must be equal to " + valueToCompareWith);
    }
  }

  public static <T extends Number & Comparable<T>> void requirePositive(T value, String valueName) {
    boolean valid = switch (value) {
      case BigInteger bigInteger -> bigInteger.compareTo(BigInteger.ZERO) > 0;
      case Number number -> number.longValue() > 0;
    };
    if (!valid) {
      throw new IllegalArgumentException(valueName + " must be positive");
    }
  }

  public static <T extends Comparable<T>> void requireNonLess(T value, T valueToCompareWith, String valueName) {
    if (value.compareTo(valueToCompareWith) < 0) {
      throw new IllegalArgumentException(valueName + " must be not less than " + valueToCompareWith);
    }
  }

}
