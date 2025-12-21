package org.reminstant.crypto.symmetric.provider;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class EncryptHypergraphDataProvider implements EncryptDataProvider {

  @Override
  public Stream<Arguments> provideMessagesAndKeys() {
    return Stream.empty(); // TODO:
  }

  @Override
  public Stream<Arguments> provideMessagesAndKeysAndIVs() {
    return Stream.empty(); // TODO:
  }

  @Override
  public Stream<Arguments> provideMessagesAndKeysAndIVsAndDeltas() {
    return Stream.empty(); // TODO:
  }

  @Override
  public Stream<Arguments> provideKeysAndIVs() {
    return Stream.empty(); // TODO:
  }
}
