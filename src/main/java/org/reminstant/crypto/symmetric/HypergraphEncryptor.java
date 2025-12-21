package org.reminstant.crypto.symmetric;

import org.reminstant.Validator;
import org.reminstant.crypto.Bits;
import org.reminstant.crypto.SymmetricEncryptor;
import org.reminstant.domain.HomogenousHypergraph;
import org.reminstant.math.MathExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.IntUnaryOperator;

public class HypergraphEncryptor implements SymmetricEncryptor {

  public enum SmallBlockSizeUnit {
    BIT,
    BYTE
  }

  private static final Logger log = LoggerFactory.getLogger(HypergraphEncryptor.class);
  private static final byte BIT_MODE_MASK = (byte) 0b1000_0000;

  private final int smallBlockBitSize;
  private final int blockBitSize;
  private final int batchBlockCount;
  private final int batchByteSize;
  private final int[][] hypergraphAdjacencyLists;

  public HypergraphEncryptor(HomogenousHypergraph key, int smallBlockSize, SmallBlockSizeUnit unit,
                             boolean batchModeWarnEnabled) {
    this(key, smallBlockSize * getUnitBitSize(unit), batchModeWarnEnabled);
  }

  public HypergraphEncryptor(HomogenousHypergraph key, int smallBlockSize, SmallBlockSizeUnit unit) {
    this(key, smallBlockSize * getUnitBitSize(unit), true);
  }

  private HypergraphEncryptor(HomogenousHypergraph key, int smallBlockBitSize,
                              boolean batchModeWarnEnabled) {
    Validator.requireNonNull(key, "key");
    Validator.requirePositive(smallBlockBitSize, "smallBlockBitSize");

    if (smallBlockBitSize > Byte.SIZE && smallBlockBitSize % Byte.SIZE != 0) {
      throw new IllegalArgumentException("Blocks of 1/2/4-bit size are only supported with BIT unit mode");
    }

    this.smallBlockBitSize = smallBlockBitSize;
    this.blockBitSize = smallBlockBitSize * key.getVerticesCount();

    if (blockBitSize % Byte.SIZE == 0) {
      this.batchBlockCount = 1;
    } else {
      if (batchModeWarnEnabled) {
        log.warn("HypergraphEncryptor works in a batch mode");
      }
      this.batchBlockCount = Byte.SIZE / MathExtension.gcd(blockBitSize, Byte.SIZE);
    }

    this.batchByteSize = batchBlockCount * blockBitSize / Byte.SIZE;

    this.hypergraphAdjacencyLists = new int[key.getVerticesCount() - key.getEdgeDimension() + 1][];
    for (int i = 0; i < hypergraphAdjacencyLists.length; ++i) {
      int vertex = i;
      hypergraphAdjacencyLists[i] = key.getVerticesAdjacentTo(vertex)
          .filter(incidentVertex -> incidentVertex > vertex)
          .toArray();
    }
  }

  @Override
  public byte[] encrypt(byte[] data) {
    return transform(data, IntUnaryOperator.identity());
  }

  @Override
  public byte[] decrypt(byte[] data) {
    return transform(data, i -> hypergraphAdjacencyLists.length - 1 - i);
  }

  @Override
  public int getBlockByteSize() {
    return batchByteSize;
  }



  private byte[] transform(byte[] data, IntUnaryOperator vertexSelector) {
    Validator.requireNonNull(data, "data");
    Validator.requireEquals(data.length, batchByteSize, "data.length");

    data = Arrays.copyOf(data, data.length);

    if (batchBlockCount > 1) {
      batchTransform(data, vertexSelector);
    } else if (smallBlockBitSize % Byte.SIZE != 0) {
      bitBlockTransform(data, vertexSelector);
    } else {
      byteBlockTransform(data, vertexSelector);
    }

    return data;
  }

