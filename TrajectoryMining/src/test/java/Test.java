import common.HyperPoint;
import common.HyperSpace;
import common.KDTree;
import common.Utils;
import personClustering.JarvisPatrick;
import stayPointDetection.StayPoint;
import stayRegionDetection.StayRegion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 测试类
 * Created by Silocean on 2016-12-24.
 */
public class Test {
    public static void main(String[] args) {
        HashMap<String, Integer> map1 = new HashMap<>();
        map1.put("北工大", 110);
        map1.put("中蓝", 125);
        map1.put("欢乐谷", 3);
        map1.put("798", 5);
        map1.put("天桥", 13);
        HashMap<String, Integer> map2 = new HashMap<>();
        map2.put("北工大", 200);
        map2.put("中蓝", 210);
        map2.put("故宫", 1);
        map2.put("欢乐谷", 1);
        map2.put("天桥", 27);
        map2.put("潘家园", 7);
        ArrayList<Integer> list1 = new ArrayList<>();
        ArrayList<Integer> list2 = new ArrayList<>();

        ArrayList<String> temp2 = new ArrayList<>();
        temp2.addAll(map2.keySet());

        boolean flag;
        for (String s1 : map1.keySet()) {
            flag = true;
            for (String s2 : map2.keySet()) {
                if (s1 == s2) {
                    flag = false;
                    list1.add(map1.get(s1));
                    list2.add(map2.get(s2));
                    temp2.remove(s2);
                    break;
                }
            }
            if (flag) { // 1中有的，2中没有
                list1.add(map1.get(s1));
                list2.add(0);
            }
        }
        for (String s : temp2) {
            list1.add(0);
            list2.add(map2.get(s));
        }

        for (Integer integer : list1) {
            System.out.print(integer + " ");
        }
        System.out.println();
        for (Integer integer : list2) {
            System.out.print(integer + " ");
        }
    }

