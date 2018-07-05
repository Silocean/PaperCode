package evaluation;

import common.Utils;
import org.junit.Test;
import personClustering.DBSCAN;
import personClustering.JarvisPatrick;
import stayRegionDetection.StayRegionDetection;
import stayRegionDetection.StayRegionDetection2;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Silocean on 2017-04-20.
 */
public class Evaluation {

    @Test
    public void testDunn() {
        try {
            HashMap<Integer, Double> map = new HashMap<>();
            //Vector<Integer>[] components = new JarvisPatrick().start(5, 3);
            //Utils.printComponents(components);
            for (int i = 1; i <= 30; i++) {
                for (int j = 1; j < i; j++) {
                    Vector<Integer>[] components = new JarvisPatrick().start(i, j);
                    //map.put(components.length, dunn(components));
                    System.out.println("(" + i + "," + j + ")" + components.length + "--" + dunn(components));
                    //System.out.println("====================");
                }
            }
            /*List<Map.Entry<Integer, Double>> list = new ArrayList<>(map.entrySet());
            Collections.sort(list, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
            for (Map.Entry<Integer, Double> entry : list) {
                System.out.println(entry.getKey() + "," + entry.getValue());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDunn2() {
        try {
            int j = 1;
            while (j <= 30) {
                for (int i = j; i <= 30; i++) {
                    Vector<Integer>[] components = new JarvisPatrick().start(i, j);
                    System.out.println("(" + i + "," + j + ")" + components.length + "--" + dunn(components));
                }
                j++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDunn3() {
        try {
            for (int i = 10; i <= 30; i++) {
                Vector<Integer>[] components = new JarvisPatrick().start(i, 10);
                System.out.println(i + "," + components.length + "," + Utils.removeNoise(components).length
                        + "--" + new DecimalFormat("#0.00000").format(dunn(Utils.removeNoise(components))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tDunnDBSCAN() {
        try {
            for (int i = 1; i <= 9; i++) {
                Vector<Integer>[] components = new DBSCAN().start(0.1 * i, 1);
                //Utils.printComponents(Utils.removeNoise(components));
                //Utils.printSortedComponentsSimilarityMatrix(components);
                System.out.println(Utils.removeNoise(components).length + "," + new DecimalFormat("#0.00000").format(dunn(Utils.removeNoise(components))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tDunnJP() {
        try {
            for (int i = 0; i <= 10; i++) {
                Utils.saveAllUsersSimilarities(0.1 * i);
                Vector<Integer>[] components = new JarvisPatrick().start(12, 10);
                System.out.println((0.1 * i) + "==" + Utils.removeNoise(components).length + "==" +
                        new DecimalFormat("#0.00000").format(dunn(Utils.removeNoise(components))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSilhouette() {
        try {
            HashMap<Integer, Double> map = new HashMap<>();
            //Vector<Integer>[] components = new JarvisPatrick().start(5, 3);
            //System.out.println(silhouette(components));
            for (int i = 1; i <= 182; i++) {
                for (int j = 1; j < i; j++) {
                    Vector<Integer>[] components = new JarvisPatrick().start(i, j);
                    map.put(components.length, silhouette(components));
                    System.out.println("(" + i + "," + j + ")" + components.length + "--" + silhouette(components));
                }
            }
            /*List<Map.Entry<Integer, Double>> list = new ArrayList<>(map.entrySet());
            Collections.sort(list, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
            for (Map.Entry<Integer, Double> entry : list) {
                System.out.println(entry.getKey() + "," + entry.getValue());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Silhouette index（值越小越好）
     *
     * @param clusters
     * @return
     * @throws Exception
     */
    private double silhouette(Vector<Integer>[] clusters) throws Exception {

        double total = 0;

        for (Vector<Integer> cluster : clusters) {
            double sum = 0;
            for (Integer x : cluster) {
                double a = a(cluster, x);
                double b = b(cluster, x, clusters);
                sum += (b - a) / (Math.max(b, a));
                // System.out.println(a + "--" + b);
            }
            sum = sum / cluster.size();
            total += sum;
        }

        return total / clusters.length;
    }

    private double a(Vector<Integer> cluster, int x) throws Exception {
        double sum = 0;
        for (Integer y : cluster) {
            if (x != y) {
                sum += (1 - Utils.getPersonSimilarityFromFile(x, y));
            }
        }
        return sum / (cluster.size() - 1);
    }

    private double b(Vector<Integer> cluster, int x, Vector<Integer>[] clusters) throws Exception {
        double min = Double.MAX_VALUE;
        for (Vector<Integer> c : clusters) {
            if (!c.equals(cluster)) {
                double sum = 0;
                for (Integer y : c) {
                    sum += (1 - Utils.getPersonSimilarityFromFile(x, y));
                }
                sum = sum / c.size();
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
    private double dunn(Vector<Integer>[] clusters) throws Exception {
        double min = Double.MAX_VALUE;
        for (Vector<Integer> c1 : clusters) {
            for (Vector<Integer> c2 : clusters) {
                if (!c1.equals(c2)) {
                    double similarity = minDissimilarityBetweenTwoClusters(c1, c2);
                    if (similarity < min) {
                        min = similarity;
                    }
                }
            }
        }

        double max = 0;
        for (Vector<Integer> c : clusters) {
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
    private double minDissimilarityBetweenTwoClusters(Vector<Integer> c1, Vector<Integer> c2) throws Exception {
        double min = Double.MAX_VALUE;
        for (Integer x : c1) {
            for (Integer y : c2) {
                double similarity = 1 - Utils.getPersonSimilarityFromFile(x, y);
                if (similarity < min) {
                    min = similarity;
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
    private double maxDissimilarityInOneCluster(Vector<Integer> c) throws Exception {
        double max = 0;
        for (Integer x : c) {
            for (Integer y : c) {
                if (x != y) {
                    double similarity = 1 - Utils.getPersonSimilarityFromFile(x, y);
                    if (similarity > max) {
                        max = similarity;
                    }
                }
            }
        }
        return max;
    }

    @Test
    public void testStayRegionDetection() {
        /*StayRegionDetection srd = new StayRegionDetection();
        System.out.println(srd.start(36*5));
        StayRegionDetection2 srd2 = new StayRegionDetection2();
        System.out.println(srd2.start(36*5));*/
        for (int i = 1; i <= 5; i++) {
            System.out.println(36 * i + ", " + new StayRegionDetection().start(36 * i));
        }
        System.out.println("=====================");
        for (int i = 1; i <= 5; i++) {
            System.out.println(36 * i + ", " + new StayRegionDetection2().start(36 * i));
        }
    }
}
