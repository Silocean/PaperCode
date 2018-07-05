import org.junit.Test;

import java.util.Vector;

/**
 * Created by Silocean on 2016-12-08.
 */
public class SimpleDBSCAN {

    @Test
    public void test() {
        double eps = 3; // 半径
        int minpts = 1; // 指定半径内点数阈值
        Vector<SimplePoint> points = getInpuData();
        Vector<SimpleCluster> clusters = dbscan(points, eps, minpts);
        for (SimpleCluster cluster : clusters) {
            for (SimplePoint point : cluster.getPoints()) {
                System.out.println(point);
            }
            System.out.println("=====================");
        }
    }

    @Test
    public void testDunn() {
        Vector<SimplePoint> points = getInpuData();
        for (int i = 1; i <= 10; i++) {
            Vector<SimpleCluster> clusters = dbscan(points, i, 1);
            System.out.println(i + "," + clusters.size() + "--" + dunn(clusters));
        }
    }

    @Test
    public void testSilhouette() {
        Vector<SimplePoint> points = getInpuData();
        for (int i = 1; i <= 10; i++) {
            Vector<SimpleCluster> clusters = dbscan(points, i, 1);
            System.out.println(i + "," + clusters.size() + "--" + silhouette(clusters));
        }
    }

    @Test
    public void t1() {
        Vector<SimpleCluster> clusters = new Vector<>();
        SimpleCluster c1 = new SimpleCluster();
        Vector<SimplePoint> ps1 = new Vector<>();
        ps1.add(new SimplePoint(2, 3));
        ps1.add(new SimplePoint(2, 4));
        ps1.add(new SimplePoint(1, 4));
        ps1.add(new SimplePoint(1, 3));
        ps1.add(new SimplePoint(2, 2));
        ps1.add(new SimplePoint(3, 2));
        c1.setPoints(ps1);

        SimpleCluster c2 = new SimpleCluster();
        Vector<SimplePoint> ps2 = new Vector<>();
        ps2.add(new SimplePoint(8, 7));
        ps2.add(new SimplePoint(8, 6));
        ps2.add(new SimplePoint(7, 7));
        ps2.add(new SimplePoint(7, 6));
        ps2.add(new SimplePoint(8, 5));
        c2.setPoints(ps2);

        SimpleCluster c3 = new SimpleCluster();
        Vector<SimplePoint> ps3 = new Vector<>();
        ps3.add(new SimplePoint(8, 19));
        ps3.add(new SimplePoint(8, 20));
        c3.setPoints(ps3);

        SimpleCluster c4 = new SimpleCluster();
        Vector<SimplePoint> ps4 = new Vector<>();
        ps4.add(new SimplePoint(5, 56));
        ps4.add(new SimplePoint(5, 57));
        ps4.add(new SimplePoint(6, 56));
        c4.setPoints(ps4);

        clusters.add(c1);
        clusters.add(c2);
        clusters.add(c3);
        clusters.add(c4);

        System.out.println(silhouette(clusters));
    }

    @Test
    public void t2() {
        Vector<SimpleCluster> clusters = new Vector<>();
        SimpleCluster c1 = new SimpleCluster();
        Vector<SimplePoint> ps1 = new Vector<>();
        ps1.add(new SimplePoint(2, 3));

        ps1.add(new SimplePoint(1, 4));
        ps1.add(new SimplePoint(1, 3));
        ps1.add(new SimplePoint(2, 2));
        ps1.add(new SimplePoint(3, 2));
        c1.setPoints(ps1);

        SimpleCluster c2 = new SimpleCluster();
        Vector<SimplePoint> ps2 = new Vector<>();
        ps2.add(new SimplePoint(8, 7));
        ps2.add(new SimplePoint(8, 6));
        ps2.add(new SimplePoint(7, 7));
        ps2.add(new SimplePoint(7, 6));
        ps2.add(new SimplePoint(8, 5));
        ps2.add(new SimplePoint(2, 4));
        c2.setPoints(ps2);

        SimpleCluster c3 = new SimpleCluster();
        Vector<SimplePoint> ps3 = new Vector<>();
        ps3.add(new SimplePoint(8, 19));
        ps3.add(new SimplePoint(8, 20));
        c3.setPoints(ps3);

        SimpleCluster c4 = new SimpleCluster();
        Vector<SimplePoint> ps4 = new Vector<>();
        ps4.add(new SimplePoint(5, 56));
        ps4.add(new SimplePoint(5, 57));
        ps4.add(new SimplePoint(6, 56));
        c4.setPoints(ps4);

        clusters.add(c1);
        clusters.add(c2);
        clusters.add(c3);
        clusters.add(c4);

        System.out.println(silhouette(clusters));
    }

