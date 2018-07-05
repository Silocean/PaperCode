package common;

import org.junit.Test;

import java.io.*;
import java.util.LinkedList;

/**
 * 轨迹抽取
 * Created by Silocean on 2016-11-04.
 */
public class TrajectoryExtract {

    private static int number = 0;

    private static BufferedReader br = null;

    @Test
    public void test() {
        File file = new File("D:/Geolife Trajectories 1.3/Data/031/Trajectory");
        // System.out.println(file.getAbsolutePath());
        System.out.println(file.getAbsolutePath().split("\\\\")[3]);
    }

    @Test
    public void testExtractAllTrajectoryToOneFile() {
        try {
            number = 0;
            File file = new File("D:/Geolife Trajectories 1.3");
            extractAllTrajectoryToOneFile(file);
            System.out.println(number);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResource();
        }
    }

    @Test
    public void testExtractSingleUserTrajectoryToOneFile() {
        try {
            number = 0;
            File file = new File("D:/Geolife Trajectories 1.3/Data/174/Trajectory");
            extractSingleUserTrajectoryToOneFile(file);
            System.out.println(number);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResource();
        }
    }

    @Test
    public void TestExtractSingleUserTrajectoryToMultiFiles() {
        try {
            number = 0;
            File file = new File("D:/Geolife Trajectories 1.3/Data/012/Trajectory");
            extractSingleUserTrajectoryToMultiFiles(file);
            System.out.println(number);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResource();
        }
    }

    @Test
    public void TestExtractAllUsersTrajectoryToMultiFiles() {
        try {
            File file = new File("D:/Geolife Trajectories 1.3/Data/");
            File[] files = file.listFiles();
            for (File f : files) {
                number = 0;
                File temp = new File("D:/Geolife Trajectories 1.3/Data/" + f.getName() + "/Trajectory");
                extractSingleUserTrajectoryToMultiFiles(temp);
                System.out.println("user:" + f.getName() + "'s trajectories have been extracted, total lines:" + number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResource();
        }
    }

    /**
     * 抽取单条轨迹经纬度数据
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static LinkedList<String[]> extractSingleTrajectory(File file) throws Exception {
        br = new BufferedReader(new FileReader(file));
        String str;
        int count = 0;
        LinkedList<String[]> coodinates = new LinkedList<String[]>();
        while ((str = br.readLine()) != null) {
            count++;
            if (count > 6) {
                number++;
                String[] splits = str.split(",");
                String latitude = splits[0];
                String longitude = splits[1];
                String date = splits[5];
                String time = splits[6];
                coodinates.add(new String[]{latitude, longitude, date, time});
            }
        }
        return coodinates;
    }

    /**
     * 抽取单个用户的所有轨迹经纬度数据到多个excel文件中，每条轨迹存放在一个文件中
     *
     * @param file
     */
    public static void extractSingleUserTrajectoryToMultiFiles(File file) throws Exception {
        String[] splits = file.getAbsolutePath().split("\\\\");
        File newFolder = new File("trajectory/" + splits[3]);
        if (!newFolder.exists()) {
            newFolder.mkdirs();
        }

        File[] files = file.listFiles();
        for (File f : files) {
            LinkedList<String[]> coordinates = extractSingleTrajectory(f);
            BufferedWriter bw = new BufferedWriter(new FileWriter(newFolder.getAbsolutePath() + "/" + f.getName().split("\\.")[0] + ".csv"));
            for (String[] coordinate : coordinates) {
                bw.append(coordinate[0] + "," + coordinate[1] + "," + coordinate[2] + "," + coordinate[3] + "\n");
            }
            bw.close();
        }
    }

    /**
     * 抽取单个用户的所有经纬度数据到一个excel文件中
     *
     * @param file
     * @throws Exception
     */
    public static void extractSingleUserTrajectoryToOneFile(File file) throws Exception {
        String[] splits = file.getAbsolutePath().split("\\\\");
        File newFolder = new File("trajectory/" + splits[3]);
        if (!newFolder.exists()) {
            newFolder.mkdirs();
        }

        File[] files = file.listFiles();
        BufferedWriter bw = new BufferedWriter(new FileWriter(newFolder.getAbsolutePath() + "/" + splits[3] + ".csv"));
        int count = 0;
        for (File f : files) {
            LinkedList<String[]> coordinates = extractSingleTrajectory(f);
            for (String[] coordinate : coordinates) {
                bw.append(coordinate[0] + "," + coordinate[1] + "," + coordinate[2] + "," + coordinate[3] + "\n");
            }
            count++;
            System.out.println("extracting trajectory " + count);
        }
        bw.close();
        System.out.println("done");
    }

    /**
     * 抽取所有经纬度数据到一个excel文件中
     *
     * @param file
     */
    public static void extractAllTrajectoryToOneFile(File file) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter("trajectory.csv"));

        tree(file, bw);

        bw.close();
        System.out.println("done");
    }

    private static void tree(File file, BufferedWriter bw) throws Exception {
        File[] files = file.listFiles();
        int count = 0;
        for (File f : files) {
            if (f.isDirectory()) {
                tree(f, bw);
            } else {
                if (f.getName().endsWith(".plt")) {
                    String user = f.getAbsolutePath().split("\\\\")[3];
                    LinkedList<String[]> coordinates = extractSingleTrajectory(f);
                    for (String[] coordinate : coordinates) {
                        bw.append(coordinate[0] + "," + coordinate[1] + "," + coordinate[2] + "," + coordinate[3] + "\n");
                    }
                    count++;
                    System.out.println("extracting user:" + user + "'s trajectory " + count);
                }
            }
        }
    }

    public static void closeResource() {
        try {
            if (br != null) {
                br.close();
                br = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
