package com.meic.shapefile.manager;

import java.util.HashMap;
import java.util.Map;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;

import com.meic.shapefile.ShapeFileConstants;
import com.meic.shapefile.excutor.AreaToLineExcuter;
import com.meic.shapefile.excutor.GeometryExcuter;
import com.vividsolutions.jts.geom.MultiLineString;

public class AreaToLineManager extends GeometryManager {

	public AreaToLineManager(String baseFile, String newFile) throws Exception {
		super(baseFile, newFile);
	}

	public AreaToLineManager(String baseFile, String newFile, int threadNum) throws Exception {
		super(baseFile, newFile, threadNum);
	}

	@Override
	public synchronized void onExcuteFinished(String fid, int pid, Object... params) {
		processCount++;
		if (params[0] != null) {
			Map<String, Class<?>> _params = new HashMap<>();
			_params.put(ShapeFileConstants.ID, Integer.class);
			_params.put(ShapeFileConstants.THE_GEOM, MultiLineString.class);
			SimpleFeatureBuilder _featureBuilder = getSimpleFeatureBuilder(_params);
			SimpleFeature _newFeature = _featureBuilder.buildFeature(fid, new Object[] { params[0] });
			_newFeature.setAttribute(ShapeFileConstants.ID, pid);
			_newFeature.setAttribute(ShapeFileConstants.THE_GEOM, params[0]);
			newFeatures.put(pid, _newFeature);
		}
		if (processCount == features.size()) {
			write();
			super.onExcuteFinished();
		}
	}

	@Override
	public void process() {
		// int i = 0;
		for (SimpleFeature _feature : features.values()) {
			String _identity = _feature.getID();
			String[] _ids = _identity.split("\\.");
			GeometryExcuter _excuter = new AreaToLineExcuter(_feature, this, Integer.parseInt(_ids[1]));
			excute(_excuter);
		}
	}

}
