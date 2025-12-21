package org.reminstant.domain;

import org.reminstant.Validator;
import org.reminstant.experiments.IsomorphicallyComparable;
import org.reminstant.math.Combinatorics;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class HomogenousHypergraph implements IsomorphicallyComparable<HomogenousHypergraph> {

  private final int verticesCount;

  private final int edgeDimension;

  private final int edgeMaxCount;

  private final BitSet edges;

  public HomogenousHypergraph(int verticesCount, int edgeDimension) {
    Validator.requireNonLess(verticesCount, 0, "verticesCount");
    Validator.requireNonLess(edgeDimension, 2, "edgeDimension");

    this.verticesCount = verticesCount;
    this.edgeDimension = edgeDimension;
    this.edgeMaxCount = Combinatorics.combinationCount(verticesCount, edgeDimension).intValueExact();
    this.edges = new BitSet(edgeMaxCount);
  }

  public static HomogenousHypergraph ofEdges(HyperEdge... edges) {
    return ofEdges(List.of(edges));
  }

  public static HomogenousHypergraph ofEdges(List<HyperEdge> edges) {
    Validator.requireNonEmpty(edges, "List edges");

    if (edges.stream().map(HyperEdge::dimension).distinct().count() > 1) {
      throw new IllegalArgumentException("Edges has different dimensions");
    }

    int edgeDimension = edges.getFirst().dimension();
    int verticesCount = 1 + edges.stream()
        .map(HyperEdge::maxVertex)
        .max(Integer::compareTo)
        .orElse(0);

    HomogenousHypergraph graph = new HomogenousHypergraph(verticesCount, edgeDimension);
    for (HyperEdge e : edges) {
      graph.addEdge(e);
    }
    return graph;
  }

//  public static org.reminstant.experiments.Graph ofTree(Tree tree) {
//    return ofEdges(tree.getEdges());
//  }

  public int getVerticesCount() {
    return verticesCount;
  }

  public int getEdgeDimension() {
    return edgeDimension;
  }

  public Stream<HyperEdge> getEdges() {
    return edges.stream()
        .mapToObj(idx -> HyperEdge.ofIndex(idx, verticesCount, edgeDimension));
  }

  public Stream<HyperEdge> getEdgesIncidentTo(int vertex) {
    return edges.stream()
        .mapToObj(edgeIndex -> HyperEdge.ofIndex(edgeIndex, verticesCount, edgeDimension))
        .filter(edge -> edge.contains(vertex));
  }

  public IntStream getVerticesAdjacentTo(int vertex) {
    return getEdgesIncidentTo(vertex)
        .flatMapToInt(HyperEdge::stream)
        .distinct()
        .filter(v -> v != vertex);
  }



  public boolean addEdge(HyperEdge edge) {
    if (edge.dimension() != edgeDimension) {
      throw new IllegalArgumentException("Edge dimension must be %d".formatted(edgeDimension));
    }
    if (edge.maxVertex() >= verticesCount) {
      throw new IllegalArgumentException("One of vertices is not included in graph");
    }

    int bitIndex = edge.getIndex(verticesCount);
    if (edges.get(bitIndex)) {
      return false;
    }

    edges.set(bitIndex);
    return true;
  }



  public HomogenousHypergraph complement() {
    HomogenousHypergraph graph = new HomogenousHypergraph(verticesCount, edgeDimension);
    graph.edges.xor(this.edges);
    return graph;
  }

//  public HomogenousHypergraph difference(HomogenousHypergraph otherGraph) {
//    List<Edge> res = new ArrayList<>(getEdges());
//    res.retainAll(new HashSet<>(otherGraph.getEdges()));
//    return org.reminstant.experiments.Graph.ofEdges(res);
//  }



  @Override
  public boolean isomorphicTo(HomogenousHypergraph otherGraph) {
    if (equals(otherGraph)) {
      return true;
    }

    Iterator<int[]> permutationGenerator = Combinatorics.permutationGenerator(verticesCount);

    while (permutationGenerator.hasNext()) {
      int[] mapping = permutationGenerator.next();
      BitSet mappedEdges = new BitSet(edgeMaxCount);
      edges.stream()
          .mapToObj(idx -> HyperEdge.ofIndex(idx, verticesCount, edgeDimension))
          .map(e -> e.mapBy(mapping))
          .mapToInt(e -> e.getIndex(verticesCount))
          .forEach(mappedEdges::set);

      if (mappedEdges.equals(otherGraph.edges)) {
        return true;
      }
    }

    return false;
  }



  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof HomogenousHypergraph that)) return false;

    return verticesCount == that.verticesCount && edgeDimension == that.edgeDimension && edges.equals(that.edges);
  }

  @Override
  public int hashCode() {
    int result = verticesCount;
    result = 31 * result + edgeDimension;
    result = 31 * result + edges.hashCode();
    return result;
  }
}
