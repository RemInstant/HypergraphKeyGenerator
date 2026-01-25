package org.reminstant.math.graphtheory.hyper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class HHTreeCodeFactoryTest {

//  @ParameterizedTest
//  @CsvSource({
//      "-1, 2",
//      "2, -1",
//      "-1, -1"
//  })
//  void test_initiation_negativeArguments(int verticesCount, int edgeDimension) {
//    assertThatThrownBy(() -> HHTreeCodeFactory.ofParams(verticesCount, edgeDimension))
//        .isInstanceOf(IllegalArgumentException.class);
//  }



  @ParameterizedTest
//  @CsvSource({
//      "3, 2, 3",
//      "4, 2, 16",
//      "5, 2, 125",
//      "7, 2, 16807",
//      "3, 3, 1",
//      "5, 3, 15",
//      "7, 3, 735",
//      "9, 3, 76545",
//      "4, 4, 1",
//      "7, 4, 70",
//      "10, 4, 28000",
//      "5, 5, 1",
//      "9, 5, 315"
//  })
  @MethodSource("configurationProvider")
  void test_count_happyPath(int verticesCount, int edgeDimension, BigInteger expectedCount) {
    BigInteger count = HHTreeCodeFactory.ofParams(verticesCount, edgeDimension).count();

    assertThat(count)
        .isEqualTo(expectedCount);
  }

  @ParameterizedTest
  @MethodSource("configurationProvider")
  void test_byOrdinal_elementsCount(int verticesCount, int edgeDimension, BigInteger count) {
    var factory = HHTreeCodeFactory.ofParams(verticesCount, edgeDimension);
    BigInteger lastElement = count.subtract(BigInteger.ONE);

    assertThatNoException()
        .isThrownBy(() -> factory.byOrdinal(lastElement));
    assertThatException()
        .isThrownBy(() -> factory.byOrdinal(count))
        .isInstanceOf(NoSuchElementException.class);
  }

  @ParameterizedTest
  @MethodSource("configurationProvider")
  void test_byOrdinal_elementUniqueness(int verticesCount, int edgeDimension) {
    var factory = HHTreeCodeFactory.ofParams(verticesCount, edgeDimension);
    int count = factory.count().intValueExact();

    List<HHTreeCode> codes = new ArrayList<>();
    for (int i = 0; i < count; ++i) {
      codes.add(factory.byOrdinal(BigInteger.valueOf(i)));
    }

    assertThat(codes)
        .doesNotHaveDuplicates();
  }

//  @ParameterizedTest
//  @CsvSource({
//      "0, 0, 0,  '[]'",
//      "1, 0, 0,  '[]'",
//      "9, 0, 0,  '[]'",
//      "1, 1, 0,  '[0]'",
//      "5, 1, 0,  '[0]'",
//      "5, 1, 4,  '[4]'",
//      "5, 2, 0,  '[0, 0]'",
//      "5, 2, 1,  '[0, 1]'",
//      "5, 2, 5,  '[1, 0]'",
//      "5, 2, 24, '[4, 4]'",
//      "2, 5, 0,  '[0, 0, 0, 0, 0]'",
//      "2, 5, 8,  '[0, 1, 0, 0, 0]'",
//      "2, 5, 18, '[1, 0, 0, 1, 0]'",
//      "2, 5, 31, '[1, 1, 1, 1, 1]'",
//  })
//  void test_toOrdinal_happyPath(int verticesCount, int edgeDimension, BigInteger expectedOrdinal,
//                                @CsvToIntArray int[] object) {
//    BigInteger ordinal = HHTreeCodeFactory.ofParams(verticesCount, edgeDimension).toOrdinal(object);
//
//    assertThat(ordinal)
//        .isEqualTo(expectedOrdinal);
//  }

//  @Test
//  void test_generator_elements() {
//    Iterable<int[]> expectedArrangements = List.of(
//        new int[]{ 0, 0, 0 }, new int[]{ 0, 0, 1 }, new int[]{ 0, 0, 2 }, new int[]{ 0, 0, 3 },
//        new int[]{ 0, 1, 0 }, new int[]{ 0, 1, 1 }, new int[]{ 0, 1, 2 }, new int[]{ 0, 1, 3 },
//        new int[]{ 0, 2, 0 }, new int[]{ 0, 2, 1 }, new int[]{ 0, 2, 2 }, new int[]{ 0, 2, 3 },
//        new int[]{ 0, 3, 0 }, new int[]{ 0, 3, 1 }, new int[]{ 0, 3, 2 }, new int[]{ 0, 3, 3 },
//        new int[]{ 1, 0, 0 }, new int[]{ 1, 0, 1 }, new int[]{ 1, 0, 2 }, new int[]{ 1, 0, 3 },
//        new int[]{ 1, 1, 0 }, new int[]{ 1, 1, 1 }, new int[]{ 1, 1, 2 }, new int[]{ 1, 1, 3 },
//        new int[]{ 1, 2, 0 }, new int[]{ 1, 2, 1 }, new int[]{ 1, 2, 2 }, new int[]{ 1, 2, 3 },
//        new int[]{ 1, 3, 0 }, new int[]{ 1, 3, 1 }, new int[]{ 1, 3, 2 }, new int[]{ 1, 3, 3 },
//        new int[]{ 2, 0, 0 }, new int[]{ 2, 0, 1 }, new int[]{ 2, 0, 2 }, new int[]{ 2, 0, 3 },
//        new int[]{ 2, 1, 0 }, new int[]{ 2, 1, 1 }, new int[]{ 2, 1, 2 }, new int[]{ 2, 1, 3 },
//        new int[]{ 2, 2, 0 }, new int[]{ 2, 2, 1 }, new int[]{ 2, 2, 2 }, new int[]{ 2, 2, 3 },
//        new int[]{ 2, 3, 0 }, new int[]{ 2, 3, 1 }, new int[]{ 2, 3, 2 }, new int[]{ 2, 3, 3 },
//        new int[]{ 3, 0, 0 }, new int[]{ 3, 0, 1 }, new int[]{ 3, 0, 2 }, new int[]{ 3, 0, 3 },
//        new int[]{ 3, 1, 0 }, new int[]{ 3, 1, 1 }, new int[]{ 3, 1, 2 }, new int[]{ 3, 1, 3 },
//        new int[]{ 3, 2, 0 }, new int[]{ 3, 2, 1 }, new int[]{ 3, 2, 2 }, new int[]{ 3, 2, 3 },
//        new int[]{ 3, 3, 0 }, new int[]{ 3, 3, 1 }, new int[]{ 3, 3, 2 }, new int[]{ 3, 3, 3 }
//    );
//
//    Generator<int[]> generator = HHTreeCodeFactory.ofParams(4, 3).generator();
//
//    assertThat(generator)
//        .toIterable()
//        .hasSameSizeAs(expectedArrangements)
//        .containsExactlyElementsOf(expectedArrangements);
//  }

  @ParameterizedTest
  @MethodSource("configurationProvider")
  void test_generator_count(int verticesCount, int edgeDimension, BigInteger expectedCount) {
    var generator = HHTreeCodeFactory.ofParams(verticesCount, edgeDimension).generator();

    assertThat(generator.getRemaining())
        .hasSize(expectedCount.intValueExact());
  }

  @ParameterizedTest
  @MethodSource("configurationProvider")
  void test_generator_elementUniqueness(int verticesCount, int edgeDimension) {
    var generator = HHTreeCodeFactory.ofParams(verticesCount, edgeDimension).generator();

    assertThat(generator)
        .toIterable()
        .doesNotHaveDuplicates();
  }

  @ParameterizedTest
  @MethodSource("configurationProvider")
  void test_generator_syncWithOrdinals(int verticesCount, int edgeDimension) {
    var factory = HHTreeCodeFactory.ofParams(verticesCount, edgeDimension);
    int count = factory.count().intValueExact();

    List<HHTreeCode> codes = new ArrayList<>();
    for (int i = 0; i < count; ++i) {
      codes.add(factory.byOrdinal(BigInteger.valueOf(i)));
    }

    var generator = factory.generator();

    assertThat(generator)
        .toIterable()
        .containsExactlyElementsOf(codes);
  }



//  @ParameterizedTest
//  @CsvSource({
//      "0, 0",
//      "5, 0",
//      "0, 1",
//      "1, 1",
//      "1, 5",
//      "2, 1",
//      "2, 2",
//      "5, 5",
//      "10, 5",
//      "50, 3"
//  })
//  void test_generatorAndCountSync(int verticesCount, int edgeDimension) {
//    HHTreeCodeFactory factory = HHTreeCodeFactory.ofParams(verticesCount, edgeDimension);
//
//    BigInteger count = factory.count();
//    Generator<?> generator = factory.generator();
//
//    assertThat(generator)
//        .toIterable()
//        .hasSize(count.intValueExact());
//  }

  static Stream<Arguments> configurationProvider() {
    return Stream.of(
        // verticesCount, edgeDimension, experimental count
//        Arguments.of(0, 2, 0),
//        Arguments.of(2, 2, 1),
        Arguments.of(3,  2, BigInteger.valueOf(3)),
        Arguments.of(4,  2, BigInteger.valueOf(16)),
        Arguments.of(5,  2, BigInteger.valueOf(125)),
        Arguments.of(7,  2, BigInteger.valueOf(16807)),
        Arguments.of(3,  3, BigInteger.valueOf(1)),
        Arguments.of(5,  3, BigInteger.valueOf(15)),
        Arguments.of(7,  3, BigInteger.valueOf(735)),
        Arguments.of(9,  3, BigInteger.valueOf(76545)),
        Arguments.of(4,  4, BigInteger.valueOf(1)),
        Arguments.of(7,  4, BigInteger.valueOf(70)),
        Arguments.of(10, 4, BigInteger.valueOf(28000)),
        Arguments.of(5,  5, BigInteger.valueOf(1)),
        Arguments.of(9,  5, BigInteger.valueOf(315))
    );
  }
}
