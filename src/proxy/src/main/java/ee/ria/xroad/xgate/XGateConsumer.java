package ee.ria.xroad.xgate;

//import ee.ria.xroad.common.conf.globalconf.Configuration;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
//import java.util.function.Consumer;

public class XGateConsumer implements XGate {

    private KafkaConsumer<String, String> consumer;
    private final CopyOnWriteArrayList<String> consumerTopics;
    //TODO:associate client communications with topics, and/or keys and/or values
    private final ISCommunication clientCommunication;

    public XGateConsumer(Properties config, ISCommunication clientCommunication) {
        this.consumerTopics = new CopyOnWriteArrayList<>();
        this.clientCommunication = clientCommunication;
    }

    public XGateConsumer(KafkaConsumer<String, String> consumer, ISCommunication clientCommunication) {
        this.consumer = consumer;
        this.consumerTopics = new CopyOnWriteArrayList<>();
        this.clientCommunication = clientCommunication;
    }

    @Override
    public List<String> getTopics() {
        return consumerTopics;
    }


    public void consumeFrom(String topic) {
        consumerTopics.add(topic);
        consumer.subscribe(consumerTopics);
    }

    public void setConsumer(KafkaConsumer<String, String> consumer) {
        this.consumer = consumer;
    }

    public void removeSub(String topic) {
        consumerTopics.remove(topic);
    }

    public void startup() {
        Thread productionThread = new Thread() {
            @Override
            public void run() {
                if (consumerTopics.isEmpty()) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
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
                }
            }
        };


    }
}
