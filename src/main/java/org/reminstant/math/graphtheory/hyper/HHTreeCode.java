package org.reminstant.math.graphtheory.hyper;

import org.reminstant.math.graphtheory.PruferCode;
import org.reminstant.structure.Pair;
import org.reminstant.utils.ArrayUtils;
import org.reminstant.math.graphtheory.ordinary.Tree;

import java.util.*;

public class HHTreeCode implements PruferCode<HomogenousHyperTree> {

  private final int[] partition;
  private final int[] code;
  private final int[] joints;
  private final int edgeDimension;

  HHTreeCode(int[] partition, int[] code, int[] joints) {
    this.partition = partition;
    this.code = code;
    this.joints = joints;
    this.edgeDimension = partition.length / (code.length + 1) + 1;
  }

  // TODO: refactor?
  public static HHTreeCode ofTree(HomogenousHyperTree tree) {
    List<HyperEdge> edges = tree.getEdges();
    int verticesCount = tree.getVerticesCount();
    if (edges.isEmpty()) {
      throw new IllegalStateException("Tree has no edges");
    }

    int edgeDimension = edges.getFirst().dimension();
    int edgeCount = edges.size();

    List<Pair<int[], Integer>> partitionBlocksWithMarkedVertex = new ArrayList<>();
    Set<HyperEdge> visited = new HashSet<>();
    Queue<Integer> vertexQueue = new ArrayDeque<>();
    vertexQueue.add(verticesCount - 1);

    while (!vertexQueue.isEmpty()) {
      int vertex = vertexQueue.poll();
      List<HyperEdge> vertexEdges = edges.stream().filter(e -> e.contains(vertex)).toList();

      for (HyperEdge edge : vertexEdges) {
        if (visited.contains(edge)) {
          continue;
        }
        visited.add(edge);

        for (int i = 0; i < edgeDimension; ++i) {
          if (edge.getVertex(i) != vertex) {
            vertexQueue.add(edge.getVertex(i));
          }
        }

        int[] partitionBlock = edge.stream().filter(v -> v != vertex).sorted().toArray();
        partitionBlocksWithMarkedVertex.add(Pair.of(partitionBlock, vertex));
      }
    }

    partitionBlocksWithMarkedVertex.sort(Comparator.comparing(p -> p.first()[0]));
    List<int[]> partitionBlocks = partitionBlocksWithMarkedVertex.stream().map(Pair::first).toList();
    List<Integer> markedVertices = partitionBlocksWithMarkedVertex.stream().map(Pair::second).toList();

    int blockLength = edgeDimension - 1;
    int[] partition = new int[verticesCount - 1];
    Map<Integer, Integer> vertexToBlockIndex = new HashMap<>();
    vertexToBlockIndex.put(verticesCount - 1, edgeCount);

    for (int i = 0; i < partitionBlocks.size(); ++i) {
      int[] block = partitionBlocks.get(i);
      for (int j = 0; j < blockLength; ++j) {
        int vertex = block[j];
        partition[i * blockLength + j] = vertex;
        vertexToBlockIndex.put(vertex, i);
      }
    }

    int nonRootEdgesCount = (int) markedVertices.stream().filter(v -> v != verticesCount - 1).count();
    int[] joints = new int[nonRootEdgesCount];
    int jointsIndex = 0;
    Tree.Builder blockTreeBuilder = Tree.builder();

    for (int blockIndex = 0; blockIndex < markedVertices.size(); ++blockIndex) {
      int markedVertex = markedVertices.get(blockIndex);
      int adjacentBlockIndex = vertexToBlockIndex.get(markedVertex);
      blockTreeBuilder.addEdge(blockIndex, adjacentBlockIndex);
      if (adjacentBlockIndex < edgeCount) {
        int[] adjacentBlock = partitionBlocks.get(adjacentBlockIndex);
        joints[jointsIndex] = ArrayUtils.indexOf(adjacentBlock, markedVertex);
        jointsIndex++;
      }
    }
    int[] code = blockTreeBuilder.build().toPruferCode();

    return new HHTreeCode(partition, code, joints);
  }

  @Override
  public HomogenousHyperTree toTree() {
    int blockLength = edgeDimension - 1;
    int edgeCount = partition.length / blockLength;

    Tree blockTree = Tree.ofPruferCode(code);
    List<SortedSet<Integer>> blockAdjacencyList = blockTree.getAdjacencyListView();
    int[] adjacentBlockIndices = new int[edgeCount];

    boolean[] visited = new boolean[edgeCount + 1];
    Queue<Integer> blockIndexQueue = new ArrayDeque<>();
    blockIndexQueue.add(edgeCount);

    while (!blockIndexQueue.isEmpty()) {
      int blockIndex = blockIndexQueue.poll();
      visited[blockIndex] = true;

      for (int adjacentBlockIndex : blockAdjacencyList.get(blockIndex)) {
        if (visited[adjacentBlockIndex]) {
          continue;
        }
        blockIndexQueue.add(adjacentBlockIndex);
        adjacentBlockIndices[adjacentBlockIndex] = blockIndex;
      }
    }

    int jointsIndex = 0;
    HomogenousHyperTree.Builder builder = HomogenousHyperTree.builder();
    for (int blockIndex = 0; blockIndex < adjacentBlockIndices.length; ++blockIndex) {
      int adjacentBlockIndex = adjacentBlockIndices[blockIndex];

      int[] edgeVertices = new int[edgeDimension];
      System.arraycopy(partition, blockIndex * blockLength, edgeVertices, 0, blockLength);

      int lastElement = partition.length;
      if (adjacentBlockIndex < edgeCount) {
        lastElement = partition[adjacentBlockIndex * blockLength + joints[jointsIndex]];
        jointsIndex++;
      }
      edgeVertices[edgeDimension - 1] = lastElement;

      builder.addEdgeUnvalidated(edgeVertices);
    }

    return builder.build();
  }

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof HHTreeCode that)) return false;

    return Arrays.equals(partition, that.partition) &&
        Arrays.equals(code, that.code) &&
        Arrays.equals(joints, that.joints);
  }

  @Override
  public int hashCode() {
    int result = Arrays.hashCode(partition);
    result = 31 * result + Arrays.hashCode(code);
    result = 31 * result + Arrays.hashCode(joints);
    return result;
  }

  @Override
  public String toString() {
    return "PruferCode{" +
        "edgeDimension=" + edgeDimension +
        ", partition=" + Arrays.toString(partition) +
        ", code=" + Arrays.toString(code) +
        ", joints=" + Arrays.toString(joints) +
        '}';
  }
}
