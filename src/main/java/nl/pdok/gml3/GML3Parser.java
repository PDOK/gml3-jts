package nl.pdok.gml3;

import nl.pdok.gml3.exceptions.GML3ParseException;
import com.vividsolutions.jts.geom.Geometry;
import java.io.Reader;

/**
 *
 * Parses sources/ string to GML3 (either 3.1.1 or GML 3.2.1)
 */
public interface GML3Parser {

    /**
     * Default SRID (28992: Amersfoort RD/ new)
     */
    static final int DEFAULT_SRID = 28992;
    
    /**
     * Default  default arc approximation error
     */
    static final double ARC_APPROXIMATION_ERROR = 0.01;
    
    /**
     *
     * @param reader
     * @return The Geometry object in the reader
     * @throws GML3ParseException When no geometry could be created
     */
    Geometry toJTSGeometry(Reader reader) throws GML3ParseException;

    /**
     *
     * @param gml
     * @return The Geometry object represented by the string
     * @throws GML3ParseException When no geometry could be created
     */
    Geometry toJTSGeometry(String gml) throws GML3ParseException;
}
