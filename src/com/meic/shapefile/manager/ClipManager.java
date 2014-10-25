package com.meic.shapefile.manager;

import java.util.HashMap;
import java.util.Map;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.GeometryClipper;
import org.opengis.feature.simple.SimpleFeature;

import com.meic.shapefile.ShapeFileConstants;
import com.meic.shapefile.excutor.ClipExcutor;
import com.meic.shapefile.excutor.GeometryExcuter;
import com.meic.shapefile.util.GeometryUtil;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.MultiPolygon;

public class ClipManager extends GeometryManager {
	private Envelope clipEnvelope;
	private GeometryClipper clipper;
	long startTime = 0;

	public ClipManager(String baseFile, String newFile, String clipFile) throws Exception {
		super(baseFile, newFile);
		init(clipFile);
	}

	public ClipManager(String baseFile, String newFile, int threadNum, String clipFile) throws Exception {
		super(baseFile, newFile, threadNum);
		init(clipFile);
	}

	private void init(String clipFile) throws Exception {
		clipEnvelope = GeometryUtil.getEnvelope(clipFile);
		clipper = new GeometryClipper(clipEnvelope);
	}

	@Override
	public synchronized void onExcuteFinished(String fid, int pid, Object... params) {
		processCount++;
		if (params[0] != null) {
			Map<String, Class<?>> _params = new HashMap<>();
			_params.put(ShapeFileConstants.ID, Integer.class);
			_params.put(ShapeFileConstants.THE_GEOM, MultiPolygon.class);
			SimpleFeatureBuilder _featureBuilder = getSimpleFeatureBuilder(_params);
			SimpleFeature _newFeature = _featureBuilder.buildFeature(fid, new Object[] { params[0] });
			_newFeature.setAttribute(ShapeFileConstants.ID, pid);
			_newFeature.setAttribute(ShapeFileConstants.THE_GEOM, params[0]);
			newFeatures.put(pid, _newFeature);
		}
		System.out.print(processCount + ",");
		if (processCount == features.size()) {
			write();
			super.onExcuteFinished();
			long _cost = System.currentTimeMillis() - startTime;
			System.out.println();
			System.out.println("\ncosttime=" + _cost);
		}
	}

	@Override
	public void process() {
		int i = 0;
		startTime = System.currentTimeMillis();
		for (SimpleFeature _feature : features.values()) {
			String _identity = _feature.getID();
			String[] _ids = _identity.split("\\.");
			GeometryExcuter _excuter = new ClipExcutor(_feature, this, Integer.parseInt(_ids[1]), clipEnvelope, clipper);
			excute(_excuter);
			i++;
		}
		System.out.println("total = " + i);
	}

}
