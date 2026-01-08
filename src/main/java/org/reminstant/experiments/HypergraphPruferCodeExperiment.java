package org.reminstant.experiments;

import org.reminstant.math.graphtheory.hyper.HomogenousHyperTree;
import org.reminstant.math.graphtheory.hyper.HomogenousHyperTreeCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HypergraphPruferCodeExperiment {
  private static final Logger log = LoggerFactory.getLogger(HypergraphPruferCodeExperiment.class);

  public static void main(String[] args) {
    HomogenousHyperTree hyperTree = HomogenousHyperTree.builder()
        .addEdgeUnvalidated(0, 1)
        .build();

    var tCode = HomogenousHyperTreeCode.ofTree(hyperTree);
    var restoredHyperTree = tCode.toTree();

    System.out.println(hyperTree);
    System.out.println(tCode);
    System.out.println(restoredHyperTree);
    if (!hyperTree.equals(restoredHyperTree)) {
      throw new RuntimeException("not equal");
    }

    if (false) {
      return;
    }

    long duplicateCount = 0;
    long totalCount = 0;

    var treeCodeGenerator = HomogenousHyperTreeCode.generator(7, 3);

    System.out.println();
    while (treeCodeGenerator.hasNext()) {
      var treeCode = treeCodeGenerator.next();
      HomogenousHyperTree tree = null;
      HomogenousHyperTree restoredTree = null;
      var restoredTreeCode = treeCode;

      try {
        tree = treeCode.toTree();
        restoredTreeCode = HomogenousHyperTreeCode.ofTree(tree);
//            restoredTree = HomogenousHyperTree.ofPruferCode(restoredTreeCode);
        if (!treeCode.equals(restoredTreeCode)) {
          throw new RuntimeException("not equal");
        }
      } catch (Exception e) {
        log.info("\n{}\n{}\n{}", treeCode, restoredTreeCode, tree, e);
        System.out.println();
        duplicateCount++;
      }
      totalCount++;
    }

//    Iterator<int[]> partitionGenerator = Combinatorics.setPartitionGenerator(6, 3);
//    while (partitionGenerator.hasNext()) {
//      int[] partition = partitionGenerator.next();
//
//      Iterator<int[]> codeGenerator = Combinatorics.arrangementWithRepetitionGenerator(4, 2);
//
//      while (codeGenerator.hasNext()) {
//        int[] code = codeGenerator.next();
//        int c = (int) Arrays.stream(code).filter(v -> v != 3).count();
//
//        Iterator<int[]> jointsGenerator = Combinatorics.arrangementWithRepetitionGenerator(2, c);
////        Iterator<int[]> jointsGenerator = Combinatorics.arrangementWithRepetitionGenerator(2, 3);
//
//        while (jointsGenerator.hasNext()) {
//          int[] joints = jointsGenerator.next();
//
//          HomogenousHyperTree tree = null;
//          HomogenousHyperTree restoredTree = null;
//          var pruferCode = new HomogenousHyperTree.PruferCode(3, partition, code, joints);
//          var restoredPruferCode = pruferCode;
//
//          try {
//            tree = HomogenousHyperTree.ofPruferCode(pruferCode);
//            restoredPruferCode = tree.toPruferCode();
////            restoredTree = HomogenousHyperTree.ofPruferCode(restoredPruferCode);
//            if (!pruferCode.equals(restoredPruferCode)) {
//              throw new RuntimeException("not equal");
//            }
//          } catch (Exception e) {
//            log.info("\n{}\n{}\n{}", pruferCode, restoredPruferCode, tree, e);
//            System.out.println();
//            duplicateCount++;
//          }
//          totalCount++;
//        }
//      }
//    }

    log.info("DUPLICATE COUNT={}", duplicateCount);
    log.info("TOTAL COUNT={}", totalCount);
    log.info("FIXED COUNT={}", totalCount - duplicateCount);
  }
}
