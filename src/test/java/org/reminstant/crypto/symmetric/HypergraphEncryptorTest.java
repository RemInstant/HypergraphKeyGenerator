package org.reminstant.crypto.symmetric;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.reminstant.domain.HomogenousHypergraph;
import org.reminstant.domain.HyperEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.reminstant.crypto.symmetric.HypergraphEncryptor.SmallBlockSizeUnit.BIT;
import static org.reminstant.crypto.symmetric.HypergraphEncryptor.SmallBlockSizeUnit.BYTE;

class HypergraphEncryptorTest {

  private static final Logger log = LoggerFactory.getLogger(HypergraphEncryptorTest.class);

  @ParameterizedTest
  @MethodSource("byteBlockModeProvider")
  void testEncryptWithByteBlockMode(HomogenousHypergraph key, int smallBlockSize,
                                    byte[] message, byte[] expectedCipher) {
    // SETUP
    byte[] originalMessage = Arrays.copyOf(message, message.length);

    // EXECUTION
    var encryptor = new HypergraphEncryptor(key, smallBlockSize, BYTE);
    byte[] actualCipher = encryptor.encrypt(message);

    // ASSERTION
    assertThat(message).containsExactly(originalMessage);
    assertThat(actualCipher).containsExactly(expectedCipher);
  }

  @ParameterizedTest
  @MethodSource("byteBlockModeProvider")
  void testDecryptWithByteBlockMode(HomogenousHypergraph key, int smallBlockSize,
                                    byte[] expectedMessage, byte[] cipher) {
    // SETUP
    byte[] originalCipher = Arrays.copyOf(cipher, cipher.length);

    // EXECUTION
    var encryptor = new HypergraphEncryptor(key, smallBlockSize, BYTE);
    byte[] actualMessage = encryptor.decrypt(cipher);

    // ASSERTION
    assertThat(cipher).containsExactly(originalCipher);
    assertThat(actualMessage).containsExactly(expectedMessage);
  }

  @ParameterizedTest
  @MethodSource("bitBlockModeProvider")
  void testEncryptWithBitBlockMode(HomogenousHypergraph key, int smallBlockSize,
                                   byte[] message, byte[] expectedCipher) {
    // SETUP
    byte[] originalMessage = Arrays.copyOf(message, message.length);

    // EXECUTION
    var encryptor = new HypergraphEncryptor(key, smallBlockSize, BIT);
    byte[] actualCipher = encryptor.encrypt(message);

    // ASSERTION
    assertThat(message).containsExactly(originalMessage);
    assertThat(actualCipher).containsExactly(expectedCipher);
  }

  @ParameterizedTest
  @MethodSource("bitBlockModeProvider")
  void testDecryptWithBitBlockMode(HomogenousHypergraph key, int smallBlockSize,
                                   byte[] expectedMessage, byte[] cipher) {
    // SETUP
    byte[] originalCipher = Arrays.copyOf(cipher, cipher.length);

    // EXECUTION
    var encryptor = new HypergraphEncryptor(key, smallBlockSize, BIT);
    byte[] actualMessage = encryptor.decrypt(cipher);

    // ASSERTION
    assertThat(cipher).containsExactly(originalCipher);
    assertThat(actualMessage).containsExactly(expectedMessage);
  }

  @ParameterizedTest
  @MethodSource("batchBlockModeProvider")
  void testEncryptWithBatchMode(HomogenousHypergraph key, int smallBlockSize,
                                byte[] message, byte[] expectedCipher) {
    // SETUP
    byte[] originalMessage = Arrays.copyOf(message, message.length);

    // EXECUTION
    boolean warn = false;
    HypergraphEncryptor encryptor = new HypergraphEncryptor(key, smallBlockSize, BIT, warn);
    byte[] actualCipher = encryptor.encrypt(message);

    // ASSERTION
    assertThat(message).containsExactly(originalMessage);
    assertThat(actualCipher).containsExactly(expectedCipher);
  }

  @ParameterizedTest
  @MethodSource("batchBlockModeProvider")
  void testDecryptWithBatchMode(HomogenousHypergraph key, int smallBlockSize,
                                byte[] expectedMessage, byte[] cipher) {
    // SETUP
    byte[] originalCipher = Arrays.copyOf(cipher, cipher.length);

    // EXECUTION
    boolean warn = false;
    HypergraphEncryptor encryptor = new HypergraphEncryptor(key, smallBlockSize, BIT, warn);
    byte[] actualMessage = encryptor.decrypt(cipher);

    // ASSERTION
    assertThat(cipher).containsExactly(originalCipher);
    assertThat(actualMessage).containsExactly(expectedMessage);
  }



