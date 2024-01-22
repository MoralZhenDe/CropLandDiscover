package zju.gislab.moral.progress;

import zju.gislab.moral.file.io.ImageFileFactory;
import zju.gislab.moral.progress.ImageFileProgress;
import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Logger;

/***
 * 生成测试csv数据，保持测试数据和INPUT数据的格式类似；
 */
public class ExtractTestData {

    private static final Logger logger = Logger.getLogger(ExtractTestData.class.getName());

    public static void run(String sourceMoids, String sourceCDLWW, String maskPath, String resultDir) {
        File file = new File(sourceMoids);
        String testFileName = file.getName().substring(0, file.getName().lastIndexOf("."));
        String maskedModisPath = Paths.get(resultDir, testFileName + "_modis_masked.vrt").toString();
        String maskedWwPath = Paths.get(resultDir, testFileName + "_ww_masked.vrt").toString();
        String csvPath = Paths.get(resultDir, testFileName + "_masked.csv").toString();
        ImageFileProgress.clipByMask(sourceMoids, maskPath, maskedModisPath);
        ImageFileProgress.clipByMask(sourceCDLWW, maskPath, maskedWwPath);
        String newfile = pasteLabel(maskedModisPath, maskedWwPath);
        convert(newfile, csvPath);
    }

    private static String pasteLabel(String maskedModisPath, String maskedWwPath) {
        ImageFileFactory imf = new ImageFileFactory(maskedModisPath);
        String newfile = imf.appendBand(maskedWwPath, 1);
        imf.close();
        return newfile;
    }

    private static void convert(String maskedImg, String csvPath) {
        ImageFileFactory iff = new ImageFileFactory(maskedImg);
        logger.info(iff.getFileInfo());
        iff.convert2CSV(csvPath);
        iff.close();
    }
}
