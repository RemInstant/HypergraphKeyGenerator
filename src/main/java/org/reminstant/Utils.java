package org.reminstant;

import java.util.Arrays;

public class Utils {
  private Utils() { }

  public record Pair<T1, T2>(T1 first, T2 second) { }

  public static int arrayIndexOf(int[] array, int elem) {
    for (int j = 0; j < array.length; ++j) {
      if (array[j] == elem) {
        return j;
      }
    }
    return -1;
  }

  public static void arrayReverse(int[] array, int from, int to) {
    if (from > to) {
      throw new IllegalArgumentException(from + " > " + to);
    }

    int left = from;
    int right = to - 1;
    while (left < right) {
      int tmp = array[left];
      array[left] = array[right];
      array[right] = tmp;
      left++;
      right--;
    }
  }

}
