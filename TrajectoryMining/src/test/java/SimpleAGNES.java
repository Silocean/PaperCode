import org.junit.Test;

import java.util.Vector;

/**
 * Created by Silocean on 2016-12-24.
 */
public class SimpleAGNES {

    @Test
    public void test() {
        int clusterNum = 4;
        Vector<SimplePoint> points = getInputData();
        Vector<SimpleCluster> clusters = agnes(points, clusterNum);
        for (SimpleCluster cluster : clusters) {
            for (SimplePoint point : cluster.getPoints()) {
                System.out.println(point);
            }
            System.out.println("========" + cluster.getClusterName() + "========\n");
        }
    }

    /**
     * 初始化测试数据
     *
     * @return
     */
    private Vector<SimplePoint> getInputData() {
        Vector<SimplePoint> points = new Vector<>();

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

        points.add(new SimplePoint(8, 20));
        points.add(new SimplePoint(8, 19));
        points.add(new SimplePoint(7, 18));
        points.add(new SimplePoint(7, 17));
        points.add(new SimplePoint(8, 20));
        return points;
    }

    /**
     * personClustering.AGNES
     *
     * @param points
     * @param clusterNum
     * @return
     */
    private Vector<SimpleCluster> agnes(Vector<SimplePoint> points, int clusterNum) {
        // 聚类结果集（初始时每个point是一个簇）
        Vector<SimpleCluster> clusters = initClusters(points);

        while (clusters.size() > clusterNum) { // 达到指定的簇个数时结束算法
            double min = Double.MAX_VALUE;

            int indexP = 0;
            int indexQ = 0;

            // 选择两个簇进行比较
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = 0; j < clusters.size(); j++) {
                    if (i != j) {
                        SimpleCluster clusterP = clusters.get(i);
                        SimpleCluster clusterQ = clusters.get(j);

                        double clusterSimilarity = getTwoClustersSimilarityByGroupAverage(clusterP, clusterQ);
                        if (clusterSimilarity < min) {
                            min = clusterSimilarity;
                            indexP = i;
                            indexQ = j;
                        }
                    }
                }
            }
            // 合并两个距离最近的簇
            clusters = mergeClusters(clusters, indexP, indexQ);
        }
        return clusters;
    }

    /**
     * 两个簇之间的距离
     * 单链：两个不同簇中任意两点之间的最短距离
     *
     * @param clusterP
     * @param clusterQ
     * @return
     */
    private double getTwoClustersSimilarityBySingleLink(SimpleCluster clusterP, SimpleCluster clusterQ) {
        Vector<SimplePoint> pointsP = clusterP.getPoints();
        Vector<SimplePoint> pointsQ = clusterQ.getPoints();

        double min = Double.MAX_VALUE;
        // 比较两个簇中所有点
        for (SimplePoint pointP : pointsP) {
            for (SimplePoint pointQ : pointsQ) {
                double distance = getDistance(pointP, pointQ);
                if (distance < min) {
                    min = distance;
                }
            }
        }
        return min;
    }

    /**
     * 两个簇之间的距离
     * 全链：两个不同簇中任意两点之间的最长距离
     *
     * @param clusterP
     * @param clusterQ
     * @return
     */
    private double getTwoClustersSimilarityByCompleteLink(SimpleCluster clusterP, SimpleCluster clusterQ) {
        Vector<SimplePoint> pointsP = clusterP.getPoints();
        Vector<SimplePoint> pointsQ = clusterQ.getPoints();

        double max = Double.MIN_VALUE;
        // 比较两个簇中所有点
        for (SimplePoint pointP : pointsP) {
            for (SimplePoint pointQ : pointsQ) {
                double distance = getDistance(pointP, pointQ);
                if (distance > max) {
                    max = distance;
                }
            }
        }
        return max;
    }

    /**
     * 两个簇之间的距离
     * 组平均：两个不同簇中所有点对邻近度的平均值
     *
     * @param clusterP
     * @param clusterQ
     * @return
     */
    private double getTwoClustersSimilarityByGroupAverage(SimpleCluster clusterP, SimpleCluster clusterQ) {
        Vector<SimplePoint> pointsP = clusterP.getPoints();
        Vector<SimplePoint> pointsQ = clusterQ.getPoints();

        double sum = 0;
        double num = pointsP.size() * pointsQ.size();
        // 比较两个簇中所有点
        for (SimplePoint pointP : pointsP) {
            for (SimplePoint pointQ : pointsQ) {
                double distance = getDistance(pointP, pointQ);
                sum += distance;
            }
        }
        return sum / num;
    }

    /**
     * 合并两个距离最近的簇
     * （把q中的点全部添加到p中，并从clusters中删除q）
     *
     * @param clusters
     * @param indexP
     * @param indexQ
     * @return
     */
    private Vector<SimpleCluster> mergeClusters(Vector<SimpleCluster> clusters, int indexP, int indexQ) {
        SimpleCluster clusterP = clusters.get(indexP);
        SimpleCluster clusterQ = clusters.get(indexQ);

        Vector<SimplePoint> pointsP = clusterP.getPoints();
        Vector<SimplePoint> pointsQ = clusterQ.getPoints();

        for (SimplePoint point : pointsQ) {
            pointsP.add(point);
        }

        clusterP.setPoints(pointsP);
        clusters.remove(indexQ);
        return clusters;
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
     * 初始化聚簇（每个point都是一个单独的簇）
     *
     * @param points
     * @return
     */
    private Vector<SimpleCluster> initClusters(Vector<SimplePoint> points) {
        Vector<SimpleCluster> initialClusters = new Vector<>();
        for (int i = 0; i < points.size(); i++) {
            Vector<SimplePoint> tmpPoints = new Vector<>();
            tmpPoints.add(points.get(i));

            SimpleCluster tmpCluster = new SimpleCluster();
            tmpCluster.setPoints(tmpPoints);
            tmpCluster.setClusterName("SimpleCluster:" + i);

            initialClusters.add(tmpCluster);
        }
        return initialClusters;
    }


}
