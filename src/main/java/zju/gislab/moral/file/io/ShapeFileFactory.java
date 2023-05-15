package zju.gislab.moral.file.io;

import org.gdal.gdal.gdal;
import org.gdal.ogr.*;
import zju.gislab.moral.enity.Feature;
import zju.gislab.moral.enity.Field;
import zju.gislab.moral.enity.enums.WE_FieldType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ShapeFileFactory {

    private static final Logger logger = Logger.getLogger(ShapeFileFactory.class.getName());
    private DataSource ds = null;

    private void initialize() {
        ogr.RegisterAll();
        gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
        gdal.SetConfigOption("SHAPE_ENCODING", "UTF8");
    }

    public ShapeFileFactory(String shpPath, boolean ifUpdate) {
        initialize();
        this.ds = ogr.Open(shpPath, ifUpdate);
    }

    public ShapeFileFactory(String shpPath) {
        initialize();
        this.ds = ogr.Open(shpPath,false);
    }

    /***
     * 获取shapefile文件信息
     */
    public String getFileInfo() {
        int LayerCount = ds.GetLayerCount();
        StringBuilder infoStr = new StringBuilder("File: " + ds.getName());

        infoStr.append("\r\n");
        infoStr.append(String.format("LayerCount: %d;", LayerCount));
        infoStr.append("\r\n");
        infoStr.append("PROJ: ").append(ds.GetLayer(0).GetSpatialRef().GetName());

        for (int i = 0; i < LayerCount; i++) {
            infoStr.append("\r\n");
            infoStr.append(String.format("Layer_" + i + "_Name: " + ds.GetLayer(i).GetName()));
            infoStr.append("\r\n");
            infoStr.append(String.format("Layer_" + i + "_Count: " + ds.GetLayer(i).GetFeatureCount()));
        }
        return infoStr.toString();
    }

    /***
     * 创建字段
     */
    public boolean createNewField(int layerIndex, String fieldName, WE_FieldType fieldType) {
        org.gdal.ogr.Layer layer = ds.GetLayer(layerIndex);
        if (layer.GetLayerDefn().GetFieldIndex(fieldName) > 0) {
            logger.warning("Field with the same name already exists.");
            return false;
        }

        FieldDefn fieldDefn = new FieldDefn();
        fieldDefn.SetName(fieldName);

        switch (fieldType) {
            case STRING:
                fieldDefn.SetType(ogr.OFTString);
            case INT:
                fieldDefn.SetType(ogr.OFTInteger);
            case LONG:
                fieldDefn.SetType(ogr.OFTInteger64);
            case FLOAT:
                fieldDefn.SetType(ogr.OFSTFloat32);
            case DOUBLE:
                fieldDefn.SetType(ogr.OFTReal);
            case DATE:
                fieldDefn.SetType(ogr.OFTDate);
            default:
                fieldDefn.SetType(ogr.OFTString);
        }

        layer.CreateField(fieldDefn);
        return layer.GetLayerDefn().GetFieldIndex(fieldName) > 0;
    }

    public boolean createNewField(String fieldName, WE_FieldType fieldType) {
        return createNewField(0, fieldName, fieldType);
    }

    /***
     * 删除字段
     */
    public boolean deleteField(int layerIndex, String fieldName) {
        org.gdal.ogr.Layer layer = ds.GetLayer(layerIndex);
        int fieldIndex = layer.GetLayerDefn().GetFieldIndex(fieldName);
        if (fieldIndex < 0) {
            logger.warning("字段不存在：" + fieldName);
            return false;
        } else {
            layer.DeleteField(fieldIndex);
            return true;
        }
    }

    public boolean deleteField(String fieldName) {
        return deleteField(0, fieldName);
    }

    /***
     * 指定字段index，按索引位置更新单个Feature
     */
    public boolean updateFeatureByIndex(int layerIndex, long featureIndex, int fieldIndex, Object fieldValue) {
        boolean ifUpdated = true;
        try {
            org.gdal.ogr.Layer layer = ds.GetLayer(layerIndex);
            org.gdal.ogr.Feature fe = layer.GetFeature(featureIndex);
            FieldDefn fdn = fe.GetDefnRef().GetFieldDefn(fieldIndex);

            switch (fdn.GetTypeName()) {
                case "Integer64":
                    fe.SetField(fieldIndex, (int) fieldValue);
                case "Real":
                    fe.SetField(fieldIndex, (double) fieldValue);
                default:
                    fe.SetField(fieldIndex, fieldValue.toString());
            }
            layer.SetFeature(fe);

        } catch (Exception e) {
            logger.warning(e.getMessage());
            ifUpdated = false;
        }
        return ifUpdated;
    }

    public boolean updateFeatureByIndex(long featureIndex, int fieldIndex, Object fieldValue) {
        return updateFeatureByIndex(0, featureIndex, fieldIndex, fieldValue);
    }

    /***
     * 指定字段名，按索引位置更新单个Feature
     */
    public boolean updateFeatureByIndex(int layerIndex, long featureIndex, String fieldName, Object fieldValue) {
        org.gdal.ogr.Layer layer = ds.GetLayer(layerIndex);
        int fIndex = layer.GetLayerDefn().GetFieldIndex(fieldName);
        if (fIndex < 0) {
            logger.warning("字段不存在:" + fieldName);
            return false;
        } else {
            return updateFeatureByIndex(layerIndex, featureIndex, fIndex, fieldValue);
        }
    }

    public boolean updateFeatureByIndex(long featureIndex, String fieldName, Object fieldValue) {
        return updateFeatureByIndex(0, featureIndex, fieldName, fieldValue);
    }


    /***
     * 按索引位置获取单个Feature
     */
    public Feature getFeatureByIndex(int layerIndex, long featureIndex) {
        org.gdal.ogr.Layer layer = ds.GetLayer(layerIndex);
        org.gdal.ogr.Feature fe = layer.GetFeature(featureIndex);
        Feature cell = new Feature();
        cell.setGeom(fe.GetGeometryRef());

        Field[] fields = new Field[fe.GetFieldCount()];
        for (int fi = 0; fi < fields.length; fi++) {
            FieldDefn fdn = fe.GetDefnRef().GetFieldDefn(fi);
            Field fie = new Field();
            fie.setName(fdn.GetName());
            fie.setType(fdn.GetTypeName());
            fie.setValue(CatchFieldValue(fe, fi, fdn.GetTypeName()));
            fields[fi] = fie;
        }

        cell.setFields(fields);
        return cell;
    }

    public Feature getFeatureByIndex(long featureIndex) {
        return getFeatureByIndex(0, featureIndex);
    }

    /***
     * 按index，获取geometry
     */
    public Geometry getGeomByIndex(int layerIndex, long featureIndex) {
        org.gdal.ogr.Layer layer = ds.GetLayer(layerIndex);
        org.gdal.ogr.Feature fe = layer.GetFeature(featureIndex);
        return fe.GetGeometryRef();
    }
    public Geometry getGeomByIndex(long featureIndex) {
        return getGeomByIndex(0, featureIndex);
    }

    /***
     *获取单要素的指定属性值
     */
    public Field getFieldByIndex(int layerIndex, long featureIndex,String fieldName) {
        org.gdal.ogr.Layer layer = ds.GetLayer(layerIndex);
        org.gdal.ogr.Feature fe = layer.GetFeature(featureIndex);
        int fi = fe.GetDefnRef().GetFieldIndex(fieldName);
            FieldDefn fdn = fe.GetDefnRef().GetFieldDefn(fi);
            Field fie = new Field();
            fie.setName(fdn.GetName());
            fie.setType(fdn.GetTypeName());
            fie.setValue(CatchFieldValue(fe, fi, fdn.GetTypeName()));
        return fie;
    }

    public Field getFieldByIndex(long featureIndex,String fieldName) {
        return getFieldByIndex(0, featureIndex,fieldName);
    }

    /***
     * 获取Features总数
     */
    public long getFeatureCount(int layerIndex) {
        org.gdal.ogr.Layer layer = ds.GetLayer(layerIndex);
        return layer.GetFeatureCount();
    }

    public long getFeatureCount() {
        return getFeatureCount(0);
    }

    /***
     * 获取字段名以及字段类型
     */
    public String[] getFieldNames(int layerIndex) {
        org.gdal.ogr.Layer layer = ds.GetLayer(layerIndex);
        FeatureDefn featureDefn = layer.GetLayerDefn();

        int fieldCount = featureDefn.GetFieldCount();
        String[] names = new String[fieldCount];
        for (int i = 0; i < fieldCount; i++) {
            FieldDefn fdn = featureDefn.GetFieldDefn(i);
            names[i] = fdn.GetName() + "-" + fdn.GetTypeName();
        }
        return names;
    }

    public String[] getFieldNames() {
        return getFieldNames(0);
    }

    /***
     * 全量读取Features至内存
     */
    public List<Feature> getFeatures(int layerIndex) {
        org.gdal.ogr.Layer layer = ds.GetLayer(layerIndex);
        List<Feature> features = new ArrayList<>();
        long featureCount = layer.GetFeatureCount();
        for (long i = 0L; i < featureCount; i++) {
            org.gdal.ogr.Feature fe = layer.GetFeature(i);
            Feature cell = new Feature();
            cell.setGeom(fe.GetGeometryRef());
            FeatureDefn featureDefn = layer.GetLayerDefn();
            Field[] fields = new Field[featureDefn.GetFieldCount()];
            for (int fi = 0; fi < fields.length; fi++) {
                FieldDefn fdn = featureDefn.GetFieldDefn(fi);
                Field fie = new Field();
                fie.setName(fdn.GetName());
                fie.setType(fdn.GetTypeName());
                fie.setValue(CatchFieldValue(fe, fi, fdn.GetTypeName()));
                fields[fi] = fie;
            }
            cell.setFields(fields);
            features.add(cell);
        }
        return features;
    }

    public void close() {
        this.ds =null;
    }

    public List<Feature> getFeatures() {
        return getFeatures(0);
    }

    private Object CatchFieldValue(org.gdal.ogr.Feature feature, int f_index, String type) {
        switch (type) {
            case "Integer64":
                return feature.GetFieldAsInteger64(f_index);
            case "Real":
                return feature.GetFieldAsDouble(f_index);
            default:
                return feature.GetFieldAsString(f_index);
        }
    }

}
