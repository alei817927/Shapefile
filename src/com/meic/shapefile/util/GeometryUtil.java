package com.meic.shapefile.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;

import com.meic.shapefile.GeometryComparator;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class GeometryUtil {
	private static final int DEFAULT_BATCH_UNION_COUNT = 10;
	private static GeometryComparator DEFAULT_GEOMETRY_COMPARATOR = new GeometryComparator();

	private static List<Geometry> mergeGeometrys(List<Geometry> geometrys, int batchCount, int round) {
		List<Geometry> _unionGeometrys = new ArrayList<>();
		Geometry _tmpGeometry = null;
		int i = 0;
		for (; i < geometrys.size(); i++) {
			Geometry _geometry = geometrys.get(i);
			if (i % batchCount == 0 && i > 0) {
				_unionGeometrys.add((Geometry) _tmpGeometry.clone());
				_tmpGeometry = null;
			}
			_tmpGeometry = union(_tmpGeometry, _geometry);
		}
		_unionGeometrys.add(_tmpGeometry);
		i = 0;
		if (_unionGeometrys.size() > batchCount) {
			Collections.sort(_unionGeometrys, DEFAULT_GEOMETRY_COMPARATOR);
			return mergeGeometrys(_unionGeometrys, batchCount, round + 1);
		}
		return _unionGeometrys;
	}

	public static Geometry union(Geometry g1, Geometry g2) {
		if (g1 == null) {
			return (Geometry) g2.clone();
		}
		return g1.union(g2);
	}

	public static Geometry union(List<Geometry> geometrys) {
		return union(geometrys, DEFAULT_BATCH_UNION_COUNT);
	}

	public static Geometry union(List<Geometry> geometrys, int batchCount) {
		List<Geometry> _unionGeometrys = mergeGeometrys(geometrys, batchCount, 1);
		Geometry _unionGeometry = null;
		for (Geometry _geometry : _unionGeometrys) {
			_unionGeometry = union(_unionGeometry, _geometry);
		}
		return _unionGeometry;
	}

	public static List<Geometry> readShpFile(String file) throws Exception {
		List<Geometry> _geometrys = new ArrayList<>();
		ShpFiles sf = new ShpFiles(file);
		ShapefileReader r = new ShapefileReader(sf, false, false, new GeometryFactory());
		while (r.hasNext()) {
			Geometry _shape = (Geometry) r.nextRecord().shape(); // com.vividsolutions.jts.geom.Geometry;
			_geometrys.add(_shape);
		}
		r.close();
		return _geometrys;
	}

	public static Envelope getEnvelope(String file) throws Exception {
		ShpFiles _shpFile = new ShpFiles(file);
		ShapefileReader _reader = new ShapefileReader(_shpFile, false, false, new GeometryFactory());
		Envelope _envelope = null;
		while (_reader.hasNext()) {
			Geometry _shape = (Geometry) _reader.nextRecord().shape();
			if (_envelope == null) {
				_envelope = _shape.getEnvelopeInternal();
			} else {
				_envelope.expandToInclude(_shape.getEnvelopeInternal());
			}
		}
		_reader.close();
		return _envelope;
	}
}
