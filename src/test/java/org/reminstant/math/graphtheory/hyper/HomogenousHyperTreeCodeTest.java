package org.reminstant.math.graphtheory.hyper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class HomogenousHyperTreeCodeTest {

  @ParameterizedTest
  @MethodSource("configurationProvider")
  void test_generatorElementUniqueness(int verticesCount, int edgeDimension) {
    var generator = HomogenousHyperTreeCode.generator(verticesCount, edgeDimension);

    assertThat(generator)
        .toIterable()
        .doesNotHaveDuplicates();
  }

  @ParameterizedTest
  @MethodSource("configurationProvider")
  void test_toTreeInjection(int verticesCount, int edgeDimension) {
    var generator = HomogenousHyperTreeCode.generator(verticesCount, edgeDimension);

    assertThat(generator)
        .toIterable()
        .map(HomogenousHyperTreeCode::toTree)
        .doesNotHaveDuplicates();
  }

  @ParameterizedTest
  @MethodSource("configurationProvider")
  void test_toTreeSurjection(int verticesCount, int edgeDimension, int experimentalCount) {
    Set<HomogenousHyperTree> trees = new HashSet<>();
    var generator = HomogenousHyperTreeCode.generator(verticesCount, edgeDimension);

    while (generator.hasNext()) {
      trees.add(generator.next().toTree());
    }

    assertThat(trees)
        .hasSize(experimentalCount);
  }

  @ParameterizedTest
  @MethodSource("configurationProvider")
  void test_codeCount(int verticesCount, int edgeDimension, int experimentalCount) {
    var count = HomogenousHyperTreeCode.count(verticesCount, edgeDimension);

    assertThat(count)
        .isEqualTo(experimentalCount);
  }



  static Stream<Arguments> configurationProvider() {
    return Stream.of(
        // verticesCount, edgeDimension, experimental count
//        Arguments.of(0, 2, 0),
//        Arguments.of(2, 2, 1),
        Arguments.of(3, 2, 3),
        Arguments.of(4, 2, 16),
        Arguments.of(5, 2, 125),
        Arguments.of(7, 2, 16807),
        Arguments.of(3, 3, 1),
        Arguments.of(5, 3, 15),
        Arguments.of(7, 3, 735),
        Arguments.of(9, 3, 76545),
        Arguments.of(4, 4, 1),
        Arguments.of(7, 4, 70),
        Arguments.of(10, 4, 28000),
        Arguments.of(5, 5, 1),
        Arguments.of(9, 5, 315)
    );
  }

}
