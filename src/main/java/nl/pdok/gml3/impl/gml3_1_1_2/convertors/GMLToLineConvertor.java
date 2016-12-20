package nl.pdok.gml3.impl.gml3_1_1_2.convertors;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import nl.pdok.gml3.exceptions.DeprecatedGeometrySpecificationException;
import nl.pdok.gml3.exceptions.GeometryException;
import nl.pdok.gml3.exceptions.GeometryValidationErrorType;
import nl.pdok.gml3.exceptions.InvalidGeometryException;
import nl.pdok.gml3.exceptions.UnsupportedGeometrySpecificationException;
import nl.pdok.gml3.impl.geometry.extended.ArcLineString;
import nl.pdok.gml3.impl.geometry.extended.CompoundLineString;
import nl.pdok.gml3.impl.geometry.extended.Ring;

import org.opengis.gml_3_1_1.*;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>GMLToLineConvertor class.</p>
 *
 * @author GinkeM
 * @version $Id: $Id
 */
public class GMLToLineConvertor {
	
	private static final int NUMBER_OF_COORDINATES_NEEDED_FOR_ARC = 3;
	private static final int NUMBER_OF_COORDINATES_NEEDED_FOR_RING = 4;
	private static final int NUMBER_OF_ORDINATES_NEEDED_FOR_LINE_PER_DIMENSION = 2;
	private GeometryFactory geometryFactory;
	private GMLToPointConvertor gmlToPointConvertor;
	
	/**
	 * <p>Constructor for GMLToLineConvertor.</p>
	 *
	 * @param geometryFactory a {@link com.vividsolutions.jts.geom.GeometryFactory} object.
	 * @param gmlToPointConvertor a {@link nl.pdok.gml3.impl.gml3_1_1_2.convertors.GMLToPointConvertor} object.
	 */
	public GMLToLineConvertor(GeometryFactory geometryFactory, 
			GMLToPointConvertor gmlToPointConvertor) {
		this.geometryFactory = geometryFactory;
		this.gmlToPointConvertor = gmlToPointConvertor;
	}
	
	/**
	 * <p>translateAbstractRing.</p>
	 *
	 * @param abstractRingPropertyType a {@link org.opengis.gml_3_1_1.AbstractRingPropertyType} object.
	 * @return a {@link com.vividsolutions.jts.geom.LinearRing} object.
	 * @throws nl.pdok.gml3.exceptions.GeometryException if any.
	 */
	public LinearRing translateAbstractRing(AbstractRingPropertyType abstractRingPropertyType) throws GeometryException {
		AbstractRingType abstractRing = abstractRingPropertyType.getRing().getValue(); 
		if (abstractRing instanceof LinearRingType) {
			return translateLinearRingType((LinearRingType) abstractRing);

		} 
		else if (abstractRing instanceof RingType) {
			return translateRing((RingType) abstractRing);
		} 
		else {
			// should not even validate
			throw new UnsupportedGeometrySpecificationException("Invalid ring declared");
		}

	}

	private LinearRing translateRing(RingType ring) throws GeometryException {
		List<LineString> segments = new ArrayList<LineString>();
		// (according to the xsd at least one curve member is mandatory)
		for(int i =0 ; i<ring.getCurveMember().size(); i++) {
			CurvePropertyType curveProperty = ring.getCurveMember().get(i);
			JAXBElement<? extends AbstractCurveType> elementWithCurve = curveProperty.getCurve();
			if(elementWithCurve == null) {
				throw new InvalidGeometryException(GeometryValidationErrorType.CURVE_CONTAINS_NO_SEGMENTS, null);
			}
			AbstractCurveType abstractCurve = elementWithCurve.getValue();
			if (abstractCurve instanceof CurveType) {
				segments.addAll(translateCurveTypeToSegments((CurveType) abstractCurve));
			} 
			else if (abstractCurve instanceof LineStringType) {
				LineStringType line = (LineStringType) abstractCurve;
				segments.add(convertLineString(line));
				
			} 
			else {
				throw new UnsupportedGeometrySpecificationException("Only linestrings and curves are supported for rings");
			}
			
		}

		int dimension = ring.getSrsDimension() != null ? ring.getSrsDimension() : 2;

		LineString[] array = segments.toArray(new LineString[] {});
		if (!isClosed(array, dimension)) {
            Coordinate firstCoordinate = array.length == 0 ? null : array[0].getCoordinate();
			throw new InvalidGeometryException(GeometryValidationErrorType.RING_NOT_CLOSED, firstCoordinate);
		}

		return Ring.createRing(geometryFactory, array);
	}

