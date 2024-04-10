package nl.pdok.gml3;

import java.io.Reader;
import nl.pdok.gml3.exceptions.GML3ParseException;
import org.locationtech.jts.geom.Geometry;

/**
 * <p>
 * Parses sources/ string to GML3 (either 3.1.1 or GML 3.2.1)
 * </p>
 *
 * @author raymond
 * @version $Id: $Id
 */
public interface GMLParser {

  /**
   * Default SRID (28992: Amersfoort RD/ new).
   */
  static final int DEFAULT_SRID = 28992;

  /**
   * Default default arc approximation error.
   */
  static final double ARC_APPROXIMATION_ERROR = 0.01;

  /**
   * <p>
   * toJTSGeometry.
   * </p>
   *
   * @param reader a {@link java.io.Reader} object.
   * @return The Geometry object in the reader
   * @throws nl.pdok.gml3.exceptions.GML3ParseException When no geometry could be created
   */
  Geometry toJTSGeometry(Reader reader) throws GML3ParseException;

  /**
   * <p>
   * toJTSGeometry.
   * </p>
   *
   * @param gml a {@link java.lang.String} object.
   * @return The Geometry object represented by the string
   * @throws nl.pdok.gml3.exceptions.GML3ParseException When no geometry could be created
   */
  Geometry toJTSGeometry(String gml) throws GML3ParseException;
}
