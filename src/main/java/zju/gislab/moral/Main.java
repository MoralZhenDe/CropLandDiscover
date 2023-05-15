package zju.gislab.moral;

import zju.gislab.moral.progress.ExtractTestData;
import zju.gislab.moral.progress.SpatialConnectModis2CPCL;
import zju.gislab.moral.progress.SplitInputFileByRate;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        String cdlPath = "C:\\Users\\moral\\Desktop\\博士论文\\CDL\\2021\\cdl_ww_2021.shp";
        String modisDir = "C:\\Users\\moral\\Desktop\\博士论文\\MODIS\\2021";
        String cpclDir = "C:\\Users\\moral\\Desktop\\博士论文\\CPCL\\2021\\reprojected";
        String fileBindingPath = "C:\\Users\\moral\\Desktop\\博士论文\\_INPUT\\wheat-2021.fbd";

        String inputPath = "C:\\Users\\moral\\Desktop\\博士论文\\_INPUT\\wheat-2021.rdx";
        String ratedFileDir = "C:\\Users\\moral\\Desktop\\博士论文\\_INPUT\\ratedFiles\\";


//        BindingFiles.run(cdlPath,modisDir,cpclDir,fileBindingPath);
//        SpatialConnectCPCL2CDL.run(cdlPath,fileBindingPath);
//        SpatialConnectModis2CPCL.run(cdlPath,inputPath,fileBindingPath);
//        * id,lon,lat,week,rate,progressIndex,b1,b2,b3,b4,b5,b6,b7
//        SplitInputFileByRate rfIo = new SplitInputFileByRate(ratedFileDir,8);
//        rfIo.run(inputPath);

        //提取测试数据
        String testImgPath = "C:\\Users\\moral\\Desktop\\博士论文\\MODIS\\2021\\MODIS-USA-2021-09-12.tif";
        String maskPath = "C:\\Users\\moral\\Desktop\\博士论文\\_TEST\\mask\\Mask.shp";
        String testResultDir = "C:\\Users\\moral\\Desktop\\博士论文\\_TEST\\result\\2021";
        ExtractTestData.run(testImgPath, maskPath, testResultDir);

    }

}