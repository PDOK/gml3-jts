package nl.pdok.gml3.exceptions;

/**
 * <p>
 * GML3ParseException class.
 * </p>
 *
 * @author niek
 * @version $Id: $Id
 */
public class GML3ParseException extends Exception {

  /**
   * <p>
   * Constructor for GML3ParseException.
   * </p>
   *
   * @param msg a {@link java.lang.String} object.
   */
  public GML3ParseException(String msg) {
    super(msg);
  }

  /**
   * <p>
   * Constructor for GML3ParseException.
   * </p>
   *
   * @param message a {@link java.lang.String} object.
   * @param cause a {@link java.lang.Throwable} object.
   */
  public GML3ParseException(String message, Throwable cause) {
    super(message, cause);
  }

}
