package src;

// ヘルプ削除@0520
// import java.io.BufferedReader;
// import java.io.IOException;
// import java.io.InputStream;
// import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Tool {
    final static String MY_NAME = "ohayashi";
    final static int VERSION_NUM = 20240521;

    final static int FIT_TIME_ZERO = 631065600;
    final static double POW2_31 = 0x80000000L; // 1000 0000 0000 0000 0000 0000 0000 0000 (2)
    final static double POSITION_CONVERT_CONST = 180.0 / POW2_31;
    final static double POW10_8 = 100000000;
    final static int SHRINK_DEFAULT = 5;
    final static boolean WANT_CSV_DEFAULT = false;
    final static int SEGMENT_LIMIT_MIN_SEC = 300;
    final static double SPEED_NEARY_ZERO = 0.2; // 停止・スキップするスピード

    /**
     * 読み込むcsvの1行のサイズ（実測値）
     */
    final static int ONE_LINE_SIZE = 248;

    /*
     * gpx出力のときにインデントをつけるかどうか
     * "yes" or "no"
     */
    final static String gpxIndent = "no";

    /**
     * FitCSVToolで変換したCSVについて
     * int : 列番号
     * String : 単位情報
     */
    // public class COL {
    //     final static int TIMESTAMP = 4;
    //     final static int POSITION_LAT = 7;
    //     final static int POSITION_LON = 10;
    //     final static int DISTANCE = 13;
    //     final static int ALTITUDE = 16;
    //     final static int SPEED = 19;
    //     final static int TEMPRATURE = 22;
    //     final static String UNIT_SEMI = "semicircles";
    //     final static String UNIT_DEG = "degrees";
    // }

    /**
     * @param timestamp by FIT
     * @return yyyy-MM-dd hh:mm:ss (Tokyo)
     */
    public static String fitTimeToTokyo(int timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp + FIT_TIME_ZERO).plus(9,ChronoUnit.HOURS);
        return instant.toString().replaceAll("[TZ]", " ").trim();
    }

    /**
     * @param timestamp by FIT
     * @return yyyy-MM-dd hh:mm:ss (UTC)
     */
    public static String fitTimeToUTC(int timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp + FIT_TIME_ZERO);
        return instant.toString();
    }

    /**
     * degrees = semicircles * 180 / (2^31)
     * 小数点以下8桁になるよう四捨五入
     * 
     * @param pos [semicircles]
     * @return [degrees]
     */
    public static BigDecimal positionConvert(String pos) {
        BigDecimal bd = new BigDecimal(pos)
                .multiply(new BigDecimal("180"))
                .divide(new BigDecimal(POW2_31))
                .setScale(8, RoundingMode.HALF_UP);
        return bd;
    }

    public static Double positionConvert(double pos) {
        double deg = 0.0;
        deg = pos * POSITION_CONVERT_CONST;
        deg = ((double) Math.round(deg * POW10_8)) / POW10_8;
        return deg;
    }

    /**
     * ヘルプ機能は削除@0520
     * https://qiita.com/atmospheri/items/8cee0cc2ab7de5a9b46e
     *
     */
    // public static void openHelp() {
    //     String path = "com/ohayashi/csvtogpx/help.txt";
    //     InputStream is = ClassLoader.getSystemResourceAsStream(path);
    //     try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
    //         String line;
    //         while ((line = br.readLine()) != null) {
    //             System.out.println(line);
    //         }
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }
}
