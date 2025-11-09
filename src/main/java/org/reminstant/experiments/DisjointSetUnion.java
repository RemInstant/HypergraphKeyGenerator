package org.reminstant.experiments;

import java.util.Arrays;

public class DisjointSetUnion {

  private int[] leaders;
  private int[] sizes;

  public DisjointSetUnion(int size) {
    leaders = new int[size];
    sizes = new int[size];
    for (int i = 0; i < size; ++i) {
      leaders[i] = i;
      sizes[i] = 1;
    }
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
    leaders[ly] = lx;
    sizes[lx] += sizes[ly];
    return true;
  }

  private int findLeader(int a) {
    if (a >= leaders.length) {
      extend(a + 1);
    }

    if (leaders[a] == a) {
      return a;
    }
    leaders[a] = findLeader(leaders[a]);
    return leaders[a];
  }

  private void extend(int newSize) {
    int oldSize = leaders.length;

    if (newSize < oldSize) {
      throw new IllegalArgumentException("new size must be not less than old one");
    }

    leaders = Arrays.copyOf(leaders, newSize);
    sizes = Arrays.copyOf(sizes, newSize);
    for (int i = oldSize; i < newSize; ++i) {
      leaders[i] = i;
      sizes[i] = 1;
    }
  }

}
