package nl.pdok.gml3.test;

import static nl.pdok.gml3.test.GML3ParserTest.GML3_2_1_SURFACE;

import nl.pdok.gml3.GMLParser;
import nl.pdok.gml3.exceptions.GML3ParseException;
import nl.pdok.gml3.impl.gml3_2_1.GML321GeotoolsParserImpl;
import nl.pdok.gml3.impl.gml3_2_1.GML321ParserImpl;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class PerformanceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(GMLGeotoolsParserTest.class);

  @Test
  public void performanceTest() throws GML3ParseException {
    final int iterations = 1000;
    gml321PerformanceTest(new GML321GeotoolsParserImpl(), iterations);
    gml321PerformanceTest(new GML321ParserImpl(), iterations);
  }

  private void gml321PerformanceTest(GMLParser parser, int iterations) throws GML3ParseException {

    long startTime = System.currentTimeMillis();
    for (int i = 0; i < iterations; i++) {
      Geometry geometry = parser.toJTSGeometry(GML3_2_1_SURFACE);
      Assert.assertNotNull(geometry);
      if (i % 100 == 0 && i > 0) {
        LOGGER.debug("{} done", i);
      }
    }
    LOGGER.info("Parser {} => {} milliseconds", parser, System.currentTimeMillis() - startTime);
  }
}
