package ee.ria.xroad.xgate;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static spark.Spark.*;

public class ISInOutCommunicationSocket extends Thread implements ISInCommunication,ISOutCommunication{


    private ISOutWebSocket webSocket;
    private BlockingQueue<String> inResults;


    public ISInOutCommunicationSocket() {
        this.inResults = new LinkedBlockingQueue<>();
        this.webSocket = new ISOutWebSocket();
    }

    public void run(){
        port(1234);
        webSocket("/xgatews", this.webSocket);
        init();
    }

    @Override
    public List<String> receive() {
        List<String> results = new LinkedList<>();
        this.inResults.drainTo(results, XGateConfig.BATCH_SIZE);
        return results;
    }

    @Override
    public void send(String message) {
        this.webSocket.sendMessage(message);
    }



    @WebSocket
    public class ISOutWebSocket {

        // Store sessions if you want to, for example, broadcast a message to all users
        private Session session;
        private final BlockingQueue<String> bufferedMessages = new LinkedBlockingQueue<>();

        @OnWebSocketConnect
        public void connected(Session session) {
            this.session=session;
            List<String> results = new ArrayList<>();
            bufferedMessages.drainTo(results);
            for (String mex: results
            ) {
                try {
                    this.session.getRemote().sendString(mex);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @OnWebSocketClose
        public void closed(Session session, int statusCode, String reason) {
            this.session.close();
            this.session=null;
        }

        @OnWebSocketMessage
        public void message(Session session, String message) throws IOException {
            inResults.offer(message);
        }

        public void sendMessage(String message){
            if(session==null)
                bufferedMessages.add(message);
            else {
                try {
                    session.getRemote().sendString(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
