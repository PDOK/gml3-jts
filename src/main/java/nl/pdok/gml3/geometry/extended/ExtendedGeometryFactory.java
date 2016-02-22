package nl.pdok.gml3.geometry.extended;

import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * @author GinkeM
 */
public class ExtendedGeometryFactory extends GeometryFactory {
	
	private static final long serialVersionUID = 6752447631106671661L;
	
	public static final double DEFAULT_MAXIMUM_ARC_APPROXIMATION_ERROR = 0.001;
	private double  maximumArcApproximationError = DEFAULT_MAXIMUM_ARC_APPROXIMATION_ERROR;
	
	public ExtendedGeometryFactory() {
		super();
	}

	public ExtendedGeometryFactory(CoordinateSequenceFactory coordinateSequenceFactory) {
		super(coordinateSequenceFactory);
	}

	public ExtendedGeometryFactory(PrecisionModel precisionModel, int SRID,
                                   CoordinateSequenceFactory coordinateSequenceFactory) {
		super(precisionModel, SRID, coordinateSequenceFactory);
	}

	public ExtendedGeometryFactory(PrecisionModel precisionModel, int SRID) {
		super(precisionModel, SRID);
	}

	public ExtendedGeometryFactory(PrecisionModel precisionModel) {
		super(precisionModel);
	}
	
	public double getMaximumArcApproximationError() {
		return maximumArcApproximationError;
	}
	
	public void setMaximumArcApproximationError(double value) {
		this.maximumArcApproximationError = value;
	}

}
