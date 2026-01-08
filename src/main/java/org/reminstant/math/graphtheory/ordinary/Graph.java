package org.reminstant.math.graphtheory.ordinary;

import org.reminstant.Validator;
import org.reminstant.math.Combinatorics;
import org.reminstant.math.IsomorphicallyComparable;

import java.util.*;

public class Graph implements IsomorphicallyComparable<Graph> {

  private final int verticesCount;
  List<SortedSet<Integer>> adjacencyList;

  public Graph(int verticesCount) {
    Validator.requireNonLess(verticesCount, 0, "verticesCount");

    this.verticesCount = verticesCount;
    adjacencyList = new ArrayList<>();
    for (int i = 0; i < verticesCount; ++i) {
      adjacencyList.add(new TreeSet<>());
    }
  }

  public static Graph ofEdges(List<Edge> edges) {
    int verticesCount = 1 + edges.stream()
        .map(e -> Math.max(e.u(), e.v()))
        .max(Integer::compareTo)
        .orElse(0);
    Graph graph = new Graph(verticesCount);
    for (Edge e : edges) {
      graph.addEdge(e.u(), e.v());
    }
    return graph;
  }

  public static Graph ofTree(Tree tree) {
    return ofEdges(tree.getEdges());
  }



  public List<Edge> getEdges() {
    List<Edge> edges = new ArrayList<>();
    for (int u = 0; u < verticesCount; ++u) {
      for (int v : adjacencyList.get(u)) {
        if (v > u) {
          edges.add(new Edge(u, v));
        }
      }
    }
    return edges;
  }



  public boolean addEdge(int u, int v) {
    if (u >= verticesCount || v >= verticesCount) {
      throw new IllegalArgumentException("One of vertices is not included in graph");
    }
    adjacencyList.get(u).add(v);
    return adjacencyList.get(v).add(u);
  }



  public Graph complement() {
    Graph graph = new Graph(verticesCount);
    for (int i = 0; i < verticesCount; ++i) {
      for (int j = i + 1; j < verticesCount; ++j) {
        if (!adjacencyList.get(i).contains(j)) {
          graph.addEdge(i, j);
        }
      }
    }
    return graph;
  }

  public Graph difference(Graph otherGraph) {
    List<Edge> res = new ArrayList<>(getEdges());
    res.retainAll(new HashSet<>(otherGraph.getEdges()));
    return Graph.ofEdges(res);
  }



  public boolean hasCycles() {
    byte[] visitFlags = new byte[verticesCount];
    for (int vertex = 0; vertex < verticesCount; ++vertex) {
      if (executeCycleDFS(visitFlags, vertex, -1)) {
        return true;
      }
    }
    return false;
  }

  private boolean executeCycleDFS(byte[] visitFlags, int u, int prev) {
    if (visitFlags[u] > 0) {
      return true;
    }
    visitFlags[u] = 1;
    for (int v : adjacencyList.get(u)) {
      if (v == prev) {
        continue;
      }
      if (executeCycleDFS(visitFlags, v, u)) {
        return true;
      }
    }
    visitFlags[u] = 0;
    return false;
  }



  @Override
  public boolean isomorphicTo(Graph otherGraph) {
    if (equals(otherGraph)) {
      return true;
    }

    Set<Edge> edges = new HashSet<>(getEdges());
    Set<Edge> otherEdges = new HashSet<>(otherGraph.getEdges());
    Iterator<int[]> permutationGenerator = Combinatorics.permutationGenerator(verticesCount);

    while (permutationGenerator.hasNext()) {
      int[] mapping = permutationGenerator.next();
      Set<Edge> mappedEdges = HashSet.newHashSet(edges.size());
      for (Edge e : edges) {
        mappedEdges.add(e.mapBy(mapping));
      }
      if (mappedEdges.equals(otherEdges)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof Graph graph)) return false;

    return verticesCount == graph.verticesCount && Objects.equals(adjacencyList, graph.adjacencyList);
  }

  @Override
  public int hashCode() {
    int result = verticesCount;
    result = 31 * result + Objects.hashCode(adjacencyList);
    return result;
  }

}
