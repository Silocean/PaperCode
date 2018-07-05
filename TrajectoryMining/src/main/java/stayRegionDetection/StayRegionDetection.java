package stayRegionDetection;

import common.Common;
import common.Utils;
import org.junit.Test;
import stayPointDetection.StayPoint;

import java.io.*;
import java.util.Vector;

/**
 * DBSCAN方式获取人员停留区域（不包含语义位置信息）
 * Created by Silocean on 2016-12-11.
 */
public class StayRegionDetection {

    public long start(int number) {
        long time = 0;
        try {
            deleteAllFiles(new File("clusters"));
            File file = new File("staypoints/");
            File[] files = file.listFiles();
            long start = System.currentTimeMillis();
            for (int i = 0; i <= number; i++) {
                extractSingleUserStayRegions(files[i]);
            }
            long end = System.currentTimeMillis();
            time = end - start;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    @Test
    public void testExtractAllUsersStayRegions() {
        try {
            deleteAllFiles(new File("clusters"));
            long start = System.currentTimeMillis();
            File file = new File("staypoints/");
            File[] files = file.listFiles();
            for (File f : files) {
                extractSingleUserStayRegions(f);
            }
            long end = System.currentTimeMillis();
            System.out.println("time:" + (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testExtractSingleUserStayRegions() {
        try {
            File file = new File("staypoints/001/staypoints.csv");
            Vector<StayPoint> points = getStayPoints(file);
            double eps = Common.eps; // 半径
            int minpts = 1; // 指定半径内点数阈值
            Vector<StayRegion> clusters = dbscan(points, eps, minpts);

            File newFolder = new File("clusters/" + file.getAbsolutePath().split("\\\\")[4]);
            if (!newFolder.exists()) {
                newFolder.mkdirs();
            }

            BufferedWriter bw;
            for (int i = 0; i < clusters.size(); i++) {
                StayRegion stayRegion = clusters.get(i);
                bw = new BufferedWriter(new FileWriter("clusters/" +
                        file.getAbsolutePath().split("\\\\")[4] + "/cluster" + i + ".csv"));
                for (StayPoint point : stayRegion.getStayPoints()) {
                    bw.append(point.getLat() + "," + point.getLon() + "," +
                            Utils.convertTimeFromLongToString(point.getArrTime()) + "," +
                            Utils.convertTimeFromLongToString(point.getLevTime()) + "\n");
                    System.out.println(point);
                }
                System.out.println(stayRegion.getRadius());
                bw.close();
                System.out.println("=====================");
            }
            System.out.println(clusters.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractSingleUserStayRegions(File f) {
        try {
            File file = new File(f.getPath() + "/staypoints.csv");
            Vector<StayPoint> points = getStayPoints(file);
            double eps = 100; // 半径
            int minpts = 1; // 指定半径内点数阈值
            Vector<StayRegion> clusters = dbscan(points, eps, minpts);

            File newFolder = new File("clusters/" + file.getAbsolutePath().split("\\\\")[4]);
            if (!newFolder.exists()) {
                newFolder.mkdirs();
            }

            BufferedWriter bw;
            for (int i = 0; i < clusters.size(); i++) {
                StayRegion stayRegion = clusters.get(i);
                bw = new BufferedWriter(new FileWriter("clusters/" +
                        file.getAbsolutePath().split("\\\\")[4] + "/cluster" + i + ".csv"));
                for (StayPoint point : stayRegion.getStayPoints()) {
                    bw.append(point.getLat() + "," + point.getLon() + "," +
                            Utils.convertTimeFromLongToString(point.getArrTime()) + "," +
                            Utils.convertTimeFromLongToString(point.getLevTime()) + "\n");
                }
                bw.close();
            }
            // System.out.println("User:" + f.getName() + "'s stayRegions have been extracted!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件中获取stay points
     *
     * @param file
     * @return
     * @throws Exception
     */
    private Vector<StayPoint> getStayPoints(File file) throws Exception {
        Vector<StayPoint> stayPoints = new Vector<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str;
        while ((str = br.readLine()) != null) {
            String[] splits = str.split(",");
            double lon = Double.parseDouble(splits[1]);
            double lat = Double.parseDouble(splits[0]);
            long arrTime = Utils.convertTimeFromStringToLong(splits[2]);
            long levTime = Utils.convertTimeFromStringToLong(splits[3]);
            StayPoint stayPoint = new StayPoint(lon, lat, arrTime, levTime);
            stayPoints.add(stayPoint);
        }
        return stayPoints;
    }

    /**
     * stayRegionDetection.StayRegionDetection
     *
     * @param points
     * @param eps
     * @param minpts
     * @return
     */
    private Vector<StayRegion> dbscan(Vector<StayPoint> points, double eps, int minpts) {
        Vector<StayPoint> neighbours; // 单个cluster
        Vector<StayRegion> clusters = new Vector<>(); // 聚类结果集（clusters）
        Vector<StayPoint> visitedPoints = new Vector<>(); // 访问过的点

        for (StayPoint stayPoint : points) {
            if (!isVisited(visitedPoints, stayPoint)) { // 如果该点没有被访问过
                visit(visitedPoints, stayPoint); // 标记为访问过
                neighbours = getNeighbours(points, stayPoint, eps);
                if (neighbours.size() >= minpts) { // 如果邻域内的点数超过阈值，对其中每个点分别做进一步判断
                    int index = 0;
                    while (index < neighbours.size()) {
                        StayPoint p = neighbours.get(index);
                        if (!isVisited(visitedPoints, p)) {
                            visit(visitedPoints, p);
                            Vector<StayPoint> neigh = getNeighbours(points, p, eps);
                            if (neigh.size() >= minpts) { // 如果邻域内某个点的邻域中的点数也超过阈值，加入到cluster中
                                neighbours = merge(neighbours, neigh);
                            }
                        }
                        index++;
                    }
                    StayRegion stayRegion = new StayRegion(neighbours);
                    clusters.add(stayRegion);
                }
            }
        }
        return clusters;
    }

    /**
     * 判断该点是否被访问过
     *
     * @param visitedPoints
     * @param stayPoint
     * @return
     */
    private boolean isVisited(Vector<StayPoint> visitedPoints, StayPoint stayPoint) {
        return visitedPoints.contains(stayPoint);
    }

    /**
     * 访问该点
     *
     * @param visitedPoints
     * @param stayPoint
     */
    private void visit(Vector<StayPoint> visitedPoints, StayPoint stayPoint) {
        visitedPoints.add(stayPoint);
    }

    /**
     * 获取某个点指定邻域内的所有点（初级版）
     * 进阶版可以使用kd-tree来降低时间复杂度（待实现）
     *
     * @param points
     * @param point
     * @param eps
     * @return
     */
    private Vector<StayPoint> getNeighbours(Vector<StayPoint> points, StayPoint point, double eps) {
        Vector<StayPoint> neighbours = new Vector<>();
        for (StayPoint stayPoint : points) {
            if (Utils.spatialDistance(stayPoint, point) <= eps) {
                neighbours.add(stayPoint);
            }
        }
        return neighbours;
    }

    /**
     * 将b中的所有点合并到a中
     *
     * @param a
     * @param b
     * @return
     */
    private Vector<StayPoint> merge(Vector<StayPoint> a, Vector<StayPoint> b) {
        for (StayPoint point : b) {
            if (!a.contains(point)) {
                a.add(point);
            }
        }
        return a;
    }

    private void deleteAllFiles(File file) throws Exception {
        if (file.isFile()) {
            file.delete();
        } else {
            for (File f : file.listFiles()) {
                deleteAllFiles(f);
            }
        }
    }

}
