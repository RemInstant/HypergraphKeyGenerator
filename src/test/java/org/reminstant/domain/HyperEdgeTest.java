//package org.reminstant.domain;
//
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class HyperEdgeTest {
//
//  @Test
//  void testHyperEdgeOfBitIndex() {
//    int n = 5;
//    int k = 3;
//
//    List<HyperEdge> expectedEdges = List.of(
//        new HyperEdge(0, 1, 2), new HyperEdge(0, 1, 3),
//        new HyperEdge(0, 1, 4), new HyperEdge(0, 2, 3),
//        new HyperEdge(0, 2, 4), new HyperEdge(0, 3, 4),
//        new HyperEdge(1, 2, 3), new HyperEdge(1, 2, 4),
//        new HyperEdge(1, 3, 4), new HyperEdge(2, 3, 4)
//    );
//
//    List<HyperEdge> edges = List.of(
//        HyperEdge.ofBitIndex(0, k, n), HyperEdge.ofBitIndex(1, k, n),
//        HyperEdge.ofBitIndex(2, k, n), HyperEdge.ofBitIndex(3, k, n),
//        HyperEdge.ofBitIndex(4, k, n), HyperEdge.ofBitIndex(5, k, n),
//        HyperEdge.ofBitIndex(6, k, n), HyperEdge.ofBitIndex(7, k, n),
//        HyperEdge.ofBitIndex(8, k, n), HyperEdge.ofBitIndex(9, k, n)
//    );
//
//    assertThat(edges)
//        .containsExactlyElementsOf(expectedEdges);
//  }
//
//  @Test
//  void testHyperEdgeToBitIndex() {
//    int n = 5;
//
//    List<Integer> expectedIndices = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
//
//    List<Integer> indices = List.of(
//        new HyperEdge(0, 1, 2).toBitIndex(n), new HyperEdge(0, 1, 3).toBitIndex(n),
//        new HyperEdge(0, 1, 4).toBitIndex(n), new HyperEdge(0, 2, 3).toBitIndex(n),
//        new HyperEdge(0, 2, 4).toBitIndex(n), new HyperEdge(0, 3, 4).toBitIndex(n),
//        new HyperEdge(1, 2, 3).toBitIndex(n), new HyperEdge(1, 2, 4).toBitIndex(n),
//        new HyperEdge(1, 3, 4).toBitIndex(n), new HyperEdge(2, 3, 4).toBitIndex(n)
//    );
//
//    assertThat(indices)
//        .containsExactlyElementsOf(expectedIndices);
//  }
//
//}
