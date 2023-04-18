package zju.gislab.moral.enity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class FileBinding implements Serializable {
    private int week;
    private String cpclPath;
    private String modisPath;

    public FileBinding(int week,String cpclPath,String modisPath){
        this.week = week;
        this.cpclPath = cpclPath;
        this.modisPath = modisPath;
    }
}
