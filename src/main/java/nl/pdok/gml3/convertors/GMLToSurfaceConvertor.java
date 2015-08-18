package nl.pdok.gml3.convertors;

	import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.opengis.gml_3_1_1.AbstractRingPropertyType;
import org.opengis.gml_3_1_1.AbstractSurfacePatchType;
import org.opengis.gml_3_1_1.AbstractSurfaceType;
import org.opengis.gml_3_1_1.MultiSurfaceType;
import org.opengis.gml_3_1_1.PolygonPatchType;
import org.opengis.gml_3_1_1.PolygonType;
import org.opengis.gml_3_1_1.SurfaceArrayPropertyType;
import org.opengis.gml_3_1_1.SurfacePatchArrayPropertyType;
import org.opengis.gml_3_1_1.SurfacePropertyType;
import org.opengis.gml_3_1_1.SurfaceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import nl.pdok.gml3.exceptions.GeometryException;
import nl.pdok.gml3.exceptions.GeometryValidationErrorType;
import nl.pdok.gml3.exceptions.InvalidGeometryException;
import nl.pdok.gml3.exceptions.UnsupportedGeometrySpecificationException;

/**
 * @author GinkeM
 */
public class GMLToSurfaceConvertor {

		private Logger log = LoggerFactory.getLogger(getClass());
		private GeometryFactory geometryFactory;
		private GMLToLineConvertor gmlToLineConvertor;

		public GMLToSurfaceConvertor(GeometryFactory geometryFactory, GMLToLineConvertor 
				gmlToLineConvertor) {
			this.geometryFactory = geometryFactory;
			this.gmlToLineConvertor = gmlToLineConvertor;
		}
		
		public Geometry convertMultiSurface(MultiSurfaceType surfaces) throws GeometryException {
			List<Polygon> polygons = new ArrayList<Polygon>();
			for(SurfacePropertyType surface : surfaces.getSurfaceMember()) {
				JAXBElement<? extends AbstractSurfaceType> element = surface.getSurface();
				Geometry result = convertElementContainingSurface(element);
				addResultingPolygonsToList(result, polygons);
			}
			
			SurfaceArrayPropertyType array = surfaces.getSurfaceMembers();
			if(array != null) {
				List<JAXBElement<? extends AbstractSurfaceType>> arraySurfaceMembers = array.getSurface();
				if(arraySurfaceMembers != null) {
					for(JAXBElement<? extends AbstractSurfaceType> surfaceMember : arraySurfaceMembers) {
						Geometry result = convertElementContainingSurface(surfaceMember);
						addResultingPolygonsToList(result, polygons);
					}
				}
			}
			
			return convertPolygonListToMuliPolygonOrSinglePolygon(polygons);
		}
		
		private Geometry convertPolygonListToMuliPolygonOrSinglePolygon(List<Polygon> polygons)
				throws GeometryException {
			if(polygons.size() < 1) {
				throw new InvalidGeometryException(
						GeometryValidationErrorType.MULTI_SURFACE_DID_NOT_CONTAIN_MEMBERS, null);
			}
			else if (polygons.size() == 1) {
				return polygons.get(0);
			} 
			else {
				MultiPolygon multi = new MultiPolygon(
						polygons.toArray(new Polygon[] {}), geometryFactory);
				return multi;
			}
		}
		
		private Geometry convertElementContainingSurface(
				JAXBElement<? extends AbstractSurfaceType> surface) throws GeometryException {
			if(surface != null) {
				AbstractSurfaceType abstractSurfaceType = surface.getValue();
				if(abstractSurfaceType != null) {
					return convertSurface(abstractSurfaceType);
				}
			}
			
			log.warn("gml surface element in collection did not contain surface element");
			return null;
		}
		
		private void addResultingPolygonsToList(Geometry geometry, List<Polygon> polygons) {
			if(geometry != null) {
				if(geometry instanceof MultiPolygon) {
					MultiPolygon collection = (MultiPolygon) geometry;
					for(int i=0; i<collection.getNumGeometries(); i++) {
						polygons.add((Polygon)collection.getGeometryN(i));
					}
				}
				else {
					polygons.add((Polygon)geometry);
				}
			}
		}
		

		public Geometry convertSurface(AbstractSurfaceType abstractSurface)
				throws GeometryException {
			if (abstractSurface instanceof SurfaceType) {
				List<Polygon> polygons = new ArrayList<Polygon>();
				SurfaceType surface = (SurfaceType) abstractSurface;
				SurfacePatchArrayPropertyType patches = surface.getPatches().getValue();
				// opmerking multipliciteit 2 of meer is afgevangen door xsd
				for (int i = 0; i < patches.getSurfacePatch().size(); i++) {
					AbstractSurfacePatchType abstractPatch = patches
							.getSurfacePatch().get(i).getValue();
					if (abstractPatch instanceof PolygonPatchType) {
						PolygonPatchType polygonPatch = (PolygonPatchType) abstractPatch;
						Polygon polygon = convertPolygonPatch(polygonPatch);
						polygons.add(polygon);

					} else {
						throw new UnsupportedGeometrySpecificationException(
								"Only polygon patch type is supported");
					}
				}

				return convertPolygonListToMuliPolygonOrSinglePolygon(polygons);
			} 
			else if (abstractSurface instanceof PolygonType) {
				PolygonType polygonType = (PolygonType) abstractSurface;
				if(polygonType.getExterior() == null) {
					throw new InvalidGeometryException(GeometryValidationErrorType.POLYGON_HAS_NO_EXTERIOR, null);
				}
				
				AbstractRingPropertyType abstractRing = polygonType.getExterior().getValue();
				LinearRing shell = gmlToLineConvertor.translateAbstractRing(abstractRing);
				LinearRing[] innerRings = new LinearRing[polygonType.getInterior().size()];
				for (int i = 0; i < polygonType.getInterior().size(); i++) {
					innerRings[i] = gmlToLineConvertor.translateAbstractRing(polygonType.getInterior()
							.get(i).getValue());
				}

				return geometryFactory.createPolygon(shell, innerRings);

			} else {
				throw new UnsupportedGeometrySpecificationException(
						"Only Surface and Polygon are "
								+ "supported as instances of _Surface");
			}

		}

		public Polygon convertPolygonPatch(PolygonPatchType polygonPatch)
				throws GeometryException {
			if(polygonPatch.getExterior() == null) {
				throw new InvalidGeometryException(GeometryValidationErrorType.POLYGON_HAS_NO_EXTERIOR, null);
			}
			
			AbstractRingPropertyType abstractRing = polygonPatch.getExterior().getValue();
			LinearRing exteriorShell = gmlToLineConvertor.translateAbstractRing(abstractRing);
			if (!CGAlgorithms.isCCW(exteriorShell.getCoordinates())) {
				throw new InvalidGeometryException(
						GeometryValidationErrorType.OUTER_RING_IS_NOT_CCW, null);
			}

			LinearRing[] innerRings = new LinearRing[polygonPatch.getInterior().size()];
			for (int i = 0; i < polygonPatch.getInterior().size(); i++) {
				innerRings[i] = gmlToLineConvertor.translateAbstractRing(polygonPatch.getInterior()
						.get(i).getValue());
				if (CGAlgorithms.isCCW(innerRings[i].getCoordinates())) {
					throw new InvalidGeometryException(
							GeometryValidationErrorType.INNER_RING_IS_CCW, null);
				}

			}

			return geometryFactory.createPolygon(exteriorShell, innerRings);
		}

	}
