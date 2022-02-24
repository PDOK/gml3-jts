package nl.pdok.gml3.impl;

import org.locationtech.jts.geom.Geometry;
import java.io.Reader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import nl.pdok.gml3.GMLParser;
import nl.pdok.gml3.exceptions.GML3ParseException;
import nl.pdok.gml3.impl.gml3_1_1_2.GML3112ParserImpl;
import nl.pdok.gml3.impl.gml3_2_1.GML321ParserImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>GMLMultiVersionParserImpl class.</p>
 *
 * @author Niek Hoogma
 *
 * Parser which tries parsers for various GML versions.
 * It tries the last success full parser first in order to speed up batch processing.
 *
 * This class is not thread-safe.
 * @version $Id: $Id
 */
public class GMLMultiVersionParserImpl implements GMLParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(GMLMultiVersionParserImpl.class);

    private final Set<GMLParser> parsers;
    private GMLParser lastUsedParser;

    /**
     * <p>Constructor for GMLMultiVersionParserImpl.</p>
     */
    public GMLMultiVersionParserImpl() {
        this(GMLParser.ARC_APPROXIMATION_ERROR, GMLParser.DEFAULT_SRID);
    }

    /**
     * <p>Constructor for GMLMultiVersionParserImpl.</p>
     *
     * @param maximumArcApproximationError a double.
     * @param srid a int.
     */
    public GMLMultiVersionParserImpl(double maximumArcApproximationError, final int srid) {
        this.parsers = new HashSet<>();
        this.parsers.add(new GML3112ParserImpl(maximumArcApproximationError, srid));
        this.parsers.add(new GML321ParserImpl(maximumArcApproximationError, srid));
        // GML321GeotoolsParserImpl have a different arcApproximationError and is therefore not used here.
        
        LOGGER.info("{}: Supported gml versions 3.1.1.2, 3.2.1.", getClass().getSimpleName());
    }

    /** {@inheritDoc} */
    @Override
    public Geometry toJTSGeometry(Reader reader) throws GML3ParseException {
        GMLParser parserToTry = lastUsedParser;
        if (parserToTry == null) {
            parserToTry = parsers.iterator().next();
        }

        try {
            return parserToTry.toJTSGeometry(reader);
        } catch (GML3ParseException ex) {
            LOGGER.info("Not parseable using {}. Trying other GML parser versions. {} : {}", parserToTry, parserToTry, ex.getMessage());

            Iterator<GMLParser> it = parsers.iterator();

            while (it.hasNext()) {
                GMLParser parser = it.next();
                if (!parser.equals(parserToTry)) {

                    try {
                        Geometry res = parser.toJTSGeometry(reader);

                        this.lastUsedParser = parser;
                        LOGGER.info("Succesfully parsed using {}. Next geometry will also first be parsed with this parser", parser);

                        return res;
                    } catch (GML3ParseException ex1) {
                        LOGGER.debug("{} : {}", parser, ex.getMessage());
                    }
                }
            }

            // No suitable parser found
            throw new GML3ParseException("No suitable Parser. Is the GML version supported?");
        }
    }

    /** {@inheritDoc} */
    @Override
    public Geometry toJTSGeometry(String gml) throws GML3ParseException {
        if(StringUtils.isBlank(gml)) {
            throw new GML3ParseException("Emtpy GML-string provided");
        }
        
        GMLParser parserToTry = lastUsedParser;
        if (parserToTry == null) {
            parserToTry = parsers.iterator().next();
        }

        try {
            Geometry result = parserToTry.toJTSGeometry(gml);
            return result;
        } catch (GML3ParseException ex) {
            LOGGER.info("Not parseable using last-used parser {}. Trying other GML parser versions. {} : {}", parserToTry, parserToTry, ex.getMessage());

            Iterator<GMLParser> it = parsers.iterator();

            while (it.hasNext()) {
                GMLParser parser = it.next();
                if (!parser.equals(parserToTry)) {

                    try {
                        Geometry res = parser.toJTSGeometry(gml);

                        this.lastUsedParser = parser;
                        LOGGER.info("Succesfully parsed using {}. Next geometry will also first be parsed with this parser", parser);

                        return res;
                    } catch (GML3ParseException ex1) {
                        LOGGER.debug("{} : {}", parser, ex.getMessage());
                    }
                }
            }

            // No suitable parser found
            throw new GML3ParseException("No suitable Parser. Is the GML version supported?");
        }
    }
}
