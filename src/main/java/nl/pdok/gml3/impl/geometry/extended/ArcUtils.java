package nl.pdok.gml3.impl.geometry.extended;

import org.locationtech.jts.algorithm.CGAlgorithms;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

/**
 * Utilities for handling arcs with JTS
 *
 * @author GinkeM
 * @version $Id: $Id
 */
public final class ArcUtils {
	
	private ArcUtils() {
	}
	
	private static boolean isCCW(Coordinate[] oldCoordinates) {
		Coordinate[] coordinates = new Coordinate[oldCoordinates.length+1];
		int i=0;
		for(Coordinate coordinate : oldCoordinates) {
			coordinates[i++] = coordinate;
		}
		
		coordinates[coordinates.length-1] = coordinates[0]; 
		return CGAlgorithms.isCCW(coordinates);
	}
	
	private static Coordinate[] orderCoordinates(Coordinate[] coordinates, GeometryFactory factory) {
		boolean counterClockWise = isCCW(coordinates); 
		if(!counterClockWise) {
			/*
			 * reverse the coordinates, otherwise a circle is created (the method that densifies the
			 * arc will continue over the whole arc of the circle)
			 */
			CoordinateSequence work = factory.getCoordinateSequenceFactory().create(coordinates);
			CoordinateSequence sequence = (CoordinateSequence) work.clone();
			CoordinateSequences.reverse(sequence);
			return sequence.toCoordinateArray();
		}
		
		return coordinates;

	}

	/**
	 * <p>densify.</p>
	 *
	 * @param coordinates an array of {@link org.locationtech.jts.geom.Coordinate} objects.
	 * @param factory a {@link org.locationtech.jts.geom.GeometryFactory} object.
	 * @return a {@link org.locationtech.jts.geom.CoordinateSequence} object.
	 */
	public static CoordinateSequence densify(Coordinate[] coordinates, GeometryFactory factory) {
		double tolerance = ExtendedGeometryFactory.DEFAULT_MAXIMUM_ARC_APPROXIMATION_ERROR;
		if(factory instanceof ExtendedGeometryFactory) {
			tolerance = ((ExtendedGeometryFactory) factory).getMaximumArcApproximationError();
		}
		
		LineString ls = factory.createLineString(coordinates);
		Envelope envelope = ls.getEnvelopeInternal();
		// bbox bepalen
		double threshold = tolerance * 3;
		if(envelope.getHeight() <= threshold || envelope.getWidth() <= threshold) {
			// do nothing with small arc, to prevent floating point shizzle
            return new CoordinateArraySequence(coordinates);
		}
		
		// make sure the original coordinates are not changed
		Coordinate[] orderedCoordinates = orderCoordinates(coordinates, factory);
		Coordinate[] result = SmallCircle.linearizeArc(
                orderedCoordinates[0].x, orderedCoordinates[0].y,
                orderedCoordinates[1].x, orderedCoordinates[1].y,
                orderedCoordinates[2].x, orderedCoordinates[2].y, tolerance);
		CoordinateSequence out = new CoordinateArraySequence(result);
		// if the points were reversed, the densified line needs to be reversed again
		if(!orderedCoordinates[0].equals(coordinates[0])) {
			CoordinateSequences.reverse(out);
		}
		
		return out;
		
	}
	
	/**
	 * <p>add.</p>
	 *
	 * @param c1 an array of {@link org.locationtech.jts.geom.Coordinate} objects.
	 * @param c2 an array of {@link org.locationtech.jts.geom.Coordinate} objects.
	 * @return an array of {@link org.locationtech.jts.geom.Coordinate} objects.
	 */
	public static Coordinate[] add(Coordinate[] c1, Coordinate[] c2) {
		if (c1 == null) {
            return c2;
        }
        if (c2 == null) {
            return c1;
        }
        Coordinate[] c3 = new Coordinate[c1.length + c2.length];
        System.arraycopy(c1, 0, c3, 0, c1.length);
        System.arraycopy(c2, 0, c3, c1.length, c2.length);
        return c3;
	}

}

