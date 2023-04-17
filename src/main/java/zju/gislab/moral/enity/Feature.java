package zju.gislab.moral.enity;

import lombok.Getter;
import lombok.Setter;
import org.gdal.ogr.Geometry;

import java.security.PublicKey;

@Getter
@Setter
public class Feature {
    private Geometry geom;
    private Field[] fields;

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<fields.length;i++){
            sb.append(fields[i].getName()+":"+fields[i].getValue()+"\r\n");
        }
        return sb.toString();
    }
}