    @org.junit.Test
    public void testGetPOI() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("clusters/179/cluster13.csv"));
            String str;
            Vector<StayPoint> points = new Vector<>();
            while ((str = br.readLine()) != null) {
                double lat = Double.parseDouble(str.split(",")[0]);
                double lon = Double.parseDouble(str.split(",")[1]);
                StayPoint point = new StayPoint(lon, lat);
                points.add(point);
            }
            StayRegion region = new StayRegion(points);
            HyperSpace space = Utils.getQueryRange(region);

            KDTree tree = Utils.constructKDTree();
            Set<HyperPoint> set = tree.rangeQuery(space);
            for (HyperPoint point : set) {
                System.out.println(point);
            }
            //System.out.println(tree.nearestPoint(new common.HyperPoint(new double[]{region.getMeanLon(), region.getMeanLat()})));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testSortHashMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("c", 1);
        map.put("m", 5);
        map.put("b", 6);
        map.put("a", 3);
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
        for (Map.Entry<String, Integer> entry : list) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    @org.junit.Test
    public void testEqualHashMap() {
        HashMap<String, Integer> map1 = new HashMap<>();
        map1.put("c", 1);
        map1.put("b", 2);
        HashMap<HashMap<String, Integer>, Integer> m = new HashMap<>();
        m.put(map1, 5);
        HashMap<String, Integer> map2 = new HashMap<>();
        map2.put("b", 2);
        map2.put("a", 1);
        HashMap<HashMap<String, Integer>, Integer> n = new HashMap<>();
        n.put(map2, 5);
        n.put(map1, 56);
        System.out.println(n.equals(m));
        System.out.println(n);
        n.remove(map1);
        System.out.println(n);
    }

    @org.junit.Test
    public void testVectorRemoveElement() {
        Vector<String> vector = new Vector<>();
        vector.add("a");
        vector.add("b");
        vector.add("c");
        System.out.println(vector);
        vector.remove(0);
        System.out.println(vector);
        vector.remove("c");
        System.out.println(vector);
    }

    @org.junit.Test
    public void testCosine() {
        double[] a = new double[]{3, 56, 2};
        double[] b = new double[]{40, 70, 1};
        /*double[] w = new double[]{0.8, 0.9, 0.7};
        for (int i = 0; i < a.length; i++) {
            a[i] = a[i] * w[i];
            b[i] = b[i] * w[i];
        }*/
        double numerator = 0;
        for (int i = 0; i < a.length; i++) {
            numerator += a[i] * b[i];
        }
        double tmp1 = 0;
        double tmp2 = 0;
        for (int i = 0; i < a.length; i++) {
            tmp1 += a[i] * a[i];
            tmp2 += b[i] * b[i];
        }
        double denominator = Math.sqrt(tmp1) * Math.sqrt(tmp2);
        System.out.println(numerator / denominator);
    }

    @org.junit.Test
    public void testTanimoto() {
        double[] a = new double[]{3, 56, 2};
        double[] b = new double[]{47, 60, 1};
        double[] w = new double[]{0.9, 0.9, 0.9};
        /*for (int i = 0; i < a.length; i++) {
            a[i] = a[i] * w[i];
            b[i] = b[i] * w[i];
        }*/
        double numerator = 0;
        for (int i = 0; i < a.length; i++) {
            numerator += a[i] * b[i];
        }
        double tmp1 = 0;
        double tmp2 = 0;
        for (int i = 0; i < a.length; i++) {
            tmp1 += a[i] * a[i];
            tmp2 += b[i] * b[i];
        }
        double denominator = tmp1 + tmp2 - numerator;
        System.out.println(numerator / denominator);
    }

    @org.junit.Test
    public void testEuclidean() {
        double[] a = new double[]{5, 3, 2};
        double[] b = new double[]{5, 2, 3};
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        System.out.println(1.0 / (1.0 + Math.sqrt(sum)));
    }

    @org.junit.Test
    public void t() {
        String str = "啊哈哈大王, 知春路36附近, 餐饮服务;中餐厅;中餐厅, 116.336151,39.974367, 116.342326,39.975708, 116.348959,39.981366";
        System.out.println(str.split(", ")[2].split(";")[2]);
    }

    public double KLDistance(double[] p1, double[] p2) {
        double result = 0;
        for (int i = 0; i < p1.length; i++) {
            if (p1[i] != 0) {
                result += p1[i] * Math.log(p1[i] / p2[i]);
            }
        }
        return result;
    }

    public double JSDistance(double kl1, double kl2) {
        return (kl1 + kl2) / 2.0;
    }

    @org.junit.Test
    public void t2() {
        double[] p1 = new double[]{0.0, 0.3, 0.4, 0.3};
        //double[] p2 = new double[]{0.000000001, 0.000000001, 10.0 / 20.0, 10.0 / 20.0};
        double[] p2 = new double[]{0.1, 0.3, 0.6, 0.0};
        /*double[] p1 = new double[]{
                0.009538408630174017,
                0.008848009751698062,
                0.008756489189917975,
                0.000001,
                0.000001,
                0.000001
        };
        double[] p2 = new double[]{
                0.000001,
                0.000001,
                0.000001,
                0.00872496522205349,
                0.008108171408190876,
                0.00793944661866665
        };*/
        /*for (int i = 0; i < p2.length; i++) {
            p2[i] = 0.9 * p2[i] + 0.1 * p1[i];
        }*/

        double[] q = new double[p1.length];
        for (int i = 0; i < p1.length; i++) {
            q[i] = (p1[i] + p2[i]) / 2.0;
        }
        double kl1 = KLDistance(p1, q);
        double kl2 = KLDistance(p2, q);
        System.out.println(1 - JSDistance(kl1, kl2));
        System.out.println(KLDistance(p1, p2));
    }

    @org.junit.Test
    public void testRandomColor() {
        String r, g, b;
        Random random = new Random();
        r = Integer.toHexString(random.nextInt(256));
        g = Integer.toHexString(random.nextInt(256));
        b = Integer.toHexString(random.nextInt(256));

        r = r.length() == 1 ? "0" + r : r;
        g = g.length() == 1 ? "0" + g : g;
        b = b.length() == 1 ? "0" + b : b;

        System.out.println("#" + r + g + b);
    }

    @org.junit.Test
    public void tttt() {
        try {
            new JarvisPatrick().start(5, 3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void m() {
        System.out.println(new DecimalFormat("#0.0000").format(0.1415926));
    }

    @org.junit.Test
    public void testKLDivergence() {
        double[] p1 = new double[]{0.7, 0.2, 0, 0, 0.1};
        double[] p2 = new double[]{0, 0, 0.7, 0.2, 0.1};
        double kldiv = 0;
        for (int i = 0; i < p1.length; i++) {
            if (p1[i] == 0) {
                continue;
            }
            if (p2[i] == 0) {
                continue;
            }
            kldiv += p1[i] * (Math.log(p1[i] / p2[i]) / Math.log(2));
        }
        System.out.println(Math.abs(kldiv));
    }


}

