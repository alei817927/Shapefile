package com.meic.shapefile.manager;

import com.meic.shapefile.ShapeFileConstants;
import com.meic.shapefile.excutor.BufferExecutor;
import com.meic.shapefile.excutor.GeometryExecutor;
import com.vividsolutions.jts.geom.MultiPolygon;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;

import java.util.HashMap;
import java.util.Map;

public class BufferManager extends GeometryManager {
  private double distance;

  public BufferManager(String baseFile, String newFile, double distance) throws Exception {
    super(baseFile, newFile);
    this.distance = distance;
  }

  public BufferManager(String baseFile, String newFile, int threadNum, double distance) throws Exception {
    super(baseFile, newFile, threadNum);
    this.distance = distance;
  }

  @Override
  public void onExecuteFinished(String fid, int pid, Object... params) {
    processCount++;
    if (params[0] != null) {
      Map<String, Class<?>> _params = new HashMap<>();
      _params.put(ShapeFileConstants.ID, Integer.class);
      _params.put(ShapeFileConstants.THE_GEOM, MultiPolygon.class);
      SimpleFeatureBuilder _featureBuilder = getSimpleFeatureBuilder(_params);
      SimpleFeature _newFeature = _featureBuilder.buildFeature(fid, new Object[]{params[0]});
      _newFeature.setAttribute(ShapeFileConstants.ID, pid);
      _newFeature.setAttribute(ShapeFileConstants.THE_GEOM, params[0]);
      newFeatures.put(pid, _newFeature);
    }
    if (processCount == features.size()) {
      write();
      super.onExecuteFinished();
    }

  }

  @Override
  public void process() {
    // int i = 0;
    for (SimpleFeature _feature : features.values()) {
      String _identity = _feature.getID();
      String[] _ids = _identity.split("\\.");
      GeometryExecutor _executor = new BufferExecutor(_feature, this, Integer.parseInt(_ids[1]), distance);
      execute(_executor);
    }
  }

}
