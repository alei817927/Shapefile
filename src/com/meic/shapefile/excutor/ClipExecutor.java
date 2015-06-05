package com.meic.shapefile.excutor;

import org.geotools.geometry.jts.GeometryClipper;
import org.opengis.feature.simple.SimpleFeature;

import com.meic.shapefile.manager.GeometryManager;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class ClipExecutor extends GeometryExecutor {
	private Envelope clipEnvelope;
	private GeometryClipper clipper;
	private static Polygon EMPTY_POLYGON = new Polygon(null, null, new GeometryFactory());

	public ClipExecutor(SimpleFeature feature, GeometryManager manager, int id, Envelope clipEnvelope, GeometryClipper clipper) {
		super(feature, manager, id);
		this.clipEnvelope = clipEnvelope;
		this.clipper = clipper;
	}

	@Override
	public void run() {

		Geometry _shape = (Geometry) feature.getDefaultGeometry();
		Envelope _envelope = _shape.getEnvelopeInternal();
		Geometry _clipedShape;
		if (clipEnvelope.intersects(_envelope)) {
			_clipedShape = clipper.clip(_shape, true);
		} else {
			_clipedShape = EMPTY_POLYGON;
		}
		manager.onExecuteFinished(feature.getID(), id, _clipedShape);
	}

}
