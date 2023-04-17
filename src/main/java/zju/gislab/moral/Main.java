package zju.gislab.moral;

import zju.gislab.moral.progress.SpatialConnect2CDL;

public class Main {

    public static void main(String[] args) {
        String cdlPath = "C:\\Users\\moral\\Desktop\\博士论文\\CDL\\2021\\cdl_ww_2021.shp";
        String modisDir = "C:\\Users\\moral\\Desktop\\博士论文\\MODIS\\2021";
        String cpclDir = "C:\\Users\\moral\\Desktop\\博士论文\\CPCL\\2021\\reprojected";

        SpatialConnect2CDL.run(cdlPath,modisDir,cpclDir);

    }

}