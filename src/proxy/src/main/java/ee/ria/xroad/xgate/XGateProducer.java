package ee.ria.xroad.xgate;//import org.apache.kafka.clients.consumer.KafkaConsumer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;

public class XGateProducer extends XGateComponent {

    private KafkaProducer<String, String> producer;
    private ISInCommunication clientCommunication;

    public XGateProducer(ISInCommunication clientCommunication) {
        super();
        this.producer = new KafkaProducer<>(XGateConfig.getDefaultProdProps());
        this.clientCommunication = clientCommunication;
    }

    public XGateProducer(ISInCommunication clientCommunication, String brokerAddress, String initialTopic) {
        super();
        this.topics.add(initialTopic);
        this.producer = new KafkaProducer<>(XGateConfig.getBrokerProdProps(brokerAddress));
        this.clientCommunication = clientCommunication;
    }

    public XGateProducer(ISInCommunication clientCommunication, KafkaProducer<String, String> producer) {
        super();
        this.producer = producer;
        this.clientCommunication = clientCommunication;
    }

    //TODO: make a mapping between member names and topic names (rooms)
    public void produce() {
        if(clientCommunication instanceof Thread)
            ((Thread) clientCommunication).start();

        new Thread(() -> {
            while(true) {
                List<String> results = clientCommunication.receive();
                for (String topic : topics
                ) {
                    for (String result : results
                    ) {
                        ProducerRecord<String, String> record = new ProducerRecord<>(topic,
                                "key", result);
                        producer.send(record);
                    }
                }
            }
        }).start();

    }

}