	private LinearRing translateLinearRingType(LinearRingType ring) throws GeometryException {
		if (ring.getPosList() == null) {
			throw new DeprecatedGeometrySpecificationException("Geen post list voor ring gespecificeerd");
		}

		CoordinateArraySequence sequence = gmlToPointConvertor.translateOrdinates(ring.getPosList());
		int length = sequence.size();
        Coordinate firstCoordinate = length == 0 ? null : sequence.getCoordinate(0);

		if (length < NUMBER_OF_COORDINATES_NEEDED_FOR_RING) {
			throw new InvalidGeometryException(GeometryValidationErrorType.TOO_FEW_POINTS, firstCoordinate);
		}
		
		if (!isClosed(sequence)) {
			throw new InvalidGeometryException(GeometryValidationErrorType.RING_NOT_CLOSED, firstCoordinate);
		}

		return geometryFactory.createLinearRing(sequence);
	}
	
	private void valideerSegmentArray(CurveSegmentArrayPropertyType array) throws GeometryException {
		int size =  array.getCurveSegment().size();
		if(size == 0) {
			throw new InvalidGeometryException(GeometryValidationErrorType.CURVE_CONTAINS_NO_SEGMENTS, null);
		}
	}

	/**
	 * <p>translateCurveTypeToSegments.</p>
	 *
	 * @param curve a {@link org.opengis.gml_3_1_1.CurveType} object.
	 * @return a {@link java.util.List} object.
	 * @throws nl.pdok.gml3.exceptions.GeometryException if any.
	 */
	public List<LineString> translateCurveTypeToSegments(CurveType curve) throws GeometryException {
		List<LineString> list = new ArrayList<LineString>();
		CurveSegmentArrayPropertyType array = curve.getSegments();
		valideerSegmentArray(array);
		int size =  array.getCurveSegment().size();
		
		for (int i = 0; i < size; i++) {
			AbstractCurveSegmentType curveProperty = array.getCurveSegment().get(i).getValue();
			
			if (curveProperty instanceof LineStringSegmentType) {
				LineStringSegmentType line = (LineStringSegmentType) curveProperty;
				if (line.getPosList() == null) {
					throw new DeprecatedGeometrySpecificationException(
							"Geen poslist voor linestringsegment binnen curve gespecificeerd");
				}
				
				CoordinateArraySequence sequence = gmlToPointConvertor.translateOrdinates(line.getPosList());
				LineString lineString = new LineString(sequence, geometryFactory);
				list.add(lineString);
			} 
			else if (curveProperty instanceof ArcType && !(curveProperty instanceof CircleType)) {
				ArcType arc = (ArcType) curveProperty;
				list.add(translateArc(arc));
			} 
			else {
				throw new UnsupportedGeometrySpecificationException(
						"Only arc and linestring are supported within a Curve segment");
			}

		}
		return list;
	}
	
	private ArcLineString translateArc(ArcType arc) throws GeometryException {
		if (arc.getInterpolation() != CurveInterpolationType.CIRCULAR_ARC_3_POINTS) {
			throw new UnsupportedGeometrySpecificationException(
					"Het arc attribuut interpolation moet circularArc3Points zijn");
		}
        CoordinateArraySequence sequence = getCoordinatesForArc(arc);
        validateArcHasThreeCoordinates(sequence);
        validateArcIsNotAStraightLine(sequence);
        return new ArcLineString(sequence, geometryFactory);
	}

    private CoordinateArraySequence getCoordinatesForArc(ArcType arc) throws GeometryException {
        if(arc.getPosList() != null) {
            return gmlToPointConvertor.translateOrdinates(arc.getPosList());
        }
        else if(arc.getPosOrPointPropertyOrPointRep() != null &&
                arc.getPosOrPointPropertyOrPointRep().size() > 0) {
            List<String> values = new ArrayList<String>();
            Iterator<JAXBElement<?>> iterator = arc.getPosOrPointPropertyOrPointRep().iterator();
            while(iterator.hasNext()) {
                Object value = iterator.next().getValue();
                if(value instanceof DirectPositionType) {
                    DirectPositionType position = (DirectPositionType) value;
                    values.addAll(position.getValue());
                }
                else {
                    throw new DeprecatedGeometrySpecificationException(
                            "Geen poslist voor arc binnen curve gespecificeerd");
                }
            }
            return gmlToPointConvertor.translateOrdinates(values, 2); // could be 3 too?
        }
        else {
            throw new DeprecatedGeometrySpecificationException("Geen poslist voor arc binnen curve gespecificeerd");
        }
    }

