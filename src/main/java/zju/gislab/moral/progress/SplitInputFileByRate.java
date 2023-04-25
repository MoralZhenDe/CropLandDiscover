package zju.gislab.moral.progress;

import zju.gislab.moral.file.io.SystemFileFactory;

import java.io.*;
import java.time.Instant;
import java.util.logging.Logger;

/***
 * 根据生长周期，分割input数据，0-7对应8个生长阶段；
 * todo 抛弃按rate均匀划分的方案，参考作物自身生长规律进行划分；
 */
public class SplitInputFileByRate {
    private static final Logger logger = Logger.getLogger(SplitInputFileByRate.class.getName());

    private int batchSize = 100000;
    private String rdxPath = "";
    private int rateCount = 0;
    private BufferedWriter[] bws = null;

    public SplitInputFileByRate(String rdxPath,int rateCount) {
        this.rdxPath = rdxPath;
        this.rateCount = rateCount;
        bws = new BufferedWriter[rateCount];
        for (int i = 0; i < rateCount; i++) {
            try {
                bws[i] = new BufferedWriter(new FileWriter(rdxPath + i + ".rdx"));
            } catch (IOException e) {
                logger.warning("子文件输出流创建失败：" + e.getMessage());
            }
        }
    }

    public void run(String inputPath) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(inputPath))) {
            int bufferSize = 0;
            long totalSize = 0L;
            String tmp = bufferedReader.readLine();
            while (!tmp.isEmpty()) {
                String[] cells = tmp.split(",");
                double rate = Double.parseDouble(cells[4]);
                BufferedWriter writer =null;
                for(int rateIndex = 0;rateIndex<this.rateCount;rateIndex++) {
                    if(rate==((rateIndex+1.0)/rateCount)){
                        writer = bws[rateIndex];
                        break;
                    }
                }

                assert writer != null:"文件流异常！";
                writer.write(tmp);
                writer.newLine();
                if(bufferSize>batchSize){
                    writer.flush();
                    bufferSize=0;
                    System.out.print('\r');
                    System.out.print("处理中..."+totalSize);
                }else {
                    bufferSize++;
                    totalSize++;
                }
                tmp = bufferedReader.readLine();
                if(tmp==null)
                    break;
            }
            System.out.println();
            System.out.println("分级完成："+totalSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.close();
    }

    private void close() {
        for (BufferedWriter bw : this.bws) {
            try {
                bw.close();
            } catch (IOException e) {
                logger.warning("子文件流关闭失败：" + e.getMessage());
            }
            this.bws = null;
        }
    }
}
