package nl.pdok.gml3;

import static org.junit.Assert.*;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class GML3EncoderTest {

	@Test
	public void test() throws JAXBException {
		GML3Encoder encoder = new GML3Encoder();
		
		GeometryFactory geometryFactory = new GeometryFactory();
		
		Coordinate[] square = { new Coordinate(1, 1), new Coordinate(1, 5), new Coordinate(5, 5), new Coordinate(5, 1), new Coordinate(1, 1)};
		
		Polygon polygon_1 = geometryFactory.createPolygon(square);
		
		System.out.println(encoder.toGML(polygon_1));
		
	}

}
