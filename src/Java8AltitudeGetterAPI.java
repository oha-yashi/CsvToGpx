package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * URL(string)はjava20以降推奨されない
 * マイクロソフトCopilotにてAltitudeGetterAPI.javaを
 * java8で使えるようにしたもの
 */
public class Java8AltitudeGetterAPI {
    String urlBase = "https://cyberjapandata2.gsi.go.jp/general/dem/scripts/getelevation.php?";
    double AGA_ERROR = -1.0;

    public double getAltitude(double lat, double lon) {
        String url = urlBase + "lon=" + lon + "&lat=" + lat + "&outtype=JSON";

        double altitude = 0.0;

        try {
            @SuppressWarnings("deprecation")
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");

            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = reader.readLine();
                reader.close();

                // {"elevation":2.7,"hsrc":"5m\uff08\u30ec\u30fc\u30b6\uff09"}
                String altString = response
                        .replace("{", "")
                        .replace("}", "")
                        .split(",")[0].split(":")[1];
                if (altString.equals("\"-----\""))
                    altString = "0.0"; // エラーが出るのでこちらで書き換える
                altitude = Double.valueOf(altString);
            } else {
                System.err.println("HTTPエラーコード: " + statusCode);
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            return AGA_ERROR;
        }

        return altitude;
    }
}
