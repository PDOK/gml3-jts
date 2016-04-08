package nl.pdok.gml3.exceptions;

/**
 * Er is een geometry gespecificeerd via een deprecated constructie die nog in GML 3.1.1 aanwezig
 * zijn in verband met compatibiliteit tov GML2
 *
 * @author GinkeM
 * @version $Id: $Id
 */
// Opmerking kan verwijderd worden indien gml3.2 gebruikt wordt ipv 3.1.1
public class DeprecatedGeometrySpecificationException 
		extends UnsupportedGeometrySpecificationException {

	private static final long serialVersionUID = 2901023621749902625L;

	/**
	 * <p>Constructor for DeprecatedGeometrySpecificationException.</p>
	 */
	public DeprecatedGeometrySpecificationException() {
		super();
	}

	/**
	 * <p>Constructor for DeprecatedGeometrySpecificationException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public DeprecatedGeometrySpecificationException(String message) {
		super(message);
	}

}
