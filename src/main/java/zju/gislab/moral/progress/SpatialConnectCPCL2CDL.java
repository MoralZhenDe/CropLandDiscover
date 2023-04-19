package zju.gislab.moral.progress;

import org.gdal.ogr.Geometry;
import zju.gislab.moral.enity.FileBinding;
import zju.gislab.moral.file.io.ImageFileFactory;
import zju.gislab.moral.file.io.ShapeFileFactory;
import zju.gislab.moral.file.io.SystemFileFactory;
import zju.gislab.moral.tools.ConsoleProgressBar;

import java.util.logging.Logger;

public class SpatialConnectCPCL2CDL {
    private static final Logger logger = Logger.getLogger(SpatialConnectCPCL2CDL.class.getName());
    private static final String fieldPrefix = "_W";


    public static void run(String cdlPath,  String fileBindsPath) {

        FileBinding[] fileBindings = SystemFileFactory.readFileBinding(fileBindsPath);

        ShapeFileFactory sff = new ShapeFileFactory(cdlPath, true);
        long featureCount = sff.getFeatureCount();

        assert fileBindings != null;
        ConsoleProgressBar cpb = new ConsoleProgressBar("CPCL Spatial Match", fileBindings.length - 1, '#');
        for (int j = 0; j <= fileBindings.length - 1; j++) {
            cpb.show(j);
            ImageFileFactory imf = new ImageFileFactory(fileBindings[j].getCpclPath());
            for (long i = 0L; i < featureCount; i++) {
                Geometry geo = sff.getGeomByIndex(i);
                double[] value = new double[1];
                //-9999代表null，0代表不在范围内
                if (imf.getValueBylonlat(1, geo.GetX(), geo.GetY(), value)) {
                    sff.updateFeatureByIndex(i, fieldPrefix + fileBindings[j].getWeek(), value[0]);
                } else {
                    sff.updateFeatureByIndex(i, fieldPrefix + fileBindings[j].getWeek(), 0.0);
                }

            }
            imf.close();
        }
        sff.close();

        logger.info("************************************* CPCL DONE *************************************");

    }

}
