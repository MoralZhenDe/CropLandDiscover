package zju.gislab.moral.tools.Helper;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class S2Helper {
    private static final Logger logger = Logger.getLogger(S2Helper.class.getName());
    private static String[] values = {"_AOT_","_B01_","_B02_","_B03_","_B04_","_B05_","_B06_","_B07_","_B08_","_B11_","_B12_"};

    public static List<File> PickUsefullPath(String rootPath){
        List<File> result = new ArrayList<>();
        for(File file : new File(rootPath).listFiles()){
            for(String val :values){
                if (file.getName().contains(val)){
                    result.add(file);
                    break;
                }
            }
        }
        return result;
    }

    public static int[] GetDateFromFileName(String fileName) throws ParseException {
        int _Index = fileName.indexOf("_") + 1;
        String dateStr = fileName.substring(_Index, _Index + 8);
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse(dateStr));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new int[]{year,month,day};
    }

//    public static List<String> FilePathes(String rootPath){
//        File root = new File(rootPath);
//        if(root.isDirectory()){
//            List<String> fileList = new ArrayList<>();
//            for(File subfile : root.listFiles()){
////                if()
//                return null;
//            }
//            return fileList;
//        }else {
//            logger.warning("s2 root path error.");
//            return null;
//        }
//    }
}
