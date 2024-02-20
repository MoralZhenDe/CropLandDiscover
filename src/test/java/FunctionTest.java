import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.ogr;
import org.gdal.osr.SpatialReference;
import org.junit.Test;
import zju.gislab.moral.converts.DateConverter;
import zju.gislab.moral.enity.Feature;
import zju.gislab.moral.enity.enums.WE_FieldType;
import zju.gislab.moral.file.io.ImageFileFactory;
import zju.gislab.moral.file.io.ShapeFileFactory;
import zju.gislab.moral.file.io.SystemFileFactory;
import zju.gislab.moral.file.operation.FileOperation;
import zju.gislab.moral.tools.Helper.CdlHelper;
import zju.gislab.moral.tools.Helper.FileHelper;
import zju.gislab.moral.tools.Helper.S2Helper;
import zju.gislab.moral.tools.TXTPreviewer;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javafx.stage.Stage;

import javax.swing.*;

public class FunctionTest {
    @Test
    public void test_convert2csv(){

        String cpField = "C:\\Users\\moral\\Desktop\\博士论文\\第一章实验\\cdl_2019_tkss_clip_00.tif";
        String S2 = "C:\\Users\\moral\\Desktop\\博士论文\\第一章实验\\S2_Tkss_2019-0000000000-0000000000.tif";

        ImageFileFactory s2Imf = new ImageFileFactory(S2);
        ImageFileFactory cpImg = new ImageFileFactory(cpField);
        SpatialReference s2SRS = s2Imf.getSRS();
        SpatialReference cpSRS = cpImg.getSRS();
        int[] size_S2 =s2Imf.getImageSize();
        int[] size_CDL =cpImg.getImageSize();
        System.out.println("S2: " + s2SRS.GetName()+"-"+size_S2[0]+"x"+size_S2[1]);
        System.out.println("CDL: " + cpSRS.GetName()+"-"+size_CDL[0]+"x"+size_CDL[1]);

        String csvPath = "C:\\Users\\moral\\Desktop\\博士论文\\第一章实验\\" + cpImg.getImgName().replace(".tif", ".csv");
        s2Imf.convert2CSVWithCDL(csvPath, cpField);
        s2Imf.close();

    }


    @Test
    public void test_pickByXY() {
        String sourcePath = "C:\\Users\\moral\\Desktop\\Global-Cropland\\_moral_zhijiang\\SAM_fine_tune\\test";
        List<File> pngs = FileHelper.getFileListDeep(sourcePath, ".TIF");
        for (File img : pngs) {
            ImageFileFactory imf = new ImageFileFactory(img.getAbsolutePath(), true);
            int[] xy = imf.getImageSize();
            if (xy[0] != 1024 | xy[1] != 1024) {
                System.out.println(xy[0] + ":" + xy[1] + ":" + imf.getImgPath());
            }
            imf.close();
        }
    }


    @Test
    public void test_translateMaskCsv() {
        String sourcePath = "C:\\Users\\moral\\Desktop\\Global-Cropland\\_moral_zhijiang\\SAM_Mask_data\\fine_tune_Date\\russia\\segment";
        List<File> pngs = FileHelper.getFileListDeep(sourcePath, ".png");
        for (File png : pngs) {
            String targetPath = png.getAbsolutePath().replace(".png", ".csv");
            ImageFileFactory imf = new ImageFileFactory(png.getAbsolutePath(), true);
            imf.convert2CSV_Binary(1, targetPath);
            imf.close();
        }
    }
    @Test
    public void test_cutByMask() {
        String sourceImg = "C:\\Users\\moral\\Desktop\\Global-Cropland\\_moral_zhijiang\\SAM_fine_tune\\tmp\\boundary_buf_bin.tif";
        ImageFileFactory imf = new ImageFileFactory(sourceImg);
        String sourcePath = "C:\\Users\\moral\\Desktop\\Global-Cropland\\_moral_zhijiang\\SAM_fine_tune\\tmp";
        String sourceRoot = "C:\\Users\\moral\\Desktop\\Global-Cropland\\_moral_zhijiang\\SAM_fine_tune\\train_mask\\";
        int[] tr = {1024, 1024};
        List<File> extents = FileHelper.getFileListDeep(sourcePath, ".shp");
        for (File extent : extents) {
            String targetMask = sourceRoot + extent.getName().replace(".shp", ".tif");
            imf.clipByMask_ts(targetMask, extent.getAbsolutePath(),tr);
        }
        imf.close();
    }
    @Test
    public void test_Translate2RGB() throws IOException {
        String sourcePath = "C:\\Users\\moral\\Desktop\\Global-Cropland\\_moral_zhijiang\\SAM_fine_tune\\test";
        String targetRoot = "C:\\Users\\moral\\Desktop\\Global-Cropland\\_moral_zhijiang\\SAM_fine_tune\\test_source\\";
        int[] rgb = {4, 3, 2};
        List<File> imgs = FileHelper.getFileListDeep(sourcePath, ".TIF");
        for (File img : imgs) {
            ImageFileFactory imf = new ImageFileFactory(img.getAbsolutePath());
            imf.RGB(rgb, targetRoot+img.getName().replace(".TIF", ".tif"));
            imf.close();
        }
    }

