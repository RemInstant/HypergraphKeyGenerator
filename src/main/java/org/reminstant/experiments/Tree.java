package org.reminstant.experiments;

import org.reminstant.Utils;

import java.util.*;

public class Tree implements IsomorphicallyComparable<Tree> {

  private final Set<Edge> edges;
  private final NavigableSet<Integer> vertices;
  private final DisjointSetUnion dsu;
  private int ephemeralRoot;

  private String cachedSerializedStructure;


  public static Tree ofPruferCode(int[] pruferCode) {
    if (pruferCode.length == 0) {
      Tree result = new Tree();
      result.addEdge(0, 1);
      return result;
    }

    int size = pruferCode.length + 2;
    if (Arrays.stream(pruferCode).max().orElseThrow() >= size) {
      throw new IllegalArgumentException("Invalid prufer code " + Arrays.toString(pruferCode));
    }

    Tree result = new Tree();
    boolean[] used = new boolean[size];
    int[] pruferCnts = new int[size];
    for (int k : pruferCode) {
      pruferCnts[k]++;
    }

    for (int u : pruferCode) {
      int v = -1;
      for (int j = 0; j < size && v == -1; ++j) {
        if (!used[j] && pruferCnts[j] == 0) {
          v = j;
        }
      }

      used[v] = true;
      pruferCnts[u] -= 1;
      result.addEdgeInternal(u, v);
    }

    int lastU = -1;
    int lastV = -1;
    for (int i = 0; i < size; ++i) {
      if (!used[i]) {
        lastV = lastU;
        lastU = i;
      }
    }

    result.addEdgeInternal(lastU, lastV);
    return result;
  }


  public Tree() {
    edges = new HashSet<>();
    vertices = new TreeSet<>();
    dsu = new DisjointSetUnion(8);
    ephemeralRoot = -1;
    cachedSerializedStructure = "[]";
  }


  public int getSize() {
    return vertices.size();
  }

  public int getEdgesCnt() {
    return edges.size();
  }

  public List<Integer> getVertices() {
    return List.copyOf(vertices);
  }

  public List<Edge> getEdges() {
    return List.copyOf(edges);
  }


  public boolean addEdge(int u, int v) {
    if (edges.contains(new Edge(u, v))) {
      return false;
    }
    if (dsu.isUnited(u, v)) {
      throw new IllegalArgumentException("This edge produces a cycle");
    }
    if (!edges.isEmpty() && !dsu.isUnited(u, ephemeralRoot) && !dsu.isUnited(v, ephemeralRoot)) {
      throw new IllegalArgumentException("This edge produces a new component");
    }

    return addEdgeInternal(u, v);
  }

  private boolean addEdgeInternal(int u, int v) {
    boolean res = edges.add(new Edge(u, v));
    vertices.add(u);
    vertices.add(v);
    dsu.unite(u, v);

    if (ephemeralRoot == -1) {
      ephemeralRoot = u;
    }

    return res;
  }


  public int[] toPruferCode() {
    if (edges.isEmpty()) {
      throw new IllegalStateException("Tree has no edges");
    }
    if (vertices.getLast() + 1 != vertices.size()) {
      throw new IllegalStateException("Tree vertices is not normalized");
    }

    int[] degrees = new int[vertices.size()];
    Set<Edge> edgesCopy = HashSet.newHashSet(getEdgesCnt());
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
    int[] centers = getCenters();
    int[] otherCenters = otherTree.getCenters();

    if (centers.length != otherCenters.length) {
      return false;
    }

    String structure = serializeStructure(centers);
    String otherStructure = otherTree.serializeStructure(otherCenters);

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



  private int[] getCenters() {
    Map<Integer, Integer> degrees = HashMap.newHashMap(vertices.size());
    Set<Edge> edgesCopy = HashSet.newHashSet(getEdgesCnt());
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

  private String serializeStructure(int[] centers) {
    if (cachedSerializedStructure.length() == 2 * vertices.size() + 2) {
      return cachedSerializedStructure;
    }

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
    b.append("]");

    cachedSerializedStructure = b.toString();
    return cachedSerializedStructure;
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
}
