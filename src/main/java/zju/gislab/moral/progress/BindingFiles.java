package zju.gislab.moral.progress;

import zju.gislab.moral.converts.DateConverter;
import zju.gislab.moral.enity.FileBinding;
import zju.gislab.moral.file.io.SystemFileFactory;
import zju.gislab.moral.file.io.ShapeFileFactory;
import zju.gislab.moral.file.operation.FileOperation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BindingFiles {
    private static final Logger logger = Logger.getLogger(BindingFiles.class.getName());

    private static final String pattern = "yyyy-MM-dd";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    public static void run(String cdlPath,String modisDir, String cpclDir,String fileBindsPath){
        List<String> modisPaths = FileOperation.GetFilesBySuffix(modisDir, "tif");
        List<String> cpclPaths = FileOperation.GetFilesBySuffix(cpclDir, "tif");
        List<FileBinding> fileBindings = new ArrayList<>();
        if (!CheckDateFormat(cdlPath, modisPaths, cpclPaths, fileBindings)) {
            System.exit(0);
        }

        //save fileBindings
        SystemFileFactory.saveFileBinding(fileBindings,fileBindsPath);

        logger.info("************************************* CHECK DONE *************************************");
    }


    private static boolean CheckDateFormat(String cdlPath, List<String> modeisPaths, List<String> cpclPaths, List<FileBinding> fileBindings) {
        //检查shp文件字段完整性
        int fnCount = 0;
        int cpclCount = cpclPaths.size();
        int modisCount = modeisPaths.size();
        ShapeFileFactory sf = new ShapeFileFactory(cdlPath);
        for (String fn : sf.getFieldNames()) {
            if (fn.startsWith("_W"))
                fnCount++;
        }
        sf.close();
        logger.info("field： " + fnCount + ". cpcl: " + cpclCount + ". modis: " + modisCount);
        if (fnCount == cpclCount & cpclCount == modisCount)
            logger.info("数据量检查完成。");
        else {
            logger.warning("数据量检查，不通过。");
            return false;
        }

        for (String cp : cpclPaths) {
            FileBinding fb = new FileBinding();
            String dateTag = getDateFromCpclPath(cp, fb);
            for (String mp : modeisPaths) {
                if (dateTag.equals(getDateFromModisPath(mp))) {
                    fb.setModisPath(mp);
                    break;
                }
            }
            fileBindings.add(fb);
        }

        if (fileBindings.size() != cpclCount) {
            logger.warning("日期匹配不完全！");
            return false;
        }
        logger.info("日期匹配检查完成。");
        return true;
    }


    private static String getDateFromCpclPath(String absPath, FileBinding fb) {
        int tailIndex = absPath.lastIndexOf(".");
        String timeInfo = absPath.substring(tailIndex - 5, tailIndex);
        int year = 2000 + Integer.parseInt(timeInfo.substring(0, 2));
        int week = Integer.parseInt(timeInfo.substring(3, 5));
        fb.setWeek(week);
        fb.setCpclPath(absPath);
        return simpleDateFormat.format(DateConverter.convertNassWeek2Date(year, week));
    }

    private static String getDateFromModisPath(String absPath) {
        int tailIndex = absPath.lastIndexOf(".");
        String timeInfo = absPath.substring(tailIndex - 10, tailIndex);
        return timeInfo.substring(0, 10);
    }
}
