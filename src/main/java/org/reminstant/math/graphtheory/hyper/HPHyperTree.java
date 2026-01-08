package org.reminstant.math.graphtheory.hyper;

import org.reminstant.utils.ArrayUtils;
import org.reminstant.math.IsomorphicallyComparable;

import java.util.*;

public class HPHyperTree implements IsomorphicallyComparable<HPHyperTree> {

  private final int verticesCount;
  private final Set<HyperEdge> edges;
  private String serializedStructure;


  private HPHyperTree(int verticesCount, Set<HyperEdge> edges) {
    this.verticesCount = verticesCount;
    this.edges = edges;
  }

  public static Builder builder() {
    return new Builder();
  }



  public static class Builder {

    private final Set<HyperEdge> edges;
    private final NavigableSet<Integer> vertices;
    private int edgeDimension;
    private int maxVertex;

    Builder() {
      edges = new HashSet<>();
      vertices = new TreeSet<>();
      edgeDimension = -1;
      maxVertex = -1;
    }

    public int getEdgeDimension() {
      return edgeDimension;
    }

    // One of the vertices must be new while others have to be already included in the tree
    Builder addEdge(int... edgeVertices) {
      if (edgeDimension == -1) {
        HyperEdge e = new HyperEdge(edgeVertices);
        vertices.addAll(Arrays.stream(edgeVertices).boxed().toList());
        edges.add(e);
        edgeDimension = e.dimension();
        maxVertex = e.maxVertex();
        return this;
      }

      if (edgeVertices.length != edgeDimension) {
        throw new IllegalArgumentException(
            "Illegal edge dimension (%d): HP-Hypertree must be homogenous with edge dimension of %d"
                .formatted(edgeVertices.length, edgeDimension));
      }

      HyperEdge e = new HyperEdge(edgeVertices);
      if (edges.contains(e)) {
        return this;
      }


      int oldVerticesCount = 0;
      int newVertex = -1;
      for (int v : edgeVertices) {
        if (vertices.contains(v)) {
          oldVerticesCount++;
        } else {
          vertices.add(v);
          maxVertex = Math.max(maxVertex, v);
          newVertex = v;
        }
      }

//      if (oldVerticesCount != edgeDimension - 1) {
//        throw new IllegalArgumentException("All vertices except one must be already included in the HP-Hypertree.");
//      }

//      vertices.add(newVertex);
      edges.add(e);
//      maxVertex = Math.max(maxVertex, newVertex);

      return this;
    }

    HPHyperTree build() {
      if (canBuild()) {
        return new HPHyperTree(vertices.size(), new HashSet<>(edges));
      }
      throw new IllegalStateException("Vertices are not connected");
    }

    boolean canBuild() {
      return check();
//      return vertices.size() - 1 == maxVertex;
    }

    private boolean check() {
      if (vertices.size() - 1 != maxVertex) {
        return false;
      }

      int[] degrees = new int[vertices.size()];
      Set<HyperEdge> edgesCopy = HashSet.newHashSet(edges.size());
      edgesCopy.addAll(edges);

      for (HyperEdge e : edgesCopy) {
        for (int v : e) {
          degrees[v]++;
        }
      }

      while (edgesCopy.size() > 1) {
        int leaf = ArrayUtils.indexOf(degrees, 1);
        HyperEdge incidentEdge = edgesCopy.stream().filter(e -> e.contains(leaf)).findAny().orElseThrow();
        edgesCopy.remove(incidentEdge);
        for (int v : incidentEdge) {
          if (v != leaf && degrees[v] == 1) {
            return false;
          }
          degrees[v]--;
        }
      }

      return true;
    }
  }


  // TODO:
  public static HPHyperTree ofPruferCode(HPPruferCode pruferCode) {
    int edgeDimension = pruferCode.edgeDimension();
    int[] code = pruferCode.code();
    if (pruferCode.code().length == 0) {
      int[] edgeVertices = new int[edgeDimension];
      for (int i = 0; i < edgeDimension; ++i) {
        edgeVertices[i] = i;
      }
      return new HPHyperTree(edgeDimension, Set.of(new HyperEdge(edgeVertices)));
    }

    int verticesCnt = code.length / (edgeDimension - 1) + edgeDimension;
    if (Arrays.stream(code).max().orElseThrow() >= verticesCnt) {
      throw new IllegalArgumentException("Invalid prufer code " + Arrays.toString(code));
    }

    HPHyperTree.Builder b = builder();

    boolean[] used = new boolean[verticesCnt];
    int[] pruferCnts = new int[verticesCnt];
    for (int k : code) {
      pruferCnts[k]++;
    }

    for (int i = 0; i < code.length; i += edgeDimension - 1) {
      int v = -1;
      for (int j = 0; j < verticesCnt && v == -1; ++j) {
        if (!used[j] && pruferCnts[j] == 0) {
          v = j;
        }
      }

      int[] edgeVertices = new int[edgeDimension];
      for (int j = 1; j < edgeDimension; ++j) {
        edgeVertices[j] = code[i + j - 1];
        pruferCnts[code[i + j - 1]]--;
      }
      edgeVertices[0] = v;
      used[v] = true;
      b.addEdge(edgeVertices);
    }

    int[] lastEdgeVertices = new int[edgeDimension];
    int idx = 0;
    for (int i = 0; i < verticesCnt; ++i) {
      if (!used[i]) {
        lastEdgeVertices[idx] = i;
        idx++;
      }
    }

    b.addEdge(lastEdgeVertices);
    return b.build();
  }



  public int getVerticesCount() {
    return verticesCount;
  }

  public int getEdgesCount() {
    return edges.size();
  }

  public List<HyperEdge> getEdges() {
    return List.copyOf(edges);
  }



  // TODO:
//  public HPPruferCode toPruferCode() {
//    if (edges.isEmpty()) {
//      throw new IllegalStateException("Tree has no edges");
//    }
//
//    int[] degrees = new int[verticesCount];
//    Set<HyperEdge> edgesCopy = HashSet.newHashSet(getEdgesCount());
//    edgesCopy.addAll(edges);
//
//    for (Edge e : edgesCopy) {
//      degrees[e.u()] += 1;
//      degrees[e.v()] += 1;
//    }
//
//    int[] pruferCode = new int[edgesCopy.size() - 1];
//    for (int i = 0; i < pruferCode.length; ++i) {
//      int leaf = ArrayUtils.arrayIndexOf(degrees, 1);
//      Edge incidentEdge = edgesCopy.stream().filter(e -> e.contains(leaf)).findAny().orElseThrow();
//      edgesCopy.remove(incidentEdge);
//
//      int adjacent = incidentEdge.getAdjacent(leaf);
//      degrees[leaf] -= 1;
//      degrees[adjacent] -= 1;
//      pruferCode[i] = adjacent;
//    }
//
//    return pruferCode;
//  }


  // TODO:
  @Override
  public boolean isomorphicTo(HPHyperTree otherTree) {
    if (verticesCount != otherTree.verticesCount) {
      return false;
    }
    return false;

//    String structure = serializeStructure();
//    String otherStructure = otherTree.serializeStructure();

//    return structure.equals(otherStructure);
  }

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof HPHyperTree tree)) {
      return false;
    }

    return edges.equals(tree.edges);
  }

  @Override
  public int hashCode() {
    return edges.hashCode();
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
