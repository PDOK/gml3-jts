package nl.pdok.gml3;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.opengis.gml_3_1_1.AbstractGeometryType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKTWriter;

import nl.pdok.gml3.convertors.GMLToJTSGeometryConvertor;
import nl.pdok.gml3.convertors.GMLToLineConvertor;
import nl.pdok.gml3.convertors.GMLToPointConvertor;
import nl.pdok.gml3.convertors.GMLToSurfaceConvertor;
import nl.pdok.gml3.exceptions.GeometryException;

/*
Not threadsafe.
*/
public class GML3Parser {
	
	private final GMLToJTSGeometryConvertor gmlToJtsGeoConvertor;
    private final Unmarshaller unmarshaller;
	
	public static void main(String[] args) throws IOException {
		
		String geoString = FileUtils.readFileToString(new File(args[0]));
		System.out.println("geo-string -->> \n" + geoString);
		
		GML3Parser gml3ToGeometry = new GML3Parser();
		Geometry geo = gml3ToGeometry.toJTSGeometry(geoString);
		
		System.out.println("wkt -->> \n" + new WKTWriter().writeFormatted(geo));

	}	
	public GML3Parser(){
		
		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(AbstractGeometryType.class);
			GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 28992);
			GMLToPointConvertor pointConvertor = new GMLToPointConvertor(geometryFactory);
			GMLToLineConvertor lineConvertor = new GMLToLineConvertor(geometryFactory, pointConvertor);
			GMLToSurfaceConvertor surfaceConvertor = new GMLToSurfaceConvertor(geometryFactory, lineConvertor);
			
			gmlToJtsGeoConvertor = new GMLToJTSGeometryConvertor();
			gmlToJtsGeoConvertor.setGmlToPointConvertor(pointConvertor);
			gmlToJtsGeoConvertor.setGmlToLineConvertor(lineConvertor);
			gmlToJtsGeoConvertor.setGmlToSurfaceConvertor(surfaceConvertor);
            
            unmarshaller = jaxbContext.createUnmarshaller();
		
		} catch (JAXBException e) {
			throw new IllegalStateException("Object cannot be created. Cause: "+ e.getMessage());
		}
	}
	
	public Geometry toJTSGeometry(String gml)  {
		AbstractGeometryType abstractGeometryType;
		try {
			abstractGeometryType = parseGeometryFromGML(gml);
			return gmlToJtsGeoConvertor.convertGeometry(abstractGeometryType);
		} catch (JAXBException jaxbException) {
			throw new IllegalArgumentException("Input cannot be serialized to gml3-objects, gml: " + gml + ". " +
											   "Cause: " + jaxbException.getMessage());
		} catch (GeometryException geometryException) {
			throw new IllegalArgumentException("Input is not a valid geometry (gml3), gml: " + gml + ". " +
					   "Cause: " + geometryException.getMessage());		}
	}
	
	private AbstractGeometryType parseGeometryFromGML(String gml) throws JAXBException {
		StringReader reader = new StringReader(gml);
		Source source = new StreamSource(reader);
		
		Object o = unmarshaller.unmarshal(source);
		if ((o.getClass() == JAXBElement.class)) {
			@SuppressWarnings("rawtypes")
			JAXBElement jbe = (JAXBElement) o;
			o = jbe.getValue();
		}
		
		return (AbstractGeometryType) o;
	}
}
