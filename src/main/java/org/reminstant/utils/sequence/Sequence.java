package org.reminstant.utils.sequence;

import org.reminstant.structure.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static org.reminstant.utils.sequence.ProxySequence.ofSourceCopy;

public interface Sequence<E> extends Iterator<E> {

  void restart();

  default List<E> getRemaining() {
    List<E> list = new ArrayList<>();
    while (hasNext()) {
      list.add(next());
    }
    return list;
  }

  default <R> Sequence<R> map(Function<E, R> mapper) {
    Sequence<E> parentGenerator = this;
    return new Sequence<>() {
      @Override
      public void restart() {
        parentGenerator.restart();
      }

      @Override
      public boolean hasNext() {
        return parentGenerator.hasNext();
      }

      @Override
      public R next() {
        return mapper.apply(parentGenerator.next());
      }
    };
  }

  default <E2> Sequence<Pair<E,E2>> combine(Sequence<E2> secondaryGenerator) {
    return combine(ignored -> {
      secondaryGenerator.restart();
      return secondaryGenerator;
    });
  }

  default <E2> Sequence<Pair<E,E2>> combine(Function<E, Sequence<E2>> secondaryGeneratorFunction) {
    Sequence<E> mainGenerator = this;
    return new Sequence<>() {
      private E mainValue;
      private Sequence<E2> secondaryGenerator;

      @Override
      public void restart() {
        mainGenerator.restart();
        secondaryGenerator = null;
      }

      @Override
      public boolean hasNext() {
        updateGenerators();
        return secondaryGenerator != null && secondaryGenerator.hasNext();
      }

      @Override
      public Pair<E, E2> next() {
        if (!hasNext()) throw new NoSuchElementException();
        E2 secondValue = secondaryGenerator.next();
        return Pair.of(mainValue, secondValue);
      }

      private void updateGenerators() {
        while (mainGenerator.hasNext()
            && (secondaryGenerator == null || !secondaryGenerator.hasNext())) {
          nextMain();
        }
      }

      private void nextMain() {
        mainValue = mainGenerator.next();
        secondaryGenerator = secondaryGeneratorFunction.apply(mainValue);
      }
    };
  }

  static <E> Sequence<E> ofSource(Collection<E> source) {
    return ofSourceCopy(source);
  }

  static <E> Sequence<E> ofSource(Stream<E> source) {
    return ofSourceCopy(source.toList());
  }

  static <E> Sequence<E> ofSource(E[] source) {
    return ofSourceCopy(Arrays.asList(source));
  }

  static Sequence<Integer> ofSource(int... source) {
    return ofSourceCopy(Arrays.stream(source).boxed().toList());
  }

  static Sequence<Long> ofSource(long... source) {
    return ofSourceCopy(Arrays.stream(source).boxed().toList());
  }

  static Sequence<Double> ofSource(double... source) {
    return ofSourceCopy(Arrays.stream(source).boxed().toList());
  }

  static <E> Sequence<E> ofTransformation(UnaryOperator<E> transformation, E initialData) {
    return new IterateSequence<>(transformation, initialData);
  }
}
