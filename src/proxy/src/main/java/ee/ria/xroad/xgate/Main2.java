package ee.ria.xroad.xgate;

public class Main2 {
    public static void main(String[] args){
        ISOutCommunication outCommunication = new ISOutCommunicationKafka("default2");

        XGate xGate = new XGate(outCommunication);
        xGate.consumeFrom("localhost:9092", "default");
    }
}