    private void validateArcHasThreeCoordinates(CoordinateArraySequence sequence) throws InvalidGeometryException {
        if(sequence.size()!=NUMBER_OF_COORDINATES_NEEDED_FOR_ARC){
            throw new InvalidGeometryException(GeometryValidationErrorType.ARC_MUST_HAVE_THREE_COORDINATES, sequence.getCoordinate(0), null);
        }
    }

    private void validateArcIsNotAStraightLine(CoordinateArraySequence sequence) throws GeometryException {
		if(CGAlgorithms.isOnLine(sequence.getCoordinate(1), 
				new Coordinate[]{sequence.getCoordinate(0), sequence.getCoordinate(2)})) {
			throw new InvalidGeometryException(GeometryValidationErrorType.ARC_IS_A_STRAIGHT_LINE, 
					sequence.getCoordinate(1), "arc should not be a straight line");
		}
	}

	private boolean isClosed(LineString[] array, int dimension) throws InvalidGeometryException {
		int length = array.length;
		int ordinatesLength = 0;
		
		if (length != 0) {
			LineString first = array[0];
			LineString last = array[length - 1];
			Coordinate coordinate1 = first.getCoordinateN(0);
			Coordinate coordinate2 = last.getCoordinateN(last.getNumPoints() - 1);
			
			for(int i=0; i<length && ordinatesLength<NUMBER_OF_COORDINATES_NEEDED_FOR_RING;i++) {
				ordinatesLength += array[i].getNumPoints();
			}
			
			if (ordinatesLength < NUMBER_OF_COORDINATES_NEEDED_FOR_RING) {
				throw new InvalidGeometryException(GeometryValidationErrorType.TOO_FEW_POINTS, coordinate1);
			}
			
			return coordinate1.equals2D(coordinate2);
		}

		return false;
	}

	private boolean isClosed(CoordinateArraySequence sequence) {
		int length = sequence.size();

		if (length < NUMBER_OF_COORDINATES_NEEDED_FOR_RING) {
			return false;
		}
		return sequence.getCoordinate(0).equals2D(sequence.getCoordinate(length - 1));
	}

	private LineString convertLineString(LineStringType lineStringType) throws GeometryException {
		if (lineStringType.getPosList() == null) {
			throw new DeprecatedGeometrySpecificationException("Geen poslist voor lineString gespecificeerd");
		}

		int dimension = lineStringType.getSrsDimension() != null ? lineStringType.getSrsDimension() : 2;

		List<String> posList = lineStringType.getPosList().getValue();
		if(posList.size() < NUMBER_OF_ORDINATES_NEEDED_FOR_LINE_PER_DIMENSION * dimension) {
			throw new InvalidGeometryException(GeometryValidationErrorType.TOO_FEW_POINTS, null);
		}
		
		CoordinateArraySequence sequence = gmlToPointConvertor.translateOrdinates(lineStringType.getPosList());
		return new LineString(sequence, geometryFactory);
	}

	/**
	 * <p>convertAbstractCurve.</p>
	 *
	 * @param abstractGeometryType a {@link org.opengis.gml_3_1_1.AbstractCurveType} object.
	 * @return a {@link com.vividsolutions.jts.geom.LineString} object.
	 * @throws nl.pdok.gml3.exceptions.GeometryException if any.
	 */
	public LineString convertAbstractCurve(AbstractCurveType abstractGeometryType) throws GeometryException {
		if(abstractGeometryType instanceof LineStringType) {
			return convertLineString((LineStringType) abstractGeometryType);
		}
		else if(abstractGeometryType instanceof CurveType) {
			List<LineString> segments = translateCurveTypeToSegments((CurveType) abstractGeometryType);
			LineString[] array = segments.toArray(new LineString[] {});
			return CompoundLineString.createCompoundLineString(geometryFactory, array);
		}
		else {
			throw new UnsupportedGeometrySpecificationException(
					"Only arc and linestring are supported within a Curve segment");
		}
		
	}

}
