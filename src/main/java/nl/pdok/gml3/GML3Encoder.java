package nl.pdok.gml3;

import java.io.ByteArrayOutputStream;
import java.io.ObjectStreamField;
import java.net.MulticastSocket;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.opengis.gml_3_1_1.AbstractGeometryType;
import org.opengis.gml_3_1_1.AbstractRingPropertyType;
import org.opengis.gml_3_1_1.AbstractRingType;
import org.opengis.gml_3_1_1.DirectPositionListType;
import org.opengis.gml_3_1_1.LinearRingPropertyType;
import org.opengis.gml_3_1_1.LinearRingType;
import org.opengis.gml_3_1_1.MultiSurfaceType;
import org.opengis.gml_3_1_1.ObjectFactory;
import org.opengis.gml_3_1_1.PolygonPropertyType;
import org.opengis.gml_3_1_1.PolygonType;
import org.opengis.gml_3_1_1.RingPropertyType;
import org.opengis.gml_3_1_1.SurfaceArrayPropertyType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class GML3Encoder {
	
	private Marshaller marshaller;
	
	public GML3Encoder(){
		try {
		
			JAXBContext jaxbContext = JAXBContext.newInstance(AbstractGeometryType.class);
			marshaller = jaxbContext.createMarshaller();
		
		} catch (JAXBException e) {
			throw new IllegalStateException("GML cannot be created. Cause: "+ e.getMessage());
		}
	}

	
	public String toGML(Geometry geometry) throws JAXBException{
		
		
		ObjectFactory objectFactory = new ObjectFactory();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		Polygon polygon = (Polygon) geometry;
		
		DirectPositionListType positionList = objectFactory.createDirectPositionListType();
		
		Coordinate[] coordinates = polygon.getExteriorRing().getCoordinates();
		for (Coordinate coord:coordinates){
			positionList.getValue().add(String.valueOf(coord.x));
			positionList.getValue().add(String.valueOf(coord.y));
		}
		
		LinearRingType linearRingType = objectFactory.createLinearRingType();
		linearRingType.setPosList(positionList);
		
		JAXBElement<LinearRingType> ringType = objectFactory.createLinearRing(linearRingType);
				
		AbstractRingPropertyType type = objectFactory.createAbstractRingPropertyType();
		type.setRing(ringType);
				
		JAXBElement<AbstractRingPropertyType> ringPropertyType = objectFactory.createExterior(type);

				
		PolygonType polygonType = objectFactory.createPolygonType();
		polygonType.setExterior(ringPropertyType);
		
		JAXBElement<PolygonType> polygonJaxb = objectFactory.createPolygon(polygonType);
		
        SurfaceArrayPropertyType surfaceArrayPropertyType = objectFactory.createSurfaceArrayPropertyType();
		surfaceArrayPropertyType.getSurface().add(polygonJaxb);
		
		MultiSurfaceType multiSurfaceType = objectFactory.createMultiSurfaceType();
		multiSurfaceType.setSurfaceMembers(surfaceArrayPropertyType);
		
		marshaller.marshal(objectFactory.createMultiSurface(multiSurfaceType), baos);
		return "gml --> " + baos.toString();
	}
	
}
