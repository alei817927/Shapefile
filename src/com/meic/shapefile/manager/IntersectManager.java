package com.meic.shapefile.manager;

import com.meic.shapefile.ShapeFileConstants;
import com.meic.shapefile.excutor.GeometryExecutor;
import com.meic.shapefile.excutor.IntersectExecutor;
import com.meic.shapefile.util.GeometryUtil;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntersectManager extends GeometryManager {
  long startTime = 0;
  protected List<Geometry> geometries;

  public IntersectManager(String baseFile, String opFile, String newFile) throws Exception {
    super(baseFile, newFile);
    geometries = GeometryUtil.readShpFile(opFile);
  }

  public IntersectManager(String baseFile, String opFile, String newFile, int threadNum) throws Exception {
    super(baseFile, newFile, threadNum);
    geometries = GeometryUtil.readShpFile(opFile);
  }

  @Override
  public void process() {
    startTime = System.currentTimeMillis();
    int i = 0;
    for (SimpleFeature _feature : features.values()) {
      String _identity = _feature.getID();
      String[] _ids = _identity.split("\\.");
      GeometryExecutor _executor = new IntersectExecutor(_feature, geometries, this, Integer.parseInt(_ids[1]));
      execute(_executor);
      i++;
    }
    System.out.println("total = " + i);
  }

  @Override
  public synchronized void onExecuteFinished(String fid, int pid, Object... params) {
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
      SimpleFeature _newFeature = _featureBuilder.buildFeature(fid, new Object[]{params[0]});
      _newFeature.setAttribute(ShapeFileConstants.ID, pid);
      _newFeature.setAttribute(ShapeFileConstants.THE_GEOM, params[0]);
      _newFeature.setAttribute(ShapeFileConstants.AREA, params[1]);
      _newFeature.setAttribute(ShapeFileConstants.RATE, params[2]);
      newFeatures.put(pid, _newFeature);
    }
    System.out.print(processCount + ",");
    if (processCount == features.size()) {
      write();
      super.onExecuteFinished();
      long _cost = System.currentTimeMillis() - startTime;
      System.out.println();
      System.out.println("\ncosttime=" + _cost);
    }
  }

}
