package com.meic.shapefile;

import java.util.Comparator;

import com.vividsolutions.jts.geom.Geometry;

public class GeometryComparator implements Comparator<Geometry> {

	@Override
	public int compare(Geometry o1, Geometry o2) {
		return (int) (o1.getLength() - o2.getLength());
	}

}