    @Test
    public void test_Translate2Binary() throws IOException {
        List<File> imgs = FileHelper.getFileListDeep("C:\\Users\\moral\\Desktop\\Global-Cropland\\_moral_zhijiang\\SAM_Mask_data\\fine_tune_Date\\russia\\segment", ".tif");
        for (File img : imgs) {
            ImageFileFactory imf = new ImageFileFactory(img.getAbsolutePath());
            imf.Binary(1, img.getAbsolutePath().replace(".tif", ".png"));
            imf.close();
        }
    }

    @Test
    public void test_CreateMask() throws IOException {
        List<File> imgs = FileHelper.getFileListDeep("C:\\Users\\moral\\Desktop\\Global-Cropland\\_moral_zhijiang\\SAM_fine_tune\\tmp", ".TIF");
        for (File img : imgs) {
            ShapeFileFactory mask = new ShapeFileFactory();
            mask.createFromIMG(img.getAbsolutePath());
            System.out.println(mask.getFilePath() + "**************************DONE");
            mask.close();
        }
        System.out.println("**************************DONE**************************");
    }

    @Test
    public void test_Polygonize() {
        String sourceMask = "C:\\Users\\moral\\Downloads\\demo_out.tif";
        ImageFileFactory img = new ImageFileFactory(sourceMask);
        String sourceIMG = "C:\\Users\\moral\\Desktop\\Global-Cropland\\_moral_zhijiang\\Extract_S2_R2.tif";
        ImageFileFactory sourceImgTrans = new ImageFileFactory(sourceIMG);
        img.setGeoTransform(sourceImgTrans.getGeoTransform());
        img.setSpatialReference(sourceImgTrans.getSRS());
        img.polygonize(1);
        img.close();
    }

    @Test
    public void testBandStat() {
        String cpcRoot = "C:\\Users\\moral\\Desktop\\Global Cropland\\_moral_zhijiang\\CPCL_500m\\";

        ImageFileFactory img = new ImageFileFactory(cpcRoot);
        double[][] re = img.getStatistics(1);
        for (double[] d : re) {
            System.out.println(d[0]);
        }
        int[] histogramData = img.getHistogram(1, 0, 1, 100);

        String csvFilePath = "C:\\Users\\moral\\Desktop\\Global Cropland\\_moral_zhijiang\\histogram_data.csv";

        // 调用导出方法
        SystemFileFactory.exportHistogram(histogramData, csvFilePath);

        img.close();

    }


    @Test
    public void txtPreview() {
        String cpclDir = "C:\\Users\\moral\\Desktop\\博士论文\\_INPUT\\ratedFiles\\1.rdx";
        TXTPreviewer.fromHead(cpclDir, 10);
        TXTPreviewer.fromEnd(cpclDir, 10);
    }

    @Test
    public void testHelper() throws ParseException, IOException {
        String workspacePath = "C:\\Users\\moral\\Desktop\\Global Cropland\\_moral_zhijiang\\";
        String s2Name = workspacePath + "S2\\02\\R20m\\";
        List<File> s2Source = S2Helper.PickUsefullPath(s2Name);
        ImageFileFactory tmpImg = new ImageFileFactory(s2Source.get(0).getAbsolutePath());
        double[] targetSize = tmpImg.getExtent();

        String s2Path = "T14SNH_20210924T171949_TCI_20m.jp2";
        String cdlRoot = "C:\\Users\\moral\\Desktop\\Global Cropland\\_moral_zhijiang\\CDL_30m\\";
        int[] date = S2Helper.GetDateFromFileName(s2Path);
        System.out.println(DateConverter.convertDate2NassWeek(date[0], date[1], date[2]));

        String re = CdlHelper.GetCdlByDateAndExtent(cdlRoot, 2021, targetSize[0], targetSize[1], targetSize[2], targetSize[3]);
        System.out.println(re);
    }

