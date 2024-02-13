package nl.pdok.gml3.impl.gml3_2_1.converters;

import org.locationtech.jts.geom.Geometry;
import net.opengis.gml.v_3_2_1.*;
import nl.pdok.gml3.exceptions.GeometryException;
import nl.pdok.gml3.impl.geometry.extended.ExtendedGeometryFactory;

/**
 * Converteerd van gml3.2.1 naar JTS polygoon
 *
 * @author GinkeM
 * @version $Id: $Id
 */
public class GML321ToJTSGeometryConvertor {

    private final GML321ToPointConvertor gmlToPointConvertor;
    private final GML321ToSurfaceConvertor gmlToSurfaceConvertor;
    private final GML321ToLineConvertor gmlToLineConvertor;

    /**
     * <p>Constructor for GML321ToJTSGeometryConvertor.</p>
     *
     * @param geometryFactory a {@link nl.pdok.gml3.impl.geometry.extended.ExtendedGeometryFactory} object.
     */
    public GML321ToJTSGeometryConvertor(ExtendedGeometryFactory geometryFactory) {
        gmlToPointConvertor = new GML321ToPointConvertor(geometryFactory);
        gmlToLineConvertor = new GML321ToLineConvertor(geometryFactory, gmlToPointConvertor);
        gmlToSurfaceConvertor = new GML321ToSurfaceConvertor(geometryFactory, gmlToLineConvertor);
    }

    /**
     * <p>convertGeometry.</p>
     *
     * @param abstractGeometryType a {@link net.opengis.gml.v_3_2_1.AbstractGeometryType} object.
     * @return a {@link org.locationtech.jts.geom.Geometry} object.
     * @throws nl.pdok.gml3.exceptions.GeometryException if any.
     */
    public Geometry convertGeometry(AbstractGeometryType abstractGeometryType) throws GeometryException {
        if (abstractGeometryType instanceof AbstractSurfaceType surfaceType) {
            return gmlToSurfaceConvertor.convertSurface(surfaceType);

        } else if (abstractGeometryType instanceof MultiPointType multiPointType) {
            return gmlToPointConvertor.convertMultiPoint(multiPointType);
        } else if (abstractGeometryType instanceof PointType pointType) {
            return gmlToPointConvertor.convertPoint(pointType);
        } else if (abstractGeometryType instanceof AbstractCurveType curveType) {
            return gmlToLineConvertor.convertAbstractCurve(curveType);
        } else if (abstractGeometryType instanceof MultiSurfaceType multiSurfaceType) {
            return gmlToSurfaceConvertor.convertMultiSurface(multiSurfaceType);
        } else if (abstractGeometryType instanceof MultiCurveType multiCurveType) {
            return gmlToLineConvertor.convertMultiCurve(multiCurveType);
        } else {
            throw new IllegalArgumentException("Geometry type not supported: "
                    + abstractGeometryType.getClass());
        }
    }

}
