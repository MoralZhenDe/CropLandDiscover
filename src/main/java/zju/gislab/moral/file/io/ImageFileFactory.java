package zju.gislab.moral.file.io;

import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.WarpOptions;
import org.gdal.gdal.gdal;
import zju.gislab.moral.tools.ConsoleProgressBar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;
import java.util.logging.Logger;

public class ImageFileFactory {
    private static final Logger logger = Logger.getLogger(ImageFileFactory.class.getName());
    private Dataset dataset = null;

    private void initialize() {
        gdal.AllRegister();
    }

    public ImageFileFactory(String imgPath) {
        initialize();
        this.dataset = gdal.Open(imgPath);
    }

    /***
     * 获取文件元数据
     */
    public String getFileInfo() {
        double[] geotransform = dataset.GetGeoTransform();
        //Band Index start with 1 not 0? Fine...
        Band bd = dataset.GetRasterBand(1);

        return "Origin  X: " + geotransform[0] + "\r\n" +
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
    public boolean getValueBylonlat(int bandIndex, double lon, double lat, double[] val) {
        Band band = this.dataset.GetRasterBand(bandIndex);
        int[] rcIndex = geo2ImageXY(dataset.GetGeoTransform(), lon, lat);

        return getValueByRowCol(bandIndex, rcIndex[0], rcIndex[1], val);
    }

    /***
     * 根据行列号，波段号取像元值
     */
    public boolean getValueByRowCol(int bandIndex, int row, int col, double[] val) {
        Band band = this.dataset.GetRasterBand(bandIndex);
        if (row > 0 & col > 0 & row < dataset.GetRasterXSize() & col < dataset.GetRasterYSize()) {
            band.ReadRaster(row, col, 1, 1, val);
            return true;
        } else {
            return false;
        }
    }

    /***
     * 重投影
     */
    public void reprojectByWrap(String targetPath, int targetEPSG) {
        String sourceEPSG = dataset.GetSpatialRef().GetAttrValue("AUTHORITY", 1);
        Vector<String> options = new Vector<>();
        options.add("-s_srs");
        options.add("EPSG:" + sourceEPSG);
        options.add("-t_srs");
        options.add("EPSG:" + targetEPSG);
        WarpOptions warpOptions = new WarpOptions(options);
        Dataset[] src_array = {dataset};
        gdal.Warp(targetPath, src_array, warpOptions);
        logger.info("\"************************************* PROJECT DONE *************************************\"");
    }

    /***
     * 按掩膜裁切影像
     * @param targetPath
     * @param maskPath
     */
    public void clipByMask(String targetPath, String maskPath){
        Vector<String> options = new Vector<>();
        options.add("-cutline");
        options.add(maskPath);
        options.add("-crop_to_cutline");
        options.add("true");
        WarpOptions warpOptions = new WarpOptions(options);
        Dataset[] src_array = {dataset};
        gdal.Warp(targetPath, src_array, warpOptions);
        logger.info("\"************************************* CUT DONE *************************************\"");
    }

    /***
     * 获取影像行列总数
     */
    public int[] getImageSize() {
        return new int[]{this.dataset.GetRasterXSize(), this.dataset.GetRasterYSize()};
    }
    /***
     * 影像转换为csv
     * lon,lat,b1...bn
     */
    public void convert2CSV(String csvPath) {
        int BATCHSIZE = 100000;
        int count = 0;
        ConsoleProgressBar cpb = new ConsoleProgressBar("Test data EX", (long) (dataset.GetRasterYSize()-1) *(dataset.GetRasterYSize()-1),'#');
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(csvPath))) {
            for (int row = 0; row < this.dataset.GetRasterYSize(); row++) {
                for (int col = 0; col < this.dataset.GetRasterXSize(); col++) {
                    cpb.show(row*col);
                    double[] lonLat = imageXY2Geo(dataset.GetGeoTransform(), row, col);
                    StringBuilder sbr = new StringBuilder(row + "_" + col);
                    sbr.append(",").append(lonLat[0]).append(",").append(lonLat[1]);
                    for (int bandIndex = 1; bandIndex <= dataset.getRasterCount(); bandIndex++) {
                        double[] val = new double[1];
                        this.dataset.GetRasterBand(bandIndex).ReadRaster(col, row, 1, 1, val);
                        sbr.append(",").append(val[0]);
                    }
                    bufferedWriter.write(sbr.toString());
                    bufferedWriter.newLine();
                    count++;
                    if (count > BATCHSIZE) {
                        count = 0;
                        bufferedWriter.flush();
                    }
                }
            }
        } catch (Exception e) {
            logger.warning("文件异常：" + e.getMessage());
        }
    }

    public void close() {
        this.dataset = null;
    }

    private static int[] lonlat2Pixel(double[] gt, double X, double Y) {
        double Yline = Math.floor(((Y - gt[3]) * gt[1] - (X - gt[0]) * gt[4]) / (gt[5] * gt[1] - gt[2] * gt[4]));
        double Xpixel = Math.floor((X - gt[0] - Yline * gt[2]) / gt[1]);
        int[] ints = new int[2];
        ints[0] = new Double(Xpixel).intValue();
        ints[1] = new Double(Yline).intValue();
        return ints;
    }

    //By WANG LuoQi
    private static int[] geo2ImageXY(double[] trans, double x, double y) {
        double d1 = trans[1] * trans[5];
        double d2 = trans[2] * trans[4];

        int row = (int) ((trans[5] * x - trans[2] * y - trans[0] * trans[5] + trans[2] * trans[3]) / (d1 - d2));
        int col = (int) ((trans[4] * x - trans[1] * y - trans[0] * trans[4] + trans[1] * trans[3]) / (d2 - d1));
        return new int[]{row, col};
    }

    public static double[] imageXY2Geo(double[] trans, int row, int col) {
        double lon = trans[0] + col * trans[1] + row * trans[2] + trans[1] / 2;
        double lat = trans[3] + col * trans[4] + row * trans[5] + trans[5] / 2;
        return new double[]{lon, lat};
    }

}
