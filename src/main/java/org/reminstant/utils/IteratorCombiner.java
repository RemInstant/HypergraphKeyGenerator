package org.reminstant.utils;

import org.reminstant.structure.Pair;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class IteratorCombiner<T1, T2> implements Iterator<Pair<T1, T2>> {

  private final Iterator<T1> mainIterator;
  private final Function<T1, Iterator<T2>> secondaryIteratorFunction;

  private Iterator<T2> secondaryIterator;
  private Pair<T1, T2> nextPair;

  public IteratorCombiner(Iterator<T1> mainIterator, Function<T1, Iterator<T2>> secondaryIteratorFunction) {
    this.mainIterator = mainIterator;
    this.secondaryIteratorFunction = secondaryIteratorFunction;
    secondaryIterator = null;
    nextPair = null;

    while (nextPair == null && mainIterator.hasNext()) {
      nextMain();
    }
  }

  @Override
  public boolean hasNext() {
    return nextPair != null;
  }

  @Override
  public Pair<T1, T2> next() {
    if (!hasNext()) throw new NoSuchElementException();
    Pair<T1, T2> curPair = nextPair;
    nextPair = null;

    if (secondaryIterator.hasNext()) {
      T2 secondaryValue = secondaryIterator.next();
      nextPair = Pair.of(curPair.first(), secondaryValue);
    }

    while (nextPair == null && mainIterator.hasNext()) {
      nextMain();
    }

    return curPair;
  }

  private void nextMain() {
    T1 mainValue = mainIterator.next();
    secondaryIterator = secondaryIteratorFunction.apply(mainValue);
    if (secondaryIterator.hasNext()) {
      T2 secondaryValue = secondaryIterator.next();
      nextPair = Pair.of(mainValue, secondaryValue);
    }
  }
}
