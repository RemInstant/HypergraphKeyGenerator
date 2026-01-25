package org.reminstant.utils;

import org.reminstant.structure.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

public interface Generator<E> extends Iterator<E> {

  void restart();

  default List<E> getRemaining() {
    List<E> list = new ArrayList<>();
    while (hasNext()) {
      list.add(next());
    }
    return list;
  }

  default <R> Generator<R> map(Function<E, R> mapper) {
    Generator<E> parentGenerator = this;
    return new Generator<>() {
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

  default <E2> Generator<Pair<E,E2>> combine(Generator<E2> secondaryGenerator) {
    return combine(ignored -> {
      secondaryGenerator.restart();
      return secondaryGenerator;
    });
  }

  default <E2> Generator<Pair<E,E2>> combine(Function<E, Generator<E2>> secondaryGeneratorFunction) {
    Generator<E> mainGenerator = this;
    return new Generator<>() {
      private E mainValue;
      private Generator<E2> secondaryGenerator;

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
}
