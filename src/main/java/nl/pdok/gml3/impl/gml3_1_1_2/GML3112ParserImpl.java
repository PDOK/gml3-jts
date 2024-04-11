package nl.pdok.gml3.impl.gml3_1_1_2;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.Reader;
import java.io.StringReader;
import javax.xml.transform.stream.StreamSource;
import net.opengis.gml.v_3_1_1.AbstractGeometryType;
import nl.pdok.gml3.GMLParser;
import nl.pdok.gml3.exceptions.GML3ParseException;
import nl.pdok.gml3.exceptions.GeometryException;
import nl.pdok.gml3.exceptions.InvalidGeometryException;
import nl.pdok.gml3.impl.geometry.extended.ExtendedGeometryFactory;
import nl.pdok.gml3.impl.gml3_1_1_2.convertors.GMLToJTSGeometryConvertor;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.PrecisionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * GML3112ParserImpl class.
 * </p>
 *
 * @author raymond
 * @version $Id: $Id
 */
public class GML3112ParserImpl implements GMLParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(GML3112ParserImpl.class);
  private static final JAXBContext GML_3112_JAXB_CONTEXT;
  private static final ThreadLocal<Unmarshaller> GML_3112_UNMARSHALLER;

  static {
    try {
      GML_3112_JAXB_CONTEXT = JAXBContext.newInstance(AbstractGeometryType.class);
      LOGGER.debug("Created JAXB context");
      GML_3112_UNMARSHALLER = new ThreadLocal<>() {
        @Override
        protected Unmarshaller initialValue() {
          try {
            return GML_3112_JAXB_CONTEXT.createUnmarshaller();
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

  private final GMLToJTSGeometryConvertor gmlToJTSGeometryConvertor;

  /**
   * <p>
   * Constructor for GML3112ParserImpl.
   * </p>
   */
  public GML3112ParserImpl() {
    this(GMLParser.ARC_APPROXIMATION_ERROR, GMLParser.DEFAULT_SRID);
  }

  /**
   * <p>
   * Constructor for GML3112ParserImpl.
   * </p>
   *
   * @param maximumArcApproximationError a double.
   * @param srid an int.
   */
  public GML3112ParserImpl(final double maximumArcApproximationError, final int srid) {
    ExtendedGeometryFactory geometryFactory =
        new ExtendedGeometryFactory(new PrecisionModel(), srid);
    geometryFactory.setMaximumArcApproximationError(maximumArcApproximationError);
    this.gmlToJTSGeometryConvertor = new GMLToJTSGeometryConvertor(geometryFactory);

    LOGGER.info("Created a GML 3.1.1.2 parser for SRID {} with MaximumArcApproximationError {}",
        srid, maximumArcApproximationError);
  }

  /** {@inheritDoc} */
  @Override
  public Geometry toJTSGeometry(Reader reader) throws GML3ParseException {
    try {
      AbstractGeometryType abstractGeometryType = parseGeometryFromGML(reader);
      return gmlToJTSGeometryConvertor.convertGeometry(abstractGeometryType);
    } catch (JAXBException jaxbException) {
      throw new GML3ParseException(
          "Input cannot be serialized to gml3-objects. " + "Cause: " + jaxbException.getMessage(),
          jaxbException);
    } catch (InvalidGeometryException invalidGeometryException) {
      throw new GML3ParseException("Input is not a valid geometry (gml3). " + "Reason: "
          + invalidGeometryException.getErrorType(), invalidGeometryException);
    } catch (GeometryException geometryException) {
      throw new GML3ParseException(
          "Input is not a valid geometry (gml3). " + "Cause: " + geometryException.getMessage(),
          geometryException);
    }
  }

  /** {@inheritDoc} */
  @Override
  public Geometry toJTSGeometry(String gml) throws GML3ParseException {
    if (StringUtils.isBlank(gml)) {
      throw new GML3ParseException("Empty GML-string provided");
    }
    return toJTSGeometry(new StringReader(gml));
  }

  private AbstractGeometryType parseGeometryFromGML(Reader reader) throws JAXBException {
    JAXBElement<AbstractGeometryType> unmarshalled =
        (JAXBElement<AbstractGeometryType>) GML_3112_UNMARSHALLER.get()
            .unmarshal(new StreamSource(reader));
    return unmarshalled.getValue();
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "GML3_1_1_2_Parser";
  }
}
