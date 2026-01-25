package org.reminstant.utils;

import java.util.NoSuchElementException;
import java.util.function.UnaryOperator;

public class TransformGenerator<E> implements Generator<E> {

  private final E initialData;
  private final UnaryOperator<E> transformation;

  private E data;

  private TransformGenerator(UnaryOperator<E> transformation, E initialData) {
    this.initialData = initialData;
    this.transformation = transformation;
    this.data = initialData;
  }

  public static <E> TransformGenerator<E> of(UnaryOperator<E> transformation, E initialData) {
    return new TransformGenerator<>(transformation, initialData);
  }

  @Override
  public boolean hasNext() {
    return data != null;
  }

  @Override
  public E next() {
    if (!hasNext()) throw new NoSuchElementException();
    E cur = data;
    data = transformation.apply(cur);
    return cur;
  }

  @Override
  public void restart() {
    data = initialData;
  }
}
