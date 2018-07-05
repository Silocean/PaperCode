package stayPointDetection;

import common.Utils;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;

/**
 * 获取人员停留点
 * Created by Silocean on 2016-12-07.
 */
public class StayPointsDetection {

    @Test
    public void testSingleTrajectory() {
        File file = new File("trajectory/008/20081025041134.csv");
        singleTrajectoryStayPointDetection(file);
    }

    @Test
    public void testExtractSingleUserStayPoints() {
        try {
            File file = new File("trajectory/001/");
            File newFolder = new File("staypoints/" + file.getAbsolutePath().split("\\\\")[4]);
            if (!newFolder.exists()) {
                newFolder.mkdirs();
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(newFolder.getAbsolutePath() + "/staypoints.csv"));
            File[] files = file.listFiles();
            for (File f : files) {
                List<StayPoint> staypoints = singleTrajectoryStayPointDetection(f);
                for (StayPoint staypoint : staypoints) {
                    bw.append(staypoint.getLat() + "," + staypoint.getLon() + "," +
                            Utils.convertTimeFromLongToString(staypoint.getArrTime()) + "," +
                            Utils.convertTimeFromLongToString(staypoint.getLevTime()) + "\n");
                }
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExtractAllUsersStayPoints() {
        try {
            File file = new File("trajectory/");
            File[] files = file.listFiles();
            for (File f : files) {
                extractSingleUserTrajectories(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractSingleUserTrajectories(File file) {
        try {
            File newFolder = new File("staypoints/" + file.getAbsolutePath().split("\\\\")[4]);
            if (!newFolder.exists()) {
                newFolder.mkdirs();
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(newFolder.getAbsolutePath() + "/staypoints.csv"));
            File[] files = file.listFiles();
            for (File f : files) {
                List<StayPoint> staypoints = singleTrajectoryStayPointDetection(f);
                for (StayPoint staypoint : staypoints) {
                    bw.append(staypoint.getLat() + "," + staypoint.getLon() + "," +
                            Utils.convertTimeFromLongToString(staypoint.getArrTime()) + "," +
                            Utils.convertTimeFromLongToString(staypoint.getLevTime()) + "\n");
                }
            }
            bw.close();
            System.out.println("User:" + file.getName() + "'s stayPoints have been extracted!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSpatialDistance() {
        Point p1 = new Point(116.329391, 39.982066, 1224879094000L);
        Point p2 = new Point(116.329368, 39.982207, 1224879096000L);
        System.out.println(Utils.spatialDistance(p1, p2));
    }

    /**
     * 提取单条轨迹中的stay point
     *
     * @param file
     * @return
     */
    public List<StayPoint> singleTrajectoryStayPointDetection(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String str;
            List<Point> list = new ArrayList<>(); // 单条轨迹中所有轨迹点
            List<StayPoint> result = new ArrayList<>(); // 提取出的"stay points"
            while ((str = br.readLine()) != null) {
                String[] splits = str.split(",");
                double lon = Double.parseDouble(splits[1]);
                double lat = Double.parseDouble(splits[0]);
                long time = Utils.convertTimeFromStringToLong(splits[2] + " " + splits[3]);
                Point point = new Point(lon, lat, time);
                list.add(point);
            }
            for (int i = 0; i < list.size(); i++) {
                int j = i + 1;
                while (j < list.size()) {
                    if (Utils.spatialDistance(list.get(i), list.get(j)) >= 200) { // 空间距离大于200m
                        if (Utils.timeDistance(list.get(i), list.get(j)) >= 30 * 60 * 1000) { // 时间距离大于30min
                            int subTrajectorySize = j - i + 1;
                            double sumLon = 0;
                            double sumLat = 0;
                            for (int k = i; k <= j; k++) {
                                sumLon += list.get(k).lon;
                                sumLat += list.get(k).lat;
                            }
                            double meanLon = sumLon / subTrajectorySize;
                            double meanLat = sumLat / subTrajectorySize;
                            // System.out.println(meanLat + "," + meanLon);
                            StayPoint stayPoint = new StayPoint(meanLon, meanLat, list.get(i).time, list.get(j).time);
                            result.add(stayPoint);
                        }
                        i = j;
                    }
                    j++;
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void testCalculateVelocity() {
        try {
            File file = new File("trajectory/008/20081024114834.csv");
            calculateVelocity(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算速度
     *
     * @param file
     * @throws Exception
     */
    public void calculateVelocity(File file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        List<Point> list = new ArrayList<>();
        String str;
        while ((str = br.readLine()) != null) {
            String[] splits = str.split(",");
            double lon = Double.parseDouble(splits[1]);
            double lat = Double.parseDouble(splits[0]);
            long time = Utils.convertTimeFromStringToLong(splits[2] + " " + splits[3]);
            Point point = new Point(lon, lat, time);
            list.add(point);
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\velocity.txt"));
        for (int i = 0; i < list.size() - 1; i++) {
            double velocity = Utils.spatialDistance(list.get(i), list.get(i + 1)) /
                    (Utils.timeDistance(list.get(i), list.get(i + 1)) / 1000);
            bw.append(velocity + " ");
        }
        for (int i = 0; i < list.size() - 1; i++) {
            bw.append(list.get(i).time + " ");
        }
        bw.close();
    }

}

