package org.reminstant.experiments;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reminstant.math.Combinatorics;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class PruferTest {

  @Test
  void pruferEncodingTest() {
    int[] expectedPruferCode = { 3, 4, 3, 5, 4, 2 };

    Tree tree = new Tree();
    tree.addEdge(0, 3);
    tree.addEdge(3, 6);
    tree.addEdge(3, 5);
    tree.addEdge(5, 4);
    tree.addEdge(4, 1);
    tree.addEdge(4, 2);
    tree.addEdge(2, 7);

    int[] pruferCode = tree.toPruferCode();

    Assertions.assertArrayEquals(expectedPruferCode, pruferCode);
  }

  @Test
  void pruferEncodingMinimalTest() {
    int[] expectedPruferCode = { };

    Tree tree = new Tree();
    tree.addEdge(0, 1);

    int[] pruferCode = tree.toPruferCode();

    Assertions.assertArrayEquals(expectedPruferCode, pruferCode);
  }

  @Test
  void pruferEncodingEmptyTest() {
    Tree tree = new Tree();
    Assertions.assertThrows(IllegalStateException.class, tree::toPruferCode);
  }

  @Test
  void pruferEncodingNotNormalizedTest() {
    Tree tree = new Tree();
    tree.addEdge(0, 6);
    Assertions.assertThrows(IllegalStateException.class, tree::toPruferCode);
  }


  @Test
  void pruferDecodingTest() {
    Tree expectedTree = new Tree();
    expectedTree.addEdge(0, 3);
    expectedTree.addEdge(3, 6);
    expectedTree.addEdge(3, 5);
    expectedTree.addEdge(5, 4);
    expectedTree.addEdge(4, 1);
    expectedTree.addEdge(4, 2);
    expectedTree.addEdge(2, 7);

    int[] pruferCode = { 3, 4, 3, 5, 4, 2 };
    Tree tree = Tree.ofPruferCode(pruferCode);

    Assertions.assertEquals(expectedTree, tree);
  }

  @Test
  void pruferDecodingMinimalTest() {
    Tree expectedTree = new Tree();
    expectedTree.addEdge(0, 1);

    int[] pruferCode = { };
    Tree tree = Tree.ofPruferCode(pruferCode);

    Assertions.assertEquals(expectedTree, tree);
  }

  @Test
  void pruferBijectionTest() {
    int verticesCnt = 6;
    List<int[]> originPruferCodes = Combinatorics.getArrangementsWithRepetition(verticesCnt, verticesCnt - 2);

    SequencedSet<Tree> trees = new LinkedHashSet<>();
    for (int[] code : originPruferCodes) {
      trees.add(Tree.ofPruferCode(code));
    }

    List<int[]> imagePruferCodes = new ArrayList<>();
    for (Tree tree : trees) {
      imagePruferCodes.add(tree.toPruferCode());
    }

    assertThat(trees)
        .hasSize(originPruferCodes.size());
    assertThat(imagePruferCodes)
        .hasSize(originPruferCodes.size())
        .containsExactlyElementsOf(originPruferCodes);
  }

  @Test
  void isomorphismClassesTest() {
    int verticesCnt = 5;
    List<int[]> originPruferCodes = Combinatorics.getArrangementsWithRepetition(verticesCnt, verticesCnt - 2);

    IsomorphicClassifier<Tree> classifier = new IsomorphicClassifier<>();
    for (int[] code : originPruferCodes) {
      classifier.add(Tree.ofPruferCode(code));
    }

    System.out.println(classifier.getClassCount());
    System.out.println(classifier.getClassSizes());
  }




}
