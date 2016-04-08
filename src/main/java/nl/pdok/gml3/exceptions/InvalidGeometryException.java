package nl.pdok.gml3.exceptions;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * <p>InvalidGeometryException class.</p>
 *
 * @author GinkeM
 * @version $Id: $Id
 */
public class InvalidGeometryException extends GeometryException {

    private static final long serialVersionUID = 7209290697869699994L;
    private GeometryValidationErrorType errorType;
    private Coordinate coordinate;

    /**
     * <p>Constructor for InvalidGeometryException.</p>
     *
     * @param errorType a {@link nl.pdok.gml3.exceptions.GeometryValidationErrorType} object.
     */
    @Deprecated
    public InvalidGeometryException(GeometryValidationErrorType errorType) {
        this(errorType, null, null);
    }

    /**
     * <p>Constructor for InvalidGeometryException.</p>
     *
     * @param errorType a {@link nl.pdok.gml3.exceptions.GeometryValidationErrorType} object.
     * @param coordinate a {@link com.vividsolutions.jts.geom.Coordinate} object.
     */
    public InvalidGeometryException(GeometryValidationErrorType errorType, Coordinate coordinate) {
        this(errorType, coordinate, null);
    }

    /**
     * <p>Constructor for InvalidGeometryException.</p>
     *
     * @param errorType a {@link nl.pdok.gml3.exceptions.GeometryValidationErrorType} object.
     * @param coordinate a {@link com.vividsolutions.jts.geom.Coordinate} object.
     * @param message a {@link java.lang.String} object.
     */
    public InvalidGeometryException(GeometryValidationErrorType errorType, Coordinate coordinate,
            String message) {
        super(message);
        this.errorType = errorType;
        this.coordinate = coordinate;
    }

    /**
     * <p>Getter for the field <code>errorType</code>.</p>
     *
     * @return a {@link nl.pdok.gml3.exceptions.GeometryValidationErrorType} object.
     */
    public GeometryValidationErrorType getErrorType() {
        return errorType;
    }

    /**
     * <p>Getter for the field <code>coordinate</code>.</p>
     *
     * @return a {@link com.vividsolutions.jts.geom.Coordinate} object.
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

}
