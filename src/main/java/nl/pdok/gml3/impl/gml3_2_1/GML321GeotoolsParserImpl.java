package nl.pdok.gml3.impl.gml3_2_1;

import org.geotools.xsd.Parser;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import javax.xml.parsers.ParserConfigurationException;
import nl.pdok.gml3.GMLParser;
import nl.pdok.gml3.exceptions.GML3ParseException;
import org.geotools.gml3.v3_2.GMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * A GML 3.2.1 based on Geotools. Note this parser is less accurate than GML321ParserImpl for arcs
 *
 * @author niek
 * @version $Id: $Id
 */
public class GML321GeotoolsParserImpl implements GMLParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(GML321GeotoolsParserImpl.class);
    private final ThreadLocal<Parser> PARSER_THREAD_LOCAL;

    /**
     * <p>Constructor for GML321GeotoolsParserImpl.</p>
     */
    public GML321GeotoolsParserImpl() {
        // By default, do not use strict validating/ parsing to improve performance
        this(GMLParser.DEFAULT_SRID, false, false);
    }

    /**
     * <p>Constructor for GML321GeotoolsParserImpl.</p>
     *
     * @param srid a int.
     * @param strictParsing a boolean.
     * @param strictValidating a boolean.
     */
    public GML321GeotoolsParserImpl(final int srid, final boolean strictParsing, final boolean strictValidating) {
        PARSER_THREAD_LOCAL =
            ThreadLocal.withInitial(() -> buildParser(srid, strictParsing, strictValidating));
        LOGGER.info("Create a parser for SRID: {}, strictParsing: {}, strictValidating: {}", srid, strictParsing, strictValidating);
    }

    private Parser buildParser(int srid, boolean strictParsing, boolean strictValidating) {
        GMLConfiguration configuration = new GMLConfiguration(true);
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), srid);
        configuration.setGeometryFactory(geometryFactory);
        Parser parser = new Parser(configuration);
        parser.setStrict(strictParsing);
        parser.setValidating(strictValidating);
        return parser;
    }

    /** {@inheritDoc} */
    @Override
    public Geometry toJTSGeometry(Reader reader) throws GML3ParseException {
        try {
            Object result = PARSER_THREAD_LOCAL.get().parse(reader);
            if (result instanceof Geometry geometry) {
                return geometry;
            } else {
                throw new GML3ParseException("Cannot convert gml geometry to JTS");
            }
        } catch (RuntimeException | IOException | SAXException | ParserConfigurationException ex) {
            throw new GML3ParseException(ex.getMessage(), ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Geometry toJTSGeometry(String gml) throws GML3ParseException {
        return toJTSGeometry(new StringReader(gml));
    }
}
