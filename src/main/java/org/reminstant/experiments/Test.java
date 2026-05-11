package org.reminstant.experiments;

import org.reminstant.math.graphtheory.hyper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class Test {
  private static final Logger log = LoggerFactory.getLogger(Test.class);

  public static void main(String[] args) {

//    var g = new HHOverlappingGenerator(5, 3, 10, 777);
    var g = new HHExtendingGenerator(7, 4, 4, 6, 777);
//    var g = new HHFixedExtendingGenerator(13, 5, 3, 777);
    var analyzer = new HHGeneratorAnalyzer(g, 5000000);

//    long iter = 0;
//    HomogenousHypergraph graph = null;
//    while (graph == null || graph.getEdgeCount() < 35) {
//      graph = g.next();
//      iter++;
//      if (iter % 1000000 == 0) {
//        log.info("iter {}", iter);
//      }
//    }

    Path path = Path.of("distribution");
    analyzer.analyze(path, false);

//    var g = new HHOverlappingGenerator(256, 4, 30, 1235);

//    var graph = g.next();

//    log.info("{}", graph.getEdges().toList());
//    log.info("{}", graph.getEdgesBitset().toByteArray());

//    var analyzer = new HypergraphAnalyzer(graph);

//    log.info("{}", analyzer.getIncidenceProportion());
//    log.info("{}", analyzer.getIncidenceProportionByVertices());

//    var factory = HHTreeCodeFactory.ofParams(4, 2);
//
//    for (int i = 0; i < factory.count().intValueExact(); ++i) {
//      var code = factory.byOrdinal(BigInteger.valueOf(i));
//      log.info("{}: {}", i, code);
//    }



//    byte[] data = new byte[] {
//        -38, -93, -62, -70, 25, -101, -18, -117, -113, 75, -92, 75, -118, -84, -100, 76
//    };
//    var key = generateSparseKUniformHypergraph(12345, 3);
//    var encryptor = new HypergraphEncryptor(key, 1, HypergraphEncryptor.SmallBlockSizeUnit.BIT);
//
//    log.info("n = {}", key.getVerticesCount());
//    log.info("expected blockByteSize = {}", encryptor.getBlockByteSize());
//    log.info("actual blockByteSize = {}", data.length);
//    var q = encryptor.encrypt(data);
//    var e = encryptor.decrypt(q);
//    log.info("eq = {}", Arrays.equals(data, e));
  }
}
