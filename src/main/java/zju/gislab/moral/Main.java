package zju.gislab.moral;

import zju.gislab.moral.progress.BindingFiles;
import zju.gislab.moral.progress.SpatialConnectCPCL2CDL;
import zju.gislab.moral.progress.SpatialConnectRef2CPCL;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        String cdlPath = "C:\\Users\\moral\\Desktop\\博士论文\\CDL\\2021\\cdl_ww_2021.shp";
        String modisDir = "C:\\Users\\moral\\Desktop\\博士论文\\MODIS\\2021";
        String cpclDir = "C:\\Users\\moral\\Desktop\\博士论文\\CPCL\\2021\\reprojected";
        String fileBindingPath = "C:\\Users\\moral\\Desktop\\博士论文\\_INPUT\\wheat-2021.fbd";

        String inputPath = "C:\\Users\\moral\\Desktop\\博士论文\\_INPUT\\wheat-2021.rdx";

        BindingFiles.run(cdlPath,modisDir,cpclDir,fileBindingPath);
        SpatialConnectCPCL2CDL.run(cdlPath,fileBindingPath);
        SpatialConnectRef2CPCL.run(cdlPath,inputPath,fileBindingPath);

    }

}