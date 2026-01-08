package org.reminstant.experiments;

import org.reminstant.crypto.Bits;
import org.reminstant.crypto.SymmetricEncryptor;
import org.reminstant.crypto.symmetric.HypergraphEncryptor;
import org.reminstant.crypto.symmetric.HypergraphEncryptor.SmallBlockSizeUnit;
import org.reminstant.math.graphtheory.hyper.HomogenousHypergraph;
import org.reminstant.math.graphtheory.hyper.HyperEdge;

public class EncryptorExperiment {
  public static void main(String[] args) {
    HomogenousHypergraph hypergraph = HomogenousHypergraph.ofEdges(
        new HyperEdge(0, 1, 2),
        new HyperEdge(0, 1, 3),
        new HyperEdge(0, 1, 4),
        new HyperEdge(0, 2, 3),
        new HyperEdge(0, 2, 4),
        new HyperEdge(1, 2, 3),
        new HyperEdge(1, 2, 4),
        new HyperEdge(1, 3, 4),
        new HyperEdge(2, 3, 4)
    );

    SymmetricEncryptor enc = new HypergraphEncryptor(hypergraph, 1, SmallBlockSizeUnit.BIT);

    byte[] text = new byte[5];
    for (int i = 0; i < 32; ++i) {
      text[0] = (byte) (i << 3);
      byte[] cipher = enc.encrypt(text);
      byte[] decrypted = enc.decrypt(cipher);
      System.out.println(Bits.toBinaryString(text[0]) + " -> "
          + Bits.toBinaryString(cipher[0]) + " -> " + Bits.toBinaryString(decrypted[0]));
    }

  }
}
