package ee.ria.xroad.xgate;

import java.net.URL;

public class Main {

    public static void main(String[] args) throws Exception {

//        ISInCommunication postEndpoint = new ISInCommunicationPost();
        ISInCommunication getEndpoint = new ISInCommunicationGet(new URL("https://api.hel.fi/kore/v1/school/"));

        XGate xGate = new XGate(getEndpoint);
        xGate.produceTo("localhost:9092", "default");

    }
}
