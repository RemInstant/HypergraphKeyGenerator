package org.reminstant.math;

import java.util.*;

public class EqualityClassifier<T> {

  Map<T, Integer> equalityClasses;

  public EqualityClassifier() {
    equalityClasses = new HashMap<>();
  }

  public void add(T o) {
    equalityClasses.merge(o, 1, Integer::sum);
  }

  public Map<T, Integer> getClassification() {
    return Collections.unmodifiableMap(equalityClasses);
  }

  public int getClassCount() {
    return equalityClasses.size();
  }

  public List<Integer> getClassSizes() {
    return new ArrayList<>(equalityClasses.values());
  }

}
