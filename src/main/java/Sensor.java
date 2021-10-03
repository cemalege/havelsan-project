import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.awt.*;
import java.util.Properties;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class Sensor {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        Producer<Integer, String> producer = new KafkaProducer<Integer, String>(props);

        Scanner scan = new Scanner(System.in);
        System.out.print("The X-axis of the target :");
        String targetX = scan.nextLine();
        System.out.print("The Y-axis of the target :");
        String targetY = scan.nextLine();

        Point sensorACoordinates = new Point(-5, 1);
        Point sensorBCoordinates = new Point(5, -1);
        Point targetCoordinates = new Point(parseInt(targetX), parseInt(targetY));

        System.out.println("Coordinates of the target  : X=" + targetCoordinates.getX()
            + " Y=" + targetCoordinates.getY());
        System.out.println("Coordinates of the sensorA : X=" + sensorACoordinates.getX()
                + " Y=" + sensorACoordinates.getY());
        System.out.println("Coordinates of the sensorB : X=" + sensorBCoordinates.getX()
                + " Y=" + sensorBCoordinates.getY());

        double a = targetCoordinates.getX() - sensorACoordinates.getX();
        double b = targetCoordinates.getY() - sensorACoordinates.getY();
        double targetBearing = Math.toDegrees(Math.atan(a / b));
        if (targetBearing <= 0) {
            if (b >= 0 && a < 0) targetBearing = targetBearing + 360;
            else if (b < 0 && a >= 0) targetBearing = targetBearing + 180;
        } else {
            if (a < 0 && b < 0) targetBearing = 270 -targetBearing;
        }

        ProducerRecord<Integer, String> sensorARec = new ProducerRecord<Integer, String>(
                "locations",
                0,
                sensorACoordinates.getX() + ":" + sensorACoordinates.getY() + ":" + targetBearing);

        a = targetCoordinates.getX() - sensorBCoordinates.getX();
        b = targetCoordinates.getY() - sensorBCoordinates.getY();
        targetBearing = Math.toDegrees(Math.atan(a / b));
        if (targetBearing <= 0) {
            if (b >= 0 && a < 0) targetBearing = targetBearing + 360;
            else if (b < 0 && a >= 0) targetBearing = targetBearing + 180;
        } else {
            if (a < 0 && b < 0) targetBearing = 270 -targetBearing;
        }

        ProducerRecord<Integer, String> sensorBRec = new ProducerRecord<Integer, String>(
                "locations",
                1,
                sensorBCoordinates.getX() + ":" + sensorBCoordinates.getY() + ":" + targetBearing);

        producer.send(sensorARec);
        producer.send(sensorBRec);

        producer.close();
    }
}
