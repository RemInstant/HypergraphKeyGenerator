//package org.reminstant.domain;
//
//import org.reminstant.math.graphtheory.hyper.HPHyperTree;
//import org.reminstant.math.graphtheory.hyper.HPPruferCode;
//import org.reminstant.math.EqualityClassifier;
//import org.reminstant.math.Combinatorics;
//import org.testng.annotations.Test;
//
//import java.util.Arrays;
//import java.util.Iterator;
//import java.util.List;
//
//public class HPHyperTreeTest {
//
//  @Test
//  void testEq() {
//    int k = 3;
//    int t = 3;
//    int n = t + k;
//
//    EqualityClassifier<HPHyperTree> eqClassifier = new EqualityClassifier<>();
//    List<int[]> codeElements = Combinatorics.getCombinations(n, k - 1);
//    Iterator<int[]> codeRecipes = Combinatorics.arrangementWithRepetitionGenerator(codeElements.size(), t);
//
//    while (codeRecipes.hasNext()) {
//      int[] codeRecipe = codeRecipes.next();
//      int[] code = new int[(k - 1) * t];
//      for (int i = 0; i < codeRecipe.length; ++i) {
//        int[] elem = codeElements.get(codeRecipe[i]);
//        System.arraycopy(elem, 0, code, i * elem.length, elem.length);
//      }
//
//      try {
//        HPHyperTree tree = HPHyperTree.ofPruferCode(new HPPruferCode(k, null, code));
//        eqClassifier.add(tree);
//      } catch (Exception ex) {
//        System.out.println(Arrays.toString(code));
//        throw ex;
//      }
//    }
//
//    System.out.println(eqClassifier.getClassCount());
//    System.out.println(eqClassifier.getClassSizes());
//  }
//
//}
