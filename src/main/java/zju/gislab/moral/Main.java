package zju.gislab.moral;

import org.gdal.osr.SpatialReference;
import zju.gislab.moral.file.io.ImageFileFactory;
import zju.gislab.moral.file.io.ShapeFileFactory;
import zju.gislab.moral.file.io.SystemFileFactory;
import zju.gislab.moral.progress.*;
import zju.gislab.moral.tools.ConsoleProgressBar;
import zju.gislab.moral.tools.Helper.CdlHelper;
import zju.gislab.moral.tools.Helper.CpclHelper;
import zju.gislab.moral.tools.Helper.FileHelper;
import zju.gislab.moral.tools.Helper.S2Helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Main {

    private static final String workspacePath = "C:\\Users\\moral\\Desktop\\Global-Cropland\\_moral_zhijiang\\";
    private static final String s2Name = workspacePath + "S2\\02\\R20m\\";
    private static final String cdlRoot = workspacePath + "CDL_30m\\";
    private static final String cpcRoot = workspacePath + "CPCL_500m\\";
    private static final String tmpWorkspacePath = workspacePath + "Tmp\\";

    public static void main(String[] args) throws IOException, ParseException {
        String tmpSpace = tmpWorkspacePath + System.currentTimeMillis() + "\\";
        File tmpSpaceFile = new File(tmpSpace);
        tmpSpaceFile.mkdir();

        String cpField = workspacePath + "CropFields\\russia_2019.tif";
        String S2 = workspacePath + "S2\\S2_Russia_2019\\S2_Russia_2019.tif";

        ImageFileFactory s2Imf = new ImageFileFactory(S2);
        ImageFileFactory cpImg = new ImageFileFactory(cpField);
        SpatialReference s2SRS = s2Imf.getSRS();
        SpatialReference cpSRS = cpImg.getSRS();
        System.out.println("S2: " + s2SRS.GetName());
        System.out.println("CDL: " + cpSRS.GetName());

        String csvPath = tmpSpace + cpImg.getImgName().replace(".tif", ".csv");
        s2Imf.convert2CSVWithCDL(csvPath, cpField);
        s2Imf.close();

    }

    public static void main_S22(String[] args) throws IOException, ParseException {
        String tmpSpace = tmpWorkspacePath + System.currentTimeMillis() + "\\";
        File tmpSpaceFile = new File(tmpSpace);
        tmpSpaceFile.mkdir();

        String cdl = cdlRoot + "2021\\CDL-30-USA-2021-0000000000-0000065536.tif_.tif";
        String S2 = workspacePath + "S2\\part-demo.tif";

        ImageFileFactory s2Imf = new ImageFileFactory(S2);
        ImageFileFactory cdlImf = new ImageFileFactory(cdl);
        SpatialReference s2SRS = s2Imf.getSRS();
        SpatialReference cdlSRS = cdlImf.getSRS();
        System.out.println("S2: " + s2SRS.GetName());
        System.out.println("CDL: " + cdlSRS.GetName());

        //按行列号匹配像元，导出csv
        String csvPath = tmpSpace + cdlImf.getImgName().replace(".tif", ".csv");
        s2Imf.convert2CSVWithCDL(csvPath, cdl);
        s2Imf.close();
    }

    public static void main_S2(String[] args) throws IOException, ParseException {
        if (args[0].equals("y")) {
            perProgress();
        }
        String tmpSpace = tmpWorkspacePath + System.currentTimeMillis() + "\\";
        File tmpSpaceFile = new File(tmpSpace);
        tmpSpaceFile.mkdir();

        List<File> s2Source = S2Helper.PickUsefullPath(s2Name);

        List<String> ready2MosaicImgs = new ArrayList<>();
        //文件名获取S2拍摄时间
        int[] yyyyMMdd = S2Helper.GetDateFromFileName(s2Source.get(0).getName());
        int year = yyyyMMdd[0];
        int month = yyyyMMdd[1];
        int day = yyyyMMdd[2];
        //获取S2影像的四至作为MASK
        ImageFileFactory tmpImg = new ImageFileFactory(s2Source.get(0).getAbsolutePath());
        ShapeFileFactory sff = new ShapeFileFactory(tmpSpace, tmpImg.getExtent(), tmpImg.getSRS());
        int[] targetSize = tmpImg.getImageSize();
        double[] targetExtent = tmpImg.getExtent();
        String maskPath = sff.getFilePath();
        tmpImg.close();
        sff.close();
        //按MASK进行CDL分割, 重采样统一行列号
        String matchedCDL = CdlHelper.GetCdlByDateAndExtent(cdlRoot, year, targetExtent[0], targetExtent[1], targetExtent[2], targetExtent[3]);
        ImageFileFactory cdl = new ImageFileFactory(matchedCDL);
        String outCdlPath = tmpSpace + "masked_" + cdl.getImgName();
        cdl.resampleByTSwithMask(outCdlPath, maskPath, targetSize[0], targetSize[1]);
        cdl.close();
        ready2MosaicImgs.add(outCdlPath);
        //按MASK进行CPC分割, 重采样统一行列号
        File cpclFile = CpclHelper.GetCdlByDate(cpcRoot, year, month, day);
        ImageFileFactory cpcl = new ImageFileFactory(cpclFile.getAbsolutePath());
        String outCpcPath = tmpSpace + "masked_" + cpcl.getImgName();
        cpcl.resampleByTSwithMask(outCpcPath, maskPath, targetSize[0], targetSize[1]);
        cpcl.close();
        //以masked-cdl为模板构建vrt

        //追加b1-b7+lable（0，1）+cpcl（0-1，4个阶段）波段信息

        //导出csv


    }

    public static void perProgress() {
        List<File> s2Source = S2Helper.PickUsefullPath(s2Name);
        ImageFileFactory imf = new ImageFileFactory(s2Source.get(0).getAbsolutePath());
        SpatialReference targetSrs = imf.getSRS();
        imf.close();
        String epsg = targetSrs.GetAuthorityCode("PROJCS");
        System.out.println("S2 SRS: " + targetSrs.GetName());
        System.out.println("S2 SRS: " + epsg);
        //统一讲cdl数据的坐标系与S2统一
        List<File> cdls = FileHelper.getFileListDeep(cdlRoot, ".tif");
        ConsoleProgressBar cpb = new ConsoleProgressBar("CDL Reproj", (long) cdls.size(), '#');
        int count = 0;
        for (File cdl : cdls) {
            cpb.show(count++);
            String targetName = "reproj_" + epsg + "_" + cdl.getName();
            ImageFileFactory im = new ImageFileFactory(cdl.getAbsolutePath());
            String targetPath = cdl.getParent() + "\\" + targetName;
            im.reprojectByWrap(targetPath, Integer.parseInt(epsg));
            im.close();
        }
        System.out.println("CDL Reproj DONE.");
        //统一讲cdl-cpc数据的坐标系与S2统一
        List<File> cpcls = FileHelper.getFileListDeep(cpcRoot, ".tif");
        cpb = new ConsoleProgressBar("CPC Reproj", (long) cpcls.size(), '#');
        count = 0;
        for (File cpcl : cpcls) {
            String targetName = "reproj_" + epsg + "_" + cpcl.getName();
            String targetPath = cpcl.getParent() + "\\" + targetName;
            ImageFileFactory im = new ImageFileFactory(cpcl.getAbsolutePath());
            im.reprojectByWrap(targetPath, Integer.parseInt(epsg));
            im.close();
        }
        System.out.println("CPC Reproj DONE.");
        getHistogram();
    }

    public static void getHistogram() {
        List<File> cpcls = FileHelper.getFileListDeep(cpcRoot, ".tif");
        for (File cpcl : cpcls) {
            if (cpcl.getName().contains("reproj")) {
                String targetName = "histogram_" + cpcl.getName().replace(".tif", ".csv");
                String targetPath = cpcl.getParent() + "\\" + targetName;
                ImageFileFactory img = new ImageFileFactory(cpcl.getAbsolutePath());
                int[] histogram_data = img.getHistogram(1, 0, 1, 100);
                SystemFileFactory.exportHistogram(histogram_data, targetPath);
            }
        }
    }

    public static void main4Modis(String[] args) throws IOException {
        String cdlPath = "C:\\Users\\moral\\Desktop\\博士论文\\CDL\\2021\\cdl_ww_2021.shp";
        String modisDir = "C:\\Users\\moral\\Desktop\\博士论文\\MODIS\\2021";
        String cpclDir = "C:\\Users\\moral\\Desktop\\博士论文\\CPCL\\2021\\reprojected";
        String fileBindingPath = "C:\\Users\\moral\\Desktop\\博士论文\\_INPUT\\wheat-2021.fbd";

        String inputPath = "C:\\Users\\moral\\Desktop\\博士论文\\_INPUT\\wheat-2021.rdx";
        String ratedFileDir = "C:\\Users\\moral\\Desktop\\博士论文\\_INPUT\\ratedFiles\\";


        BindingFiles.run(cdlPath, modisDir, cpclDir, fileBindingPath);
        SpatialConnectCPCL2CDL.run(cdlPath, fileBindingPath);
        SpatialConnectModis2CPCL.run(cdlPath, inputPath, fileBindingPath);
//        * id,lon,lat,week,rate,progressIndex,b1,b2,b3,b4,b5,b6,b7
        SplitInputFileByRate rfIo = new SplitInputFileByRate(ratedFileDir, 8);
        rfIo.run(inputPath);

        //提取测试数据
        String testImgPath = "C:\\Users\\moral\\Desktop\\博士论文\\MODIS\\2021\\MODIS-USA-2021-09-26.tif";
        String testCdlPath = "C:\\Users\\moral\\Desktop\\博士论文\\CDL\\2021\\CDL-30-USA-2021-20230308T085203Z-001\\Resample\\CDL_500_2021_ww.tif";
        String maskPath = "C:\\Users\\moral\\Desktop\\博士论文\\_TEST\\mask\\Mask.shp";
        String testResultDir = "C:\\Users\\moral\\Desktop\\博士论文\\_TEST\\result\\2021";
        ExtractTestData.run(testImgPath, testCdlPath, maskPath, testResultDir);

    }

}