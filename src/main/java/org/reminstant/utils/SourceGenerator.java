package org.reminstant.utils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class SourceGenerator<E> implements Generator<E> {

  private final Queue<E> elements;
  private final Queue<E> stash;

  private SourceGenerator(Queue<E> source, Queue<E> stash) {
    this.elements = source;
    this.stash = stash;
  }

  private static <E> SourceGenerator<E> ofSourceCopy(Collection<E> source, Collection<E> stash) {
    return new SourceGenerator<>(new ArrayDeque<>(source), new ArrayDeque<>(stash));
  }

  private static <E> SourceGenerator<E> ofSourceCopy(Collection<E> source) {
    return new SourceGenerator<>(new ArrayDeque<>(source), new ArrayDeque<>());
  }

  public static <E> SourceGenerator<E> ofSource(Collection<E> source) {
    return ofSourceCopy(source);
  }

  public static <E> SourceGenerator<E> ofSource(Stream<E> source) {
    return ofSourceCopy(source.toList());
  }

  public static <E> SourceGenerator<E> ofSource(E[] source) {
    return ofSourceCopy(Arrays.asList(source));
  }

  public static SourceGenerator<Integer> ofSource(int... source) {
    return ofSourceCopy(Arrays.stream(source).boxed().toList());
  }

  public static SourceGenerator<Long> ofSource(long... source) {
    return ofSourceCopy(Arrays.stream(source).boxed().toList());
  }

  public static SourceGenerator<Double> ofSource(double... source) {
    return ofSourceCopy(Arrays.stream(source).boxed().toList());
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
  public <R> Generator<R> map(Function<E, R> mapper) {
    return ofSourceCopy(
        elements.stream().map(mapper).toList(),
        stash.stream().map(mapper).toList());
  }
}