    /**
     * Silhouette index（值越小越好）
     *
     * @param clusters
     * @return
     * @throws Exception
     */
    private double silhouette(Vector<SimpleCluster> clusters) {

        double total = 0;

        for (SimpleCluster cluster : clusters) {
            double sum = 0;
            for (SimplePoint x : cluster.getPoints()) {
                double a = a(cluster, x);
                double b = b(cluster, x, clusters);
                sum += (b - a) / (Math.max(b, a));
                // System.out.println(a + "--" + b);
            }
            sum = sum / cluster.getPoints().size();
            total += sum;
        }

        return total / clusters.size();
    }

    private double a(SimpleCluster cluster, SimplePoint x) {
        double sum = 0;
        for (SimplePoint y : cluster.getPoints()) {
            if (x != y) {
                sum += getDistance(x, y);
            }
        }
        return sum / (cluster.getPoints().size() - 1);
    }

    private double b(SimpleCluster cluster, SimplePoint x, Vector<SimpleCluster> clusters) {
        double min = Double.MAX_VALUE;
        for (SimpleCluster c : clusters) {
            if (!c.equals(cluster)) {
                double sum = 0;
                for (SimplePoint y : c.getPoints()) {
                    sum += getDistance(x, y);
                }
                sum = sum / c.getPoints().size();
                if (sum < min) {
                    min = sum;
                }
            }
        }
        return min;
    }

    /**
     * Dunn's index（值越小越好）
     *
     * @param clusters
     * @return
     * @throws Exception
     */
    private double dunn(Vector<SimpleCluster> clusters) {
        double min = Double.MAX_VALUE;
        for (SimpleCluster c1 : clusters) {
            for (SimpleCluster c2 : clusters) {
                if (!c1.equals(c2)) {
                    double similarity = minDissimilarityBetweenTwoClusters(c1, c2);
                    if (similarity < min) {
                        min = similarity;
                    }
                }
            }
        }

        double max = 0;
        for (SimpleCluster c : clusters) {
            double similarity = maxDissimilarityInOneCluster(c);
            if (similarity > max) {
                max = similarity;
            }
        }
        double dunn = min / max;
        //System.out.println(min + "--" + max);

        return dunn;
    }

    /**
     * 两簇中最相似的两人的相似度（跟单链类似）
     *
     * @param c1
     * @param c2
     * @return
     */
    private double minDissimilarityBetweenTwoClusters(SimpleCluster c1, SimpleCluster c2) {
        double min = Double.MAX_VALUE;
        for (SimplePoint x : c1.getPoints()) {
            for (SimplePoint y : c2.getPoints()) {
                double distance = getDistance(x, y);
                if (distance < min) {
                    min = distance;
                }
            }
        }
        return min;
    }

