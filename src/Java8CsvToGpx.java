package src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Java 8 で実行できるバージョン
 */
class Java8CsvToGpx {
    private static Scanner scanner;

    private static class outputFileName {
        private String name = "";

        void set(String name) {
            this.name = name;
        }

        String csv() {
            return name + ".csv";
        }

        String gpx() {
            return name + ".gpx";
        }

        boolean isEmpty() {
            return name.isEmpty();
        }
    }

    private static int shrink = Tool.SHRINK_DEFAULT;
    private static boolean wantCSV = Tool.WANT_CSV_DEFAULT;
    private static int skipAltitudeGetter = 1;

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("CsvEdit ver " + Tool.VERSION_NUM);
        scanner = new Scanner(System.in);

        sortArgs(args);
        outputFileName outputFileName = new outputFileName();
        try {
            settingScan(outputFileName);
        } catch (Exception e) { // ヘルプを出して、終わり
            // ヘルプ削除@0520
            // Tool.openHelp();
            System.err.println("エラーが発生しました\n");
            
            return;
        }

        // 書き込むファイル
        FileWriter writer = null;
        try {
            if (wantCSV) {
                writer = new FileWriter(outputFileName.csv(), true); // append=true : 書き足し
                writer.write("timestamp,segment,position_lat,position_long,altitude\n");
            }

            Java8AltitudeGetterAPI api = new Java8AltitudeGetterAPI(); // ここで用意する
            int segment = -1;
            int segmentLimit = Tool.SEGMENT_LIMIT_MIN_SEC;
            int lastTime = 0;

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc.createElement("gpx");
            root.setAttribute("creator", Tool.MY_NAME);
            doc.appendChild(root);
            Element trkseg = null;

            for (String filename : args) { // 各読み込みファイルに対して
                if (!filename.endsWith(".csv")) // csvファイルじゃなかったら
                    continue; // 飛ばす
                int lineCount = -1; // 行を読み込んだ数
                int processedLineCount = -1; // 処理された行の数
                double distance = -1.0; // ワープ防止@0521

                try {
                    File file = new File(filename);
                    long size = file.length();
                    int processSize = (int) size / Tool.ONE_LINE_SIZE / shrink;
                    String[] fnSplit = filename.split("\\\\");
                    System.out.println("start reading \"" + fnSplit[fnSplit.length - 1] + "\" : " + processSize + "points");

                    Scanner fileScanner = new Scanner(file);
                    ColumnData cd = new ColumnData();
                    // 各行の操作
                    System.out.println(String.valueOf(fileScanner.hasNextLine()));
                    // TODO: ここがうまく動かない
                    while (fileScanner.hasNextLine()) {
                        /*
                         * 余談
                         * ファイルの最初にByteOrderMarkという制御文字が入っていることがあるが、今回は1行目は絶対使わないので無視する
                         * https://teratail.com/questions/54754
                         */
                        String[] data = fileScanner.nextLine().split(",", 0);
                        if (data[0].equals("Data") && data[1].equals("8")) {
                            lineCount++; // -1始まりなので最初に0になる
                            if (lineCount % shrink != 0)
                                continue; // shrinkの倍数のlineCountだけ読む
                            double nowDistance = Double.parseDouble(data[cd.getCol_distance()].split("\"")[1]);
                            if (nowDistance == distance) {
                                /*
                                 * ワープ防止@0521
                                 * distance が変わらないときスキップする。トンネル内ではGPS情報が更新されないが時間だけ進むので
                                 * トンネル入口で何秒か停止して出口にワープしたような挙動になりそこだけ異常な速度が出る
                                 * 停止している状態を排除することでワープがなくなる
                                 */
                                continue;
                            } else {
                                distance = nowDistance;
                            }
                            
                            processedLineCount++; // 飛ばされずに処理される行数

                            /*
                             * 進捗を表示。バックスペース6つで"???.?%"を消す。"?.?%"もこれで問題なく消える
                             * intで計算を進めて最後に10.0で割ることで小数点以下1桁までにしている
                             */
                            double percentage = Math.min(100.0, 1000 * processedLineCount / processSize / 10.0);
                            System.out.print("\b\b\b\b\b\b" + percentage + "%");

                            // タイムスタンプ
                            Integer timestamp = Integer.parseInt(data[cd.getCol_timestamp()].split("\"")[1]);
                            // segment
                            if (timestamp > lastTime + segmentLimit) {
                                // セグメントが変わったとき
                                segment++;
                                Element trk = doc.createElement("trk");
                                root.appendChild(trk);
                                Element num = doc.createElement("number");
                                num.setTextContent(String.valueOf(segment));
                                trk.appendChild(num);
                                trkseg = doc.createElement("trkseg");
                                trk.appendChild(trkseg);
                                // trkタグの新調
                            }
                            lastTime = timestamp;

                            // 緯度 = latitude
                            String Lat = data[cd.getCol_position_lat()].replace("\"", "");
                            double latDegrees = Tool.positionConvert(Double.parseDouble(Lat));

                            // 経度 = longitude
                            String Lon = data[cd.getCol_position_lon()].replace("\"", "");
                            double lonDegrees = Tool.positionConvert(Double.parseDouble(Lon));

                            // 高度 = altitude
                            double alt = -1;
                            if (processedLineCount % skipAltitudeGetter == 0) {
                                // String alt = data[Tool.col_altitude].replace("\"","");
                                alt = api.getAltitude(latDegrees, lonDegrees);
                            }

                            // CSV書き出し
                            if (wantCSV) {
                                String altString = alt == -1 ? "" : Double.toString(alt);
                                writer.write(
                                        Tool.fitTimeToTokyo(timestamp) + ","
                                                + segment + ","
                                                + String.valueOf(latDegrees) + ","
                                                + String.valueOf(lonDegrees) + ","
                                                + altString + "\n");

                            }

                            // gpxの各行構成
                            Element trkpt = doc.createElement("trkpt");
                            trkpt.setAttribute("lat", String.valueOf(latDegrees));
                            trkpt.setAttribute("lon", String.valueOf(lonDegrees));
                            if (alt != -1) {
                                Element ele = doc.createElement("ele");
                                ele.appendChild(doc.createTextNode(String.valueOf(alt)));
                                trkpt.appendChild(ele);
                            }
                            Element time = doc.createElement("time");
                            time.appendChild(doc.createTextNode(Tool.fitTimeToUTC(timestamp)));
                            trkpt.appendChild(time);
                            trkseg.appendChild(trkpt);
                        }
                    }
                    fileScanner.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // gpxに変換

                String gpxPath = outputFileName.gpx();
                System.out.println("\nwritnig to " + gpxPath);
                File gpx = new File(gpxPath);
                if (!gpx.exists())
                    gpx.createNewFile();
                Transformer tf = TransformerFactory.newInstance().newTransformer();
                tf.setOutputProperty("indent", "no");
                tf.transform(new DOMSource(doc), new StreamResult(gpx));
                /*
                 * gpxここで変換しちゃダメでは？と思うが変換に成功している
                 * 読み込みファイルのループの外↓でやらねば？と思うが
                 * 連続ファイルは正常に追記されている。
                 * なお、一回処理したあと同じファイル名を指定したときは上書きになる
                 * 
                 * あと、引っ張ってきたファイルのあるディレクトリにgpxが生成されるのも不可解
                 */
            }
            // 読み込みファイルのループの外、ここ

            if (wantCSV)
                writer.close();
        } catch (IOException | ParserConfigurationException | TransformerFactoryConfigurationError
                | TransformerException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 名前順=時間順に並び替える
     * 
     * @param args
     */
    private static void sortArgs(String[] args) {
        Arrays.sort(args);

        System.out.print("sorted args = ");
        for (String string : args)
            if (string.endsWith(".csv"))
                System.out.print(string + ", ");
        System.out.println("\b\b");
    }

    /**
     * 各種設定
     * 
     * @throws Exception
     */
    private static void settingScan(outputFileName outputFileName) throws Exception {
        while (outputFileName.isEmpty()) {
            // ヘルプ削除@0520
            // System.out.println("if you need help, type \"help\" below.");
            System.out.print("output filename ???.csv >>> ");
            String scan = scanner.nextLine();
            // ヘルプ削除@0520
            // if (scan.equals("help")) {
            //     throw new Exception("help is called");
            // } else {
            //     outputFileName.set(scan);
            // }
            outputFileName.set(scan);
        }

        System.out.print("Pick up every ? points >>> ");
        shrink = Integer.parseInt(scanner.nextLine());

        System.out.print("Get altitude every ? points >>> ");
        skipAltitudeGetter = Integer.parseInt(scanner.nextLine());

        System.out.print("Do you want CSV file? (y:n) >>> ");
        wantCSV = scanner.nextLine().equals("y");
        if (wantCSV == true) {
            System.out.println("CSV file will be made.");
        }
    }
}