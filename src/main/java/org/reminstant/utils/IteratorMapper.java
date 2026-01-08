package org.reminstant.utils;

import java.util.Iterator;
import java.util.function.Function;

public class IteratorMapper<T, R> implements Iterator<R> {

  private final Iterator<T> iterator;
  private final Function<T, R> mapper;

  public IteratorMapper(Iterator<T> iterator, Function<T, R> mapper) {
    this.iterator = iterator;
    this.mapper = mapper;
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public R next() {
    return mapper.apply(iterator.next());
  }
}
