package zju.gislab.moral.progress;

import zju.gislab.moral.enity.Feature;
import zju.gislab.moral.file.io.ImageFileFactory;
import zju.gislab.moral.file.io.SystemFileFactory;

import java.util.List;
import java.util.logging.Logger;

public class ExtractTestData {

    private static final Logger logger = Logger.getLogger(ExtractTestData.class.getName());

    public static void run_quick(String maskedImg){
        ImageFileFactory iff = new ImageFileFactory(maskedImg);
        logger.info(iff.getFileInfo());



        iff.close();
    }
}
