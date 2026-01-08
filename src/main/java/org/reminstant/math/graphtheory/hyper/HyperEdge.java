package org.reminstant.math.graphtheory.hyper;

import org.reminstant.math.graphtheory.EdgeStructure;
import org.reminstant.utils.ArrayUtils;
import org.reminstant.math.Combinatorics;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;

public class HyperEdge implements EdgeStructure {

  private final int[] edgeVertices;

  public HyperEdge(int... vertices) {
    edgeVertices = Arrays.copyOf(vertices, vertices.length);
    Arrays.sort(edgeVertices);
    for (int i = 1; i < vertices.length; ++i) {
      if (edgeVertices[i - 1] == edgeVertices[i]) {
        throw new IllegalArgumentException("Duplicate vertices in edge");
      }
    }
  }


  public static HyperEdge of(int... vertices) {
    return new HyperEdge(vertices);
  }

  // TODO: external indexator
  static HyperEdge ofEdgeIndex(int edgeIndex, int verticesCount, int edgeDimension) {
    return new HyperEdge(Combinatorics
        .getCombinationByOrdinal(verticesCount, edgeDimension, edgeIndex));
  }

  int getEdgeIndex(int verticesCount) {
    return Combinatorics.getCombinationOrdinal(verticesCount, dimension(), edgeVertices).intValueExact();
  }



  public int dimension() {
    return edgeVertices.length;
  }

  public int getVertex(int index) {
    return edgeVertices[index];
  }

  public int maxVertex() {
    return edgeVertices[edgeVertices.length - 1];
  }

  public boolean contains(int vertex) {
    return ArrayUtils.indexOf(edgeVertices, vertex) != -1;
  }



  public HyperEdge mapBy(int[] mapping) {
    int[] mappedEdgeVertices = new int[dimension()];
    for (int i = 0; i < dimension(); ++i) {
      mappedEdgeVertices[i] = mapping[edgeVertices[i]];
    }
    return new HyperEdge(mappedEdgeVertices);
  }



  public IntStream stream() {
    return Arrays.stream(edgeVertices);
  }

  @Override
  public Iterator<Integer> iterator() {
    return stream().iterator();
  }

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof HyperEdge integers)) return false;

    return Arrays.equals(edgeVertices, integers.edgeVertices);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(edgeVertices);
  }

  @Override
  public String toString() {
    return "HyperEdge{" +
        "edgeVertices=" + Arrays.toString(edgeVertices) +
        '}';
  }
}
