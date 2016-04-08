package nl.pdok.gml3.impl.gml3_2_1;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import javax.xml.parsers.ParserConfigurationException;
import nl.pdok.gml3.GMLParser;
import nl.pdok.gml3.exceptions.GML3ParseException;
import org.geotools.gml3.v3_2.GMLConfiguration;
import org.geotools.xml.Parser;
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
    private final ThreadLocal<Parser> parserThreadLocal;

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
     * @param SRID a int.
     * @param strictParsing a boolean.
     * @param strictValidating a boolean.
     */
    public GML321GeotoolsParserImpl(final int SRID, final boolean strictParsing, final boolean strictValidating) {
        this.parserThreadLocal = new ThreadLocal<Parser>() {

            @Override
            protected Parser initialValue() {
                return buildParser(SRID, strictParsing, strictValidating);
            }
        };
        LOGGER.info("Create a parser for SRID: {}, strictParsing: {}, strictValidating: {}", SRID, strictParsing, strictValidating);
    }

    private Parser buildParser(int SRID, boolean strictParsing, boolean strictValidating) {
        GMLConfiguration configuration = new GMLConfiguration(true);
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), SRID);
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
            Object result = parserThreadLocal.get().parse(reader);
            if (Geometry.class.isInstance(result)) {
                return (Geometry) result;
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
