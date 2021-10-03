import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.*;

public class SensorData {
    @JsonProperty
    public Point coordinates;

    @JsonProperty
    public double bearing;

    public SensorData() {
        this.coordinates = new Point(0, 0);
        this.bearing = 0.0;
    }

    public SensorData(Point coordinates, double bearing) {
        this.coordinates = coordinates;
        this.bearing = bearing;
    }
}
