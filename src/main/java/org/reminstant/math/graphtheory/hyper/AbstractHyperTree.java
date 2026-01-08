package org.reminstant.math.graphtheory.hyper;

import org.reminstant.math.graphtheory.TreeStructure;

import java.util.*;

public class AbstractHyperTree implements TreeStructure<HyperEdge> {

  protected final int verticesCount;
  protected final Set<HyperEdge> edges;
  private String serializedStructure;


  protected AbstractHyperTree(int verticesCount, Set<HyperEdge> edges) {
    this.verticesCount = verticesCount;
    this.edges = edges;
  }



  public abstract static class Builder<T extends AbstractHyperTree> {

    protected final boolean isHomogenous;
    protected final Set<HyperEdge> edges;
    protected final NavigableSet<Integer> vertices;
    protected int edgeDimension;
    protected int maxVertex;

    protected Builder(boolean isHomogenous) {
      this.isHomogenous = isHomogenous;
      edges = new HashSet<>();
      vertices = new TreeSet<>();
      edgeDimension = -1;
      maxVertex = -1;
    }

    public boolean isHomogenous() {
      return isHomogenous;
    }

    public int getEdgeDimension() {
      return edgeDimension;
    }

    public Builder<T> clear() {
      edges.clear();
      vertices.clear();
      edgeDimension = -1;
      maxVertex = -1;
      return this;
    }

    public Builder<T> addEdge(int... edgeVertices) {
      return addEdgeInternal(true, edgeVertices);
    }

    public Builder<T> addEdgeUnvalidated(int... edgeVertices) {
      return addEdgeInternal(false, edgeVertices);
    }

    public T build() {
      if (canBuild()) {
        return instantiateHyperTree();
      }
      throw new IllegalStateException("Hyper Tree structure is illegal");
    }

    public boolean canBuild() {
      return validateHyperTree();
    }

    protected abstract T instantiateHyperTree();

    protected abstract boolean validateHyperTree();

    protected abstract void validateEdge(HyperEdge edge);

    protected void doAfterAddEdge(HyperEdge edge) {
      // for overriding
    }

    private Builder<T> addEdgeInternal(boolean needValidation, int... edgeVertices) {
      if (edges.isEmpty() && isHomogenous) {
        edgeDimension = edgeVertices.length;
      }

      if (isHomogenous && edgeVertices.length != edgeDimension) {
        throw new IllegalArgumentException("Illegal edge dimension (%d), must be %d"
            .formatted(edgeVertices.length, edgeDimension));
      }

      HyperEdge e = new HyperEdge(edgeVertices);
      if (edges.contains(e)) {
        return this;
      }

      if (needValidation) {
        validateEdge(e);
      }

      edges.add(e);
      vertices.addAll(e.stream().boxed().toList());
      maxVertex = Math.max(maxVertex, e.maxVertex());
      doAfterAddEdge(e);

      return this;
    }
  }

  public int getVerticesCount() {
    return verticesCount;
  }

  public int getEdgesCount() {
    return edges.size();
  }

  // TODO: readOnly list?
  public List<HyperEdge> getEdges() {
    return List.copyOf(edges);
  }

//  private String serializeStructure() {
//    if (serializedStructure != null) {
//      return serializedStructure;
//    }
//
//    int[] centers = getCenters();
//    List<String> components = new ArrayList<>();
//    if (centers.length == 2) {
//      components.add(serializeStructure(centers[0], centers[1]));
//      components.add(serializeStructure(centers[1], centers[0]));
//    } else {
//      components.add(serializeStructure(centers[0], -1));
//    }
//
//    Collections.sort(components);
//    StringBuilder b = new StringBuilder("[");
//    components.forEach(b::append);
//
//    serializedStructure = b.append("]").toString();
//    return serializedStructure;
//  }

//  private String serializeStructure(int center, int skipVertex) {
//    List<String> components = new ArrayList<>();
//    for (Edge e : edges) {
//      if (e.contains(center) && !e.contains(skipVertex)) {
//        int vertex = e.getAdjacent(center);
//        components.add(serializeStructure(vertex, center));
//      }
//    }
//
//    Collections.sort(components);
//    StringBuilder b = new StringBuilder("(");
//    components.forEach(b::append);
//    b.append(")");
//
//    return b.toString();
//  }
//
//  private int[] getCenters() {
//    Map<Integer, Integer> degrees = HashMap.newHashMap(verticesCount);
//    Set<Edge> edgesCopy = HashSet.newHashSet(getEdgesCount());
//    edgesCopy.addAll(edges);
//
//    for (Edge e : edgesCopy) {
//      degrees.merge(e.u(), 1, Integer::sum);
//      degrees.merge(e.v(), 1, Integer::sum);
//    }
//
//    List<Integer> curLeaves = new ArrayList<>();
//    List<Integer> nextLeaves = new ArrayList<>();
//
//    for (Map.Entry<Integer, Integer> entry : degrees.entrySet()) {
//      if (entry.getValue() == 1) {
//        curLeaves.add(entry.getKey());
//      }
//    }
//
//    while (edgesCopy.size() > 1) {
//      for (int leaf : curLeaves) {
//        Edge incidentEdge = edgesCopy.stream().filter(e -> e.contains(leaf)).findAny().orElseThrow();
//        edgesCopy.remove(incidentEdge);
//
//        int adjacent = incidentEdge.getAdjacent(leaf);
//        degrees.merge(leaf, -1, Integer::sum);
//        int adjacentNewDegree = degrees.merge(adjacent, -1, Integer::sum);
//        if (adjacentNewDegree == 1) {
//          nextLeaves.add(adjacent);
//        }
//
//        degrees.remove(leaf);
//      }
//
//      List<Integer> tmp = curLeaves;
//      curLeaves = nextLeaves;
//      nextLeaves = tmp;
//      nextLeaves.clear();
//    }
//
//    if (edgesCopy.isEmpty()) {
//      return new int[]{ degrees.keySet().iterator().next() };
//    }
//
//    Edge lastEdge = edgesCopy.iterator().next();
//    return new int[]{ lastEdge.u(), lastEdge.v() };
//  }
}
