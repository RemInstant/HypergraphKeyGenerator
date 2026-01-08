package org.reminstant.math.graphtheory;

import java.util.Iterator;

public interface TreeEncoder<T extends TreeStructure, C extends PruferCode<T>> {

  C encode(T tree);

  T decode(C treeCode);

  Iterator<C> treeCodeGenerator();
}
