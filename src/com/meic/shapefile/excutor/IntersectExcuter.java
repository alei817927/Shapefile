package com.meic.shapefile.excutor;

import java.util.ArrayList;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

import com.meic.shapefile.manager.GeometryManager;
import com.meic.shapefile.util.GeometryUtil;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class IntersectExcuter extends GeometryExcuter {
	private static double GRID_AREA = 0;
	private static Polygon EMPTY_POLYGON = new Polygon(null, null, new GeometryFactory());
	protected List<Geometry> geometrys;

	public IntersectExcuter(SimpleFeature feature, List<Geometry> geometrys, GeometryManager manager, int id) {
		super(feature, manager, id);
		this.geometrys = geometrys;
	}

	@Override
	public void run() {
		List<Geometry> _intersectGeometrys = new ArrayList<>();
		Geometry _shape = (Geometry) feature.getDefaultGeometry();
		Envelope _envelope = _shape.getEnvelopeInternal();
		for (Geometry _geometry : geometrys) {
			if (_envelope.intersects(_geometry.getEnvelopeInternal()) && _shape.intersects(_geometry)) {
				_intersectGeometrys.add(_shape.intersection(_geometry));
			}
		}
		Geometry _geometry = null;
		double _area = 0;
		if (GRID_AREA == 0) {
			GRID_AREA = _shape.getArea();
		}
		if (!_intersectGeometrys.isEmpty()) {
			_geometry = GeometryUtil.union(_intersectGeometrys);
			_area = _geometry.getArea();
		} else {
			_geometry = EMPTY_POLYGON;
		}
		double _rate = _area == 0 ? 0 : _area / GRID_AREA;
		manager.onExcuteFinished(feature.getID(), id, _geometry, _area, _rate);
	}
}
