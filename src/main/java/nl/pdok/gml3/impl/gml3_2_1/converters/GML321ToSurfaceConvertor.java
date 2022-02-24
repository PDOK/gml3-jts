package nl.pdok.gml3.impl.gml3_2_1.converters;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.locationtech.jts.algorithm.Orientation;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import net.opengis.gml.v_3_2_1.AbstractRingPropertyType;
import net.opengis.gml.v_3_2_1.AbstractSurfacePatchType;
import net.opengis.gml.v_3_2_1.AbstractSurfaceType;
import net.opengis.gml.v_3_2_1.MultiSurfaceType;
import net.opengis.gml.v_3_2_1.PolygonPatchType;
import net.opengis.gml.v_3_2_1.PolygonType;
import net.opengis.gml.v_3_2_1.SurfaceArrayPropertyType;
import net.opengis.gml.v_3_2_1.SurfacePatchArrayPropertyType;
import net.opengis.gml.v_3_2_1.SurfacePropertyType;
import net.opengis.gml.v_3_2_1.SurfaceType;

import nl.pdok.gml3.exceptions.GeometryException;
import nl.pdok.gml3.exceptions.GeometryValidationErrorType;
import nl.pdok.gml3.exceptions.InvalidGeometryException;
import nl.pdok.gml3.exceptions.UnsupportedGeometrySpecificationException;

/**
 * <p>GML321ToSurfaceConvertor class.</p>
 *
 * @author GinkeM
 * @version $Id: $Id
 */
public class GML321ToSurfaceConvertor {

    private final GeometryFactory geometryFactory;
    private final GML321ToLineConvertor gmlToLineConvertor;

    /**
     * <p>Constructor for GML321ToSurfaceConvertor.</p>
     *
     * @param geometryFactory a {@link org.locationtech.jts.geom.GeometryFactory} object.
     * @param gmlToLineConvertor a {@link nl.pdok.gml3.impl.gml3_2_1.converters.GML321ToLineConvertor} object.
     */
    public GML321ToSurfaceConvertor(GeometryFactory geometryFactory, GML321ToLineConvertor gmlToLineConvertor) {
        this.geometryFactory = geometryFactory;
        this.gmlToLineConvertor = gmlToLineConvertor;
    }

    /**
     * <p>convertMultiSurface.</p>
     *
     * @param surfaces a {@link net.opengis.gml.v_3_2_1.MultiSurfaceType} object.
     * @return a {@link org.locationtech.jts.geom.Geometry} object.
     * @throws nl.pdok.gml3.exceptions.GeometryException if any.
     */
    public Geometry convertMultiSurface(MultiSurfaceType surfaces) throws GeometryException {
        List<Polygon> polygons = new ArrayList<>();
        for (SurfacePropertyType surface : surfaces.getSurfaceMember()) {
            JAXBElement<? extends AbstractSurfaceType> element = surface.getAbstractSurface();
            Geometry result = convertElementContainingSurface(element);
            addResultingPolygonsToList(result, polygons);
        }

        SurfaceArrayPropertyType array = surfaces.getSurfaceMembers();
        if (array != null) {
            List<JAXBElement<? extends AbstractSurfaceType>> arraySurfaceMembers = array.getAbstractSurface();
            if (arraySurfaceMembers != null) {
                for (JAXBElement<? extends AbstractSurfaceType> surfaceMember : arraySurfaceMembers) {
                    Geometry result = convertElementContainingSurface(surfaceMember);
                    addResultingPolygonsToList(result, polygons);
                }
            }
        }

        return convertPolygonListToMuliPolygonOrSinglePolygon(polygons);
    }

    private Geometry convertPolygonListToMuliPolygonOrSinglePolygon(List<Polygon> polygons)
            throws GeometryException {
        if (polygons.size() < 1) {
            throw new InvalidGeometryException(
                    GeometryValidationErrorType.MULTI_SURFACE_DID_NOT_CONTAIN_MEMBERS, null);
        } else if (polygons.size() == 1) {
            return polygons.get(0);
        } else {
            MultiPolygon multi = new MultiPolygon(polygons.toArray(new Polygon[]{}), geometryFactory);
            return multi;
        }
    }

    private Geometry convertElementContainingSurface(
            JAXBElement<? extends AbstractSurfaceType> surface) throws GeometryException {
        if (surface != null) {
            AbstractSurfaceType abstractSurfaceType = surface.getValue();
            if (abstractSurfaceType != null) {
                return convertSurface(abstractSurfaceType);
            }
        }

        return null;
    }

    private void addResultingPolygonsToList(Geometry geometry, List<Polygon> polygons) {
        if (geometry != null) {
            if (geometry instanceof MultiPolygon) {
                MultiPolygon collection = (MultiPolygon) geometry;
                for (int i = 0; i < collection.getNumGeometries(); i++) {
                    polygons.add((Polygon) collection.getGeometryN(i));
                }
            } else {
                polygons.add((Polygon) geometry);
            }
        }
    }

