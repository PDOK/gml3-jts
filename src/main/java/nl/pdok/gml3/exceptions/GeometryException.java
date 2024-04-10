package nl.pdok.gml3.exceptions;

/**
 * <p>
 * GeometryException class.
 * </p>
 *
 * @author GinkeM
 * @version $Id: $Id
 */
public class GeometryException extends Exception {

  private static final long serialVersionUID = 7280151999434051137L;
  private String objectId;

  /**
   * <p>
   * Constructor for GeometryException.
   * </p>
   */
  public GeometryException() {
    super();
  }

  /**
   * <p>
   * Constructor for GeometryException.
   * </p>
   *
   * @param message a {@link java.lang.String} object.
   * @param throwable a {@link java.lang.Throwable} object.
   */
  public GeometryException(String message, Throwable throwable) {
    super(message, throwable);
  }

  /**
   * <p>
   * Constructor for GeometryException.
   * </p>
   *
   * @param message a {@link java.lang.String} object.
   */
  public GeometryException(String message) {
    super(message);
  }

  /**
   * <p>
   * Constructor for GeometryException.
   * </p>
   *
   * @param throwable a {@link java.lang.Throwable} object.
   */
  public GeometryException(Throwable throwable) {
    super(throwable);
  }

  /**
   * <p>
   * Setter for the field <code>objectId</code>.
   * </p>
   *
   * @param objectId a {@link java.lang.String} object.
   */
  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  /**
   * <p>
   * Getter for the field <code>objectId</code>.
   * </p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getObjectId() {
    return objectId;
  }

}
