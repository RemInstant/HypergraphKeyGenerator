package org.reminstant.math.graphtheory.hyper;

import org.reminstant.math.graphtheory.PruferCode;
import org.reminstant.structure.Pair;
import org.reminstant.utils.ArrayUtils;
import org.reminstant.utils.IteratorCombiner;
import org.reminstant.utils.IteratorMapper;
import org.reminstant.math.graphtheory.ordinary.Tree;
import org.reminstant.math.Combinatorics;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

public class HomogenousHyperTreeCode implements PruferCode<HomogenousHyperTree> {

  private final int[] partition;
  private final int[] code;
  private final int[] joints;
  private final int edgeDimension;

  private HomogenousHyperTreeCode(int[] partition, int[] code, int[] joints) {
    this.partition = partition;
    this.code = code;
    this.joints = joints;
    this.edgeDimension = partition.length / (code.length + 1) + 1;
  }

  public static BigInteger count(int verticesCount, int edgeDimension) {
    if (edgeDimension < 2 || (verticesCount - 1) % (edgeDimension - 1) != 0) {
      throw new IllegalArgumentException("Trees with n=%d,k=%d do not exist"
          .formatted(verticesCount, edgeDimension));
    }
    if (verticesCount == 0) {
      return BigInteger.ZERO;
    }

    // n = verticesCount
    // k = edgeDimension
    // t = (n-1)/(k-1)
    // P(n,k) - partition count
    // Q(n, k, x) - code with X maximums count
    // R(n, k, x) - joints count for code with X maximums
    // P(n, k)      = prod(i:0..t-1){C(n-1-(k-1)i, k-1)} / t! (set partition for [n-1, t])
    // Q(n, k, x)   = C(t-1, x) * t^(t-1-x)
    // R(n, k, x)   = (k-1)^(t-1-x)
    // Q*R(n, k, x) = C(t-1, x) * (n-1)^(t-1-x)
    // RESULT(n, k) = P(n, k) * sum(i:0..t){Q(n,k,i)R(n,k,i)}

    int partitionLength = verticesCount - 1;
    int blockLength = edgeDimension - 1;
    int blockCount = partitionLength / blockLength;
    BigInteger partitionLengthBig = BigInteger.valueOf(partitionLength);

    BigInteger partitionCount = Combinatorics.setPartitionCount(partitionLength, blockCount);
    BigInteger codeJointsCount = BigInteger.ZERO;

    for (int i = 0; i < blockCount; ++i) {
      codeJointsCount = codeJointsCount.add(Combinatorics
          .combinationCount(blockCount - 1, i)
          .multiply(partitionLengthBig.pow(blockCount - 1 - i)));
    }

    return partitionCount.multiply(codeJointsCount);
  }

  public static Iterator<HomogenousHyperTreeCode> generator(int verticesCount, int edgeDimension) {
    if (edgeDimension < 2 || (verticesCount - 1) % (edgeDimension - 1) != 0) {
      throw new IllegalArgumentException("Trees with n=%d,k=%d do not exist"
          .formatted(verticesCount, edgeDimension));
    }
    if (verticesCount == 0) {
      return new Iterator<>() { // TODO: Generator.of()
        @Override public boolean hasNext() { return false; }
        @Override public HomogenousHyperTreeCode next() { throw new NoSuchElementException(); }
      };
    }
    int partitionLength = verticesCount - 1;
    int blockLength = edgeDimension - 1;
    int blockCount = partitionLength / blockLength;

    Iterator<int[]> partitionGenerator = Combinatorics
        .setPartitionGenerator(verticesCount - 1, blockCount);
    Function<int[], Iterator<int[]>> codeGeneratorFunction = (ignored) -> Combinatorics
        .arrangementWithRepetitionGenerator(blockCount + 1, blockCount - 1);
    Function<Pair<int[],int[]>, Iterator<int[]>> jointsGeneratorFunction = (p) -> {
      int[] code = p.second();
      int nonRootEdgesCount = (int) Arrays.stream(code).filter(v -> v != blockCount).count();
      return Combinatorics.arrangementWithRepetitionGenerator(blockLength, nonRootEdgesCount);
    };

    Iterator<Pair<int[], int[]>> c1 = new IteratorCombiner<>(partitionGenerator, codeGeneratorFunction);
    Iterator<Pair<Pair<int[], int[]>, int[]>> c2 = new IteratorCombiner<>(c1, jointsGeneratorFunction);
    return new IteratorMapper<>(c2, p -> {
      int[] iterPartition = p.first().first();
      int[] iterCode = p.first().second();
      int[] iterJoints = p.second();
      return new HomogenousHyperTreeCode(iterPartition, iterCode, iterJoints);
    });
  }

  public static HomogenousHyperTreeCode ofTree(HomogenousHyperTree tree) {
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

    return new HomogenousHyperTreeCode(partition, code, joints);
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
    if (!(o instanceof HomogenousHyperTreeCode that)) return false;

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
