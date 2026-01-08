package org.reminstant.math.graphtheory.ordinary;


import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.*;

public class TreeTest {

  @Test
  void testVerticesCount() {
    Tree tree = Tree.builder()
        .addEdge(0, 1)
        .addEdge(1, 2)
        .addEdge(0, 3)
        .build();

    assertThat(tree.getVerticesCount()).isEqualTo(4);
  }

  @Test
  void testEdgeCount() {
    Tree tree = Tree.builder()
        .addEdge(0, 1)
        .addEdge(1, 2)
        .addEdge(0, 1)
        .addEdge(2, 1)
        .build();

    assertThat(tree.getEdgesCount()).isEqualTo(2);
  }

  @Test
  void testCycleCreating() {
    Tree.Builder builder = Tree.builder()
        .addEdge(0, 1)
        .addEdge(1, 2);

    assertThatIllegalArgumentException()
        .isThrownBy(() -> builder.addEdge(0, 2));
  }

  @Test
  void testBuildingTreeOnNotConnectedNodes() {
    Tree.Builder builder = Tree.builder()
        .addEdge(0, 1)
        .addEdge(2, 3);


    assertThat(builder.canBuild()).isFalse();
    assertThatIllegalStateException()
        .isThrownBy(builder::build);
  }

  @Test
  void testEquals1() {
    Tree tree1 = Tree.builder()
        .addEdge(0, 1)
        .addEdge(0, 2)
        .addEdge(1, 3)
        .build();

    Tree tree2 = Tree.builder()
        .addEdge(0, 1)
        .addEdge(0, 2)
        .addEdge(1, 3)
        .build();

    assertThat(tree1.equals(tree2)).isTrue();
  }

  @Test
  void testEquals2() {
    Tree tree1 = Tree.builder()
        .addEdge(0, 1)
        .addEdge(0, 2)
        .addEdge(1, 3)
        .build();

    Tree tree2 = Tree.builder()
        .addEdge(1, 0)
        .addEdge(2, 0)
        .addEdge(3, 1)
        .build();

    assertThat(tree1.equals(tree2)).isTrue();
  }

  @Test
  void testIsomorphism1() {
    Tree tree1 = Tree.builder()
        .addEdge(0, 1)
        .addEdge(0, 2)
        .addEdge(1, 3)
        .build();

    Tree tree2 = Tree.builder()
        .addEdge(0, 1)
        .addEdge(0, 2)
        .addEdge(1, 3)
        .build();

    assertThat(tree1.isomorphicTo(tree2)).isTrue();
  }

  @Test
  void testIsomorphism2() {
    Tree tree1 = Tree.builder()
        .addEdge(0, 1)
        .addEdge(0, 2)
        .addEdge(1, 3)
        .build();

    Tree tree2 = Tree.builder()
        .addEdge(1, 0)
        .addEdge(2, 0)
        .addEdge(3, 1)
        .build();

    assertThat(tree1.isomorphicTo(tree2)).isTrue();
  }

  @Test
  void testIsomorphism3() {
    Tree tree1 = Tree.builder()
        .addEdge(0, 1)
        .addEdge(0, 2)
        .addEdge(0, 3)
        .build();

    Tree tree2 = Tree.builder()
        .addEdge(2, 0)
        .addEdge(2, 1)
        .addEdge(2, 3)
        .build();

    assertThat(tree1.isomorphicTo(tree2)).isTrue();
  }

  @Test
  void testIsomorphism4() {
    Tree tree1 = Tree.builder()
        .addEdge(0, 3)
        .addEdge(0, 7)
        .addEdge(0, 6)
        .addEdge(6, 5)
        .addEdge(6, 2)
        .addEdge(2, 10)
        .addEdge(6, 1)
        .addEdge(1, 9)
        .addEdge(1, 4)
        .addEdge(5, 11)
        .addEdge(0, 8)
        .build();

    Tree tree2 = Tree.builder()
        .addEdge(9, 10)
        .addEdge(9, 4)
        .addEdge(4, 1)
        .addEdge(1, 7)
        .addEdge(4, 8)
        .addEdge(9, 0)
        .addEdge(8, 3)
        .addEdge(4, 11)
        .addEdge(11, 2)
        .addEdge(11, 5)
        .addEdge(11, 6)
        .build();

    assertThat(tree1.isomorphicTo(tree2)).isTrue();
  }
}
