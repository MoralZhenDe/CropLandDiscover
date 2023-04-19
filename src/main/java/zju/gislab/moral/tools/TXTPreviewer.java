package zju.gislab.moral.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TXTPreviewer {
    public static void fromHead(String path,int n) {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(path))){
            for (int i = 0; i < n; i++) {
                System.out.println(bufferedReader.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
