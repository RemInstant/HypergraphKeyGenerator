package org.reminstant.math.graphtheory.hyper;

import org.reminstant.math.graphtheory.ordinary.Graph;
import org.reminstant.structure.DisjointSetUnion;

import java.util.*;

public final class HomogenousHyperTree
    extends AbstractHyperTree
//    implements IsomorphicallyComparable<HomogenousHyperTree>
{

  private HomogenousHyperTree(int verticesCount, Set<HyperEdge> edges) {
    super(verticesCount, edges);
  }

  public static Builder builder() {
    return new Builder();
  }



  public static final class Builder extends AbstractHyperTree.Builder<HomogenousHyperTree> {

    private final DisjointSetUnion dsu;

    public Builder() {
      super(true);
      this.dsu = new DisjointSetUnion();
    }

    @Override
    protected HomogenousHyperTree instantiateHyperTree() {
      return new HomogenousHyperTree(vertices.size(), edges);
    }

    @Override
    protected boolean validateHyperTree() {
      int expectedMaxVertex = vertices.size() - 1;
      if (maxVertex != expectedMaxVertex || dsu.getComponentsCount() != 1) {
        return false;
      }

      int konigGraphVerticesCount = vertices.size() + edges.size();
      Graph konigGraph = new Graph(konigGraphVerticesCount);

      List<HyperEdge> edgesList = new ArrayList<>(edges);

      for (int edgeIndex = 0; edgeIndex < edges.size(); ++edgeIndex) {
        HyperEdge edge = edgesList.get(edgeIndex);
        for (int vertex : edge) {
          konigGraph.addEdge(edgeIndex, edgesList.size() + vertex);
        }
      }

      return !konigGraph.hasCycles();
    }

    @Override
    protected void validateEdge(HyperEdge edge) {
      if (!edges.isEmpty()) {
        int oldVerticesCount = 0;

        for (int vertex : edge) {
          if (vertices.contains(vertex)) {
            oldVerticesCount++;
          }
        }

        if (oldVerticesCount != 1) {
          throw new IllegalArgumentException("For the second edge and next ones all vertices except one must be new");
        }
      }
    }

    @Override
    protected void doAfterAddEdge(HyperEdge edge) {
      dsu.assureSize(edge.maxVertex() + 1);
      for (int i = 0; i < edgeDimension - 1; ++i) {
        dsu.unite(edge.getVertex(i), edge.maxVertex());
      }
    }
  }


//  public static HomogenousHyperTree ofPruferCode(PruferCode pruferCode) {
//    int edgeDimension = pruferCode.edgeDimension();
//    int[] partition = pruferCode.partition();
//    int[] code = pruferCode.code();
//    int[] joints = pruferCode.joints();
//
//    int blockLength = edgeDimension - 1;
//    int edgeCount = partition.length / blockLength;
//
//    Tree blockTree = Tree.ofPruferCode(code);
//    List<SortedSet<Integer>> blockAdjacencyList = blockTree.getAdjacencyListView();
//    int[] adjacentBlockIndices = new int[edgeCount];
//
//    boolean[] visited = new boolean[edgeCount + 1];
//    Queue<Integer> blockIndexQueue = new ArrayDeque<>();
//    blockIndexQueue.add(edgeCount);
//
//    while (!blockIndexQueue.isEmpty()) {
//      int blockIndex = blockIndexQueue.poll();
//      visited[blockIndex] = true;
//
//      for (int adjacentBlockIndex : blockAdjacencyList.get(blockIndex)) {
//        if (visited[adjacentBlockIndex]) {
//          continue;
//        }
//        blockIndexQueue.add(adjacentBlockIndex);
//        adjacentBlockIndices[adjacentBlockIndex] = blockIndex;
//      }
//    }
//
//    int jointsIndex = 0;
//    Builder builder = builder();
//    for (int blockIndex = 0; blockIndex < adjacentBlockIndices.length; ++blockIndex) {
//      int adjacentBlockIndex = adjacentBlockIndices[blockIndex];
//
//      int[] edgeVertices = new int[edgeDimension];
//      System.arraycopy(partition, blockIndex * blockLength, edgeVertices, 0, blockLength);
//
//      int lastElement = partition.length;
//      if (adjacentBlockIndex < edgeCount) {
//        lastElement = partition[adjacentBlockIndex * blockLength + joints[jointsIndex]];
//        jointsIndex++;
//      }
//      edgeVertices[edgeDimension - 1] = lastElement;
//
//      builder.addEdgeUnvalidated(edgeVertices);
//    }
//
//    return builder.build();
//  }

//  public PruferCode toPruferCode() {
//    if (edges.isEmpty()) {
//      throw new IllegalStateException("Tree has no edges");
//    }
//
//    int edgeDimension = edges.stream().findFirst().get().dimension(); // NOSONAR
//    int edgeCount = edges.size();
//
//    List<Pair<int[], Integer>> partitionBlocksWithMarkedVertex = new ArrayList<>();
//    Set<HyperEdge> visited = new HashSet<>();
//    Queue<Integer> vertexQueue = new ArrayDeque<>();
//    vertexQueue.add(verticesCount - 1);
//
//    while (!vertexQueue.isEmpty()) {
//      int vertex = vertexQueue.poll();
//      List<HyperEdge> vertexEdges = edges.stream().filter(e -> e.contains(vertex)).toList();
//
//      for (HyperEdge edge : vertexEdges) {
//        if (visited.contains(edge)) {
//          continue;
//        }
//        visited.add(edge);
//
//        for (int i = 0; i < edgeDimension; ++i) {
//          if (edge.getVertex(i) != vertex) {
//            vertexQueue.add(edge.getVertex(i));
//          }
//        }
//
//        int[] partitionBlock = edge.stream().filter(v -> v != vertex).sorted().toArray();
//        partitionBlocksWithMarkedVertex.add(Pair.of(partitionBlock, vertex));
//      }
//    }
//
//    partitionBlocksWithMarkedVertex.sort(Comparator.comparing(p -> p.first()[0]));
//    List<int[]> partitionBlocks = partitionBlocksWithMarkedVertex.stream().map(Pair::first).toList();
//    List<Integer> markedVertices = partitionBlocksWithMarkedVertex.stream().map(Pair::second).toList();
//
//    int blockLength = edgeDimension - 1;
//    int[] partition = new int[verticesCount - 1];
//    Map<Integer, Integer> vertexToBlockIndex = new HashMap<>();
//    vertexToBlockIndex.put(verticesCount - 1, edgeCount);
//
//    for (int i = 0; i < partitionBlocks.size(); ++i) {
//      int[] block = partitionBlocks.get(i);
//      for (int j = 0; j < blockLength; ++j) {
//        int vertex = block[j];
//        partition[i * blockLength + j] = vertex;
//        vertexToBlockIndex.put(vertex, i);
//      }
//    }
//
//    int nonRootEdgesCount = (int) markedVertices.stream().filter(v -> v != verticesCount - 1).count();
//    int[] joints = new int[nonRootEdgesCount];
//    int jointsIndex = 0;
//    Tree.Builder blockTreeBuilder = Tree.builder();
//
//    for (int blockIndex = 0; blockIndex < markedVertices.size(); ++blockIndex) {
//      int markedVertex = markedVertices.get(blockIndex);
//      int adjacentBlockIndex = vertexToBlockIndex.get(markedVertex);
//      blockTreeBuilder.addEdge(blockIndex, adjacentBlockIndex);
//      if (adjacentBlockIndex < edgeCount) {
//        int[] adjacentBlock = partitionBlocks.get(adjacentBlockIndex);
//        joints[jointsIndex] = ArrayUtils.arrayIndexOf(adjacentBlock, markedVertex);
//        jointsIndex++;
//      }
//    }
//    int[] code = blockTreeBuilder.build().toPruferCode();
//
//    return new PruferCode(edgeDimension, partition, code, joints);
//  }


  // TODO:
//  @Override
//  public boolean isomorphicTo(HPHyperTree otherTree) {
//    if (verticesCount != otherTree.verticesCount) {
//      return false;
//    }
//    return false;

//    String structure = serializeStructure();
//    String otherStructure = otherTree.serializeStructure();

//    return structure.equals(otherStructure);
//  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof HomogenousHyperTree tree)) {
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


  @Override
  public String toString() {
    return "HomogenousHyperTree{" +
        "verticesCount=" + verticesCount +
        ", edges=" + edges +
        '}';
  }

//  public record PruferCode(int edgeDimension,
//                           int[] partition,
//                           int[] code,
//                           int[] joints) {
//    @Override
//    public boolean equals(Object o) {
//      if (!(o instanceof PruferCode(int dim, int[] p, int[] c, int[] j))) return false;
//      return edgeDimension == dim &&
//          Arrays.equals(partition, p) &&
//          Arrays.equals(code, c) &&
//          Arrays.equals(joints, j);
//    }
//
//    @Override
//    public int hashCode() {
//      int result = edgeDimension;
//      result = 31 * result + Arrays.hashCode(partition);
//      result = 31 * result + Arrays.hashCode(code);
//      result = 31 * result + Arrays.hashCode(joints);
//      return result;
//    }
//
//    @Override
//    public String toString() {
//      return "PruferCode{" +
//          "edgeDimension=" + edgeDimension +
//          ", partition=" + Arrays.toString(partition) +
//          ", code=" + Arrays.toString(code) +
//          ", joints=" + Arrays.toString(joints) +
//          '}';
//    }
//  }
}
