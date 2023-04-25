package zju.gislab.moral.progress;

import zju.gislab.moral.enity.Feature;
import zju.gislab.moral.file.io.ImageFileFactory;
import zju.gislab.moral.file.io.SystemFileFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

/***
 * 生成测试csv数据，保持测试数据和INPUT数据的格式类似；
 */
public class ExtractTestData {

    private static final Logger logger = Logger.getLogger(ExtractTestData.class.getName());

    public static void run(String sourceImg,String maskPath,String resultDir){
        File file = new File(sourceImg);
        String testFileName = file.getName().substring(0,file.getName().lastIndexOf("."));
        String maskedPath = Paths.get(resultDir,testFileName+"_masked.tif").toString();
        String csvPath = Paths.get(resultDir,testFileName+"_masked.csv").toString();
        clipByMask(sourceImg, maskPath,maskedPath);
        convert(maskedPath,csvPath);
    }

    private static void clipByMask(String sourceImg,String maskShp,String targetImg){
        ImageFileFactory imf = new ImageFileFactory(sourceImg);
        imf.clipByMask(targetImg,maskShp);
        imf.close();
    }

    private static void convert(String maskedImg,String csvPath){
        ImageFileFactory iff = new ImageFileFactory(maskedImg);
        logger.info(iff.getFileInfo());
        iff.convert2CSV(csvPath);
        iff.close();
    }
}
