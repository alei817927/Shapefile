package com.makenv.tools.shapefile.excutor;

import org.opengis.feature.simple.SimpleFeature;

import com.makenv.tools.shapefile.manager.GeometryManager;

public abstract class GeometryExecutor implements Runnable {
	protected SimpleFeature feature;
	protected GeometryManager manager;
	protected int id;

	public GeometryExecutor(SimpleFeature feature, GeometryManager manager, int id) {
		this.feature = feature;
		this.manager = manager;
		this.id = id;
	}
}
