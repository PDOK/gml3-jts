package nl.pdok.gml3_1_1_2;

import java.io.StringReader;

import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.xml.transform.stream.StreamSource;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.PrecisionModel;
import java.io.Reader;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import nl.pdok.gml3.exceptions.GML3ParseException;
import nl.pdok.gml3.GML3Parser;

import nl.pdok.gml3_1_1_2.convertors.GMLToJTSGeometryConvertor;
import nl.pdok.gml3.exceptions.GeometryException;
import nl.pdok.gml3.geometry.extended.ExtendedGeometryFactory;
import org.opengis.gml_3_1_1.AbstractGeometryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GML3_1_1_2_Parser implements GML3Parser {

    private static final Logger LOGGER = LoggerFactory.getLogger(GML3_1_1_2_Parser.class);

    private final ThreadLocal<GMLToJTSGeometryConvertor> threadLocalConverter = new ThreadLocal<>();
    private final ThreadLocal<Unmarshaller> threadLocalUnmarshaller = new ThreadLocal<>();

    public GML3_1_1_2_Parser() {
        this(GML3Parser.ARC_APPROXIMATION_ERROR, GML3Parser.DEFAULT_SRID);
    }

    public GML3_1_1_2_Parser(double maximumArcApproximationError, final int srid) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(AbstractGeometryType.class);
            this.threadLocalUnmarshaller.set(jaxbContext.createUnmarshaller());

            ExtendedGeometryFactory geometryFactory = new ExtendedGeometryFactory(new PrecisionModel(), srid);
            geometryFactory.setMaximumArcApproximationError(maximumArcApproximationError);

            threadLocalConverter.set(new GMLToJTSGeometryConvertor(geometryFactory));
            LOGGER.info("Created a GML 3.1.1.2 parser for SRID {} with MaximumArcApproximationError {}", srid, maximumArcApproximationError);
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
            LOGGER.error(jaxbException.getMessage(), jaxbException);
            throw new GML3ParseException("Input cannot be serialized to gml3-objects. "
                    + "Cause: " + jaxbException.getMessage(), jaxbException);
        } catch (GeometryException geometryException) {
            LOGGER.error(geometryException.getMessage(), geometryException);
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
}
