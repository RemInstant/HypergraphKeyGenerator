package org.reminstant.utils.sequence;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.Function;

class ProxySequence<E> implements Sequence<E> {
  private final Queue<E> elements;
  private final Queue<E> stash;

  static <E> ProxySequence<E> ofSourceCopy(Collection<E> source) {
    return new ProxySequence<>(new ArrayDeque<>(source), new ArrayDeque<>());
  }

  private ProxySequence(Queue<E> source, Queue<E> stash) {
    this.elements = source;
    this.stash = stash;
  }

  @Override
  public boolean hasNext() {
    return !elements.isEmpty();
  }

  @Override
  public E next() {
    if (!hasNext()) throw new NoSuchElementException();
    stash.add(elements.element());
    return elements.poll();
  }

  @Override
  public void restart() {
    stash.addAll(elements);
    elements.clear();
    elements.addAll(stash);
    stash.clear();
  }

  @Override
  public <R> Sequence<R> map(Function<E, R> mapper) {
    Queue<R> mappedElements = new ArrayDeque<>();
    Queue<R> mappedStash = new ArrayDeque<>();

    for (E elem : elements) {
      mappedElements.add(mapper.apply(elem));
    }
    for (E elem : stash) {
      mappedStash.add(mapper.apply(elem));
    }

    return new ProxySequence<>(mappedElements, mappedStash);
  }
}