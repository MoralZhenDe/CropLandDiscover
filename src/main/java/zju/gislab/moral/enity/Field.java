package zju.gislab.moral.enity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Field {
    private String name;
    private String type;
    private Object value;

    @Override
    public String toString(){
        return name+": "+value.toString();
    }
}
