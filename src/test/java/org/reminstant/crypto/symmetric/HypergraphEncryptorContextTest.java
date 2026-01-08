package org.reminstant.crypto.symmetric;

import org.reminstant.crypto.SymmetricEncryptor;
import org.reminstant.crypto.symmetric.HypergraphEncryptor.SmallBlockSizeUnit;
import org.reminstant.crypto.symmetric.provider.EncryptHypergraphDataProvider;
import org.reminstant.math.graphtheory.hyper.HomogenousHypergraph;

import java.util.List;

public class HypergraphEncryptorContextTest extends AbstractContextTest {

  private static final String TEST_DIRECTORY = "src/test/resources/HypergraphEncryptorContextTest";

  HypergraphEncryptorContextTest() {
    super(new EncryptHypergraphDataProvider());
  }

  @Override
  protected String getTestDirectory() {
    return TEST_DIRECTORY;
  }

  @Override
  protected SymmetricEncryptor getEncryptor(Object key) {
    if (key instanceof List<?> keyPair &&
        keyPair.size() == 2 &&
        keyPair.get(0) instanceof HomogenousHypergraph hypergraph &&
        keyPair.get(1) instanceof Integer smallBlockSize) {
      return new HypergraphEncryptor(hypergraph, smallBlockSize, SmallBlockSizeUnit.BIT);
    }
    throw new IllegalArgumentException("Illegal key");
  }
}