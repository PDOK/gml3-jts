package nl.pdok.gml3_1_1_2.exceptions;

/**
 * Er is een geometry gespecificeerd via een deprecated constructie die nog in GML 3.1.1 aanwezig 
 * zijn in verband met compatibiliteit tov GML2 
 * @author GinkeM
 */
// Opmerking kan verwijderd worden indien gml3.2 gebruikt wordt ipv 3.1.1
public class DeprecatedGeometrySpecificationException 
		extends UnsupportedGeometrySpecificationException {

	private static final long serialVersionUID = 2901023621749902625L;

	public DeprecatedGeometrySpecificationException() {
		super();
	}

	public DeprecatedGeometrySpecificationException(String message) {
		super(message);
	}

}
