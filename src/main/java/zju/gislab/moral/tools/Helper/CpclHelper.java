package zju.gislab.moral.tools.Helper;

import zju.gislab.moral.converts.DateConverter;

import java.io.File;
import java.io.IOException;


public class CpclHelper {
    public static File GetCdlByDate(String CpclRootPath, int year, int month, int day) throws IOException {
        int week = DateConverter.convertDate2NassWeek(year, month, day);
        int yearTag = year-2000;
        for(File file : new File(CpclRootPath+year).listFiles()){
            if(file.getName().contains("reproj_")&file.getName().contains(yearTag+"w"+week)){
                return file;
            }
        }
        return null;
    }
//
//    public static void GetHistogramByMask(String cpclPath,String maskPath,String resultRoot){
//
//    }
}