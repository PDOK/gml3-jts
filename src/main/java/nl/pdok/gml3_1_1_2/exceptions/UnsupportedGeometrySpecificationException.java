package nl.pdok.gml3_1_1_2.exceptions;

/**
 * Een geometrie constructie wordt niet ondersteund door de conversie utilities
 * @author GinkeM
 */
public class UnsupportedGeometrySpecificationException extends GeometryException {

	private static final long serialVersionUID = 5883407724952570856L;

	public UnsupportedGeometrySpecificationException() {
		super();
	}

	public UnsupportedGeometrySpecificationException(String message) {
		super(message);
	}

}
