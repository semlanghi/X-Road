package ee.ria.xroad.xgate;

//import org.apache.kafka.clients.consumer.KafkaConsumer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.streams.KeyValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class XGateProducer implements XGate {

    private KafkaProducer<String, String> producer;
    private BlockingQueue<ProducerRecord<String, String>> productionQueue;
    private List<String> producerTopics;

    public XGateProducer(Properties config) {
        this.producerTopics = Collections.synchronizedList(new ArrayList<>());
        this.productionQueue = new LinkedBlockingQueue<>();
    }

    public XGateProducer(KafkaProducer<String, String> producer) {
        this.producer = producer;
        this.producerTopics = Collections.synchronizedList(new ArrayList<>());
        this.productionQueue = new LinkedBlockingQueue<>();
    }

    public List<String> getTopics() {
        return producerTopics;
    }

    //TODO: make a mapping between member names and topic names (rooms)
    public void produce(KeyValue<String, String> productionEvent, String topic) {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic,
                    productionEvent.key, productionEvent.value);
            productionQueue.put(record);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startup() {
        Thread productionThread = new Thread() {
            @Override
            public void run() {
                if (producerTopics.isEmpty()) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        producer.send(productionQueue.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };


    }
}
