package csvtogpx;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// 国土地理院のAPIを使ってLat,LonからAltを取得する
// https://maps.gsi.go.jp/development/elevation_s.html
// https://qiita.com/syoshika_/items/dd1d699aed7ffe1d36e2
public class AltitudeGetterAPI {
    String urlBase = "https://cyberjapandata2.gsi.go.jp/general/dem/scripts/getelevation.php?";
    HttpClient client = HttpClient.newHttpClient();
    double AGA_ERROR = -1.0;

    public double getAltitude(double lat, double lon) {
        String url = urlBase + "lon=" + lon + "&lat=" + lat + "&outtype=JSON";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url)).GET().build();

        double altitude = 0.0;

        String debug = "";
        try {
            HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = res.statusCode();
            debug = res.body();
            if (statusCode == 200) { // 成功
                // System.out.println(res.body());
                // {"elevation":2.7,"hsrc":"5m\uff08\u30ec\u30fc\u30b6\uff09"}
                String altString = res.body()
                        .replace("{", "")
                        .replace("}", "")
                        .split(",")[0].split(":")[1];
                if (altString.equals("\"-----\""))
                    altString = "0.0"; // エラーが出るのでこちらで書き換える
                altitude = Double.valueOf(altString);
            } else {
                System.err.println(statusCode);
            }
        } catch (Exception e) {
            System.err.println("\n");
            System.err.println(url);
            System.err.println(debug);
            return AGA_ERROR;
        }

        return altitude;
    }
}