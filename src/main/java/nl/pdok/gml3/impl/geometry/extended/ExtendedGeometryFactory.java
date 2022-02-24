package nl.pdok.gml3.impl.geometry.extended;

import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

/**
 * <p>ExtendedGeometryFactory class.</p>
 *
 * @author GinkeM
 * @version $Id: $Id
 */
public class ExtendedGeometryFactory extends GeometryFactory {
	
	private static final long serialVersionUID = 6752447631106671661L;
	
	/** Constant <code>DEFAULT_MAXIMUM_ARC_APPROXIMATION_ERROR=0.001</code> */
	public static final double DEFAULT_MAXIMUM_ARC_APPROXIMATION_ERROR = 0.001;
	private double  maximumArcApproximationError = DEFAULT_MAXIMUM_ARC_APPROXIMATION_ERROR;
	
	/**
	 * <p>Constructor for ExtendedGeometryFactory.</p>
	 */
	public ExtendedGeometryFactory() {
		super();
	}

	/**
	 * <p>Constructor for ExtendedGeometryFactory.</p>
	 *
	 * @param coordinateSequenceFactory a {@link org.locationtech.jts.geom.CoordinateSequenceFactory} object.
	 */
	public ExtendedGeometryFactory(CoordinateSequenceFactory coordinateSequenceFactory) {
		super(coordinateSequenceFactory);
	}

	/**
	 * <p>Constructor for ExtendedGeometryFactory.</p>
	 *
	 * @param precisionModel a {@link org.locationtech.jts.geom.PrecisionModel} object.
	 * @param SRID a int.
	 * @param coordinateSequenceFactory a {@link org.locationtech.jts.geom.CoordinateSequenceFactory} object.
	 */
	public ExtendedGeometryFactory(PrecisionModel precisionModel, int SRID,
                                   CoordinateSequenceFactory coordinateSequenceFactory) {
		super(precisionModel, SRID, coordinateSequenceFactory);
	}

	/**
	 * <p>Constructor for ExtendedGeometryFactory.</p>
	 *
	 * @param precisionModel a {@link org.locationtech.jts.geom.PrecisionModel} object.
	 * @param SRID a int.
	 */
	public ExtendedGeometryFactory(PrecisionModel precisionModel, int SRID) {
		super(precisionModel, SRID);
	}

	/**
	 * <p>Constructor for ExtendedGeometryFactory.</p>
	 *
	 * @param precisionModel a {@link org.locationtech.jts.geom.PrecisionModel} object.
	 */
	public ExtendedGeometryFactory(PrecisionModel precisionModel) {
		super(precisionModel);
	}
	
	/**
	 * <p>Getter for the field <code>maximumArcApproximationError</code>.</p>
	 *
	 * @return a double.
	 */
	public double getMaximumArcApproximationError() {
		return maximumArcApproximationError;
	}
	
	/**
	 * <p>Setter for the field <code>maximumArcApproximationError</code>.</p>
	 *
	 * @param value a double.
	 */
	public void setMaximumArcApproximationError(double value) {
		this.maximumArcApproximationError = value;
	}

}
