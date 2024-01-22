package zju.gislab.moral.tools.Helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

    public static List<File> getFileListDeep(String rootPath, String fileType) {
        List<File> result = new ArrayList<>();
        traverseDirectory(new File(rootPath), fileType, result);
        return result;
    }

    private static void traverseDirectory(File file, String fileType, List<File> result) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                if (file.listFiles() != null) {
                    for (File child : file.listFiles()) {
                        traverseDirectory(child, fileType, result); // 递归调用自身处理子目录
                    }
                }
            } else {
                if (file.getName().endsWith(fileType)) {
                    result.add(file);
                }
            }
        }
    }

}
