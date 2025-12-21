package org.reminstant.experiments;

import org.reminstant.math.Combinatorics;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


public class PruferTest {

  @Test
  void pruferEncodingTest() {
    int[] expectedPruferCode = { 3, 4, 3, 5, 4, 2 };

    Tree tree = Tree.builder()
        .addEdge(0, 3)
        .addEdge(3, 6)
        .addEdge(3, 5)
        .addEdge(5, 4)
        .addEdge(4, 1)
        .addEdge(4, 2)
        .addEdge(2, 7)
        .build();

    int[] pruferCode = tree.toPruferCode();

    Assert.assertEquals(expectedPruferCode, pruferCode);
  }

  @Test
  void pruferEncodingMinimalTest() {
    int[] expectedPruferCode = { };

    Tree tree = Tree.builder()
        .addEdge(0, 1)
        .build();

    int[] pruferCode = tree.toPruferCode();

    Assert.assertEquals(expectedPruferCode, pruferCode);
  }

  @Test
  void pruferEncodingEmptyTest() {
    Tree tree = Tree.builder().build();
    Assert.assertThrows(IllegalStateException.class, tree::toPruferCode);
  }

  @Test
  void pruferDecodingTest() {
    Tree expectedTree = Tree.builder()
        .addEdge(0, 3)
        .addEdge(3, 6)
        .addEdge(3, 5)
        .addEdge(5, 4)
        .addEdge(4, 1)
        .addEdge(4, 2)
        .addEdge(2, 7)
        .build();

    int[] pruferCode = { 3, 4, 3, 5, 4, 2 };
    Tree tree = Tree.ofPruferCode(pruferCode);

    Assert.assertEquals(expectedTree, tree);
  }

  @Test
  void pruferDecodingMinimalTest() {
    Tree expectedTree = Tree.builder()
        .addEdge(0, 1)
        .build();

    int[] pruferCode = { };
    Tree tree = Tree.ofPruferCode(pruferCode);

    Assert.assertEquals(expectedTree, tree);
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
