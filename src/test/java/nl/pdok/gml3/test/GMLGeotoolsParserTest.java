package nl.pdok.gml3.test;

import static nl.pdok.gml3.test.GML3ParserTest.GML3_1_1_POLYGON;
import static nl.pdok.gml3.test.GML3ParserTest.GML3_2_1_SURFACE;
import static org.junit.Assert.assertNotNull;

import nl.pdok.gml3.GMLParser;
import nl.pdok.gml3.exceptions.GML3ParseException;
import nl.pdok.gml3.impl.gml3_2_1.GML321GeotoolsParserImpl;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;

/**
 *
 * @author niek
 */
public class GMLGeotoolsParserTest {

  @Test
  public void testGML3_2_1_Geotools_Multisurface() throws GML3ParseException {
    GMLParser parser = new GML321GeotoolsParserImpl();
    Geometry geometry = parser.toJTSGeometry(GML3_2_1_SURFACE);
    assertNotNull(geometry);
    // LOGGER.info("Result: {} ", geometry);

    Geometry geometry2 = parser.toJTSGeometry(GML3_2_1_SURFACE);
    assertNotNull(geometry2);
    // LOGGER.info("Result: {} ", geometry2);
  }

  @Test(expected = GML3ParseException.class)
  public void testGML_3_2_1_Geotools_Parser_with_3_1_1_geom() throws GML3ParseException {
    GMLParser parser = new GML321GeotoolsParserImpl();
    parser.toJTSGeometry(GML3_1_1_POLYGON);
  }
}
