package stayRegionDetection;

import common.Utils;
import stayPointDetection.Point;
import stayPointDetection.StayPoint;

import java.util.Vector;

/**
 * 停留区域
 * Created by Silocean on 2017-01-07.
 */
public class StayRegion {
    private Vector<StayPoint> stayPoints; // 区域内所有停留点

    public StayRegion(Vector<StayPoint> stayPoints) {
        this.stayPoints = stayPoints;
    }

    public Vector<StayPoint> getStayPoints() {
        return stayPoints;
    }

    public void setStayPoints(Vector<StayPoint> stayPoints) {
        this.stayPoints = stayPoints;
    }

    public double getMeanLat() {
        double sumLat = 0;
        for (StayPoint stayPoint : stayPoints) {
            sumLat += stayPoint.getLat();
        }
        return sumLat / stayPoints.size();
    }

    public double getMeanLon() {
        double sumLon = 0;
        for (StayPoint stayPoint : stayPoints) {
            sumLon += stayPoint.getLon();
        }
        return sumLon / stayPoints.size();
    }

    public Point getMeanPoint() {
        return new Point(getMeanLon(), getMeanLat());
    }

    /**
     * 半径为所有停留点外接矩形的对角线的一半
     *
     * @return
     */
    public double getRadius() {
        double radius;
        if (stayPoints.size() == 1) {
            radius = 50;
        } else {
            double minLat = Double.MAX_VALUE;
            double minLon = Double.MAX_VALUE;
            double maxLat = Double.MIN_VALUE;
            double maxLon = Double.MIN_VALUE;
            for (StayPoint stayPoint : stayPoints) {
                if (stayPoint.getLat() < minLat) {
                    minLat = stayPoint.getLat();
                }
                if (stayPoint.getLon() < minLon) {
                    minLon = stayPoint.getLon();
                }
                if (stayPoint.getLat() > maxLat) {
                    maxLat = stayPoint.getLat();
                }
                if (stayPoint.getLon() > maxLon) {
                    maxLon = stayPoint.getLon();
                }
            }
            Point p1 = new Point(minLon, minLat);
            Point p2 = new Point(maxLon, maxLat);
            radius = Utils.spatialDistance(p1, p2) / 2;
        }
        return radius;
    }

}
