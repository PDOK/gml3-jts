package nl.pdok.gml3_1_1_2.geometry.extended;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author GinkeM
 */
public class CompoundLineString extends LineString {
	
	private static final long serialVersionUID = -1458579454263430369L;
	private LineString[] segments;

	protected CompoundLineString(CoordinateSequence coordinates, GeometryFactory factory,
                                 LineString... segments) {
		super(coordinates, factory);
		this.segments = segments;
	}

	public String getGeometryType() {
		return "CompoundLineString";
	}

	public Geometry reverse() {
		LineString[] seg = new LineString[segments.length];
		for(int i=seg.length, j=0; i>=0 && j<seg.length; i--, j++) {
			LineString segment = (LineString) segments[i].reverse();
			seg[j] = segment;
		}
		
		return createCompoundLineString(factory, seg);
	}
	
	public LineString[] getSegments() {
		return segments;
	}
	
	public static CompoundLineString createCompoundLineString(GeometryFactory factory,
			LineString... segments) {
		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		for(int i=0; i<segments.length; i++) {
			LineString segment = segments[i];
			Coordinate[] coordinateArray = segment.getCoordinates();
			if(i != segments.length-1) {
				int lengtWithoutLastElement = coordinateArray.length-1;
				Coordinate[] coordinateArray2 = new Coordinate[lengtWithoutLastElement];
				System.arraycopy(coordinateArray, 0, coordinateArray2, 0, lengtWithoutLastElement);
				coordinates.addAll(Arrays.asList(coordinateArray2));
			}
			else {
				coordinates.addAll(Arrays.asList(coordinateArray));
			}
			
		}
		
		CoordinateSequence coordinateSequence = new CoordinateArraySequence(
				coordinates.toArray(new Coordinate[]{}));
		CompoundLineString curveLineString = new CompoundLineString(coordinateSequence, factory,
				segments);
		return curveLineString;
	}

}
