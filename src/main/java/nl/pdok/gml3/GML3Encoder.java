package nl.pdok.gml3;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.opengis.gml_3_1_1.AbstractGeometryType;

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

}
