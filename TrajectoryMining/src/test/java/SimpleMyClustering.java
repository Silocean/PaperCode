import java.util.Vector;

/**
 * Created by Silocean on 2017-04-24.
 */
public class SimpleMyClustering {

    @org.junit.Test
    public void test() {
        Vector<SimplePoint> points = getInpuData();
        Vector<SimpleCluster> clusters = algorithm(points);
        for (SimpleCluster cluster : clusters) {
            cluster.getPoints().forEach(System.out::println);
            System.out.println("=====================");
        }
    }

    /**
     * 初始化测试数据
     *
     * @return
     */
    private Vector<SimplePoint> getInpuData() {
        Vector<SimplePoint> points = new Vector<>();
        /*points.add(new SimplePoint(1, 1));
        points.add(new SimplePoint(2, 0));
        points.add(new SimplePoint(2, 1));
        points.add(new SimplePoint(1, 4));
        points.add(new SimplePoint(3, 3));
        points.add(new SimplePoint(3, 4));
        points.add(new SimplePoint(4, 2));
        points.add(new SimplePoint(4, 3));
        points.add(new SimplePoint(4, 4));*/

        points.add(new SimplePoint(2, 3));
        points.add(new SimplePoint(1, 4));
        points.add(new SimplePoint(8, 7));
        points.add(new SimplePoint(2, 2));
        points.add(new SimplePoint(3, 2));
        points.add(new SimplePoint(7, 7));

        points.add(new SimplePoint(8, 6));
        points.add(new SimplePoint(7, 6));
        points.add(new SimplePoint(1, 3));
        points.add(new SimplePoint(8, 5));

        points.add(new SimplePoint(7, 18));
        points.add(new SimplePoint(100, 2));
        points.add(new SimplePoint(2, 4));

        points.add(new SimplePoint(8, 20));
        points.add(new SimplePoint(8, 19));
        points.add(new SimplePoint(7, 17));
        return points;
    }

    private Vector<SimpleCluster> algorithm(Vector<SimplePoint> points) {
        Vector<SimpleCluster> clusters = new Vector<>();

        for (SimplePoint point : points) {
            if (clusters.size() == 0) { // 如果还没有形成簇，生成一个簇并把第一个停留点加入到其中
                Vector<SimplePoint> ps = new Vector<>();
                ps.add(point);
                SimpleCluster cluster = new SimpleCluster();
                cluster.setPoints(ps);
                clusters.add(cluster);
            } else {
                boolean flag = false;
                double minDistance = Double.MAX_VALUE;
                int index = 0;
                for (int i = 0; i < clusters.size(); i++) {
                    double distance = getDistance(point, getMeanPoint(clusters.get(i)));
                    if (distance < minDistance) {
                        minDistance = distance;
                        index = i;
                    }
                }
                if (minDistance < 2) {
                    SimpleCluster cluster = clusters.get(index);
                    Vector<SimplePoint> points1 = cluster.getPoints();
                    points1.add(point);
                    cluster.setPoints(points1);
                    flag = true;
                }
                if (!flag) { // 如果该停留点跟任何簇的距离都大于等于2，我们认为这个点和这个簇属于不同的聚类，则新生成一个簇并把该停留点加入到其中
                    Vector<SimplePoint> ps = new Vector<>();
                    ps.add(point);
                    SimpleCluster cluster = new SimpleCluster();
                    cluster.setPoints(ps);
                    clusters.add(cluster);
                }
            }
        }

        return clusters;
    }

    private SimplePoint getMeanPoint(SimpleCluster cluster) {
        double sumX = 0;
        double sumY = 0;
        for (SimplePoint point : cluster.getPoints()) {
            sumX += point.x;
            sumY += point.y;
        }
        int size = cluster.getPoints().size();
        return new SimplePoint(sumX / size, sumY / size);
    }

    private double getDistance(SimplePoint p1, SimplePoint p2) {
        return Math.sqrt(Math.pow(Math.abs(p1.x - p2.x), 2) + Math.pow(Math.abs(p1.y - p2.y), 2));
    }

}
