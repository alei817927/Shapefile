Shapefile
=========

## Introduction 

Simple java library to process shapes from shapefile in multithreading, and it will be create a new shapefile.

## Features

1. Clip

  Extracts input features that overlay the clip features.
  
  Use this feature to cut out a piece of one feature class using one or more of the features in another feature class as a "cookie cutter". This is particularly useful for creating a new feature class—also referred to as study area or area of interest (AOI)—that contains a geographic subset of the features in another, larger feature class.

2. Buffer

  Creates buffer polygons around input features to a specified distance. An optional dissolve can be performed to combine overlapping buffers.

3. Polygon To Line

  Creates a feature class containing lines that are converted from polygon boundaries with or without considering neighboring polygons.

4. Intersect

  Computes a geometric intersection of the input features. Features or portions of features which overlap in all layers and/or feature classes will be written to the output feature class.

## Usage

1. Clip
```java
  GeometryManager _manager = new ClipManager(polygonFile, templateFile, resultFile);
  _manager.process();
```
2. Buffer

```java
	GeometryManager _manager = new BufferManager(lineFile, resultFile, 10);
	manager.process();
```

3. Polygon To Line
```java
	GeometryManager _manager = new AreaToLineManager(polygonFile, resultFile);
	_manager.process();
```
4. Intersect

```java
  GeometryManager _manager = new IntersectManager(sourceFile1, sourceFile2, resultFile);
	_manager.process();
```
