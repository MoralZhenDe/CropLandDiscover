package zju.gislab.moral.file.io.enums;

public enum WE_ImgType {
    TIF("tif"),
    IMG("img");
    private final String code;
    WE_ImgType(String code) {
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    public static WE_ImgType getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (WE_ImgType imgType : WE_ImgType.values()) {
            if (code.equals(imgType.getCode())){
                return imgType;
            }
        }
        return null;
    }

}
