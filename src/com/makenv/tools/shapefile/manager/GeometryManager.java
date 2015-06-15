package com.makenv.tools.shapefile.manager;

import com.makenv.tools.shapefile.ICallback;
import com.makenv.tools.shapefile.excutor.GeometryExecutor;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.ResourceInfo;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.factory.Hints;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Geometry manager.
 *
 * @author alei
 */
public abstract class GeometryManager {
  protected Map<String, SimpleFeature> features;
  protected Map<Integer, SimpleFeature> newFeatures;
  protected ICallback callback;
  /**
   * 默认8个线程，具体根据处理器来定
   */
  private static final int DEFAULT_THREAD_NUM = 8;
  private ExecutorService poll;
  protected String file;
  protected ResourceInfo resourceInfo;
  protected int processCount = 0;

  public GeometryManager(String baseFile, String newFile) throws Exception {
    this(baseFile, newFile, DEFAULT_THREAD_NUM);
  }

  public GeometryManager(String baseFile, String newFile, int threadNum) throws Exception {
    features = new LinkedHashMap<>();
    newFeatures = new TreeMap<>();
    poll = Executors.newFixedThreadPool(threadNum);
    file = newFile;
    Map<String, Serializable> _connectParameters = new HashMap<String, Serializable>();
    _connectParameters.put(ShapefileDataStoreFactory.URLP.key, new File(baseFile).toURI().toURL());
    _connectParameters.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, true);
    DataStore _dataStore = DataStoreFinder.getDataStore(_connectParameters);
    String[] _typeNames = _dataStore.getTypeNames();
    String _typeName = _typeNames[0];
    FeatureSource<SimpleFeatureType, SimpleFeature> _featureSource = _dataStore.getFeatureSource(_typeName);
    resourceInfo = _featureSource.getInfo();
    FeatureCollection<SimpleFeatureType, SimpleFeature> _collection = _featureSource.getFeatures();

    FeatureIterator<SimpleFeature> _iterator = _collection.features();
    while (_iterator.hasNext()) {
      SimpleFeature _feature = _iterator.next();
      features.put(_feature.getID(), _feature);
    }
    _iterator.close();
  }

  public void execute(GeometryExecutor executor) {
    poll.execute(executor);
  }

  protected SimpleFeatureBuilder getSimpleFeatureBuilder(Map<String, Class<?>> params) {
    SimpleFeatureTypeBuilder _featureTypeBuilder = new SimpleFeatureTypeBuilder();
    _featureTypeBuilder.setCRS(resourceInfo.getCRS());
    _featureTypeBuilder.setName(resourceInfo.getName());
    _featureTypeBuilder.description(resourceInfo.getDescription());
    for (String _key : params.keySet()) {
      _featureTypeBuilder.add(_key, params.get(_key));
    }
    SimpleFeatureBuilder _featureBuilder = new SimpleFeatureBuilder(_featureTypeBuilder.buildFeatureType());
    _featureBuilder.featureUserData(Hints.USE_PROVIDED_FID, Boolean.TRUE);
    return _featureBuilder;
  }

  // void w() throws Exception {
  // DataStoreFactorySpi factory = new ShapefileDataStoreFactory();
  // Map<String, Serializable> _createParameters = new HashMap<String,
  // Serializable>();
  // _createParameters.put(ShapefileDataStoreFactory.URLP.key, new
  // File(file).toURI().toURL());
  // _createParameters.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key,
  // true);
  // ShapefileDataStore _newDataStore = (ShapefileDataStore)
  // factory.createNewDataStore(_createParameters);
  // SimpleFeatureBuilder _builder = new
  // SimpleFeatureBuilder(_newDataStore.getSchema());
  // String _typeName = _builder.getFeatureType().getTypeName();
  // for (int _key : newFeatures.keySet()) {
  // SimpleFeature _feature = newFeatures.get(_key);
  // _builder.featureUserData(Hints.USE_PROVIDED_FID, Boolean.TRUE);
  // _builder.buildFeature(_typeName + "." + _key);
  // }
  // }

  @SuppressWarnings("unchecked")
  protected void write() {
    DefaultFeatureCollection _outFeatures = new DefaultFeatureCollection();
    // System.out.println();
    for (int _key : newFeatures.keySet()) {
      SimpleFeature _feature = newFeatures.get(_key);
      _outFeatures.add(_feature);
      // System.out.println(_key + ",");
    }
    // for (SimpleFeature _feature : newFeatures.values()) {
    // _feature.getUserData().put(Hints.USE_PROVIDED_FID, true);
    // _feature.getUserData().put(Hints.PROVIDED_FID,
    // _feature.getAttribute("ID"));
    // _outFeatures.add(_feature);
    // }
    try {
      DataStoreFactorySpi factory = new ShapefileDataStoreFactory();
      Map<String, Serializable> _createParameters = new HashMap<String, Serializable>();
      _createParameters.put(ShapefileDataStoreFactory.URLP.key, new File(file).toURI().toURL());
      _createParameters.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, true);
      ShapefileDataStore _newDataStore = (ShapefileDataStore) factory.createNewDataStore(_createParameters);
      _newDataStore.createSchema(_outFeatures.getSchema());
      String typeName = _newDataStore.getTypeNames()[0];
      Transaction _transaction = new DefaultTransaction("create");
      // ContentFeatureSource _featureSource =
      // _newDataStore.getFeatureSource(typeName);
      // _featureSource.setTransaction(_transaction);

      FeatureStore<SimpleFeatureType, SimpleFeature> _featureStore = (FeatureStore<SimpleFeatureType, SimpleFeature>) _newDataStore.getFeatureSource(typeName);
      _featureStore.setTransaction(_transaction);
      try {
        _featureStore.addFeatures(_outFeatures);
        // List<FeatureId> d = _featureStore.addFeatures(_outFeatures);
        // System.out.println("\n" +
        // _featureStore.getQueryCapabilities().isUseProvidedFIDSupported()
        // + "|" + _featureStore.getClass());
        // System.out.println("\n" + Arrays.toString(d.toArray()));
        _transaction.commit();
      } catch (Exception problem) {
        _transaction.rollback();
        throw problem;
      } finally {
        _transaction.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected void onExecuteFinished() {
    poll.shutdown();
    if (callback != null) {
      callback.callback();
    }
  }

  public abstract void onExecuteFinished(String fid, int pid, Object... params);

  public abstract void process();

  public void setCallback(ICallback callback) {
    this.callback = callback;
  }
}
