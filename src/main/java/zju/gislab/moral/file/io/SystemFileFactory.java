package zju.gislab.moral.file.io;

import zju.gislab.moral.enity.FileBinding;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Logger;

public class SystemFileFactory {
    private static final Logger logger = Logger.getLogger(SystemFileFactory.class.getName());

    private static final String fileBindingFile = ".fbd";
    private static final String inputFile = ".rdx";

    public static void saveFileBinding(List<FileBinding> bindings,String fileBindsPath){
        File file = new File(fileBindsPath);
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file.toPath()))) {
            FileBinding[] obj = new FileBinding[bindings.size()];
            bindings.toArray(obj);
            out.writeObject(obj);
            logger.info("日期绑定文件已生成："+fileBindsPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileBinding[] readFileBinding(String fileBindsPath){
        File file = new File(fileBindsPath);
        FileBinding[] fileBindings = null;
        try (ObjectInputStream out = new ObjectInputStream(Files.newInputStream(file.toPath()))) {
            fileBindings = (FileBinding[]) out.readObject();
            return fileBindings;
        } catch (Exception e) {
            logger.warning("文件绑定关系读取失败："+e.getMessage());
            return null;
        }
    }
}
