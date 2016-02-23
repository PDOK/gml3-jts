package nl.pdok.gml3.exceptions;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author GinkeM
 */
public class InvalidGeometryException extends GeometryException {

    private static final long serialVersionUID = 7209290697869699994L;
    private GeometryValidationErrorType errorType;
    private Coordinate coordinate;

    @Deprecated
    public InvalidGeometryException(GeometryValidationErrorType errorType) {
        this(errorType, null, null);
    }

    public InvalidGeometryException(GeometryValidationErrorType errorType, Coordinate coordinate) {
        this(errorType, coordinate, null);
    }

    public InvalidGeometryException(GeometryValidationErrorType errorType, Coordinate coordinate,
            String message) {
        super(message);
        this.errorType = errorType;
        this.coordinate = coordinate;
    }

    public GeometryValidationErrorType getErrorType() {
        return errorType;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

}