    @Test
    public void testMosaic() {
        String targetPath = "C:\\Users\\moral\\Desktop\\Global Cropland\\_moral_zhijiang\\_INPUT\\mo.tif";
        String sourceImg = "C:\\Users\\moral\\Desktop\\Global Cropland\\_moral_zhijiang\\S2\\02\\R20m\\T14SNH_20210924T171949_TCI_20m.jp2";
        ImageFileFactory imf = new ImageFileFactory(sourceImg);
        List<String> filePathes = new ArrayList<>();
        filePathes.add("C:\\Users\\moral\\Desktop\\Global Cropland\\_moral_zhijiang\\S2\\01\\R20m\\T14SMH_20210924T171949_TCI_20m.jp2");
        imf.Mosaic(filePathes, targetPath);
        imf.close();
    }

    @Test
    public void test_MetaRpc() throws IOException {
        String sourceImg = "C:\\Users\\moral\\Desktop\\XYC\\gdal_rpc_test\\noRPC.tiff";
        ImageFileFactory imf = new ImageFileFactory(sourceImg);
        imf.getRpc("C:\\Users\\moral\\Desktop\\XYC\\gdal_rpc_test\\noRPC.rpb");
    }

    @Test
    public void test_getExtent() throws IOException {
        String sourceImg = "C:\\Users\\moral\\Downloads\\S2B_MSIL2A_20210924T171949_N0500_R012_T14SNJ_20230116T181643.SAFE\\" +
                "S2B_MSIL2A_20210924T171949_N0500_R012_T14SNJ_20230116T181643.SAFE\\GRANULE\\L2A_T14SNJ_A023776_20210924T172421\\" +
                "IMG_DATA\\R20m\\T14SNJ_20210924T171949_AOT_20m.jp2";
        ImageFileFactory imf = new ImageFileFactory(sourceImg);
        double[] re = imf.getExtent();
        ShapeFileFactory sff = new ShapeFileFactory("C:\\Users\\moral\\Desktop\\Global Cropland\\_moral_zhijiang\\mask2.shp", re, imf.getSRS());
        sff.close();
    }




    @Test
    public void test_reproject() {
        String cpclDir = "C:\\Users\\moral\\Downloads\\cpc2021 (2)\\cpc2021\\wheat\\cpcwheat2021\\progress";
        String targetDir = "C:\\Users\\moral\\Desktop\\博士论文\\CPCL\\2021\\temp\\";
        List<String> cpclPaths = FileOperation.GetFilesBySuffix(cpclDir, "tif");
        for (String path : cpclPaths) {
            File tmp = new File(path);
            ImageFileFactory imf = new ImageFileFactory(path);
            imf.reprojectByWrap(targetDir + tmp.getName(), 4326);
            imf.close();
        }
    }

    @Test
    public void test_fzy_PickCellValueByLonLat() {
        String shpPath = "";
        String imgPath = "";

        gdal.AllRegister();

        DataSource ds = ogr.Open(shpPath, gdalconstConstants.GA_ReadOnly);
        org.gdal.ogr.Layer features = ds.GetLayer(0);
        Dataset dataset = gdal.Open(imgPath, gdalconstConstants.GA_ReadOnly);
        double[] geoTransform = dataset.GetGeoTransform();

        List<String> records = new ArrayList<>();
        for (long i = 0; i < features.GetFeatureCount(); i++) {
            StringBuilder sbr = new StringBuilder();
            org.gdal.ogr.Feature fe = features.GetFeature(i);
            sbr.append(fe.GetFieldAsString("id"));
            int[] row_col = geo2ImageXY(geoTransform, fe.GetGeometryRef().GetX(), fe.GetGeometryRef().GetY());
            //BandIndex start with 1, not 0
            for (int bandIndex = 1; bandIndex <= dataset.getRasterCount(); bandIndex++) {
                double[] val = new double[1];
                dataset.GetRasterBand(bandIndex).ReadRaster(row_col[0], row_col[1], 1, 1, val);
                sbr.append(",").append(String.valueOf(val[0]));
            }
            records.add(sbr.toString());
        }
        for (String str : records) {
            System.out.println(str);
        }
    }

