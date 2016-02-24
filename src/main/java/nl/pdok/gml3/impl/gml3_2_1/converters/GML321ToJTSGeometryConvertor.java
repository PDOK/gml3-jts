package nl.pdok.gml3.impl.gml3_2_1.converters;

import com.vividsolutions.jts.geom.Geometry;
import net.opengis.gml.v_3_2_1.AbstractCurveType;
import net.opengis.gml.v_3_2_1.AbstractGeometryType;
import net.opengis.gml.v_3_2_1.AbstractSurfaceType;
import net.opengis.gml.v_3_2_1.MultiPointType;
import net.opengis.gml.v_3_2_1.MultiSurfaceType;
import net.opengis.gml.v_3_2_1.PointType;
import nl.pdok.gml3.exceptions.GeometryException;
import nl.pdok.gml3.impl.geometry.extended.ExtendedGeometryFactory;

/**
 * Converteerd van gml3.2.1 naar JTS polygoon
 *
 * @author GinkeM
 */
public class GML321ToJTSGeometryConvertor {

    private final GML321ToPointConvertor gmlToPointConvertor;
    private final GML321ToSurfaceConvertor gmlToSurfaceConvertor;
    private final GML321ToLineConvertor gmlToLineConvertor;

    public GML321ToJTSGeometryConvertor(ExtendedGeometryFactory geometryFactory) {
        gmlToPointConvertor = new GML321ToPointConvertor(geometryFactory);
        gmlToLineConvertor = new GML321ToLineConvertor(geometryFactory, gmlToPointConvertor);
        gmlToSurfaceConvertor = new GML321ToSurfaceConvertor(geometryFactory, gmlToLineConvertor);
    }

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
