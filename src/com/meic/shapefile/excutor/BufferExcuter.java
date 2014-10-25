package com.meic.shapefile.excutor;

import org.opengis.feature.simple.SimpleFeature;

import com.meic.shapefile.manager.GeometryManager;
import com.vividsolutions.jts.geom.Geometry;

public class BufferExcuter extends GeometryExcuter {
	private double distance;

	public BufferExcuter(SimpleFeature feature, GeometryManager manager, int id, double distance) {
		super(feature, manager, id);
		this.distance = distance;
	}

	@Override
	public void run() {
		Geometry _shape = (Geometry) feature.getDefaultGeometry();
		manager.onExcuteFinished(feature.getID(), id, _shape.buffer(distance));
	}

}
