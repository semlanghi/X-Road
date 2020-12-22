package ee.ria.xroad.xgate;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ISInCommunicationKafka implements ISInCommunication {

    private KafkaConsumer<String, String> consumer;


    public ISInCommunicationKafka() {
        this.consumer = new KafkaConsumer<>(XGateConfig.getDefaultConsProps());
    }

    public void consumeFrom(String topic){
        consumer.subscribe(Collections.singletonList(topic));
    }

    @Override
    public List<String> receive() {
        List<String> results = new ArrayList<>();
        try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(2));
                records.forEach(record -> {
                    results.add(record.value());
                });

        } catch (WakeupException e) {
            // Using wakeup to close consumer
        } finally {
            consumer.close();
        }
        return results;
    }
}
