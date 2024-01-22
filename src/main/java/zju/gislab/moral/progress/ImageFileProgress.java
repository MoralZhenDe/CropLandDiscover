package zju.gislab.moral.progress;

import zju.gislab.moral.file.io.ImageFileFactory;

public class ImageFileProgress {
    /***
     *
     * @param imgPath 裁切影像路径
     * @param mask 裁切淹模（todo check if json format is supported）
     */
    public static void clipByMask(String imgPath,String mask,String targetImg){
        ImageFileFactory imf = new ImageFileFactory(imgPath);
        imf.clipByMask(targetImg, mask);
        imf.close();
    }
}
