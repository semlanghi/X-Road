package ee.ria.xroad.xgate;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

@Slf4j
public class AsyncMainConsumer {
    static String topicName = "default";
    static String serverIP = "10.227.70.1:9092";

    public static String getTopicName() {
        return topicName;
    }

    public static String getBrokerURL() {
        return serverIP;
    }

    public static void listen() {
        Thread productionThread = new Thread() {
            @Override
            public void run() {
                Properties props = new Properties();
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverIP);
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
                props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString() + "_default");
                props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

                KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

                consumer.subscribe(Arrays.asList(topicName));
                log.info("SUBSCRIBED TO " + topicName);

                if (consumer.listTopics().isEmpty()) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        while (true) {
                            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                            for (ConsumerRecord<String, String> record : records) {
                                log.info("-> offset " + record.offset() + "Key: " + record.key() + " Value: " + record.value());
                            }
                        }
                    } catch (WakeupException e) {

                    } finally {
                        consumer.close();
                    }
                }
            }
        };
    }
}
