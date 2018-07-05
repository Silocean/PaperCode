package stayPointDetection;

/**
 * 停留点
 * Created by Silocean on 2017-01-07.
 */
public class StayPoint extends Point {
    private long arrTime; // 到达时间
    private long levTime; // 离开时间

    public StayPoint(double lon, double lat) {
        super(lon, lat);
    }

    public StayPoint(double lon, double lat, long arrTime, long levTime) {
        super(lon, lat);
        this.arrTime = arrTime;
        this.levTime = levTime;
    }

    public long getLevTime() {
        return levTime;
    }

    public long getArrTime() {
        return arrTime;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return "stayPointDetection.StayPoint{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", arrTime=" + arrTime +
                ", levTime=" + levTime +
                '}';
    }


}
