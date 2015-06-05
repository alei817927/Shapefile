package com.meic.shapefile.excutor;

import org.opengis.feature.simple.SimpleFeature;

import com.meic.shapefile.manager.GeometryManager;
import com.vividsolutions.jts.geom.Geometry;

public class AreaToLineExecutor extends GeometryExecutor {

	public AreaToLineExecutor(SimpleFeature feature, GeometryManager manager, int id) {
		super(feature, manager, id);
	}

	@Override
	public void run() {
		Geometry _shape = (Geometry) feature.getDefaultGeometry();
		manager.onExecuteFinished(feature.getID(), id, _shape.getBoundary());
	}
}
