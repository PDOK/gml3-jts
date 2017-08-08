package nl.pdok.gml3.impl.gml3_1_1_2.convertors;

import com.vividsolutions.jts.geom.Geometry;

import net.opengis.gml.v_3_1_1.*;
import nl.pdok.gml3.exceptions.GeometryException;
import nl.pdok.gml3.impl.geometry.extended.ExtendedGeometryFactory;

/**
 * Converteerd van gml3.1.1 naar JTS polygoon
 *
 * @author GinkeM
 * @version $Id: $Id
 */
public class GMLToJTSGeometryConvertor {

    private final GMLToPointConvertor gmlToPointConvertor;
    private final GMLToSurfaceConvertor gmlToSurfaceConvertor;
    private final GMLToLineConvertor gmlToLineConvertor;

    /**
     * <p>Constructor for GMLToJTSGeometryConvertor.</p>
     *
     * @param geometryFactory a {@link nl.pdok.gml3.impl.geometry.extended.ExtendedGeometryFactory} object.
     */
    public GMLToJTSGeometryConvertor(ExtendedGeometryFactory geometryFactory) {
        gmlToPointConvertor = new GMLToPointConvertor(geometryFactory);
        gmlToLineConvertor = new GMLToLineConvertor(geometryFactory, gmlToPointConvertor);
        gmlToSurfaceConvertor = new GMLToSurfaceConvertor(geometryFactory, gmlToLineConvertor);
    }

    /**
     * <p>convertGeometry.</p>
     *
     * @param abstractGeometryType a {@link net.opengis.gml.v_3_1_1.AbstractGeometryType} object.
     * @return a {@link com.vividsolutions.jts.geom.Geometry} object.
     * @throws nl.pdok.gml3.exceptions.GeometryException if any.
     */
    public Geometry convertGeometry(AbstractGeometryType abstractGeometryType) throws GeometryException {
        if (abstractGeometryType instanceof AbstractSurfaceType) {
            return gmlToSurfaceConvertor.convertSurface((AbstractSurfaceType) abstractGeometryType);

        } else if (abstractGeometryType instanceof MultiPointType) {
            return gmlToPointConvertor.convertMultiPoint((MultiPointType) abstractGeometryType);
        } else if (abstractGeometryType instanceof PointType) {
            return gmlToPointConvertor.convertPoint((PointType) abstractGeometryType);

        } else if (abstractGeometryType instanceof AbstractCurveType) {
            return gmlToLineConvertor.convertAbstractCurve((AbstractCurveType) abstractGeometryType);
        } else if (abstractGeometryType instanceof MultiSurfaceType) {
            return gmlToSurfaceConvertor.convertMultiSurface((MultiSurfaceType) abstractGeometryType);
        } else {
            throw new IllegalArgumentException("Geometry type not supported: "
                    + abstractGeometryType.getClass());
        }
    }

}
