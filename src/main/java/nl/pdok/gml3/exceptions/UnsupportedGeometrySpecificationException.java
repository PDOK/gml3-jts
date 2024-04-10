package nl.pdok.gml3.exceptions;

/**
 * Een geometrie constructie wordt niet ondersteund door de conversie utilities
 *
 * @author GinkeM
 * @version $Id: $Id
 */
public class UnsupportedGeometrySpecificationException extends GeometryException {

  private static final long serialVersionUID = 5883407724952570856L;

  /**
   * <p>
   * Constructor for UnsupportedGeometrySpecificationException.
   * </p>
   */
  public UnsupportedGeometrySpecificationException() {
    super();
  }

  /**
   * <p>
   * Constructor for UnsupportedGeometrySpecificationException.
   * </p>
   *
   * @param message a {@link java.lang.String} object.
   */
  public UnsupportedGeometrySpecificationException(String message) {
    super(message);
  }

}
