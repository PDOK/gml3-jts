package nl.pdok.gml3.impl.geometry.extended;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

/**
 * <p>
 * CompoundLineString class.
 * </p>
 *
 * @author GinkeM
 * @version $Id: $Id
 */
public class CompoundLineString extends LineString {

  private static final long serialVersionUID = -1458579454263430369L;
  private LineString[] segments;

  /**
   * <p>
   * Constructor for CompoundLineString.
   * </p>
   *
   * @param coordinates a {@link org.locationtech.jts.geom.CoordinateSequence} object.
   * @param factory a {@link org.locationtech.jts.geom.GeometryFactory} object.
   * @param segments a {@link org.locationtech.jts.geom.LineString} object.
   */
  protected CompoundLineString(CoordinateSequence coordinates, GeometryFactory factory,
      LineString... segments) {
    super(coordinates, factory);
    this.segments = segments;
  }

  /**
   * <p>
   * getGeometryType.
   * </p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getGeometryType() {
    return "CompoundLineString";
  }

  /**
   * <p>
   * reverse.
   * </p>
   *
   * @return a {@link org.locationtech.jts.geom.Geometry} object.
   */
  public CompoundLineString reverse() {
    LineString[] seg = new LineString[segments.length];
    for (int i = seg.length, j = 0; i >= 0 && j < seg.length; i--, j++) {
      LineString segment = (LineString) segments[i].reverse();
      seg[j] = segment;
    }

    return createCompoundLineString(factory, seg);
  }

  /**
   * <p>
   * Getter for the field <code>segments</code>.
   * </p>
   *
   * @return an array of {@link org.locationtech.jts.geom.LineString} objects.
   */
  public LineString[] getSegments() {
    return segments;
  }

  /**
   * <p>
   * createCompoundLineString.
   * </p>
   *
   * @param factory a {@link org.locationtech.jts.geom.GeometryFactory} object.
   * @param segments a {@link org.locationtech.jts.geom.LineString} object.
   * @return a {@link nl.pdok.gml3.impl.geometry.extended.CompoundLineString} object.
   */
  public static CompoundLineString createCompoundLineString(GeometryFactory factory,
      LineString... segments) {
    List<Coordinate> coordinates = new ArrayList<Coordinate>();
    for (int i = 0; i < segments.length; i++) {
      LineString segment = segments[i];
      Coordinate[] coordinateArray = segment.getCoordinates();
      if (i != segments.length - 1) {
        int lengtWithoutLastElement = coordinateArray.length - 1;
        Coordinate[] coordinateArray2 = new Coordinate[lengtWithoutLastElement];
        System.arraycopy(coordinateArray, 0, coordinateArray2, 0, lengtWithoutLastElement);
        coordinates.addAll(Arrays.asList(coordinateArray2));
      } else {
        coordinates.addAll(Arrays.asList(coordinateArray));
      }

    }

    CoordinateSequence coordinateSequence =
        new CoordinateArraySequence(coordinates.toArray(new Coordinate[] {}));
    CompoundLineString curveLineString =
        new CompoundLineString(coordinateSequence, factory, segments);
    return curveLineString;
  }

}
