package org.reminstant.utils.sequence;

import java.util.NoSuchElementException;
import java.util.function.UnaryOperator;

class IterateSequence<E> implements Sequence<E> {

  private final E initialData;
  private final UnaryOperator<E> transformation;

  private E data;

  IterateSequence(UnaryOperator<E> transformation, E initialData) {
    this.initialData = initialData;
    this.transformation = transformation;
    this.data = initialData;
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
