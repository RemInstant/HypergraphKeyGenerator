package org.reminstant.domain;

import org.reminstant.Utils;
import org.reminstant.math.Combinatorics;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;

public class HyperEdge implements Iterable<Integer> {

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

  static HyperEdge ofIndex(int bitIndex, int verticesCount, int edgeDimension) {
    return new HyperEdge(Combinatorics
        .getCombinationByOrdinal(verticesCount, edgeDimension, bitIndex));
  }

  int getIndex(int verticesCount) {
    return Combinatorics.getCombinationOrdinal(verticesCount, edgeVertices).intValueExact();
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
    return Utils.arrayIndexOf(edgeVertices, vertex) != -1;
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
}
