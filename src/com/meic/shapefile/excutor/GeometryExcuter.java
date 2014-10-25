package com.meic.shapefile.excutor;

import org.opengis.feature.simple.SimpleFeature;

import com.meic.shapefile.manager.GeometryManager;

public abstract class GeometryExcuter implements Runnable {
	protected SimpleFeature feature;
	protected GeometryManager manager;
	protected int id;

	public GeometryExcuter(SimpleFeature feature, GeometryManager manager, int id) {
		this.feature = feature;
		this.manager = manager;
		this.id = id;
	}
}
