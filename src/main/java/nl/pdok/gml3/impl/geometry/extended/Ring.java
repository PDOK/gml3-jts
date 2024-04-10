package nl.pdok.gml3.impl.geometry.extended;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

/**
 * A ring contains LineStrings (which may also be Arcs). This construction extends LinearRing so it
 * can still be used by JTS. If the arc segments are stored by a datasource an instanceof opertation
 * can be used to read the segments which are Arcs, so they can be stored natively.
 *
 * @author GinkeM
 * @version $Id: $Id
 */
public class Ring extends LinearRing {

  @Serial
  private static final long serialVersionUID = 7685049284222295173L;
  private final CompoundLineString compoundLineString;

  /**
   * <p>
   * Constructor for Ring.
   * </p>
   *
   * @param coordinates a {@link org.locationtech.jts.geom.CoordinateSequence} object.
   * @param factory a {@link org.locationtech.jts.geom.GeometryFactory} object.
   * @param segments a {@link org.locationtech.jts.geom.LineString} object.
   */
  protected Ring(CoordinateSequence coordinates, GeometryFactory factory, LineString... segments) {
    super(coordinates, factory);
    this.compoundLineString = new CompoundLineString(coordinates, factory, segments);
  }

  /**
   * <p>
   * getGeometryType.
   * </p>
   *
   * @return a {@link java.lang.String} object.
   */
  @Override
  public String getGeometryType() {
    return "Ring";
  }

  /**
   * <p>
   * reverse.
   * </p>
   *
   * @return a {@link org.locationtech.jts.geom.Geometry} object.
   */
  @Override
  public LinearRing reverse() {
    return createRing(factory, compoundLineString.reverse());
  }

  /**
   * <p>
   * createRing.
   * </p>
   *
   * @param factory a {@link org.locationtech.jts.geom.GeometryFactory} object.
   * @param compoundLineString a {@link nl.pdok.gml3.impl.geometry.extended.CompoundLineString}
   *        object.
   * @return a {@link nl.pdok.gml3.impl.geometry.extended.Ring} object.
   */
  public static Ring createRing(GeometryFactory factory, CompoundLineString compoundLineString) {
    LineString[] segments = compoundLineString.getSegments();
    return createRing(factory, segments);

  }

  /**
   * <p>
   * createRing.
   * </p>
   *
   * @param factory a {@link org.locationtech.jts.geom.GeometryFactory} object.
   * @param segments a {@link org.locationtech.jts.geom.LineString} object.
   * @return a {@link nl.pdok.gml3.impl.geometry.extended.Ring} object.
   */
  public static Ring createRing(GeometryFactory factory, LineString... segments) {
    Set<Coordinate> coordinates = new LinkedHashSet<>();
    for (LineString segment : segments) {
      coordinates.addAll(Arrays.asList(segment.getCoordinates()));
    }

    List<Coordinate> coordsWithoutDuplicates = new ArrayList<>(coordinates);
    coordsWithoutDuplicates.add(coordsWithoutDuplicates.get(0));

    CoordinateSequence coordinateSequence =
        new CoordinateArraySequence(coordsWithoutDuplicates.toArray(new Coordinate[] {}));
    return new Ring(coordinateSequence, factory, segments);

  }

  /**
   * <p>
   * getSegments.
   * </p>
   *
   * @return an array of {@link org.locationtech.jts.geom.LineString} objects.
   */
  public LineString[] getSegments() {
    return compoundLineString.getSegments();

  }

}
