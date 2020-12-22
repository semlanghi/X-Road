package ee.ria.xroad.xgate;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ISOutCommunicationKafka implements ISOutCommunication {

    private KafkaProducer<String, String> producer;
    private String ISTopic;

    public ISOutCommunicationKafka(String ISTopic) {
        this.producer = new KafkaProducer<>(XGateConfig.getDefaultProdProps());
        this.ISTopic = ISTopic;
    }

    public ISOutCommunicationKafka(String ISTopic, String broker) {
        this.producer = new KafkaProducer<>(XGateConfig.getBrokerProdProps(broker));
        this.ISTopic = ISTopic;
    }

    @Override
    public void send(String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(ISTopic, "key", message);
        producer.send(record);
    }
}
