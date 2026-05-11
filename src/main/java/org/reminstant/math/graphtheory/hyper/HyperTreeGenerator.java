package org.reminstant.math.graphtheory.hyper;

import org.reminstant.utils.BigIntGenerator;
import org.reminstant.utils.Generator;

import java.math.BigInteger;
import java.util.random.RandomGenerator;

public class HyperTreeGenerator implements Generator<HomogenousHyperTree> {

  private final HHTreeCodeFactory factory;
  private final BigIntGenerator ordinalGenerator;

  public HyperTreeGenerator(int verticesCount, int edgeDimension, RandomGenerator random) {
    this.factory = HHTreeCodeFactory.ofParams(verticesCount, edgeDimension);
    this.ordinalGenerator = new BigIntGenerator(factory.count(), random);
  }

  public HyperTreeGenerator(int verticesCount, int edgeDimension, long seed) {
    this.factory = HHTreeCodeFactory.ofParams(verticesCount, edgeDimension);
    this.ordinalGenerator = new BigIntGenerator(factory.count(), seed);
  }

  public HyperTreeGenerator(int verticesCount, int edgeDimension) {
    this.factory = HHTreeCodeFactory.ofParams(verticesCount, edgeDimension);
    this.ordinalGenerator = new BigIntGenerator(factory.count());
  }

  @Override
  public HomogenousHyperTree next() {
    BigInteger ordinal = ordinalGenerator.next();
    return factory.byOrdinal(ordinal).toTree();
  }
}
