package nl.pdok.gml3.geometry.extended;

import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * @author GinkeM
 */
public class ExtendedGeometryFactory extends GeometryFactory {
	
	private static final long serialVersionUID = 6752447631106671661L;
	
	public static final double DEFAULT_TOLERANCE = 0.001;
	private double tolerance = DEFAULT_TOLERANCE;
	
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
	
	public double getTolerance() {
		return tolerance;
	}
	
	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

}
