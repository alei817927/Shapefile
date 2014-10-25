package com.meic.shapefile.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;

import com.meic.shapefile.ShapeFileConstants;
import com.meic.shapefile.excutor.GeometryExcuter;
import com.meic.shapefile.excutor.IntersectExcuter;
import com.meic.shapefile.util.GeometryUtil;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;

public class IntersectManager extends GeometryManager {
	long startTime = 0;
	protected List<Geometry> geometrys;

	public IntersectManager(String baseFile, String opFile, String newFile) throws Exception {
		super(baseFile, newFile);
		geometrys = GeometryUtil.readShpFile(opFile);
	}

	public IntersectManager(String baseFile, String opFile, String newFile, int threadNum) throws Exception {
		super(baseFile, newFile, threadNum);
		geometrys = GeometryUtil.readShpFile(opFile);
	}

	@Override
	public void process() {
		startTime = System.currentTimeMillis();
		int i = 0;
		for (SimpleFeature _feature : features.values()) {
			String _identity = _feature.getID();
			String[] _ids = _identity.split("\\.");
			GeometryExcuter _excuter = new IntersectExcuter(_feature, geometrys, this, Integer.parseInt(_ids[1]));
			excute(_excuter);
			i++;
		}
		System.out.println("total = " + i);
	}

	@Override
	public synchronized void onExcuteFinished(String fid, int pid, Object... params) {
		processCount++;
		if (params[0] != null) {
			Map<String, Class<?>> _params = new HashMap<>();
			_params.put(ShapeFileConstants.ID, Integer.class);
			_params.put(ShapeFileConstants.THE_GEOM, MultiPolygon.class);
			_params.put(ShapeFileConstants.AREA, Double.class);
			_params.put(ShapeFileConstants.RATE, Double.class);
			SimpleFeatureBuilder _featureBuilder = getSimpleFeatureBuilder(_params);
			// SimpleFeature _newFeature = _featureBuilder.buildFeature(id, new
			// Object[] { geometry, area });
			SimpleFeature _newFeature = _featureBuilder.buildFeature(fid, new Object[] { params[0] });
			_newFeature.setAttribute(ShapeFileConstants.ID, pid);
			_newFeature.setAttribute(ShapeFileConstants.THE_GEOM, params[0]);
			_newFeature.setAttribute(ShapeFileConstants.AREA, params[1]);
			_newFeature.setAttribute(ShapeFileConstants.RATE, params[2]);
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

}
