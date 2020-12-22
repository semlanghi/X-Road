package ee.ria.xroad.xgate;

public class XGate {

    private XGateConsumer consumer;
    private XGateProducer producer;
    private ISInCommunication inCommunication;
    private ISOutCommunication outCommunication;

    public XGate() {
    }

    public void setConsumer(XGateConsumer consumer) {
        this.consumer = consumer;
    }

    public void setProducer(XGateProducer producer) {
        this.producer = producer;
    }

    public void setInCommunication(ISInCommunication inCommunication) {
        this.inCommunication = inCommunication;
    }

    public void setOutCommunication(ISOutCommunication outCommunication) {
        this.outCommunication = outCommunication;
    }

    public XGate(ISInCommunication inCommunication) {
        this.inCommunication = inCommunication;
    }

    public XGate(ISOutCommunication outCommunication) {
        this.outCommunication = outCommunication;
    }

    public void consumeFrom(String broker, String topic){
        if(this.outCommunication!=null){
            this.consumer = new XGateConsumer(outCommunication, broker, topic);
            this.consumer.consume();
        }
    }

    public void produceTo(String broker, String topic){
        if(this.inCommunication!=null){
            this.producer = new XGateProducer(inCommunication, broker, topic);
            this.producer.produce();
        }
    }
}
