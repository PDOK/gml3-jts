package nl.pdok.gml3.impl.gml3_2_1.converters;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import net.opengis.gml.v_3_2_1.*;
import nl.pdok.gml3.exceptions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>GML321ToPointConvertor class.</p>
 *
 * @author GinkeM
 * @version $Id: $Id
 */
public class GML321ToPointConvertor {

    private GeometryFactory geometryFactory;

    /**
     * <p>Constructor for GML321ToPointConvertor.</p>
     *
     * @param geometryFactory a {@link org.locationtech.jts.geom.GeometryFactory} object.
     */
    public GML321ToPointConvertor(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    /**
     * <p>translateOrdinates.</p>
     *
     * @param ordinates a {@link java.util.List} object.
     * @param dimension a {@link int}.
     * @return a {@link org.locationtech.jts.geom.impl.CoordinateArraySequence} object.
     * @throws nl.pdok.gml3.exceptions.InvalidGeometryException if any.
     * @throws nl.pdok.gml3.exceptions.CoordinateMaxScaleExceededException if any.
     */
    public CoordinateArraySequence translateOrdinates(List<Double> ordinates, int dimension)
            throws InvalidGeometryException, CoordinateMaxScaleExceededException {
        if (ordinates == null || ordinates.size() < dimension) {
            throw new InvalidGeometryException(GeometryValidationErrorType.EMPTY_GEOMETRY, null);
        }

        if (ordinates.size()%dimension != 0) {
            throw new InvalidGeometryException(GeometryValidationErrorType.INVALID_COORDINATE, createCoordinateFromFirstTwoOrdinates(ordinates));
        }

        CoordinateArraySequence sequence = new CoordinateArraySequence(ordinates.size() / dimension);
        int i = 0;
        for (Double ordinate : ordinates) {
            BigDecimal bd = new BigDecimal(ordinate);
            int ordinateIndex = i % dimension;
            int index = (i / dimension);
            sequence.setOrdinate(index, ordinateIndex, bd.doubleValue());
            i++;
        }

        return sequence;

    }

    public CoordinateArraySequence translateOrdinates(DirectPositionListType posList) throws CoordinateMaxScaleExceededException, InvalidGeometryException {
        int dimension = posList.getSrsDimension() != null ? posList.getSrsDimension().intValue() : 2;

        return translateOrdinates(posList.getValue(), dimension);
    }

    private Coordinate createCoordinateFromFirstTwoOrdinates(List<Double> ordinates) {
        return new Coordinate(ordinates.get(0), ordinates.get(1));
    }

    /**
     * <p>convertPoint.</p>
     *
     * @param point a {@link net.opengis.gml.v_3_2_1.PointType} object.
     * @return a {@link org.locationtech.jts.geom.Point} object.
     * @throws nl.pdok.gml3.exceptions.GeometryException if any.
     */
    public Point convertPoint(PointType point) throws GeometryException {
        DirectPositionType pos = point.getPos();
        List<Double> values = pos.getValue();
        int dimension = pos.getSrsDimension() != null ? pos.getSrsDimension().intValue() : values.size();
        if (point.getPos() == null) {
            throw new DeprecatedGeometrySpecificationException(
                    "Geen post list voor ring gespecificeerd");
        }
        if (values.size() != dimension) {
            throw new InvalidGeometryException(GeometryValidationErrorType.POINT_INVALID_NUMBER_OF_ORDINATES, null);
        }

        CoordinateArraySequence sequence = translateOrdinates(values, dimension);
        return geometryFactory.createPoint(sequence);
    }

    /**
     * <p>convertMultiPoint.</p>
     *
     * @param multipointType a {@link net.opengis.gml.v_3_2_1.MultiPointType} object.
     * @return a {@link org.locationtech.jts.geom.Geometry} object.
     * @throws nl.pdok.gml3.exceptions.GeometryException if any.
     */
    public Geometry convertMultiPoint(MultiPointType multipointType) throws GeometryException {
        List<Point> points = new ArrayList<>();
        PointArrayPropertyType array = multipointType.getPointMembers();
        if (array != null) {
            for (PointType point : array.getPoint()) {
                points.add(convertPoint(point));
            }
        }

        for (PointPropertyType type : multipointType.getPointMember()) {
            PointType pointType = type.getPoint();
            if (pointType != null) {
                points.add(convertPoint(pointType));
            }
        }

        if (points.size() < 1) {
            throw new InvalidGeometryException(GeometryValidationErrorType.MULTI_POINT_DID_NOT_CONTAIN_MEMBERS, null);
        } else if (points.size() == 1) {
            return points.get(0);
        } else {
            return new MultiPoint(points.toArray(new Point[]{}), geometryFactory);
        }
    }

}
