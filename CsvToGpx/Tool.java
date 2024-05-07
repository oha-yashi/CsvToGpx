package CsvToGpx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;

public class Tool {
    final static String MY_NAME = "ohayashi";

    final static int FIT_TIME_ZERO = 631065600;
    final static String POW2_31 = String.valueOf(Math.pow(2, 31));
    final static int SHRINK_DEFAULT = 5;
    final static boolean WANT_CSV_DEFAULT = false;
    final static int SEGMENT_LIMIT_MIN = 300;

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
    public class COL {
        final static int TIMESTAMP = 4;
        final static int POSITION_LAT = 7;
        final static int POSITION_LON = 10;
        final static int DISTANCE = 13;
        final static int ALTITUDE = 16;
        final static int SPEED = 19;
        final static int TEMPRATURE = 22;
        final static String UNIT_SEMI = "semicircles";
        final static String UNIT_DEG = "degrees";
    }

    /**
     * @param timestamp by FIT
     * @return yyyy-MM-dd hh:mm:ss (Tokyo)
     */
    public static String fitTimeToTokyo(int timestamp){
        Instant instant = Instant.ofEpochSecond(timestamp + FIT_TIME_ZERO);
        return instant.atZone(ZoneId.of("Asia/Tokyo")).toString().replace("T"," ").replaceAll("\\+.*","");
    }
    /**
     * @param timestamp by FIT
     * @return yyyy-MM-dd hh:mm:ss (UTC)
     */
    public static String fitTimeToUTC(int timestamp){
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

    /**
     * https://qiita.com/atmospheri/items/8cee0cc2ab7de5a9b46e
     */
    public static void openHelp(){
        String path = "CsvToGpx/help.txt";
        InputStream is = ClassLoader.getSystemResourceAsStream(path);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String line;
            while ((line = br.readLine()) !=null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
