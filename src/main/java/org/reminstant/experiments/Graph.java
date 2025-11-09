package org.reminstant.experiments;

import org.reminstant.math.Combinatorics;

import java.util.*;

public class Graph implements IsomorphicallyComparable<Graph> {

  private final int size;
  List<SortedSet<Integer>> adjacencyList;

  public Graph(int size) {
    this.size = size;
    adjacencyList = new ArrayList<>();
    for (int i = 0; i < size; ++i) {
      adjacencyList.add(new TreeSet<>());
    }
  }

  public static Graph ofEdges(List<Edge> edges) {
    int size = 1 + edges.stream().map(e -> Math.max(e.u(), e.v())).max(Integer::compareTo).orElse(0);
    Graph graph = new Graph(size);
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
    for (int u = 0; u < size; ++u) {
      for (int v : adjacencyList.get(u)) {
        if (v > u) {
          edges.add(new Edge(u, v));
        }
      }
    }
    return edges;
  }



  public boolean addEdge(int u, int v) {
    if (u >= size || v >= size) {
      throw new IllegalArgumentException("One of vertices is not included in graph");
    }
    adjacencyList.get(u).add(v);
    return adjacencyList.get(v).add(u);
  }



  public Graph complement() {
    Graph graph = new Graph(size);
    for (int i = 0; i < size; ++i) {
      for (int j = i + 1; j < size; ++j) {
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



  @Override
  public boolean isomorphicTo(Graph otherGraph) {
    if (equals(otherGraph)) {
      return true;
    }

    Set<Edge> edges = new HashSet<>(getEdges());
    Set<Edge> otherEdges = new HashSet<>(otherGraph.getEdges());
    Iterator<int[]> permutationGenerator = Combinatorics.permutationGenerator(size);

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

    return size == graph.size && Objects.equals(adjacencyList, graph.adjacencyList);
  }

  @Override
  public int hashCode() {
    int result = size;
    result = 31 * result + Objects.hashCode(adjacencyList);
    return result;
  }

}