  private void byteBlockTransform(byte[] data, IntUnaryOperator vertexSelector) {
    final int smallBlockByteSize = smallBlockBitSize / Byte.SIZE;
    byte[] tmp = new byte[smallBlockByteSize];

    for (int i = 0; i < hypergraphAdjacencyLists.length; ++i) {
      Arrays.fill(tmp, (byte) 0);
      int vertex = vertexSelector.applyAsInt(i);

      for (int adjacentVertex : hypergraphAdjacencyLists[vertex]) {
        Bits.xorInPlace(tmp, 0, data, smallBlockByteSize * adjacentVertex, smallBlockByteSize);
      }
      if ((hypergraphAdjacencyLists[vertex].length & 1) == 1) {
        Bits.xorInPlace(tmp, 0, data, smallBlockByteSize * vertex, smallBlockByteSize);
      }

      Bits.xorInPlace(data, smallBlockByteSize * vertex, tmp, 0, smallBlockByteSize);
      for (int adjacentVertex : hypergraphAdjacencyLists[vertex]) {
        Bits.xorInPlace(data, smallBlockByteSize * adjacentVertex, tmp, 0, smallBlockByteSize);
      }
    }
  }

  private void bitBlockTransform(byte[] data, IntUnaryOperator vertexSelector) {
    byte[] tmp = new byte[1]; // using only first bit

    for (int i = 0; i < hypergraphAdjacencyLists.length; ++i) {
      Arrays.fill(tmp, (byte) 0);
      int vertex = vertexSelector.applyAsInt(i);

      for (int adjacentVertex : hypergraphAdjacencyLists[vertex]) {
        int resultByteIndex = smallBlockBitSize * adjacentVertex / Byte.SIZE;
        int tmpShift = smallBlockBitSize * adjacentVertex % Byte.SIZE;

        tmp[0] = (byte) (Byte.toUnsignedInt(tmp[0]) >>> tmpShift);
        Bits.xorInPlace(tmp, 0, data, resultByteIndex * adjacentVertex, 1);
        tmp[0] <<= tmpShift;
      }

      int resultCurVertexByteIndex = smallBlockBitSize * vertex / Byte.SIZE;
      int tmpCurVertexShift = smallBlockBitSize * vertex % Byte.SIZE;
      byte tmpCurVertexMask = (byte) (Byte.toUnsignedInt(BIT_MODE_MASK) >>> tmpCurVertexShift);

      tmp[0] = (byte) (Byte.toUnsignedInt(tmp[0]) >>> tmpCurVertexShift);
      if ((hypergraphAdjacencyLists[vertex].length & 1) == 1) {
        Bits.xorInPlace(tmp, 0, data, resultCurVertexByteIndex * vertex, 1);
      }
      tmp[0] &= tmpCurVertexMask;
      Bits.xorInPlace(data, resultCurVertexByteIndex * vertex, tmp, 0, 1);
      tmp[0] <<= tmpCurVertexShift;

      for (int adjacentVertex : hypergraphAdjacencyLists[vertex]) {
        int resultByteIndex = smallBlockBitSize * adjacentVertex / Byte.SIZE;
        int tmpShift = smallBlockBitSize * adjacentVertex % Byte.SIZE;

        tmp[0] = (byte) (Byte.toUnsignedInt(tmp[0]) >>> tmpShift);
        Bits.xorInPlace(data, resultByteIndex * adjacentVertex, tmp, 0, 1);
        tmp[0] <<= tmpShift;
      }
    }
  }


  private void batchTransform(byte[] data, IntUnaryOperator vertexSelector) {
    byte[] block = new byte[(blockBitSize + Byte.SIZE - 1) / Byte.SIZE];
    for (int i = 0; i < batchBlockCount; ++i) {
      Arrays.fill(block, (byte) 0);
      Bits.extractBitsInto(data, (long) i * blockBitSize, block, 0, blockBitSize);
      bitBlockTransform(block, vertexSelector);
      Bits.extractBitsInto(block, 0, data, (long) i * blockBitSize, blockBitSize);
    }
  }

  private static int getUnitBitSize(SmallBlockSizeUnit unit) {
    Validator.requireNonNull(unit, "unit");
    return switch (unit) {
      case BIT -> 1;
      case BYTE -> Byte.SIZE;
    };
  }
}
