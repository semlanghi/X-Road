package ee.ria.xroad.xgate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static ee.ria.xroad.xgate.XGateConfig.BATCH_SIZE;

public class ISInCommunicationGet extends Thread implements ISInCommunication {

    private final BlockingQueue<String> blockingQueue;
    private URL url;
    private HttpURLConnection connection;

    public ISInCommunicationGet(URL url) {
        super();
        blockingQueue = new LinkedBlockingQueue<>();
        this.url=url;
    }

    public void run(){

        int i=0;
        while(i<10){


            try {

                this.connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                //connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("accept", "application/json");
                int responseCode = connection.getResponseCode();
                System.out.println("GET Response Code :: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) { // success
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            connection.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    blockingQueue.offer(response.toString());
                    i++;
                    Thread.sleep(5000);

                } else {
                    System.out.println("GET request not worked");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

    @Override
    public List<String> receive() {
        List<String> results = new LinkedList<>();
        blockingQueue.drainTo(results, BATCH_SIZE);
        return results;
    }
}
