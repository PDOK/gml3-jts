package nl.pdok.gml3;

import com.vividsolutions.jts.geom.Geometry;
import java.io.Reader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import nl.pdok.gml3.exceptions.GML3ParseException;
import nl.pdok.gml3.gml3_1_1_2.GML3_1_1_2_Parser;
import nl.pdok.gml3.gml3_2_1.GML3_2_1_Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Niek Hoogma
 *
 * Parser which tries parsers for various versions. 
 * It tries the last success full parser first in order to speed up batch processing.
 *
 * This class is not thread-safe.
 */
public class GML3_x_x_Parser implements GML3Parser {

    private static final Logger LOGGER = LoggerFactory.getLogger(GML3_x_x_Parser.class);

    private final Set<GML3Parser> parsers;
    private GML3Parser lastUsedParser;

    public GML3_x_x_Parser() {
        this(GML3Parser.ARC_APPROXIMATION_ERROR, GML3Parser.DEFAULT_SRID);
    }

    public GML3_x_x_Parser(double maximumArcApproximationError, final int srid) {
        this.parsers = new HashSet<>();
        this.parsers.add(new GML3_1_1_2_Parser(maximumArcApproximationError, srid));
        this.parsers.add(new GML3_2_1_Parser(maximumArcApproximationError, srid));
    }

    @Override
    public Geometry toJTSGeometry(Reader reader) throws GML3ParseException {
        GML3Parser parserToTry = lastUsedParser;
        if (parserToTry == null) {
            parserToTry = parsers.iterator().next();
        }

        try {
            return parserToTry.toJTSGeometry(reader);
        } catch (GML3ParseException ex) {
            LOGGER.info("Not parseable using {}. Trying other GML parser versions. {} : {}", parserToTry, parserToTry, ex.getMessage());

            Iterator<GML3Parser> it = parsers.iterator();

            while (it.hasNext()) {
                GML3Parser parser = it.next();
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

    @Override
    public Geometry toJTSGeometry(String gml) throws GML3ParseException {
        GML3Parser parserToTry = lastUsedParser;
        if (parserToTry == null) {
            parserToTry = parsers.iterator().next();
        }

        try {
            return parserToTry.toJTSGeometry(gml);
        } catch (GML3ParseException ex) {
            LOGGER.info("Not parseable using last-used parser {}. Trying other GML parser versions. {} : {}", parserToTry, parserToTry, ex.getMessage());

            Iterator<GML3Parser> it = parsers.iterator();

            while (it.hasNext()) {
                GML3Parser parser = it.next();
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
