package zju.gislab.moral.progress;

import org.gdal.ogr.Geometry;
import zju.gislab.moral.enity.FileBinding;
import zju.gislab.moral.file.io.ImageFileFactory;
import zju.gislab.moral.file.io.ShapeFileFactory;
import zju.gislab.moral.file.io.SystemFileFactory;
import zju.gislab.moral.tools.ConsoleProgressBar;

import java.io.*;
import java.nio.file.Files;
import java.util.logging.Logger;

public class SpatialConnectRef2CPCL {
    private static final String fieldPrefix = "_W";
    private static final Logger logger = Logger.getLogger(SpatialConnectRef2CPCL.class.getName());

    public static void run(String cdlShpPath,String csvPath, String fileBindsPath) throws IOException {
        FileBinding[] fileBindings = SystemFileFactory.readFileBinding(fileBindsPath);
        assert fileBindings != null;

        BufferedWriter writeText = new BufferedWriter(new FileWriter(csvPath));

        ShapeFileFactory sff = new ShapeFileFactory(cdlShpPath);
        long featureCount = sff.getFeatureCount();

        ConsoleProgressBar cpb = new ConsoleProgressBar("Modis Spatial Match", fileBindings.length-1, '#');
        for (int j = 0; j <= fileBindings.length - 1; j++) {
            cpb.show(j);
            ImageFileFactory imf = new ImageFileFactory(fileBindings[j].getModisPath());
            for (long i = 0L; i < featureCount; i++) {
                StringBuilder tmp = new StringBuilder();
                tmp.append(i).append(",");
                Geometry geo = sff.getGeomByIndex(i);
                tmp.append(geo.GetX()).append(",").append(geo.GetY()).append(",");
                double pIndex = Double.parseDouble(sff.getFieldByIndex(i,fieldPrefix+fileBindings[j].getWeek()).getValue().toString());
                if(pIndex<=0.0){
                    continue;
                }
                tmp.append(fileBindings[j].getWeek()).append(",");
                if(pIndex<0.125)
                    tmp.append("0.125").append(",");
                else if(pIndex<0.25)
                    tmp.append("0.25").append(",");
                else if(pIndex<0.375)
                    tmp.append("0.375").append(",");
                else if(pIndex<0.5)
                    tmp.append("0.5").append(",");
                else if(pIndex<0.625)
                    tmp.append("0.625").append(",");
                else if(pIndex<0.75)
                    tmp.append("0.75").append(",");
                else if(pIndex<0.875)
                    tmp.append("0.875").append(",");
                else
                    tmp.append("1").append(",");
                tmp.append(pIndex);
                for(int bandIndex = 1;bandIndex<=7;bandIndex++){
                    double[] value = new double[1];
                    if (imf.getValueBylonlat(bandIndex, geo.GetX(), geo.GetY(), value)) {
                        if(value[0]>0){
                            tmp.append(",").append(value[0]);
                        }
                    }
                }
                tmp.append("\r\n");
                writeText.write(tmp.toString());
            }
            writeText.flush();
            imf.close();
        }
        writeText.close();
        sff.close();
        logger.info("*********Tmp Format: id,lon,lat,week,rate,progressIndex,b1,b2,b3,b4,b5,b6,b7*********");
        logger.info("************************************* MODIS DONE *************************************");
    }
}
