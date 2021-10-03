import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

import static java.lang.String.format;
import static java.time.Duration.ofMillis;

public class CentralUnit {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "central-unit-app-0");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<Integer, String> consumer = new KafkaConsumer<Integer, String>(props);
        consumer.subscribe(Arrays.asList("locations"));

        SensorData sensor_data_list[] = new SensorData[2];
        sensor_data_list[0] = new SensorData();
        sensor_data_list[1] = new SensorData();

        System.out.println(format("Starting Central Unit..."));
        while (true) {
            ConsumerRecords<Integer, String> records = consumer.poll(ofMillis(1000));
            for (ConsumerRecord<Integer, String> record : records) {
                consumer.commitAsync();

                String[] values = record.value().split(":");

                sensor_data_list[record.key()].coordinates.setLocation(Double.parseDouble(values[0]),
                        Double.parseDouble(values[1]));
                sensor_data_list[record.key()].bearing = Double.parseDouble(values[2]);

                if (record.key() == 1) {
                    for(SensorData curr : sensor_data_list) {
                        System.out.println("X: " + curr.coordinates.getX() + " Y: " +
                                curr.coordinates.getY() + " Bearing: " + curr.bearing);
                    }
                }
            }

        }
    }
}
