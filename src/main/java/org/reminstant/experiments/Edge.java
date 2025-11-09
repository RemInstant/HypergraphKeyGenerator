package org.reminstant.experiments;

public record Edge(int u, int v) {
  public Edge {
    if (u < 0 || v < 0 || u == v) {
      throw new IllegalArgumentException("Invalid edge vertices");
    }
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
