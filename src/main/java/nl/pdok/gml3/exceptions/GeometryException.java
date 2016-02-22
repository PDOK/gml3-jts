package nl.pdok.gml3.exceptions;

/**
 * @author GinkeM
 */
public class GeometryException extends Exception {

	private static final long serialVersionUID = 7280151999434051137L;
	private String objectId;

	public GeometryException() {
		super();
	}

	public GeometryException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public GeometryException(String message) {
		super(message);
	}

	public GeometryException(Throwable throwable) {
		super(throwable);
	}
	
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	public String getObjectId() {
		return objectId;
	}
	
}
