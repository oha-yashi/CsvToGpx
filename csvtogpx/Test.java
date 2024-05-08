package csvtogpx;
/*
 * このファイルは、メインと依存関係にないのでjarに変換するときにclassファイルが作られない
 */

import java.io.IOException;
import java.util.Arrays;


public class Test {
    static final int FIT_EPOCH_MS = 631065600;
    public static void main(String[] args) throws IOException {

        int timestamp = 1082442840;
        String tokyo = Tool.fitTimeToTokyo(timestamp);
        System.out.println(tokyo);
        System.out.println(Tool.fitTimeToUTC(timestamp));

        System.out.println(String.valueOf(2/3.0));

        double d = 422830688;
        System.out.println(Tool.positionConvert(String.valueOf(d)));
        System.out.println(Tool.positionConvert(d));
    }

    public static void openSS(String[] ss){
        for (String string : ss) {
            System.out.print(string + ", ");
        }System.out.println("\b\b");
    }

    public static void sortSS(String[] ss){
        Arrays.sort(ss);
        openSS(ss);
    }
}