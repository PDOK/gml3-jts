package nl.pdok.gml3.exceptions;

import org.locationtech.jts.operation.valid.TopologyValidationError;

/**
 * <p>
 * GeometryValidationErrorType class.
 * </p>
 *
 * @author GinkeM
 * @version $Id: $Id
 */
public enum GeometryValidationErrorType {

  ERROR, REPEATED_POINT, HOLE_OUTSIDE_SHELL(
      TopologyValidationError.HOLE_OUTSIDE_SHELL), NESTED_HOLES(
          TopologyValidationError.NESTED_HOLES), DISCONNECTED_INTERIOR(
              TopologyValidationError.DISCONNECTED_INTERIOR), SELF_INTERSECTION(
                  TopologyValidationError.SELF_INTERSECTION), RING_SELF_INTERSECTION(
                      TopologyValidationError.RING_SELF_INTERSECTION), NESTED_SHELLS(
                          TopologyValidationError.NESTED_SHELLS), DUPLICATE_RINGS(
                              TopologyValidationError.DUPLICATE_RINGS), TOO_FEW_POINTS(
                                  TopologyValidationError.TOO_FEW_POINTS), INVALID_COORDINATE(
                                      TopologyValidationError.INVALID_COORDINATE), RING_NOT_CLOSED(
                                          TopologyValidationError.RING_NOT_CLOSED), POLYGON_HAS_NO_EXTERIOR, ARC_IS_A_STRAIGHT_LINE, TOPOLOGY_LV_CONTAINS_AREA, TOPOLOGY_OVERLAP, EMPTY_GEOMETRY, NOT_SUPPORTED, DEPRECATED, COORDINATES_EXCEEDED_MAX_SCALE, OUTER_RING_IS_NOT_CCW, INNER_RING_IS_CCW, CURVE_CONTAINS_NO_SEGMENTS, MULTI_SURFACE_DID_NOT_CONTAIN_MEMBERS, SURFACE_CONTAINS_NO_PATCHES, MAX_NUMBER_OF_COORDINATES_EXCEEDED, MULTI_POINT_DID_NOT_CONTAIN_MEMBERS, POINT_INVALID_NUMBER_OF_ORDINATES, ARC_MUST_HAVE_THREE_COORDINATES;

  private int jtsConstant;

  private GeometryValidationErrorType() {}

  private GeometryValidationErrorType(int jtsConstant) {
    this.jtsConstant = jtsConstant;
  }

  /**
   * <p>
   * Getter for the field <code>jtsConstant</code>.
   * </p>
   *
   * @return a int.
   */
  public int getJtsConstant() {
    return jtsConstant;
  }

  /**
   * <p>
   * getGeometryValidationErrorType.
   * </p>
   *
   * @param jts a int.
   * @return a {@link nl.pdok.gml3.exceptions.GeometryValidationErrorType} object.
   */
  public static GeometryValidationErrorType getGeometryValidationErrorType(int jts) {
    for (GeometryValidationErrorType type : GeometryValidationErrorType.values()) {
      if (type.getJtsConstant() == jts) {
        return type;
      }
    }

    return ERROR;
  }

}
