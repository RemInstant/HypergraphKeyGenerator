package org.reminstant.math.graphtheory.ordinary;

import org.reminstant.math.graphtheory.EdgeStructure;

import java.util.Iterator;
import java.util.List;

public record Edge(int u, int v) implements EdgeStructure {
  public Edge(int u, int v) {
    if (u < 0 || v < 0 || u == v) {
      throw new IllegalArgumentException("Invalid edge vertices");
    }
    this.u = Math.min(u, v);
    this.v = Math.max(u, v);
  }

  public boolean contains(int x) {
    return x == u || x == v;
  }

  public int getAdjacent(int x) {
    if (x == u) {
      return v;
    }
    if (x == v) {
      return u;
    }
    return -1;
  }

  public Edge mapBy(int[] mapping) {
    return new Edge(mapping[u], mapping[v]);
  }

  @Override
  public Iterator<Integer> iterator() {
    return List.of(u, v).iterator();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Edge(int u1, int v1))) return false;
    return (u == u1 && v == v1) || (u == v1 && v == u1);
  }

  @Override
  public int hashCode() {
    int result = Math.min(u, v);
    result = 31 * result + Math.max(u, v);
    return result;
  }
}
