package zju.gislab.moral.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TXTPreviewer {
    public static void fromHead(String path, int n) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            for (int i = 0; i < n; i++) {
                System.out.println(bufferedReader.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void fromEnd(String path, int n) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            String[] lines = new String[n];
            int lastNdx = 0;
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                if (lastNdx == lines.length) {
                    lastNdx = 0;
                }
                lines[lastNdx++] = line;
            }

            for (int ndx=lastNdx; ndx != lastNdx-1; ndx++) {
                if (ndx == lines.length) {
                    ndx = 0;
                }
                System.out.println(lines[ndx]);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
