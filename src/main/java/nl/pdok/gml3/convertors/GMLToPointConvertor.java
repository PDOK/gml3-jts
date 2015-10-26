package nl.pdok.gml3.convertors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.opengis.gml_3_1_1.DirectPositionType;
import org.opengis.gml_3_1_1.MultiPointType;
import org.opengis.gml_3_1_1.PointArrayPropertyType;
import org.opengis.gml_3_1_1.PointPropertyType;
import org.opengis.gml_3_1_1.PointType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import nl.pdok.gml3.exceptions.CoordinateMaxScaleExceededException;
import nl.pdok.gml3.exceptions.DeprecatedGeometrySpecificationException;
import nl.pdok.gml3.exceptions.GeometryException;
import nl.pdok.gml3.exceptions.GeometryValidationErrorType;
import nl.pdok.gml3.exceptions.InvalidGeometryException;

/**
 * @author GinkeM
 */
public class GMLToPointConvertor {

	private static final int MAX_SCALE = 99; // mm
	private static final int REQUIRED_NUMBER_OF_ORDINATES = 2; // 2 ordinaten (2D)
	private GeometryFactory geometryFactory;

	public GMLToPointConvertor(GeometryFactory geometryFactory) {
		this.geometryFactory = geometryFactory;
	}
	
	private boolean ordinatesArePairs(int numOrdinates) {
		return (numOrdinates%REQUIRED_NUMBER_OF_ORDINATES == 0);
	}

	public CoordinateArraySequence translateOrdinates(List<String> ordinates)
			throws InvalidGeometryException, CoordinateMaxScaleExceededException {
		if (ordinates == null || ordinates.size() < REQUIRED_NUMBER_OF_ORDINATES) {
			throw new InvalidGeometryException(GeometryValidationErrorType.EMPTY_GEOMETRY, null);
		}
		
		if(!ordinatesArePairs(ordinates.size())) {
			throw new InvalidGeometryException(GeometryValidationErrorType.INVALID_COORDINATE, createCoordinateFromFirstTwoOrdinates(ordinates));
		}

		CoordinateArraySequence sequence = new CoordinateArraySequence(ordinates.size() / 2);
		int i = 0;
		for (String ordinate : ordinates) {
			BigDecimal bd = new BigDecimal(ordinate);
			int ordinateIndex = i % 2;
			int index = (i / 2);
			sequence.setOrdinate(index, ordinateIndex, bd.doubleValue());
			i++;
		}
		
		return sequence;

	}

	private Coordinate createCoordinateFromFirstTwoOrdinates(List<String> ordinates) {
		return new Coordinate(Double.valueOf(ordinates.get(0)), Double.valueOf(ordinates.get(1)));
	}

	public Point convertPoint(PointType point) throws GeometryException {
		DirectPositionType pos = point.getPos();
		if(point.getPos() == null) {
			throw new DeprecatedGeometrySpecificationException(
				"Geen post list voor ring gespecificeerd");
		}
		List<String> values = pos.getValue();
		if(values.size() != REQUIRED_NUMBER_OF_ORDINATES) {
			throw new InvalidGeometryException(GeometryValidationErrorType.POINT_INVALID_NUMBER_OF_ORDINATES, null);
		}
		
		CoordinateArraySequence sequence = translateOrdinates(values);
		return geometryFactory.createPoint(sequence);
	}

	public Geometry convertMultiPoint(MultiPointType multipointType) throws GeometryException {
		List<Point> points = new ArrayList<Point>();
		PointArrayPropertyType array = multipointType.getPointMembers();
		if(array != null) {
			for(PointType point : array.getPoint()) {
				points.add(convertPoint(point));
			}
		}
		
		for(PointPropertyType type : multipointType.getPointMember()) {
			PointType pointType = type.getPoint();
			if(pointType != null) {
				points.add(convertPoint(pointType));
			}
		}
		
		if(points.size() < 1) {
			throw new InvalidGeometryException(GeometryValidationErrorType.MULTI_POINT_DID_NOT_CONTAIN_MEMBERS, null);
		}
		else if(points.size() == 1) {
			return points.get(0);
		}
		else {
			return new MultiPoint(points.toArray(new Point[] {}), geometryFactory);
		}
	}

}
