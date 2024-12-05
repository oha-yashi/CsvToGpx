package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Scanner;


class CsvRename {
    final static int FIT_TIME_ZERO = 631065600;
    public static void main(String[] args) {
        for (String string : args) {
            readFiles(string);
        }
    }

    public static void readFiles(String fileName){
        if(!fileName.endsWith(".csv")) return; // csvじゃなかったら飛ばす

        ColumnData cd = new ColumnData();
        try {
            File file = new File(fileName);
            try (Scanner fileScanner = new Scanner(file)) {
                while (fileScanner.hasNextLine()){
                    String[] data = fileScanner.nextLine().split(",", 0);

                    if(!data[2].equals("record")){
                        continue;
                    } // record行でなければ飛ばす


                    cd.columnDataSetter(data);
                    cd.printData();

                    String value = data[cd.getCol_timestamp()].replace("\"","");
                    String fileNameByTimestamp = FTTTtoFilename(Integer.parseInt(value));
                    rename(fileName, fileNameByTimestamp);
                    // 2024-11-10T14:16:05Z -> 20241110T141605
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String FTTTtoFilename(int timestamp) {
        String s = Tool.fitTimeToTokyo(timestamp);
        s = s.replace(" ","T").replaceAll("[:-]","")+".csv";
        // 2024-11-10T14:16:05Z -> 2024-11-10 14:16:05 -> 2024-11-10T14:16:05 -> 20241110T141605.csv
        return s;
    }

    public static void rename(String beforeName, String afterName) throws IOException{
        ProcessBuilder p = new ProcessBuilder("cmd", "/c", "copy", beforeName, afterName);
        // cmd:コマンドの実行 /c:実行後終了
        Process process = p.start();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.defaultCharset()))){
            while(r.readLine() != null){
                // System.out.println(line);
            }
        }
        int result = process.exitValue();
        if(result == 0){
            System.out.println("copy " +beforeName +" to " +afterName);
        }
    }
}