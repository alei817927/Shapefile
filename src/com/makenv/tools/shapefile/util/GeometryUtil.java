package com.makenv.tools.shapefile.util;

import com.makenv.tools.shapefile.GeometryComparator;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeometryUtil {
  private static final int DEFAULT_BATCH_UNION_COUNT = 10;
  private static GeometryComparator DEFAULT_GEOMETRY_COMPARATOR = new GeometryComparator();

  private static List<Geometry> mergeGeometries(List<Geometry> geometries, int batchCount, int round) {
    List<Geometry> _unionGeometries = new ArrayList<>();
    Geometry _tmpGeometry = null;
    int i = 0;
    for (; i < geometries.size(); i++) {
      Geometry _geometry = geometries.get(i);
      if (i % batchCount == 0 && i > 0) {
        _unionGeometries.add((Geometry) _tmpGeometry.clone());
        _tmpGeometry = null;
      }
      _tmpGeometry = union(_tmpGeometry, _geometry);
    }
    _unionGeometries.add(_tmpGeometry);
    i = 0;
    if (_unionGeometries.size() > batchCount) {
      Collections.sort(_unionGeometries, DEFAULT_GEOMETRY_COMPARATOR);
      return mergeGeometries(_unionGeometries, batchCount, round + 1);
    }
    return _unionGeometries;
  }

  public static Geometry union(Geometry g1, Geometry g2) {
    if (g1 == null) {
      return (Geometry) g2.clone();
    }
    return g1.union(g2);
  }

  public static Geometry union(List<Geometry> geometries) {
    return union(geometries, DEFAULT_BATCH_UNION_COUNT);
  }

  public static Geometry union(List<Geometry> geometries, int batchCount) {
    List<Geometry> _unionGeometries = mergeGeometries(geometries, batchCount, 1);
    Geometry _unionGeometry = null;
    for (Geometry _geometry : _unionGeometries) {
      _unionGeometry = union(_unionGeometry, _geometry);
    }
    return _unionGeometry;
  }

  public static List<Geometry> readShpFile(String file) throws Exception {
    List<Geometry> _geometries = new ArrayList<>();
    ShpFiles sf = new ShpFiles(file);
    ShapefileReader r = new ShapefileReader(sf, false, false, new GeometryFactory());
    try {
      while (r.hasNext()) {
        Geometry _shape = (Geometry) r.nextRecord().shape(); // com.vividsolutions.jts.geom.Geometry;
        _geometries.add(_shape);
      }
    } finally {
      if (r != null) r.close();
    }
    return _geometries;
  }

  public static Envelope getEnvelope(String file) throws Exception {
    ShpFiles _shpFile = new ShpFiles(file);
    ShapefileReader _reader = new ShapefileReader(_shpFile, false, false, new GeometryFactory());
    Envelope _envelope = null;
    try {
      while (_reader.hasNext()) {
        Geometry _shape = (Geometry) _reader.nextRecord().shape();
        if (_envelope == null) {
          _envelope = _shape.getEnvelopeInternal();
        } else {
          _envelope.expandToInclude(_shape.getEnvelopeInternal());
        }
      }
    } finally {
      if (_reader != null) _reader.close();
    }
    return _envelope;
  }
}
