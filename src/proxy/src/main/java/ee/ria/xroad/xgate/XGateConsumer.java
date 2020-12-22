package ee.ria.xroad.xgate;//import ee.ria.xroad.common.conf.globalconf.Configuration;
//import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;

//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import java.util.function.Consumer;

public class XGateConsumer extends XGateComponent {

    private KafkaConsumer<String, String> consumer;
    //TODO:associate client communications with topics, and/or keys and/or values
    private final ISOutCommunication clientCommunication;

    public XGateConsumer(ISOutCommunication clientCommunication) {
        super();
        this.consumer = new KafkaConsumer<>(XGateConfig.getDefaultConsProps());
        this.clientCommunication = clientCommunication;
    }

    public XGateConsumer(ISOutCommunication clientCommunication, String brokerAddress, String initialTopic) {
        super();
        this.consumer = new KafkaConsumer<>(XGateConfig.getBrokerConsProps(brokerAddress));
        this.subscribeTo(initialTopic);
        this.clientCommunication = clientCommunication;
    }

    public XGateConsumer(ISOutCommunication clientCommunication, KafkaConsumer<String, String> consumer) {
        super();
        this.consumer = consumer;
        this.clientCommunication = clientCommunication;
    }



    @Override
    public void subscribeTo(String topic) {
        super.subscribeTo(topic);
        consumer.subscribe(topics);
    }

    @Override
    public void removeSub(String topic) {
        super.removeSub(topic);
        consumer.subscribe(topics);
    }

    public void consume() {
        Thread productionThread = new Thread(() -> {
            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ZERO);
                    records.forEach(record -> {
                        clientCommunication.send(record.value());
                    });
                }
            } catch (WakeupException e) {
                // Using wakeup to close consumer
            } finally {
                consumer.close();
            }
        });
        productionThread.start();

    }
}
