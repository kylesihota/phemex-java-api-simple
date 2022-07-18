import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.time.Duration;

//GitHub Version v1.0 WebClient by Kyle Veer Sihota

public class WebClient {

    private static final String PHEMEX_API_URL = "https://api.phemex.com"; // Use https://testnet-api.phemex.com for testnet

    private static final String PHEMEX_SECRET = "";

    private static final String PHEMEX_KEY = "";

    private double lastBackupPrice = 0.0;


    public double getPrice(String symbol) { // fetches price from  Phemex API for desired symbol
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()

                    .GET()
                    .uri(URI.create(PHEMEX_API_URL + "/md/ticker/24hr?symbol=" + symbol))
                    .timeout(Duration.ofSeconds(20))
                    .build();
            ArrayList<String> fetchedData = new ArrayList<String>();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(fetchedData::add)
                    .join();


            String bString = fetchedData.get(0);
            String cString = "";
            String price = "";

            double doublePrice = 0.0;
            boolean foundLastPrice = false;
            boolean foundPrice = false;
            int savedStartLastPrice = 0;

            for(int i = 0; i < bString.length() && !foundLastPrice; i++){
                if(bString.charAt(i) == 'm'){
                    if(bString.charAt(i+1) == 'a'){
                        if(bString.charAt(i+2) == 'r'){
                            if(bString.charAt(i+3) == 'k'){
                                foundLastPrice = true;

                                if(bString.charAt(i+11) == ' '){
                                    savedStartLastPrice = i+12;
                                } else {
                                    savedStartLastPrice = i+11;
                                }
                            }
                        }
                    }
                }
            }

            cString = bString.substring(savedStartLastPrice);

            for (int c = 0; !foundPrice; c++) {
                if (cString.charAt(c) == ',') {
                    price = cString.substring(0, c);
                    foundPrice = true;
                }
            }

            doublePrice = Double.parseDouble(price);
            doublePrice = doublePrice / 10000;
            lastBackupPrice = doublePrice;
            if(doublePrice == 0.0){
                System.out.println("Could not retrieve price! 0 error. ");
                return(lastBackupPrice);
            }

            return (doublePrice);
        }
        catch (Exception e){
            System.out.println("Could not retrieve price!");
            return lastBackupPrice;
        }
    }

    // Authenticates with Phemex via Signed POST request using SHA256 hash authentication to open trades. Ensure that PHEMEX_KEY and PHEMEX_SECRET are valid.

    public void openTrade(double size, String symbol) throws Exception {
        String path = "/orders";
        String query = "";
        String body = "";
        if(size>0){
            body = "{\"symbol\":\""+symbol+"\", \"clOrdID\":1,\"side\":\"Buy\",\"orderQty\":" + size + ",\"ordType\":\"Market\"}"; //json parsed body
        } else {
            body = "{\"symbol\":\""+symbol+"\", \"clOrdID\":1,\"side\":\"Sell\",\"orderQty\":" + Math.abs(size) + ",\"ordType\":\"Market\"}"; //json parsed body
        }
        String ts = String.valueOf((System.currentTimeMillis() / 1000));
        int expiry = Integer.parseInt(ts) + 600; //600 second expiry from current unix epoch time

        String requestSignature = HashString.getRequestSignature(path, query, expiry, body, PHEMEX_SECRET);

        String accessToken = PHEMEX_KEY;

        String requestExpiry = String.valueOf(expiry);

        URL url = new URL (PHEMEX_API_URL + "orders");

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        connection.setRequestMethod("POST");

        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");

        connection.setRequestProperty("x-phemex-request-expiry", requestExpiry);
        connection.setRequestProperty("x-phemex-request-signature", requestSignature);
        connection.setRequestProperty("x-phemex-access-token", accessToken);

        connection.setDoOutput(true);
        connection.setDoInput(true);

        connection.connect();

        try(OutputStream os = connection.getOutputStream()){
            byte[] input = body.getBytes("utf-8");
            os.write(input, 0, input.length);
        }


        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;

            while((responseLine = br.readLine()) != null){
                response.append(responseLine.trim());
            }

            System.out.println(response.toString());
        }


    }
}
