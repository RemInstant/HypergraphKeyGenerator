package org.reminstant.experiments;

import org.reminstant.Utils;
import org.reminstant.math.DisjointSetUnion;

import java.util.*;

public class Tree implements IsomorphicallyComparable<Tree> {

  private final int verticesCount;
  private final Set<Edge> edges;
  private String serializedStructure;


  private Tree(int verticesCount, Set<Edge> edges) {
    this.verticesCount = verticesCount;
    this.edges = edges;
  }

  public static Tree.Builder builder() {
    return new Builder();
  }


  public static class Builder {

    private final Set<Edge> edges;
    private final NavigableSet<Integer> vertices;
    private final DisjointSetUnion dsu;

    Builder() {
      edges = new HashSet<>();
      vertices = new TreeSet<>();
      dsu = new DisjointSetUnion(0);
    }

    Builder addEdge(int u, int v) {
      Edge e = new Edge(u, v);
      if (edges.contains(e)) {
        return this;
      }

      dsu.assureSize(1 + Math.max(u, v));

      if (dsu.isUnited(u, v)) {
        throw new IllegalArgumentException("This edge produces a cycle");
      }

      edges.add(e);
      vertices.add(u);
      vertices.add(v);
      dsu.unite(u, v);

      return this;
    }

    Tree build() {
      if (dsu.getComponentsCount() > 1) {
        throw new IllegalStateException("Vertices are not connected");
      }
      return new Tree(vertices.size(), new HashSet<>(edges));
    }

    boolean canBuild() {
      return dsu.getComponentsCount() == 1;
    }
  }


  public static Tree ofPruferCode(int[] pruferCode) {
    if (pruferCode.length == 0) {
      return new Tree(2, Set.of(new Edge(0, 1)));
    }

    int verticesCnt = pruferCode.length + 2;
    if (Arrays.stream(pruferCode).max().orElseThrow() >= verticesCnt) {
      throw new IllegalArgumentException("Invalid prufer code " + Arrays.toString(pruferCode));
    }

    Builder b = builder();

    boolean[] used = new boolean[verticesCnt];
    int[] pruferCnts = new int[verticesCnt];
    for (int k : pruferCode) {
      pruferCnts[k]++;
    }

    for (int u : pruferCode) {
      int v = -1;
      for (int j = 0; j < verticesCnt && v == -1; ++j) {
        if (!used[j] && pruferCnts[j] == 0) {
          v = j;
        }
      }

      used[v] = true;
      pruferCnts[u] -= 1;
      b.addEdge(u, v);
    }

    int lastU = -1;
    int lastV = -1;
    for (int i = 0; i < verticesCnt; ++i) {
      if (!used[i]) {
        lastV = lastU;
        lastU = i;
      }
    }

    b.addEdge(lastU, lastV);
    return b.build();
  }



  public int getVerticesCount() {
    return verticesCount;
  }

  public int getEdgesCount() {
    return edges.size();
  }

  public List<Edge> getEdges() {
    return List.copyOf(edges);
  }



  public int[] toPruferCode() {
    if (edges.isEmpty()) {
      throw new IllegalStateException("Tree has no edges");
    }

    int[] degrees = new int[verticesCount];
    Set<Edge> edgesCopy = HashSet.newHashSet(getEdgesCount());
    edgesCopy.addAll(edges);

    for (Edge e : edgesCopy) {
      degrees[e.u()] += 1;
      degrees[e.v()] += 1;
    }

    int[] pruferCode = new int[edgesCopy.size() - 1];
    for (int i = 0; i < pruferCode.length; ++i) {
      int leaf = Utils.arrayIndexOf(degrees, 1);
      Edge incidentEdge = edgesCopy.stream().filter(e -> e.contains(leaf)).findAny().orElseThrow();
      edgesCopy.remove(incidentEdge);

      int adjacent = incidentEdge.getAdjacent(leaf);
      degrees[leaf] -= 1;
      degrees[adjacent] -= 1;
      pruferCode[i] = adjacent;
    }

    return pruferCode;
  }


  @Override
  public boolean isomorphicTo(Tree otherTree) {
    if (verticesCount != otherTree.verticesCount) {
      return false;
    }

    String structure = serializeStructure();
    String otherStructure = otherTree.serializeStructure();

    return structure.equals(otherStructure);
  }

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof Tree tree)) {
      return false;
    }

    return edges.equals(tree.edges);
  }

  @Override
  public int hashCode() {
    return edges.hashCode();
  }



  private String serializeStructure() {
    if (serializedStructure != null) {
      return serializedStructure;
    }

    int[] centers = getCenters();
    List<String> components = new ArrayList<>();
    if (centers.length == 2) {
      components.add(serializeStructure(centers[0], centers[1]));
      components.add(serializeStructure(centers[1], centers[0]));
    } else {
      components.add(serializeStructure(centers[0], -1));
    }

    Collections.sort(components);
    StringBuilder b = new StringBuilder("[");
    components.forEach(b::append);

    serializedStructure = b.append("]").toString();
    return serializedStructure;
  }

  private String serializeStructure(int center, int skipVertex) {
    List<String> components = new ArrayList<>();
    for (Edge e : edges) {
      if (e.contains(center) && !e.contains(skipVertex)) {
        int vertex = e.getAdjacent(center);
        components.add(serializeStructure(vertex, center));
      }
    }

    Collections.sort(components);
    StringBuilder b = new StringBuilder("(");
    components.forEach(b::append);
    b.append(")");

    return b.toString();
  }

  private int[] getCenters() {
    Map<Integer, Integer> degrees = HashMap.newHashMap(verticesCount);
    Set<Edge> edgesCopy = HashSet.newHashSet(getEdgesCount());
    edgesCopy.addAll(edges);

    for (Edge e : edgesCopy) {
      degrees.merge(e.u(), 1, Integer::sum);
      degrees.merge(e.v(), 1, Integer::sum);
    }

    List<Integer> curLeaves = new ArrayList<>();
    List<Integer> nextLeaves = new ArrayList<>();

    for (Map.Entry<Integer, Integer> entry : degrees.entrySet()) {
      if (entry.getValue() == 1) {
        curLeaves.add(entry.getKey());
      }
    }

    while (edgesCopy.size() > 1) {
      for (int leaf : curLeaves) {
        Edge incidentEdge = edgesCopy.stream().filter(e -> e.contains(leaf)).findAny().orElseThrow();
        edgesCopy.remove(incidentEdge);

        int adjacent = incidentEdge.getAdjacent(leaf);
        degrees.merge(leaf, -1, Integer::sum);
        int adjacentNewDegree = degrees.merge(adjacent, -1, Integer::sum);
        if (adjacentNewDegree == 1) {
          nextLeaves.add(adjacent);
        }

        degrees.remove(leaf);
      }

      List<Integer> tmp = curLeaves;
      curLeaves = nextLeaves;
      nextLeaves = tmp;
      nextLeaves.clear();
    }

    if (edgesCopy.isEmpty()) {
      return new int[]{ degrees.keySet().iterator().next() };
    }

    Edge lastEdge = edgesCopy.iterator().next();
    return new int[]{ lastEdge.u(), lastEdge.v() };
  }
}
