package com.meic.shapefile.excutor;

import org.opengis.feature.simple.SimpleFeature;

import com.meic.shapefile.manager.GeometryManager;
import com.vividsolutions.jts.geom.Geometry;

public class AreaToLineExcuter extends GeometryExcuter {

	public AreaToLineExcuter(SimpleFeature feature, GeometryManager manager, int id) {
		super(feature, manager, id);
	}

	@Override
	public void run() {
		Geometry _shape = (Geometry) feature.getDefaultGeometry();
		manager.onExcuteFinished(feature.getID(), id, _shape.getBoundary());
	}
}
