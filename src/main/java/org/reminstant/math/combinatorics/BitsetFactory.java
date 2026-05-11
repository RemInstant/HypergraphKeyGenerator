package org.reminstant.math.combinatorics;

import org.reminstant.utils.Lazy;
import org.reminstant.utils.sequence.Sequence;

import java.math.BigInteger;
import java.util.BitSet;

import static org.reminstant.math.combinatorics.CombinatoricsUtil.validateCombinatoricsParam;

public class BitsetFactory implements DiscreteObjectFactory<BitSet> {

  private final int n;
  private final Lazy<BigInteger> count;

  private BitsetFactory(int n) {
    this.n = n;
    this.count = Lazy.ofSupplier(() -> calculateCount(n));
  }

  public static BitsetFactory ofBitsCount(int n) {
    validateCombinatoricsParam(n);
    return new BitsetFactory(n);
  }

  public BigInteger count() {
    return count.get();
  }

  public boolean isValid(BitSet bitSet) {
    return bitSet.length() == n;
  }

  public BitSet byOrdinal(BigInteger ordinal) {
    throw new UnsupportedOperationException("not implemented yet");
  }

  public BigInteger toOrdinal(BitSet bitSet) {
    throw new UnsupportedOperationException("not implemented yet");
  }

  public BitSet getNext(BitSet bitSet) {
    throwIfInvalid(bitSet);
    return getNextInner(bitSet);
  }

  public Sequence<BitSet> sequence() {
    return Sequence.ofTransformation(
        this::getNextInner,
        n > 0 ? new BitSet(n) : null);
  }

  

  private static BigInteger calculateCount(int bitsCount) {
    if (bitsCount <= 0) {
      return BigInteger.ZERO;
    }
    return BigInteger.TWO.pow(bitsCount);
  }

  private BitSet getNextInner(BitSet bitset) {
    bitset = (BitSet) bitset.clone();
    for (int i = n - 1; i >= 0; i--) {
      bitset.flip(i);
      if (bitset.get(i)) {
        return bitset;
      }
    }

    return null;
  }

  private void throwIfInvalid(BitSet bitset) {
    if (!isValid(bitset)) {
      throw new IllegalArgumentException("%s is not a Bitset of a size %d"
          .formatted(bitset.toString(), n));
    }
  }
}
