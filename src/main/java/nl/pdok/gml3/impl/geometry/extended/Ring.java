package nl.pdok.gml3.impl.geometry.extended;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A ring contains LineStrings (which may also be Arcs). This construction extends LinearRing so it 
 * can still be used by JTS. If the arc segments are stored by a datasource an instanceof opertation
 * can be used to read the segments which are Arcs, so they can be stored natively.
 * @author GinkeM
 */
public class Ring extends LinearRing {

	private static final long serialVersionUID = 7685049284222295173L;
	private CompoundLineString compoundLineString;

	protected Ring(CoordinateSequence coordinates, GeometryFactory factory, LineString... segments) {
		super(coordinates, factory);
		this.compoundLineString = new CompoundLineString(coordinates, factory, segments);
	}

	public String getGeometryType() {
		return "Ring";
	}

	public Geometry reverse() {
		return createRing(factory, (CompoundLineString) compoundLineString.reverse());
	}
	
	public static Ring createRing(GeometryFactory factory, CompoundLineString compoundLineString) {
		LineString[] segments = compoundLineString.getSegments();
		return createRing(factory, segments);
		
	}

	public static Ring createRing(GeometryFactory factory, LineString... segments) {
		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		for(LineString segment : segments) {
			coordinates.addAll(Arrays.asList(segment.getCoordinates()));
		}
		
		CoordinateSequence coordinateSequence = new CoordinateArraySequence(
				coordinates.toArray(new Coordinate[]{}));
		return new Ring(coordinateSequence, factory, segments);
		
	}

	public LineString[] getSegments() {
		return compoundLineString.getSegments();
		
	}

}

