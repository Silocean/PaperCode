import common.HyperPoint;
import common.HyperSpace;
import common.KDTree;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

// K - Dimension Space

public class TestKDTree {

    @Test
    public void test() {
        double[][] ps = {{1, 1}, {2, 3}, {0, 4}, {3, 4}, {4, 0}};
        int num = ps.length;
        HyperPoint[] hps = new HyperPoint[num];
        for (int i = 0; i < num; i++)
            hps[i] = new HyperPoint(ps[i]);

        double[][] scope = {{0, 0}, {5, 5}};
        HyperPoint min = new HyperPoint(scope[0]);
        HyperPoint max = new HyperPoint(scope[1]);
        int k = scope[0].length;
        KDTree kd = new KDTree(k, min, max);
        // Insert
        // ---------------------------------------
        // I. Single stayPointDetection.Point Insert
        // for (int i = 0; i < num; i++)
        // kd.insert(hps[i]);

        // II. Insert Points set by O(n) Median Find Algorithm
        kd.insertByMedianFinding(hps);

        // III. Using PreSort to fast insert stayPointDetection.Point Set
        // kd.insertByPreSort(hps);
        double[] ps4 = {2, 3};
        HyperPoint hp4 = new HyperPoint(ps4);

        // Nearest stayPointDetection.Point search
        HyperPoint hp5 = kd.nearestPoint(hp4);
        //System.out.println(hp5);

        // Range search
        double[][] range1 = {{1, 1}, {4, 4}};
        HyperSpace hs = new HyperSpace(new HyperPoint(range1[0]), new HyperPoint(range1[1]));
        Set<HyperPoint> qu = kd.rangeQuery(hs);
        for (HyperPoint hyperPoint : qu) {
            System.out.println(hyperPoint);
        }
    }

    @Test
    public void test2() {
        try {
            double[][] ps = new double[144000][2];
            int num = ps.length;

            File file = new File("src/main/resources/haidian.csv");
            BufferedReader br = new BufferedReader(new FileReader(file));
            //System.out.println(br.readLine());
            String str;
            int i = 0;
            while ((str = br.readLine()) != null) {
                String s = str.split(", ")[5];
                double lon = Double.parseDouble(s.split(",")[0]);
                double lat = Double.parseDouble(s.split(",")[1]);
                ps[i] = new double[]{lon, lat};
                i++;
            }

            HyperPoint[] hps = new HyperPoint[num];
            for (int j = 0; j < num; j++) {
                hps[j] = new HyperPoint(ps[j]);
            }

            double[][] scope = {{116.10, 39.021}, {116.99, 40.09}};
            HyperPoint min = new HyperPoint(scope[0]);
            HyperPoint max = new HyperPoint(scope[1]);
            int k = scope[0].length;
            KDTree tree = new KDTree(k, min, max);
            tree.insertByMedianFinding(hps);

            HyperPoint point = tree.nearestPoint(new HyperPoint(new double[]{116.76, 40.67}));
            //System.out.println(point);

            double[][] range = new double[][]{{116.348959, 39.981366}, {116.35, 39.99}};
            HyperSpace hs = new HyperSpace(new HyperPoint(range[0]), new HyperPoint(range[1]));
            Set<HyperPoint> points = tree.rangeQuery(hs);
            for (HyperPoint hyperPoint : points) {
                System.out.println(hyperPoint);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
