package org.reminstant.experiments;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class TreeTest {

  @Test
  void treeVerticesTest() {
    Iterable<Integer> expectedVertices = List.of(0, 1, 7, 11);

    Tree tree = new Tree();
    tree.addEdge(0, 1);
    tree.addEdge(1, 7);
    tree.addEdge(7, 11);

    Assertions.assertEquals(4, tree.getSize());
    Assertions.assertIterableEquals(expectedVertices, tree.getVertices());
  }

  @Test
  void treeEdgeCntTest() {
    Tree tree = new Tree();
    boolean add1 = tree.addEdge(0, 1);
    boolean add2 = tree.addEdge(1, 2);
    boolean add3 = tree.addEdge(0, 1);
    boolean add4 = tree.addEdge(2, 1);

    Assertions.assertEquals(2, tree.getEdgesCnt());
    Assertions.assertTrue(add1);
    Assertions.assertTrue(add2);
    Assertions.assertFalse(add3);
    Assertions.assertFalse(add4);
  }

  @Test
  void treeEqualsTest1() {
    Tree tree1 = new Tree();
    Tree tree2 = new Tree();

    tree1.addEdge(0, 1);
    tree1.addEdge(0, 5);
    tree1.addEdge(1, 3);

    tree2.addEdge(0, 1);
    tree2.addEdge(0, 5);
    tree2.addEdge(1, 3);

    Assertions.assertEquals(tree1, tree2);
  }

  @Test
  void treeEqualsTest2() {
    Tree tree1 = new Tree();
    Tree tree2 = new Tree();

    tree1.addEdge(0, 1);
    tree1.addEdge(0, 5);
    tree1.addEdge(1, 3);

    tree2.addEdge(1, 0);
    tree2.addEdge(5, 0);
    tree2.addEdge(3, 1);

    Assertions.assertEquals(tree1, tree2);
  }

  @Test
  void treeIsomorphismTest1() {
    Tree tree1 = new Tree();
    Tree tree2 = new Tree();

    tree1.addEdge(0, 1);
    tree1.addEdge(0, 5);
    tree1.addEdge(1, 3);

    tree2.addEdge(0, 1);
    tree2.addEdge(0, 5);
    tree2.addEdge(1, 3);

    Assertions.assertTrue(tree1.isomorphicTo(tree2));
  }

  @Test
  void treeIsomorphismTest2() {
    Tree tree1 = new Tree();
    Tree tree2 = new Tree();

    tree1.addEdge(0, 1);
    tree1.addEdge(0, 5);
    tree1.addEdge(1, 3);

    tree2.addEdge(1, 0);
    tree2.addEdge(5, 0);
    tree2.addEdge(3, 1);

    Assertions.assertTrue(tree1.isomorphicTo(tree2));
  }

  @Test
  void treeIsomorphismTest3() {
    Tree tree1 = new Tree();
    Tree tree2 = new Tree();

    tree1.addEdge(0, 1);
    tree1.addEdge(1, 2);
    tree1.addEdge(2, 3);

    tree2.addEdge(5, 6);
    tree2.addEdge(6, 7);
    tree2.addEdge(7, 8);

    Assertions.assertTrue(tree1.isomorphicTo(tree2));
  }

  @Test
  void treeIsomorphismTest4() {
    Tree tree1 = new Tree();
    Tree tree2 = new Tree();

    tree1.addEdge(0, 3);
    tree1.addEdge(0, 7);
    tree1.addEdge(0, 6);
    tree1.addEdge(6, 5);
    tree1.addEdge(6, 2);
    tree1.addEdge(2, 10);
    tree1.addEdge(6, 1);
    tree1.addEdge(1, 9);
    tree1.addEdge(1, 4);
    tree1.addEdge(5, 11);
    tree1.addEdge(0, 8);

    tree2.addEdge(9, 10);
    tree2.addEdge(9, 4);
    tree2.addEdge(4, 1);
    tree2.addEdge(1, 7);
    tree2.addEdge(4, 8);
    tree2.addEdge(9, 0);
    tree2.addEdge(8, 3);
    tree2.addEdge(4, 11);
    tree2.addEdge(11, 2);
    tree2.addEdge(11, 5);
    tree2.addEdge(11, 6);

    Assertions.assertTrue(tree1.isomorphicTo(tree2));
  }
}
