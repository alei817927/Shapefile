package com.makenv.tools.shapefile.manager;

import com.makenv.tools.shapefile.ShapeFileConstants;
import com.makenv.tools.shapefile.excutor.ClipExecutor;
import com.makenv.tools.shapefile.excutor.GeometryExecutor;
import com.makenv.tools.shapefile.util.GeometryUtil;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.MultiPolygon;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.GeometryClipper;
import org.opengis.feature.simple.SimpleFeature;

import java.util.HashMap;
import java.util.Map;

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

  public ClipManager(String baseFile, String newFile, Envelope clipEnvelope) throws Exception {
    super(baseFile, newFile);
    this.clipEnvelope = clipEnvelope;
    clipper = new GeometryClipper(clipEnvelope);
  }

  private void init(String clipFile) throws Exception {
    clipEnvelope = GeometryUtil.getEnvelope(clipFile);
    clipper = new GeometryClipper(clipEnvelope);
  }

  @Override
  public synchronized void onExecuteFinished(String fid, int pid, Object... params) {
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
    System.out.print(processCount + ",");
    if (processCount == features.size()) {
      write();
      System.out.println();
      super.onExecuteFinished();
      long _cost = System.currentTimeMillis() - startTime;
      System.out.println("\ncost time=" + _cost);
    }
  }

  @Override
  public void process() {
    int i = 0;
    startTime = System.currentTimeMillis();
    for (SimpleFeature _feature : features.values()) {
      String _identity = _feature.getID();
      String[] _ids = _identity.split("\\.");
      GeometryExecutor _executor = new ClipExecutor(_feature, this, Integer.parseInt(_ids[1]), clipEnvelope, clipper);
      execute(_executor);
      i++;
    }
    System.out.println("total = " + i);
  }

}
