package org.reminstant.math;

import java.util.*;

public class IsomorphicClassifier<T extends IsomorphicallyComparable<T>> {

  List<List<T>> isomorphicClasses;

  public IsomorphicClassifier() {
    isomorphicClasses = new ArrayList<>();
  }

  public void add(T o) {
    List<T> dest = isomorphicClasses.stream()
        .filter(clss -> o.isomorphicTo(clss.getFirst()))
        .findAny()
        .orElse(new ArrayList<>());

    if (dest.isEmpty()) {
      isomorphicClasses.add(dest);
    }

    dest.add(o);
  }

  public List<List<T>> getClassification() {
    return Collections.unmodifiableList(isomorphicClasses);
  }

  public int getClassCount() {
    return isomorphicClasses.size();
  }

  public List<Integer> getClassSizes() {
    return isomorphicClasses.stream().map(List::size).toList();
  }

}
