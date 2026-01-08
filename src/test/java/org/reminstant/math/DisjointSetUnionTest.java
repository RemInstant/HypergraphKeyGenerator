package org.reminstant.math;

import org.reminstant.structure.DisjointSetUnion;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.*;

public class DisjointSetUnionTest {

  @Test
  void testUnification() {
    DisjointSetUnion dsu = new DisjointSetUnion(6);

    dsu.unite(0, 1);
    dsu.unite(5, 4);
    dsu.unite(4, 1);

    assertThat(dsu.isUnited(0, 1)).isTrue();
    assertThat(dsu.isUnited(5, 4)).isTrue();
    assertThat(dsu.isUnited(4, 1)).isTrue();
    assertThat(dsu.isUnited(5, 0)).isTrue();
    assertThat(dsu.isUnited(3, 0)).isFalse();
  }

  @Test
  void testOutOfBound() {
    DisjointSetUnion dsu = new DisjointSetUnion(6);

    assertThatIndexOutOfBoundsException()
        .isThrownBy(() -> dsu.unite(0, 6));
  }

  @Test
  void testEmptyDsu() {
    DisjointSetUnion dsu = new DisjointSetUnion(0);

    assertThat(dsu.getSize())
        .isZero();
    assertThat(dsu.getComponentsCount()).
        isZero();
    assertThatIndexOutOfBoundsException()
        .isThrownBy(() -> dsu.unite(0, 1));
  }

  @Test
  void testLittleExtension() {
    DisjointSetUnion dsu = new DisjointSetUnion(0);

    dsu.extendTo(4);

    assertThat(dsu.getSize())
        .isEqualTo(4);
    assertThat(dsu.getComponentsCount())
        .isEqualTo(4);
    assertThatNoException()
        .isThrownBy(() -> dsu.unite(0, 3));
    assertThatIndexOutOfBoundsException()
        .isThrownBy(() -> dsu.unite(0, 4));
  }

  @Test
  void testBigExtension() {
    DisjointSetUnion dsu = new DisjointSetUnion(0);

    dsu.extendTo(777);

    assertThat(dsu.getSize())
        .isEqualTo(777);
    assertThat(dsu.getComponentsCount())
        .isEqualTo(777);
    assertThatNoException()
        .isThrownBy(() -> dsu.unite(0, 776));
    assertThatIndexOutOfBoundsException()
        .isThrownBy(() -> dsu.unite(0, 777));
  }

  @Test
  void testComponentCount1() {
    DisjointSetUnion dsu = new DisjointSetUnion(16);

    assertThat(dsu.getComponentsCount())
        .isEqualTo(16);
  }

  @Test
  void testComponentCount2() {
    DisjointSetUnion dsu = new DisjointSetUnion(16);

    dsu.unite(0, 1);
    dsu.unite(2, 3);
    dsu.unite(4, 5);
    dsu.unite(6, 7);

    assertThat(dsu.getComponentsCount())
        .isEqualTo(12);
  }

  @Test
  void testComponentCount3() {
    DisjointSetUnion dsu = new DisjointSetUnion(16);

    dsu.unite(0, 1);
    dsu.unite(2, 3);
    dsu.unite(3, 2);
    dsu.unite(1, 0);

    assertThat(dsu.getComponentsCount())
        .isEqualTo(14);
  }

  @Test
  void testComponentCount4() {
    DisjointSetUnion dsu = new DisjointSetUnion(4);

    dsu.unite(0, 1);
    dsu.unite(0, 2);
    dsu.unite(0, 3);
    dsu.unite(1, 2);
    dsu.unite(1, 3);
    dsu.unite(2, 3);

    assertThat(dsu.getComponentsCount())
        .isEqualTo(1);
  }
}
