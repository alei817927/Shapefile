package com.makenv.tools.shapefile.excutor;

import org.opengis.feature.simple.SimpleFeature;

import com.makenv.tools.shapefile.manager.GeometryManager;
import com.vividsolutions.jts.geom.Geometry;

public class BufferExecutor extends GeometryExecutor {
	private double distance;

	public BufferExecutor(SimpleFeature feature, GeometryManager manager, int id, double distance) {
		super(feature, manager, id);
		this.distance = distance;
	}

	@Override
	public void run() {
		Geometry _shape = (Geometry) feature.getDefaultGeometry();
		manager.onExecuteFinished(feature.getID(), id, _shape.buffer(distance));
	}

}
