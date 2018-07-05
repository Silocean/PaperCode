package stayPointDetection;

/**
 * 轨迹点
 * Created by Silocean on 2016-12-08.
 */
public class Point {
    public double lon;
    public double lat;
    public long time;

    public Point(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public Point(double lon, double lat, long time) {
        this.lon = lon;
        this.lat = lat;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            Point point = (Point) obj;
            return point.lon == this.lon && point.lat == this.lat;
        }
        return false;
    }

    @Override
    public String toString() {
        return "stayPointDetection.Point{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
