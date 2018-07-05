package stayRegionDetection;

import common.Common;
import common.Utils;
import org.junit.Test;
import stayPointDetection.StayPoint;

import java.io.*;
import java.util.Vector;

/**
 * DBSCAN方式获取人员停留区域（不包含语义位置信息）
 * Created by Silocean on 2017-04-23.
 */
public class StayRegionDetection2 {

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
            File file = new File("staypoints/000/staypoints.csv");
            Vector<StayPoint> points = getStayPoints(file);
            double eps = 400; // 半径
            Vector<StayRegion> clusters = algorithm(points, eps);

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
            double eps = Common.eps; // 半径
            Vector<StayRegion> clusters = algorithm(points, eps);

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
     * 自定义的单参数聚类算法
     *
     * @param stayPoints
     * @param eps
     * @return
     */
    private Vector<StayRegion> algorithm(Vector<StayPoint> stayPoints, double eps) {
        Vector<StayRegion> clusters = new Vector<>();
        for (StayPoint stayPoint : stayPoints) {
            if (clusters.size() == 0) { // 如果还没有形成簇，生成一个簇并把第一个停留点加入到其中
                Vector<StayPoint> points = new Vector<>();
                points.add(stayPoint);
                StayRegion stayRegion = new StayRegion(points);
                clusters.add(stayRegion);
            } else {
                boolean flag = false;
                double minDistance = Double.MAX_VALUE;
                int index = 0;
                for (int i = 0; i < clusters.size(); i++) {
                    double distance = getDistanceSingleLink(stayPoint, clusters.get(i));
                    //double distance = getDistanceCompleteLink(stayPoint, clusters.get(i));
                    //double distance = Utils.spatialDistance(stayPoint, clusters.get(i).getMeanPoint());
                    if (distance < minDistance) {
                        minDistance = distance;
                        index = i;
                    }
                }
                if (minDistance < eps) { // 如果距离小于eps，那么它们属于同一个聚类，需要把该停留点加入到该簇中
                    StayRegion cluster = clusters.get(index);
                    Vector<StayPoint> stayPoints1 = cluster.getStayPoints();
                    stayPoints1.add(stayPoint);
                    cluster.setStayPoints(stayPoints1);
                    flag = true;
                }
                if (!flag) { // 如果该停留点跟任何簇的距离都大于等于200米，我们认为这个点和这个簇属于不同的聚类，则新生成一个簇并把该停留点加入到其中
                    Vector<StayPoint> points = new Vector<>();
                    points.add(stayPoint);
                    StayRegion stayRegion = new StayRegion(points);
                    clusters.add(stayRegion);
                }
            }
        }
        return clusters;
    }

    private double getDistanceSingleLink(StayPoint stayPoint, StayRegion cluster) {
        double minDistance = Double.MAX_VALUE;
        for (StayPoint point : cluster.getStayPoints()) {
            double distance = Utils.spatialDistance(stayPoint, point);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        return minDistance;
    }

    private double getDistanceCompleteLink(StayPoint stayPoint, StayRegion cluster) {
        double sum = 0;
        for (StayPoint point : cluster.getStayPoints()) {
            sum += Utils.spatialDistance(stayPoint, point);
        }
        return sum / cluster.getStayPoints().size();
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