    /**
     * <p>convertSurface.</p>
     *
     * @param abstractSurface a {@link net.opengis.gml.v_3_2_1.AbstractSurfaceType} object.
     * @return a {@link org.locationtech.jts.geom.Geometry} object.
     * @throws nl.pdok.gml3.exceptions.GeometryException if any.
     */
    public Geometry convertSurface(AbstractSurfaceType abstractSurface)
            throws GeometryException {
        if (abstractSurface instanceof SurfaceType) {
            List<Polygon> polygons = new ArrayList<>();
            SurfaceType surface = (SurfaceType) abstractSurface;
            SurfacePatchArrayPropertyType patches = surface.getPatches().getValue();
            // opmerking multipliciteit 2 of meer is afgevangen door xsd
            for (int i = 0; i < patches.getAbstractSurfacePatch().size(); i++) {
                AbstractSurfacePatchType abstractPatch = patches
                        .getAbstractSurfacePatch().get(i).getValue();
                if (abstractPatch instanceof PolygonPatchType) {
                    PolygonPatchType polygonPatch = (PolygonPatchType) abstractPatch;
                    Polygon polygon = convertPolygonPatch(polygonPatch);
                    polygons.add(polygon);

                } else {
                    throw new UnsupportedGeometrySpecificationException(
                            "Only polygon patch type is supported");
                }
            }

            return convertPolygonListToMuliPolygonOrSinglePolygon(polygons);
        } else if (abstractSurface instanceof PolygonType) {
            PolygonType polygonType = (PolygonType) abstractSurface;
            if (polygonType.getExterior() == null) {
                throw new InvalidGeometryException(GeometryValidationErrorType.POLYGON_HAS_NO_EXTERIOR, null);
            }

            AbstractRingPropertyType abstractRing = polygonType.getExterior();
            LinearRing shell = gmlToLineConvertor.translateAbstractRing(abstractRing);
            LinearRing[] innerRings = new LinearRing[polygonType.getInterior().size()];
            for (int i = 0; i < polygonType.getInterior().size(); i++) {
                innerRings[i] = gmlToLineConvertor.translateAbstractRing(polygonType.getInterior()
                        .get(i));
            }

            return geometryFactory.createPolygon(shell, innerRings);

        } else {
            throw new UnsupportedGeometrySpecificationException(
                    "Only Surface and Polygon are "
                    + "supported as instances of _Surface");
        }

    }

    /**
     * <p>convertPolygonPatch.</p>
     *
     * @param polygonPatch a {@link net.opengis.gml.v_3_2_1.PolygonPatchType} object.
     * @return a {@link org.locationtech.jts.geom.Polygon} object.
     * @throws nl.pdok.gml3.exceptions.GeometryException if any.
     */
    public Polygon convertPolygonPatch(PolygonPatchType polygonPatch)
            throws GeometryException {
        if (polygonPatch.getExterior() == null) {
            throw new InvalidGeometryException(GeometryValidationErrorType.POLYGON_HAS_NO_EXTERIOR, null);
        }

        AbstractRingPropertyType abstractRing = polygonPatch.getExterior();
        LinearRing exteriorShell = gmlToLineConvertor.translateAbstractRing(abstractRing);
        if (!Orientation.isCCW(exteriorShell.getCoordinates())) {

            // Try to reverse it and try again
            exteriorShell = reverseRing(exteriorShell);
            if (!Orientation.isCCW(exteriorShell.getCoordinates())) {
                throw new InvalidGeometryException(
                        GeometryValidationErrorType.OUTER_RING_IS_NOT_CCW, null);
            }
        }

        LinearRing[] innerRings = new LinearRing[polygonPatch.getInterior().size()];
        for (int i = 0; i < polygonPatch.getInterior().size(); i++) {
            innerRings[i] = gmlToLineConvertor.translateAbstractRing(polygonPatch.getInterior()
                    .get(i));
            if (Orientation.isCCW(innerRings[i].getCoordinates())) {
                throw new InvalidGeometryException(
                        GeometryValidationErrorType.INNER_RING_IS_CCW, null);
            }

        }

        return geometryFactory.createPolygon(exteriorShell, innerRings);
    }

    /**
     * Does what it says, reverses the order of the Coordinates in the ring.
     * <p>
     * This is different then lr.reverses() in that a copy is produced using a
     * new coordinate sequence.
     * </p>
     *
     * @param lr The ring to reverse.
     * @return A new ring with the reversed Coordinates.
     */
    public static final LinearRing reverseRing(LinearRing lr) {
        GeometryFactory gf = lr.getFactory();
        CoordinateSequenceFactory csf = gf.getCoordinateSequenceFactory();

        CoordinateSequence csOrig = lr.getCoordinateSequence();
        int numPoints = csOrig.size();
        int dimensions = csOrig.getDimension();
        CoordinateSequence csNew = csf.create(numPoints, dimensions);

        for (int i = 0; i < numPoints; i++) {
            for (int j = 0; j < dimensions; j++) {
                csNew.setOrdinate(numPoints - 1 - i, j, csOrig.getOrdinate(i, j));
            }
        }

        return gf.createLinearRing(csNew);
    }
}
