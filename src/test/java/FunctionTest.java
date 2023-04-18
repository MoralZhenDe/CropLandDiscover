import org.junit.Test;
import zju.gislab.moral.converts.DateConverter;
import zju.gislab.moral.enity.Feature;
import zju.gislab.moral.enity.enums.WE_FieldType;
import zju.gislab.moral.file.io.ImageFileFactory;
import zju.gislab.moral.file.io.ShapeFileFactory;
import zju.gislab.moral.file.operation.FileOperation;
import zju.gislab.moral.tools.TXTPreviewer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FunctionTest {

    @Test
    public void txtPreview(){
        String cpclDir = "C:\\Users\\moral\\Desktop\\博士论文\\_INPUT\\wheat-2021.rdx";
        TXTPreviewer.fromHead(cpclDir,100);

    }

    @Test
    public void test_reproject(){
        String cpclDir = "C:\\Users\\moral\\Downloads\\cpc2021 (2)\\cpc2021\\wheat\\cpcwheat2021\\progress";
        String targetDir = "C:\\Users\\moral\\Desktop\\博士论文\\CPCL\\2021\\temp\\";
        List<String> cpclPaths = FileOperation.GetFilesBySuffix(cpclDir,"tif");
        for(String path : cpclPaths){
            File tmp = new File(path);
            ImageFileFactory imf = new ImageFileFactory(path);
            imf.reprojectByWrap(targetDir+tmp.getName(),4326);
            imf.close();
        }
    }

    @Test
    public void test_getCellValue(){
        String imgFilePath = "C:\\Users\\moral\\Desktop\\博士论文\\MODIS\\2021\\MODIS-USA-2021-03-28.tif";
        ImageFileFactory iio = new ImageFileFactory(imgFilePath);
        double[] cellValue = new double[1];
        iio.getValueBylonlat(1,-92.99941,40.569507 ,cellValue);
        System.out.println(cellValue[0]);

    }
    @Test
    public void test_imgInfo(){
        String imgFilePath = "C:\\Users\\moral\\Desktop\\博士论文\\CPCL\\2021\\reprojected\\wheatProg21w32.tif";
        ImageFileFactory iio = new ImageFileFactory(imgFilePath);
        System.out.println(iio.getFileInfo());
    }
    @Test
    public void test_updateByindex(){
        String shpFilePath = "C:\\Users\\moral\\Downloads\\gadm41_USA_shp\\gadm41_USA_0.shp";
        ShapeFileFactory sio = new ShapeFileFactory(shpFilePath, true);
        System.out.println("Source:"+sio.getFeatureByIndex(0));
        System.out.println("Updating:"+sio.updateFeatureByIndex(0L,"COUNTRY","CHINA"));
        System.out.println("Updated:"+sio.getFeatureByIndex(0));
    }
    @Test
    public void test_DeleteField(){
        String shpFilePath = "C:\\Users\\moral\\Downloads\\gadm41_USA_shp\\gadm41_USA_0.shp";
        ShapeFileFactory sio = new ShapeFileFactory(shpFilePath, true);
        for(String fdn:sio.getFieldNames()){
            System.out.println(fdn);
        }
        System.out.println(sio.deleteField("MORAL_1"));
        for(String fdn:sio.getFieldNames()){
            System.out.println(fdn);
        }
    }
    @Test
    public void test_CreateField(){
        String shpFilePath = "C:\\Users\\moral\\Downloads\\gadm41_USA_shp\\gadm41_USA_0.shp";
        ShapeFileFactory sio = new ShapeFileFactory(shpFilePath, true);
        System.out.println(sio.createNewField("MORAL", WE_FieldType.DOUBLE));
        for(String fdn:sio.getFieldNames()){
            System.out.println(fdn);
        }
    }
    @Test
    public void test_GetFeaturesByIndex(){
        String shpFilePath = "C:\\Users\\moral\\Desktop\\博士论文\\CDL\\2021\\cdl_ww_2021.shp";
        ShapeFileFactory sio = new ShapeFileFactory(shpFilePath);
        List<Feature> features = new ArrayList<>();
        for(long i=0L;i<1000;i++) {
            features.add(sio.getFeatureByIndex(i));
        }

        System.out.println(features);
    }

    //Get All will OOM
    @Test
    public void test_GetAllFeatures(){
        String shpFilePath = "C:\\Users\\moral\\Desktop\\博士论文\\CDL\\2021\\cdl_ww_2021.shp";
        ShapeFileFactory sio = new ShapeFileFactory(shpFilePath);
        List<Feature> features = sio.getFeatures();
        System.out.println(features.size());
    }
    @Test
    public void test_GetShapeFileInfo(){
        String shpFilePath = "C:\\Users\\moral\\Desktop\\博士论文\\CDL\\2021\\cdl_ww_2021.shp";
        ShapeFileFactory sio = new ShapeFileFactory(shpFilePath);
        System.out.println(sio.getFileInfo());
        for(String fdn:sio.getFieldNames()){
            System.out.println(fdn);
        }
    }
    @Test
    public void test_MatchWeek2Date() {
        Date date = DateConverter.convertNassWeek2Date(2021,13);
        System.out.println(date);
        Date date2 = DateConverter.convertNassWeek2Date(2021,47);
        System.out.println(date2);
    }
    @Test
    public void test_MoveUnZipModisFiles() throws IOException {
        String shpDirPath = "C:\\Users\\moral\\Desktop\\博士论文\\MODIS\\2021\\2";
        FileOperation.CollectFiles(shpDirPath);
    }
}
