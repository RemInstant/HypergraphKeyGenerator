package org.reminstant.math.graphtheory;

import org.reminstant.math.graphtheory.hyper.HomogenousHypergraph;

import java.util.ArrayList;
import java.util.List;

public class HypergraphAnalyzer {

  private final double incidenceProportion;
  private final List<Double> incidenceProportionByVertices;

  public HypergraphAnalyzer(HomogenousHypergraph graph) {
    this.incidenceProportionByVertices = new ArrayList<>();

    long verticesCount = graph.getVerticesCount();
    long totalIncidenceCount = 0;
    long maxIncidenceCount = verticesCount * (verticesCount - 1) / 2;

    for (int i = 0; i < verticesCount; ++i) {
      long vertexIncidenceCount = graph.getVerticesAdjacentTo(i).count();
      double vertexIncidenceProportion = 1. * vertexIncidenceCount / (verticesCount - 1);
      incidenceProportionByVertices.add(vertexIncidenceProportion);

      int u = i;
      totalIncidenceCount += graph.getVerticesAdjacentTo(i)
          .filter(v -> v > u)
          .count();
    }

    this.incidenceProportion = 1. * totalIncidenceCount / maxIncidenceCount;
  }

  public double getIncidenceProportion() {
    return incidenceProportion;
  }

  public List<Double> getIncidenceProportionByVertices() {
    return incidenceProportionByVertices;
  }

  public double getIncidenceProportionByVertex(int vertex) {
    return incidenceProportionByVertices.get(vertex);
  }
}
