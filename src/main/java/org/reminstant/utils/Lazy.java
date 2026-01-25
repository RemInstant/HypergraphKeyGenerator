package org.reminstant.utils;

import java.util.function.Supplier;

public class Lazy<T> {

  private final Supplier<T> supplier;

  private boolean isCalculated;
  private T value;

  private Lazy(Supplier<T> supplier) {
    this.supplier = supplier;
    this.isCalculated = false;
    this.value = null;
  }

  public static <T> Lazy<T> ofSupplier(Supplier<T> supplier) {
    return new Lazy<>(supplier);
  }

  public T get() {
    if (!isCalculated) {
      value = supplier.get();
      isCalculated = true;
    }
    return value;
  }

}
