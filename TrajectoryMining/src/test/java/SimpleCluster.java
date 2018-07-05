import java.util.Vector;

/**
 * Created by Silocean on 2016-12-23.
 */
class SimpleCluster {
    private Vector<SimplePoint> points = new Vector<>(); // 类簇中的样本点
    private String clusterName;

    public Vector<SimplePoint> getPoints() {
        return points;
    }

    public void setPoints(Vector<SimplePoint> points) {
        this.points = points;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

}
