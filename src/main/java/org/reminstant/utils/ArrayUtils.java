package org.reminstant.utils;

public class ArrayUtils {
  private ArrayUtils() { }

  public static int indexOf(int[] array, int elem) {
    for (int j = 0; j < array.length; ++j) {
      if (array[j] == elem) {
        return j;
      }
    }
    return -1;
  }

  public static int nthIndexOf(boolean[] array, boolean elem, int entryNumber) {
    int counter = entryNumber;
    for (int j = 0; j < array.length; ++j) {
      if (array[j] == elem) {
        if (counter == 0) {
          return j;
        } else {
          counter -= 1;
        }
      }
    }
    return -1;
  }

  public static void reverseInPlace(int[] array, int from, int to) {
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

  public static int[] reindex(int[] array, int[] reindexingMap) {
    int[] res = new int[array.length];
    for (int i = 0; i < array.length; ++i) {
      res[i] = array[reindexingMap[i]];
    }
    return res;
  }
}
