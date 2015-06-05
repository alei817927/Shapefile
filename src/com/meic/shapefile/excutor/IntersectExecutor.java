package com.meic.shapefile.excutor;

import com.meic.shapefile.manager.GeometryManager;
import com.meic.shapefile.util.GeometryUtil;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;

import java.util.ArrayList;
import java.util.List;

public class IntersectExecutor extends GeometryExecutor {
  private static double GRID_AREA = 0;
  private static Polygon EMPTY_POLYGON = new Polygon(null, null, new GeometryFactory());
  protected List<Geometry> geometries;

  public IntersectExecutor(SimpleFeature feature, List<Geometry> geometries, GeometryManager manager, int id) {
    super(feature, manager, id);
    this.geometries = geometries;
  }

  @Override
  public void run() {
    List<Geometry> _intersectGeometries = new ArrayList<>();
    Geometry _shape = (Geometry) feature.getDefaultGeometry();
    Envelope _envelope = _shape.getEnvelopeInternal();
    for (Geometry _geometry : geometries) {
      if (_envelope.intersects(_geometry.getEnvelopeInternal()) && _shape.intersects(_geometry)) {
        _intersectGeometries.add(_shape.intersection(_geometry));
      }
    }
    Geometry _geometry = null;
    double _area = 0;
    if (GRID_AREA == 0) {
      GRID_AREA = _shape.getArea();
    }
    if (!_intersectGeometries.isEmpty()) {
      _geometry = GeometryUtil.union(_intersectGeometries);
      _area = _geometry.getArea();
    } else {
      _geometry = EMPTY_POLYGON;
    }
    double _rate = _area == 0 ? 0 : _area / GRID_AREA;
    manager.onExecuteFinished(feature.getID(), id, _geometry, _area, _rate);
  }
}
