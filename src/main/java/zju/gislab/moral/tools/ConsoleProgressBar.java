package zju.gislab.moral.tools;

import java.text.DecimalFormat;
import java.time.Instant;

public class ConsoleProgressBar {
    /**
     * 进度条长度
     */
    private int barLen=50;

    private String jobName;
    /**
     * 总数
     */
    private long total;

    /**
     * 用于进度条显示的字符
     */
    private char showChar;

    private DecimalFormat formater = new DecimalFormat("#.##%");

    /**
     * 使用系统标准输出，显示字符进度条及其百分比
     */
    public ConsoleProgressBar(String jobName,long total, char showChar) {
        this.jobName = jobName;
        this.showChar = showChar;
        this.total=total;
    }

    /**
     * 显示进度条
     */
    public void show(long value) {
        //if (value < 0 || value > 100) {
        //    return;
        //}

        reset();

        // 比例
        float rate = (float) (value*1.0 / total);
        // 比例*进度条总长度=当前长度
        draw(barLen, rate);
        if (value == total) {
            afterComplete();
        }
    }

    /**
     * 画指定长度个showChar
     */
    private void draw(int barLen, float rate) {
        int len = (int) (rate * barLen);
        System.out.print(jobName+" Progress: ");
        for (int i = 0; i < len; i++) {
            System.out.print(showChar);
        }
        for (int i = 0; i < barLen-len; i++) {
            System.out.print(" ");
        }
        System.out.print(" |" + format(rate));
    }


    /**
     * 光标移动到行首
     */
    private void reset() {
        System.out.print('\r');
    }

    /**
     * 完成后换行
     */
    private void afterComplete() {
        System.out.print('\n');
        System.out.print("END AT:"+Instant.now());
        System.out.print('\n');
    }

    private String format(float num) {
        return formater.format(num);
    }

}


