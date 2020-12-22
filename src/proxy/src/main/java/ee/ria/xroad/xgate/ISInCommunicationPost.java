package ee.ria.xroad.xgate;

import spark.Spark;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static spark.Spark.port;
import static spark.Spark.post;

public class ISInCommunicationPost extends Thread implements ISInCommunication {

    private final BlockingQueue<String> blockingQueue;


    public ISInCommunicationPost() {
        super();
        blockingQueue = new LinkedBlockingQueue<>();
    }

    public void run(){
        port(7890);

        post("/xgate", (((request, response) -> {
            blockingQueue.add(request.body());
            response.status(200);
            return "message sent.";
        })));
    }

    public void serverStop(){
        Spark.stop();
    }

    @Override
    public List<String> receive() {
        List<String> results = new LinkedList<>();
        blockingQueue.drainTo(results, XGateConfig.BATCH_SIZE);
        return results;
    }



}
