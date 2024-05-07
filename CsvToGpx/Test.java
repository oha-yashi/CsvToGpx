package CsvToGpx;
/*
 * このファイルは、メインと依存関係にないのでjarに変換するときにclassファイルが作られない
 */

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;

public class Test {
    static final int FIT_EPOCH_MS = 631065600;
    public static void main(String[] args) throws IOException {

        // String[] ss = {"adc", "abc", "fad", "acb", "edf"};
        // openSS(ss);
        // sortSS(ss);
        // openSS(ss);

        // String s = "";
        // Scanner scanner = new Scanner(System.in);
        // while (s.isEmpty()) {
        //     System.out.println("s.isEmpty");
        //     System.out.println("input >>> ");
        //     s = scanner.nextLine();
        // }
        // System.out.println("s = " + s);
        // scanner.close();

        int timestamp = 1082442840;
        String tokyo = fitTimeToTokyo(timestamp);
        System.out.println(tokyo);
        System.out.println(fitTimeToUTC(timestamp));
    }

    public static String fitTimeToTokyo(int timestamp){
        Instant instant = Instant.ofEpochSecond(timestamp + FIT_EPOCH_MS);
        return instant.atZone(ZoneId.of("Asia/Tokyo")).toString().replace("T"," ").replaceAll("\\+.*","");
    }
    public static String fitTimeToUTC(int timestamp){
        Instant instant = Instant.ofEpochSecond(timestamp + FIT_EPOCH_MS);
        return instant.toString();
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