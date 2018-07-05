package personClustering;

import common.Common;
import common.Utils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Vector;

/**
 * JP聚类（一种基于图的聚类算法）
 * Created by Silocean on 2017-03-14.
 */
public class JarvisPatrick {

    public Vector<Integer>[] start(int k, int threshold) throws Exception {
        Vector<Person> persons = Utils.getPersonFeaturesFromJSONFile();
        return jarvisPatrick(persons.size(), k, threshold);
    }

    @Test
    public void test() {
        try {
            Vector<Person> persons = Utils.getPersonFeaturesFromJSONFile();
            for (int i = 1; i <= persons.size(); i++) {
                for (int j = 1; j < i; j++) {
                    Vector<Integer>[] components = jarvisPatrick(persons.size(), i, j);
                    Utils.printComponents(components);
                    System.out.println("============" + i + "," + j + "=============");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void t() {
        try {
            Vector<Person> persons = Utils.getPersonFeaturesFromJSONFile();
            Vector<Integer>[] components = jarvisPatrick(persons.size(), 12, 10);
            //Utils.printComponents(Utils.removeNoise(components));
            Utils.printSortedComponentsSimilarityMatrix(Utils.removeNoise(components));
            System.out.println(Utils.removeNoise(components).length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTwoPersonsSimilarity() {
        try {
            Vector<Person> persons = Utils.getPersonFeaturesFromJSONFile();
            /*for (Person person : persons) {
                System.out.println(person);
            }*/
            Person p1 = persons.get(151);
            Person p2 = persons.get(162);
            //System.out.println("place:" + Utils.getPlaceSimilarity(p1, p2));
            //System.out.println("semantic:" + Utils.getSemanticSimilarity(p1, p2));
            System.out.println(Utils.getPlaceSimilarity(p1, p2));
            System.out.println(Utils.getSemanticSimilarity(p1, p2));
            System.out.println("total:" + Utils.getPersonSimilarity(p1, p2, Common.alpha));
            //System.out.println("total:" + Utils.getPersonSimilarityFromFile(149, 160));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tttt() {
        try {
            Vector<Person> persons = Utils.getPersonFeaturesFromJSONFile();
            for (int i = 0; i <= 10; i++) {
                System.out.print(new DecimalFormat("#0.00000").format(Utils.getPersonSimilarity(persons.get(0), persons.get(3), 0.1 * i)) + ",");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tt() {
        try {
            Vector<Person> persons = Utils.getPersonFeaturesFromJSONFile();
            for (int i = 0; i < persons.size(); i++) {
                for (int j = 0; j < persons.size(); j++) {
                    //System.out.println("(" + i + "," + j + ")=" + Utils.getPersonSimilarity(persons.get(i), persons.get(j)));
                    System.out.print(new DecimalFormat("#0.00000").format(Utils.getPersonSimilarityFromFile(i, j)) + ",");
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testKNeighbors() {
        try {
            int personId = 8;
            Vector<Integer> vector = getPersonKNeighbors(personId, 3);
            System.out.println(personId + ": " + vector);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存所有用户彼此之间相似度到文件中
     */
    @Test
    public void saveAllUsersSimilarities() {
        Utils.saveAllUsersSimilarities(Common.alpha);
    }

    /**
     * JarvisPatrick聚类算法
     *
     * @param personSize 待聚类的人员
     * @param k          最近邻个数
     * @param threshold  SNN相似度阈值
     * @return
     * @throws Exception
     */
    private Vector<Integer>[] jarvisPatrick(int personSize, int k, int threshold) throws Exception {
        int[][] matrix = constructProximityMatrix(personSize, k, threshold); // 构造邻近度矩阵
        //printMatrix(matrix);
        return findConnectedComponents(matrix); // 找出相似度图的连通分支（簇）
    }

    /**
     * 找出SNN相似度图的连通分支（簇）
     *
     * @param matrix
     * @return
     * @throws Exception
     */
    private Vector<Integer>[] findConnectedComponents(int[][] matrix) throws Exception {
        Graph graph = new Graph(matrix);
        ConnectedComponent cc = new ConnectedComponent(graph);
        int M = cc.count();
        Vector<Integer>[] components = (Vector<Integer>[]) new Vector[M];
        for (int i = 0; i < M; i++) {
            components[i] = new Vector<Integer>();
        }
        for (int v = 0; v < graph.V(); v++) {
            components[cc.id(v)].add(v);
        }
        return components;
    }

    /**
     * 构造邻近度矩阵
     *
     * @param personSize 待聚类的人员
     * @param k          最近邻个数
     * @param threshold  SNN相似度阈值
     * @return
     * @throws Exception
     */
    private int[][] constructProximityMatrix(int personSize, int k, int threshold) throws Exception {
        int[][] matrix = new int[personSize][personSize];
        Vector<Vector<Integer>> allNeighbors = new Vector<>(); // 所有人员的K近邻
        for (int i = 0; i < personSize; i++) { // 对每个人求其K近邻
            allNeighbors.add(getPersonKNeighbors(i, k));
        }
        for (int i = 0; i < allNeighbors.size(); i++) {
            for (int j = i + 1; j < allNeighbors.size(); j++) {
                int common = countCommon(allNeighbors.get(i), allNeighbors.get(j));
                if (common >= threshold) {
                    matrix[i][j] = common;
                }
            }
        }
        return matrix;
    }

    /**
     * 计算两人共享k近邻数目
     *
     * @param neighbors1
     * @param neighbors2
     * @return
     */
    private int countCommon(Vector<Integer> neighbors1, Vector<Integer> neighbors2) {
        int count = 0;
        for (Integer a : neighbors1) {
            for (Integer b : neighbors2) {
                if (a == b) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 获取每个人的K近邻人员（应该用kd-tree来加速查找）
     *
     * @param personId
     * @param k
     * @return k个人员在persons中的索引
     */
    private Vector<Integer> getPersonKNeighbors(int personId, int k) throws Exception {
        // TODO: 2017-3-14
        Vector<Double> similarities = new Vector<>();
        BufferedReader br = new BufferedReader(new FileReader("similarities/" + personId + ".txt"));
        String str;
        while ((str = br.readLine()) != null) {
            double similarity = Double.parseDouble(str);
            similarities.add(similarity);
        }
        Vector<Integer> kNeighbors = new Vector<>();
        int[] arr = new int[similarities.size()];
        int count = 0;
        while (count < k) {
            int index = 0;
            double max = 0.0;
            for (int i = 0; i < similarities.size(); i++) {
                if (arr[i] == 0 && personId != i) {
                    double similarity = similarities.get(i);
                    if (similarity > max) {
                        index = i;
                        max = similarity;
                    }
                }
            }
            // 如果没有任何人跟该人相似，也就是说最大的相似度都为0.0
            if (max == 0.0 && index == 0) {
                break;
            }
            arr[index] = 1;
            kNeighbors.add(index);
            count++;
        }
        return kNeighbors;
    }

    private void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + ",");
            }
            System.out.println();
        }
    }


}
