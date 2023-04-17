package zju.gislab.moral.file.io;

import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.WarpOptions;
import org.gdal.gdal.gdal;
import org.gdal.osr.SpatialReference;

import java.util.Vector;
import java.util.logging.Logger;

public class ImagefileFactory {
    private static final Logger logger = Logger.getLogger(ImagefileFactory.class.getName());
    private Dataset dataset = null;

    private void initialize(){
        gdal.AllRegister();
    }

    public ImagefileFactory(String imgPath){
        initialize();
        this.dataset = gdal.Open(imgPath);
    }

    /***
     * 获取文件元数据
     */
    public String getFileInfo(){
        double[] geotransform = dataset.GetGeoTransform();
        //Band Index start with 1 not 0? Fine...
        Band bd = dataset.GetRasterBand(1);

        return  "Origin  X: " + geotransform[0] + "\r\n" +
                "Origin  Y: " + geotransform[3] + "\r\n" +
                "X rotate: " + geotransform[2] + "\r\n" +
                "Y rotate: " + geotransform[4] + "\r\n" +
                "Band count: " + dataset.getRasterCount() + "\r\n" +
                "Cell Size X: " + geotransform[1] + "\r\n" +
                "Cell Size Y: " + geotransform[5] + "\r\n" +
                "Cell Value Type: " + gdal.GetDataTypeName(bd.getDataType()) + "\r\n" +
                "Band Size X: " + dataset.getRasterXSize() + "\r\n" +
                "Band Size Y: " + dataset.getRasterYSize() + "\r\n" +
                "Image Proj: " + dataset.GetProjection() + "\r\n";
    }


    /***
     * 根据坐标位置，波段号获取像元值
     */
    public boolean getValueBylonlat(int bandIndex,double lon,double lat,double[] val){
        Band band = this.dataset.GetRasterBand(bandIndex);
        int[] rcIndex = geo2ImageXY(dataset.GetGeoTransform(),lon,lat);
        if(rcIndex[0]>band.GetXSize()|rcIndex[1]>band.GetYSize()) {
            return false;
        }else {
            band.ReadRaster(rcIndex[0], rcIndex[1], 1, 1, val);
            return true;
        }
    }

    /***
     * 重投影
     */
    public void reprojectByWrap(String targetPath,int targetEPSG){
        String sourceEPSG = dataset.GetSpatialRef().GetAttrValue("AUTHORITY",1);
        Vector<String> options = new Vector<>();
        options.add("-s_srs");
        options.add("EPSG:"+sourceEPSG);
        options.add("-t_srs");
        options.add("EPSG:"+targetEPSG);
        WarpOptions warpOptions = new WarpOptions(options);
        Dataset[] src_array = {dataset};
        gdal.Warp(targetPath,src_array, warpOptions);
    }

    public void close() {
        this.dataset =null;
    }

    private static int[] lonlat2Pixel(double[] gt, double X, double Y) {
        double  Yline = Math.floor(((Y - gt[3])*gt[1] - (X-gt[0])*gt[4]) / (gt[5]*gt[1]-gt[2]*gt[4]));
        double  Xpixel = Math.floor((X-gt[0] - Yline*gt[2])/gt[1]);
        int[] ints = new int[2];
        ints[0] = new Double(Xpixel).intValue();
        ints[1] = new Double(Yline).intValue();
        return ints;
    }

    //By WANG LuoQi
    private static int[] geo2ImageXY( double[] trans,double x, double y) {
        double d1 = trans[1] * trans[5];
        double d2 = trans[2] * trans[4];

        int row = (int) ((trans[5] * x - trans[2] * y - trans[0] * trans[5] + trans[2] * trans[3]) / (d1 - d2));
        int col = (int) ((trans[4] * x - trans[1] * y - trans[0] * trans[4] + trans[1] * trans[3]) / (d2 - d1));
        return new int[]{row, col};
    }

}
