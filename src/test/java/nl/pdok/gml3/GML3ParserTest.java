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
        
        @Test
        public void top10LinearRing() {
            String gml = "<gml:Polygon xmlns:gml=\"http://www.opengis.net/gml\" srsName=\"urn:ogc:def:crs:EPSG::28992\"><gml:exterior><gml:LinearRing><gml:posList srsDimension=\"2\" count=\"67\">116055.50175 488633.817954607 116055.900101069 488633.836930407 116056.294844608 488633.89368596 116056.682405761 488633.987707278 116057.059274711 488634.11814289 116057.422038477 488634.283811551 116057.767411813 488634.483212939 116058.092266965 488634.714541246 116058.393661994 488634.975701525 116058.66886742 488635.264328669 116058.915390941 488635.577808826 116059.131 488635.913303071 116059.313742009 488636.267773117 116059.461962029 488636.638008826 116059.574317755 488637.020657289 116059.649791677 488637.41225318 116059.687700291 488637.809250147 116059.687700291 488638.208052924 116059.649791677 488638.605049891 116059.574317755 488638.996645783 116059.461962029 488639.379294245 116059.313742009 488639.749529955 116059.131 488640.104 116058.915390941 488640.439494245 116058.66886742 488640.752974402 116058.393661994 488641.041601547 116058.092266965 488641.302761826 116057.767411813 488641.534090132 116057.422038477 488641.733491521 116057.059274711 488641.899160181 116056.682405761 488642.029595794 116056.294844608 488642.123617112 116055.900101069 488642.180372664 116055.50175 488642.199348465 116055.103398931 488642.180372664 116054.708655391 488642.123617112 116054.321094239 488642.029595794 116053.944225288 488641.899160181 116053.581461523 488641.733491521 116053.236088187 488641.534090132 116052.911233035 488641.302761826 116052.609838006 488641.041601547 116052.33463258 488640.752974402 116052.088109059 488640.439494245 116051.8725 488640.104 116051.689757991 488639.749529955 116051.541537971 488639.379294245 116051.429182245 488638.996645783 116051.353708323 488638.605049891 116051.315799709 488638.208052924 116051.315799709 488637.809250147 116051.353708323 488637.41225318 116051.429182245 488637.020657289 116051.541537971 488636.638008826 116051.689757991 488636.267773117 116051.8725 488635.913303071 116052.088109059 488635.577808826 116052.33463258 488635.264328669 116052.609838006 488634.975701525 116052.911233035 488634.714541246 116053.236088187 488634.483212939 116053.581461523 488634.283811551 116053.944225288 488634.11814289 116054.321094239 488633.987707278 116054.708655391 488633.89368596 116055.103398931 488633.836930407 116055.50175 488633.817954607</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon>";
            GML3Parser parser = new GML3Parser();
            Geometry geometry = parser.toJTSGeometry(gml);
            assertNotNull(geometry);
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
