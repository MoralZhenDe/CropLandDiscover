package zju.gislab.moral.tools.Helper;

import zju.gislab.moral.file.io.ImageFileFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CdlHelper {
    /***
     * Ignore the area cross multi-cdls. Only match one tif contain the mash each time.
     * @return
     */
    public static String GetCdlByDateAndExtent(String CdlRootPath,int year,double xMin,double xMax,double yMin,double yMax) throws IOException {
        List<String> result = new ArrayList<>();
        File path = new File(CdlRootPath+year);
        for(File file : Objects.requireNonNull(path.listFiles())) {
            if(file.getName().endsWith(".tif")&file.getName().contains("reproj_")){
                ImageFileFactory imf = new ImageFileFactory(file.getAbsolutePath());
                double[] extent = imf.getExtent();
                imf.close();
//                System.out.println(file.getName());
//                System.out.println(extent[0]+"*"+extent[1]+"*"+extent[2]+"*"+extent[3]);
                if(extent[0]<xMin & extent[1]>xMax & extent[2]<yMin & extent[3]>yMax)
                    result.add(file.getAbsolutePath());
            }
        }
//        System.out.println(xMin+"*"+xMax+"*"+yMin+"*"+yMax);
        return result.get(0);
    }
}
