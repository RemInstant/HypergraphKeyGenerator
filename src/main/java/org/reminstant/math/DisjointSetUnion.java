package org.reminstant.math;

import java.util.Arrays;

public class DisjointSetUnion {

  private static final int MIN_REAL_SIZE = 8;

  private int logicalSize;
  private int componentsCount;
  private int[] leaders;
  private int[] sizes;

  public DisjointSetUnion(int size) {
    logicalSize = size;
    componentsCount = size;

    int realSize = Math.max(size, MIN_REAL_SIZE);
    leaders = new int[realSize];
    sizes = new int[realSize];
    for (int i = 0; i < logicalSize; ++i) {
      leaders[i] = i;
      sizes[i] = 1;
    }
  }


  public int getSize() {
    return logicalSize;
  }

  public int getComponentsCount() {
    return componentsCount;
  }

  public boolean isUnited(int x, int y) {
    return findLeader(x) == findLeader(y);
  }

  public boolean unite(int x, int y) {
    int lx = findLeader(x);
    int ly = findLeader(y);
    if (lx == ly) {
      return false;
    }
    if (sizes[lx] < sizes[ly]) {
      int tmp = lx;
      lx = ly;
      ly = tmp;
    }
    componentsCount--;
    leaders[ly] = lx;
    sizes[lx] += sizes[ly];
    return true;
  }

  public void assureSize(int size) {
    if (logicalSize < size) {
      extendTo(size);
    }
  }

  public void extendTo(int newSize) {
    int oldLogicalSize = logicalSize;

    if (newSize < oldLogicalSize) {
      throw new IllegalArgumentException("new size must be not less than old one");
    }

    if (newSize > leaders.length) {
      int newRealSize = Math.max(newSize, leaders.length * 2);
      leaders = Arrays.copyOf(leaders, newRealSize);
      sizes = Arrays.copyOf(sizes, newRealSize);
    }

    logicalSize = newSize;
    componentsCount += newSize - oldLogicalSize;
    for (int i = oldLogicalSize; i < newSize; ++i) {
      leaders[i] = i;
      sizes[i] = 1;
    }
  }



  private int findLeader(int a) {
    if (a >= logicalSize) {
      throw new IndexOutOfBoundsException("DSU does not contain elemenent %d".formatted(a));
    }
    if (leaders[a] == a) {
      return a;
    }
    leaders[a] = findLeader(leaders[a]);
    return leaders[a];
  }
}
