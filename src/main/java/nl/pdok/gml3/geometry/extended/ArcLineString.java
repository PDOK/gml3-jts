package nl.pdok.gml3.geometry.extended;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

/**
 * Subclass of line string for storing arcs. JTS does not support arcs, so when the JTS is used on
 * arcs, the arc is densified to linear lines. This class was written to facilitate storing of Arcs
 * in the Oracle database (and not to check the topology of arcs)
 * @author GinkeM
 */
public class ArcLineString extends LineString {

	private static final long serialVersionUID = 5858160826964840982L;
	private CoordinateSequence densifiedPoints = null;
	private static final int NUMBER_OF_ORDINATES = 3;

	public ArcLineString(CoordinateSequence points, GeometryFactory factory) {
		super(points, factory);
	}
	
	/**
	 * Get the real non-densified points (densification is used to create
	 * @return
	 */
	public Coordinate[] getArcCoordinates() {
		return points.toCoordinateArray();
	}

	@Override
	public String getGeometryType() {
		return "Arc";
	}

	@Override
	public Geometry reverse() {
		CoordinateSequence seq = (CoordinateSequence) points.clone();
		CoordinateSequences.reverse(seq);
		return getFactory().createLineString(seq);
	}

	@Override
	public boolean equalsExact(Geometry other, double tolerance) {
		if (!isEquivalentClass(other)) {
			return false;
		}
		ArcLineString otherArc = (ArcLineString) other;
		if (points.size() != otherArc.points.size()) {
			return false;
		}
		for (int i = 0; i < points.size(); i++) {
			if (!equal(points.getCoordinate(i),
					otherArc.points.getCoordinate(i), tolerance)) {
				return false;
			}
		}

		return true;
	}

	@Override
	protected boolean isEquivalentClass(Geometry other) {
		return other instanceof ArcLineString;
	}

	public Object clone() {
		ArcLineString arc = (ArcLineString) super.clone();
		arc.points = (CoordinateSequence) points.clone();
		return arc;
	}
	
	/**
	 * Used instead of the call to points, so JTS thinks the arc is just a LineString with a lot of
	 * points.
	 */
	protected CoordinateSequence getDensifiedPoints() {
		if(densifiedPoints == null) {
			Coordinate[] points = getArcCoordinates();
			Coordinate[] result = null;
			
			for(int i=0;i+NUMBER_OF_ORDINATES<=points.length;i=i+2) {
				Coordinate[] arcItem = new Coordinate[NUMBER_OF_ORDINATES];
				System.arraycopy(points, i, arcItem, 0, NUMBER_OF_ORDINATES);
				CoordinateSequence sequence = ArcUtils.densify(arcItem, getFactory());
				result = ArcUtils.add(result, sequence.toCoordinateArray());
			}

			densifiedPoints = new CoordinateArraySequence(result);
		}
		
		return densifiedPoints;
	}

	@Override
	public Coordinate[] getCoordinates() {
		return getDensifiedPoints().toCoordinateArray();
	}

	@Override
	public CoordinateSequence getCoordinateSequence() {
		return getDensifiedPoints();
	}

	@Override
	public Coordinate getCoordinateN(int n) {
		return getDensifiedPoints().getCoordinate(n);
	}

	@Override
	public Point getPointN(int n) {
		return getFactory().createPoint(getDensifiedPoints().getCoordinate(n));
	}
	
	@Override
	public int getNumPoints() {
		return getDensifiedPoints().size();
	}

	@Override
	public double getLength() {
		return CGAlgorithms.length(getDensifiedPoints());
	}

	@Override
	public boolean isCoordinate(Coordinate pt) {
		for (int i = 0; i < getDensifiedPoints().size(); i++) {
			if (getDensifiedPoints().getCoordinate(i).equals(pt)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected Envelope computeEnvelopeInternal() {
		if (isEmpty()) {
			return new Envelope();
		}
		return getDensifiedPoints().expandEnvelope(new Envelope());
	}

	@Override
	public void apply(CoordinateFilter filter) {
		for (int i = 0; i < getDensifiedPoints().size(); i++) {
			filter.filter(getDensifiedPoints().getCoordinate(i));
		}
	}

	@Override
	public void apply(CoordinateSequenceFilter filter) {
		if (getDensifiedPoints().size() == 0) {
			return;
		}
		
		for (int i = 0; i < getDensifiedPoints().size(); i++) {
			filter.filter(getDensifiedPoints(), i);
			if (filter.isDone()){
				break;
			}
		}
		if (filter.isGeometryChanged()) {
			geometryChanged();
		}
	}
	
	@Override
	public void normalize() {
		for (int i = 0; i < getDensifiedPoints().size() / 2; i++) {
			int j = getDensifiedPoints().size() - 1 - i;
			// skip equal points on both ends
			if (!getDensifiedPoints().getCoordinate(i).equals(getDensifiedPoints().getCoordinate(j))) {
				if (getDensifiedPoints().getCoordinate(i).compareTo(getDensifiedPoints().getCoordinate(j)) > 0) {
					CoordinateArrays.reverse(getCoordinates());
				}
				return;
			}
		}
	}

}

