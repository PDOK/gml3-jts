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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GML321ParserImpl implements GMLParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(GML321ParserImpl.class);

    private final ThreadLocal<GML321ToJTSGeometryConvertor> threadLocalConverter = new ThreadLocal<>();
    private final ThreadLocal<Unmarshaller> threadLocalUnmarshaller = new ThreadLocal<>();

    public GML321ParserImpl() {
        this(GMLParser.ARC_APPROXIMATION_ERROR, GMLParser.DEFAULT_SRID);
    }

    public GML321ParserImpl(double maximumArcApproximationError, final int srid) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(AbstractGeometryType.class);
            this.threadLocalUnmarshaller.set(jaxbContext.createUnmarshaller());

            ExtendedGeometryFactory geometryFactory = new ExtendedGeometryFactory(new PrecisionModel(), srid);
            geometryFactory.setMaximumArcApproximationError(maximumArcApproximationError);
            threadLocalConverter.set(new GML321ToJTSGeometryConvertor(geometryFactory));

            LOGGER.info("Created a GML 3.2.1 parser for SRID {} with MaximumArcApproximationError {}", srid, maximumArcApproximationError);
        } catch (JAXBException e) {
            throw new IllegalStateException("Object cannot be created. Cause: " + e.getMessage());
        }
    }

    @Override
    public Geometry toJTSGeometry(Reader reader) throws GML3ParseException {
        try {
            AbstractGeometryType abstractGeometryType = parseGeometryFromGML(reader);
            return threadLocalConverter.get().convertGeometry(abstractGeometryType);
        } catch (JAXBException jaxbException) {
            throw new GML3ParseException("Input cannot be serialized to gml3-objects. "
                    + "Cause: " + jaxbException.getMessage(), jaxbException);
        } catch (InvalidGeometryException invalidGeometryException){
            throw new GML3ParseException("Input is not a valid geometry (gml3). "
                    + "Cause: " + invalidGeometryException.getErrorType(), invalidGeometryException);
        } catch (GeometryException geometryException) {
            throw new GML3ParseException("Input is not a valid geometry (gml3). "
                    + "Cause: " + geometryException.getMessage(), geometryException);
        }
    }

    @Override
    public Geometry toJTSGeometry(String gml) throws GML3ParseException {
        return toJTSGeometry(new StringReader(gml));
    }

    private AbstractGeometryType parseGeometryFromGML(Reader reader) throws JAXBException {
        JAXBElement unmarshalled = (JAXBElement) threadLocalUnmarshaller.get().unmarshal(new StreamSource(reader));
        return (AbstractGeometryType) unmarshalled.getValue();
    }

    @Override
    public String toString() {
        return "GML3_2_1_Parser";
    }
    
    
}
