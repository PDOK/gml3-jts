package nl.pdok.gml3.impl.gml3_2_1;

import java.io.StringReader;

import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.xml.transform.stream.StreamSource;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.PrecisionModel;
import java.io.Reader;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import net.opengis.gml.v_3_2_1.AbstractGeometryType;
import nl.pdok.gml3.exceptions.GML3ParseException;
import nl.pdok.gml3.GMLParser;

import nl.pdok.gml3.exceptions.GeometryException;
import nl.pdok.gml3.exceptions.InvalidGeometryException;
import nl.pdok.gml3.impl.geometry.extended.ExtendedGeometryFactory;
import nl.pdok.gml3.impl.gml3_2_1.converters.GML321ToJTSGeometryConvertor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>GML321ParserImpl class.</p>
 *
 * @author raymond
 * @version $Id: $Id
 */
public class GML321ParserImpl implements GMLParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(GML321ParserImpl.class);
    private static final JAXBContext GML_321_JAXB_CONTEXT;
    private static final ThreadLocal<Unmarshaller> GML_321_UNMARSHALLER;

    static {
        try {
            GML_321_JAXB_CONTEXT = JAXBContext.newInstance(AbstractGeometryType.class);
            LOGGER.debug("Created JAXB context");
            GML_321_UNMARSHALLER = new ThreadLocal<Unmarshaller>() {
                @Override
                protected Unmarshaller initialValue() {
                    try {
                        return GML_321_JAXB_CONTEXT.createUnmarshaller();
                    } catch (JAXBException ex) {
                        LOGGER.error(ex.getMessage(), ex);
                        throw new IllegalStateException(ex);
                    }
                }
            };
        } catch (JAXBException ex) {
            LOGGER.error("Could not create JAXB context. {}", ex.getMessage(), ex);
            throw new IllegalStateException("Could not create JAXB context", ex);
        }
    }

    private final GML321ToJTSGeometryConvertor gml321ToJTSGeometryConvertor;

    /**
     * <p>Constructor for GML321ParserImpl.</p>
     */
    public GML321ParserImpl() {
        this(GMLParser.ARC_APPROXIMATION_ERROR, GMLParser.DEFAULT_SRID);
    }

    /**
     * <p>Constructor for GML321ParserImpl.</p>
     *
     * @param maximumArcApproximationError a double.
     * @param srid a int.
     */
    public GML321ParserImpl(final double maximumArcApproximationError, final int srid) {
        ExtendedGeometryFactory geometryFactory = new ExtendedGeometryFactory(new PrecisionModel(), srid);
        geometryFactory.setMaximumArcApproximationError(maximumArcApproximationError);
        this.gml321ToJTSGeometryConvertor = new GML321ToJTSGeometryConvertor(geometryFactory);

        LOGGER.info("Created a GML 3.2.1 parser for SRID {} with MaximumArcApproximationError {}", srid, maximumArcApproximationError);
    }

    /** {@inheritDoc} */
    @Override
    public Geometry toJTSGeometry(Reader reader) throws GML3ParseException {
        try {
            AbstractGeometryType abstractGeometryType = parseGeometryFromGML(reader);
            return gml321ToJTSGeometryConvertor.convertGeometry(abstractGeometryType);
        } catch (JAXBException jaxbException) {
            throw new GML3ParseException("Input cannot be serialized to gml3-objects. "
                    + "Cause: " + jaxbException.getMessage(), jaxbException);
        } catch (InvalidGeometryException invalidGeometryException) {
            throw new GML3ParseException("Input is not a valid geometry (gml3). "
                    + "Cause: " + invalidGeometryException.getErrorType(), invalidGeometryException);
        } catch (GeometryException geometryException) {
            throw new GML3ParseException("Input is not a valid geometry (gml3). "
                    + "Cause: " + geometryException.getMessage(), geometryException);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Geometry toJTSGeometry(String gml) throws GML3ParseException {
        if (StringUtils.isBlank(gml)) {
            throw new GML3ParseException("Emtpy GML-string provided");
        }
        return toJTSGeometry(new StringReader(gml));
    }

    private AbstractGeometryType parseGeometryFromGML(Reader reader) throws JAXBException {
        JAXBElement unmarshalled = (JAXBElement) GML_321_UNMARSHALLER.get().unmarshal(new StreamSource(reader));
        return (AbstractGeometryType) unmarshalled.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "GML3_2_1_Parser";
    }

}