    /**
     * 一个簇中最不相似的两人的相似度（即簇的最大直径）
     *
     * @param c
     * @return
     */
    private double maxDissimilarityInOneCluster(SimpleCluster c) {
        double max = 0;
        for (SimplePoint x : c.getPoints()) {
            for (SimplePoint y : c.getPoints()) {
                if (x != y) {
                    double distance = getDistance(x, y);
                    if (distance > max) {
                        max = distance;
                    }
                }
            }
        }
        return max;
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
        points.add(new SimplePoint(2, 4));
        points.add(new SimplePoint(1, 4));
        points.add(new SimplePoint(1, 3));
        points.add(new SimplePoint(2, 2));
        points.add(new SimplePoint(3, 2));

        points.add(new SimplePoint(8, 7));
        points.add(new SimplePoint(8, 6));
        points.add(new SimplePoint(7, 7));
        points.add(new SimplePoint(7, 6));
        points.add(new SimplePoint(8, 5));

        points.add(new SimplePoint(100, 2));

        points.add(new SimplePoint(8, 19));
        points.add(new SimplePoint(8, 20));

        points.add(new SimplePoint(5, 56));
        points.add(new SimplePoint(5, 57));
        points.add(new SimplePoint(6, 56));
        return points;
    }

    /**
     * dbscan
     *
     * @param points
     * @param eps
     * @param minpts
     * @return
     */
    private Vector<SimpleCluster> dbscan(Vector<SimplePoint> points, double eps, int minpts) {
        Vector<SimplePoint> neighbours; // 单个cluster
        Vector<SimpleCluster> clusters = new Vector<>(); // 聚类结果集（clusters）
        Vector<SimplePoint> visitedPoints = new Vector<>(); // 访问过的点

        for (SimplePoint point : points) {
            if (!isVisited(visitedPoints, point)) { // 如果该点没有被访问过
                visit(visitedPoints, point); // 标记为访问过
                neighbours = getNeighbours(points, point, eps);
                if (neighbours.size() >= minpts) { // 如果邻域内的点数超过阈值，对其中每个点分别做进一步判断
                    int index = 0;
                    while (index < neighbours.size()) {
                        SimplePoint p = neighbours.get(index);
                        if (!isVisited(visitedPoints, p)) {
                            visit(visitedPoints, p);
                            Vector<SimplePoint> neigh = getNeighbours(points, p, eps);
                            if (neigh.size() >= minpts) { // 如果邻域内某个点的邻域中的点数也超过阈值，加入到cluster中
                                neighbours = merge(neighbours, neigh);
                            }
                        }
                        index++;
                    }
                    SimpleCluster cluster = new SimpleCluster(); // 单个cluster
                    cluster.setPoints(neighbours);
                    clusters.add(cluster);
                }
            }
        }
        for (int i = 0; i < clusters.size(); i++) {
            if (clusters.get(i).getPoints().size() == 1) {
                clusters.remove(i);
            }
        }
        return clusters;
    }

    /**
     * 判断该点是否被访问过
     *
     * @param point
     * @return
     */
    private boolean isVisited(Vector<SimplePoint> visitedPoints, SimplePoint point) {
        return visitedPoints.contains(point);
    }

    /**
     * 访问该点
     *
     * @param point
     */
    private void visit(Vector<SimplePoint> visitedPoints, SimplePoint point) {
        visitedPoints.add(point);
    }

    /**
     * 获取某个点指定邻域内的所有点（初级版）
     * 进阶版可以使用kd-tree来降低时间复杂度（待实现）
     *
     * @param point
     * @return
     */
    private Vector<SimplePoint> getNeighbours(Vector<SimplePoint> points, SimplePoint point, double eps) {
        Vector<SimplePoint> neighbours = new Vector<>();
        for (SimplePoint stayPoint : points) {
            if (getDistance(stayPoint, point) <= eps) {
                neighbours.add(stayPoint);
            }
        }
        return neighbours;
    }

    /**
     * 计算两点之间距离
     *
     * @param p1
     * @param p2
     * @return
     */
    private double getDistance(SimplePoint p1, SimplePoint p2) {
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    /**
     * 将b中的所有点合并到a中
     *
     * @param a
     * @param b
     * @return
     */
    private Vector<SimplePoint> merge(Vector<SimplePoint> a, Vector<SimplePoint> b) {
        for (SimplePoint point : b) {
            if (!a.contains(point)) {
                a.add(point);
            }
        }
        return a;
    }

}

