package org.reminstant.domain;

public record HPPruferCode(
    int edgeDimension,
    int[] rootEdge,
    int[] code
) {
}
