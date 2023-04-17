package zju.gislab.moral.file.operation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileOperation {

    private static final Logger logger = Logger.getLogger(FileOperation.class.getName());

    /***
     * 剪切文件
     */
    public static void Move(String src, String target) throws IOException {
        File srcFile = new File(src);
        String name = srcFile.getName();
        if ((!srcFile.exists()) | (!srcFile.isFile()))
            logger.warning("This path is not a file.");
        Files.move(Paths.get(src), Paths.get(target,srcFile.getName()), REPLACE_EXISTING);
    }

    /***
     * 递归获取所有文件，剪切整理至根目录
     */
    public static void CollectFiles(String unzipFilePath) throws IOException {
        List<File> files = new ArrayList<>();
        FindFiles(files, unzipFilePath);
        for (File f : files) {
            Move(f.getAbsolutePath(), unzipFilePath);
        }
        logger.info("collect job has done. Moved "+files+" files.");
    }

    /***
     * 根据后缀名，筛选获得所有目标文件的绝对路径
     */
    public static List<String> GetFilesBySuffix(String dirPath,String suffix){
        File rootDir  = new File(dirPath);
        List<String> targetFiles = new ArrayList<>();
        for(File file: rootDir.listFiles()){
            String fileName = file.getName();
            if(fileName.substring(fileName.lastIndexOf('.')+1).equals(suffix))
                targetFiles.add(file.getAbsolutePath());
        }
        logger.info("Match "+targetFiles.size()+" files.");
        return targetFiles;
    }


    private static void FindFiles(List<File> files, String path) {
            File nodePath = new File(path);
            if (nodePath.isFile()) {
                files.add(nodePath);
            } else {
                File[] subFiles = nodePath.listFiles();
                for (File file : subFiles) {
                    FindFiles(files, file.getAbsolutePath());
                }
            }
    }

}