  static Stream<Arguments> byteBlockModeProvider() {
    return Stream.of(
        Arguments.of(
            HomogenousHypergraph.ofEdges(
                HyperEdge.of(0, 1, 4),
                HyperEdge.of(0, 1, 5),
                HyperEdge.of(1, 2, 3),
                HyperEdge.of(3, 4, 5)
            ),
            1,
            new byte[] {
                (byte) 0x55, (byte) 0x99, (byte) 0xF0, (byte) 0x0F, (byte) 0xCC, (byte) 0xE5,
            },
            new byte[] {
                (byte) 0xB0, (byte) 0xAA, (byte) 0xD9, (byte) 0x0F, (byte) 0xD6, (byte) 0xFF,
            }
        ),
        Arguments.of(
            HomogenousHypergraph.ofEdges(
                HyperEdge.of(0, 1, 4),
                HyperEdge.of(0, 1, 5),
                HyperEdge.of(1, 2, 3),
                HyperEdge.of(3, 4, 5)
            ),
            2,
            new byte[] {
                (byte) 0x55, (byte) 0x55, (byte) 0x99, (byte) 0x99, (byte) 0xF0, (byte) 0xF0,
                (byte) 0x0F, (byte) 0x0F, (byte) 0xCC, (byte) 0xCC, (byte) 0xE5, (byte) 0xE5,
            },
            new byte[] {
                (byte) 0xB0, (byte) 0xB0, (byte) 0xAA, (byte) 0xAA, (byte) 0xD9, (byte) 0xD9,
                (byte) 0x0F, (byte) 0x0F, (byte) 0xD6, (byte) 0xD6, (byte) 0xFF, (byte) 0xFF,
            }
        )
    );
  }

  static Stream<Arguments> bitBlockModeProvider() {
    return Stream.of(
        Arguments.of(
            HomogenousHypergraph.ofEdges(
                HyperEdge.of(2, 3, 6),
                HyperEdge.of(2, 3, 7),
                HyperEdge.of(3, 4, 5),
                HyperEdge.of(5, 6, 7)
            ),
            1,
            new byte[] { (byte) 0b00011011 },
            new byte[] { (byte) 0b00111011 }
        ),
        Arguments.of(
            HomogenousHypergraph.ofEdges(
                HyperEdge.of(2, 3, 6),
                HyperEdge.of(2, 3, 7),
                HyperEdge.of(3, 4, 5),
                HyperEdge.of(5, 6, 7)
            ),
            1,
            new byte[] { (byte) 0b00111000 },
            new byte[] { (byte) 0b00101011 }
        ),
        Arguments.of(
            HomogenousHypergraph.ofEdges(
                HyperEdge.of(2, 3, 6),
                HyperEdge.of(2, 3, 7),
                HyperEdge.of(3, 4, 5),
                HyperEdge.of(5, 6, 7)
            ),
            1,
            new byte[] { (byte) 0b00110101 },
            new byte[] { (byte) 0b00001101 }
        ),
        Arguments.of(
            HomogenousHypergraph.ofEdges(
                HyperEdge.of(2, 3, 6),
                HyperEdge.of(2, 3, 7),
                HyperEdge.of(3, 4, 5),
                HyperEdge.of(5, 6, 7)
            ),
            1,
            new byte[] { (byte) 0b00010110 },
            new byte[] { (byte) 0b00011101 }
        )
    );
  }

  static Stream<Arguments> batchBlockModeProvider() {
    return Stream.of(
        Arguments.of(
            HomogenousHypergraph.ofEdges(
                HyperEdge.of(0, 1, 4),
                HyperEdge.of(0, 1, 5),
                HyperEdge.of(1, 2, 3),
                HyperEdge.of(3, 4, 5)
            ),
            1,
            new byte[] { (byte) 0b01101111, (byte) 0b10001101, (byte) 0b01010110},
            new byte[] { (byte) 0b11101110, (byte) 0b10110011, (byte) 0b01011101}
        )
    );
  }
}
