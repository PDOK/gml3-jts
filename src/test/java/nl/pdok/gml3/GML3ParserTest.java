package nl.pdok.gml3;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;

public class GML3ParserTest {

	private static final String SRC_TEST_RESOURCES_FOLDER = "src/test/resources";

	@Test
	public void testArcs() throws IOException {
		assertGmlAndWkt("arcs");
	}
	
	@Test
	public void testMulitpolygons() throws IOException {
		assertGmlAndWkt("polygon-patches");
	}

	private void assertGmlAndWkt(String testGeometry) throws IOException {
		File expectedWktFile = FileUtils.getFile(SRC_TEST_RESOURCES_FOLDER, testGeometry.concat(".wkt"));
		String expectedWkt = FileUtils.readFileToString(expectedWktFile);
		File withGMLArcs = FileUtils.getFile(SRC_TEST_RESOURCES_FOLDER, testGeometry.concat(".gml"));
		String wkt = gmlToWkt(withGMLArcs);
		assertEquals(expectedWkt, wkt);
	}

	private String gmlToWkt(File withGMLArcs) throws IOException {
		GML3Parser gml3Parser = new GML3Parser();
		String gml = FileUtils.readFileToString(withGMLArcs);
		Geometry geo = gml3Parser.toJTSGeometry(gml);
		String wkt = geo.toText();
		return wkt;
	}

}