    private static int[] geo2ImageXY(double[] trans, double x, double y) {
        double d1 = trans[1] * trans[5];
        double d2 = trans[2] * trans[4];

        int row = (int) ((trans[5] * x - trans[2] * y - trans[0] * trans[5] + trans[2] * trans[3]) / (d1 - d2));
        int col = (int) ((trans[4] * x - trans[1] * y - trans[0] * trans[4] + trans[1] * trans[3]) / (d2 - d1));
        return new int[]{row, col};
    }


    @Test
    public void test_getCellValue() {
        String imgFilePath = "C:\\Users\\moral\\Desktop\\博士论文\\MODIS\\2021\\MODIS-USA-2021-03-28.tif";
        ImageFileFactory iio = new ImageFileFactory(imgFilePath);
        double[] cellValue = new double[1];
        iio.getValueBylonlat(1, -92.99941, 40.569507, cellValue);
        System.out.println(cellValue[0]);

    }

    @Test
    public void test_imgInfo() {
        String imgFilePath = "C:\\Users\\moral\\Desktop\\Global Cropland\\_moral_zhijiang\\S2\\02\\R20m\\T14SNH_20210924T171949_AOT_20m.jp2";
        ImageFileFactory iio = new ImageFileFactory(imgFilePath);
        System.out.println(iio.getFileInfo());
    }

    @Test
    public void test_updateByindex() {
        String shpFilePath = "C:\\Users\\moral\\Downloads\\gadm41_USA_shp\\gadm41_USA_0.shp";
        ShapeFileFactory sio = new ShapeFileFactory(shpFilePath, true);
        System.out.println("Source:" + sio.getFeatureByIndex(0));
        System.out.println("Updating:" + sio.updateFeatureByIndex(0L, "COUNTRY", "CHINA"));
        System.out.println("Updated:" + sio.getFeatureByIndex(0));
    }

    @Test
    public void test_DeleteField() {
        String shpFilePath = "C:\\Users\\moral\\Downloads\\gadm41_USA_shp\\gadm41_USA_0.shp";
        ShapeFileFactory sio = new ShapeFileFactory(shpFilePath, true);
        for (String fdn : sio.getFieldNames()) {
            System.out.println(fdn);
        }
        System.out.println(sio.deleteField("MORAL_1"));
        for (String fdn : sio.getFieldNames()) {
            System.out.println(fdn);
        }
    }

    @Test
    public void test_CreateField() {
        String shpFilePath = "C:\\Users\\moral\\Downloads\\gadm41_USA_shp\\gadm41_USA_0.shp";
        ShapeFileFactory sio = new ShapeFileFactory(shpFilePath, true);
        System.out.println(sio.createNewField("MORAL", WE_FieldType.DOUBLE));
        for (String fdn : sio.getFieldNames()) {
            System.out.println(fdn);
        }
    }

    @Test
    public void test_GetFeaturesByIndex() {
        String shpFilePath = "C:\\Users\\moral\\Desktop\\博士论文\\CDL\\2021\\cdl_ww_2021.shp";
        ShapeFileFactory sio = new ShapeFileFactory(shpFilePath);
        List<Feature> features = new ArrayList<>();
        for (long i = 0L; i < 1000; i++) {
            features.add(sio.getFeatureByIndex(i));
        }

        System.out.println(features);
    }

    //Get All will OOM
    @Test
    public void test_GetAllFeatures() {
        String shpFilePath = "C:\\Users\\moral\\Desktop\\博士论文\\CDL\\2021\\cdl_ww_2021.shp";
        ShapeFileFactory sio = new ShapeFileFactory(shpFilePath);
        List<Feature> features = sio.getFeatures();
        System.out.println(features.size());
    }

    @Test
    public void test_GetShapeFileInfo() {
        String shpFilePath = "C:\\Users\\moral\\Desktop\\XYC\\aoi9.shp";
        ShapeFileFactory sio = new ShapeFileFactory(shpFilePath);
        System.out.println(sio.getFileInfo());
        for (String fdn : sio.getFieldNames()) {
            System.out.println(fdn);
        }
    }

    @Test
    public void test_MatchWeek2Date() {
        Date date = DateConverter.convertNassWeek2Date(2020, 53);
        System.out.println(date);

        Date date2 = DateConverter.convertNassWeek2Date(2021, 13);
        System.out.println(date2);

//        int week = DateConverter.convertDate2NassWeek(2021,11,14);
//        System.out.println(week);
    }

    @Test
    public void test_MoveUnZipModisFiles() throws IOException {
        String shpDirPath = "C:\\Users\\moral\\Desktop\\博士论文\\MODIS\\2021\\2";
        FileOperation.CollectFiles(shpDirPath);
    }
}
