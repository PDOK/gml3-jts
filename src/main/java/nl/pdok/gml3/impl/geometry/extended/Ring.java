    package nl.pdok.gml3.impl.geometry.extended;

    import com.vividsolutions.jts.geom.Coordinate;
    import com.vividsolutions.jts.geom.CoordinateSequence;
    import com.vividsolutions.jts.geom.Geometry;
    import com.vividsolutions.jts.geom.GeometryFactory;
    import com.vividsolutions.jts.geom.LineString;
    import com.vividsolutions.jts.geom.LinearRing;
    import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.LinkedHashSet;
    import java.util.List;
    import java.util.Set;

    /**
     * A ring contains LineStrings (which may also be Arcs). This construction extends LinearRing so it
     * can still be used by JTS. If the arc segments are stored by a datasource an instanceof opertation
     * can be used to read the segments which are Arcs, so they can be stored natively.
     *
     * @author GinkeM
     * @version $Id: $Id
     */
    public class Ring extends LinearRing {

      private static final long serialVersionUID = 7685049284222295173L;
      private CompoundLineString compoundLineString;

      /**
       * <p>
       * Constructor for Ring.
       * </p>
       *
       * @param coordinates a {@link com.vividsolutions.jts.geom.CoordinateSequence} object.
       * @param factory a {@link com.vividsolutions.jts.geom.GeometryFactory} object.
       * @param segments a {@link com.vividsolutions.jts.geom.LineString} object.
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
      public String getGeometryType() {
        return "Ring";
      }

      /**
       * <p>
       * reverse.
       * </p>
       *
       * @return a {@link com.vividsolutions.jts.geom.Geometry} object.
       */
      public Geometry reverse() {
        return createRing(factory, (CompoundLineString) compoundLineString.reverse());
      }

      /**
       * <p>
       * createRing.
       * </p>
       *
       * @param factory a {@link com.vividsolutions.jts.geom.GeometryFactory} object.
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
       * @param factory a {@link com.vividsolutions.jts.geom.GeometryFactory} object.
       * @param segments a {@link com.vividsolutions.jts.geom.LineString} object.
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
       * @return an array of {@link com.vividsolutions.jts.geom.LineString} objects.
       */
      public LineString[] getSegments() {
        return compoundLineString.getSegments();

      }

    }

