package personClustering;

import common.Common;
import common.Utils;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

/**
 * 凝聚层次聚类算法
 * Created by Silocean on 2016-12-24.
 */
public class AGNES {

    @Test
    public void test() {
        int clusterNum = 4;
        Vector<Person> persons = Utils.getPersonFeaturesFromJSONFile();
        Vector<PersonCluster> clusters = agnes(persons, clusterNum);
        for (PersonCluster cluster : clusters) {
            for (Person person : cluster.getPersons()) {
                System.out.println(person);
            }
            System.out.println("========" + cluster.getClusterName() + "========\n");
        }
    }

    @Test
    public void testTwoUsersSimilarity() {
        Vector<Person> persons = Utils.getPersonFeaturesFromJSONFile();
        /*for (Person person : persons) {
            System.out.println(person);
        }*/
        Person p1 = persons.get(181);
        Person p2 = persons.get(0);

        System.out.println(Utils.getPlaceSimilarity(p1, p2));
        System.out.println(Utils.getSemanticSimilarity(p1, p2));
        System.out.println(Utils.getPersonSimilarity(p1, p2, Common.alpha));
    }

    @Test
    public void testSemanticSimilarity() {
        LinkedHashMap<HashMap<String, Double>, Double> p1 = new LinkedHashMap();
        HashMap<String, Double> ss1 = new HashMap<>();
        HashMap<String, Double> ss2 = new HashMap<>();
        HashMap<String, Double> ss3 = new HashMap<>();
        ss1.put("健身", 3.0);
        ss1.put("吃饭", 25.0);
        ss1.put("唱歌", 10.0);
        ss2.put("科研", 75.0);
        ss3.put("学校", 3.0);
        p1.put(ss1, 100.0);
        p1.put(ss2, 25.0);
        p1.put(ss3, 90.0);

        LinkedHashMap<HashMap<String, Double>, Double> p2 = new LinkedHashMap();
        HashMap<String, Double> ssd1 = new HashMap<>();
        HashMap<String, Double> ssd2 = new HashMap<>();
        HashMap<String, Double> ssd3 = new HashMap<>();
        ssd1.put("健身", 20.0);
        ssd1.put("游泳", 3.0);
        ssd2.put("健身", 9.0);
        ssd2.put("吃饭", 10.0);
        ssd2.put("唱歌", 10.0);
        ssd3.put("科研", 30.0);
        p2.put(ssd1, 30.0);
        p2.put(ssd2, 100.0);
        p2.put(ssd3, 45.0);

        Person pp1 = new Person(new LinkedHashMap<>(), p1);
        Person pp2 = new Person(new LinkedHashMap<>(), p2);

        System.out.println(Utils.getSemanticSimilarity(pp1, pp2));

    }

    /**
     * personClustering.AGNES（凝聚层次聚类算法）
     *
     * @param persons
     * @param clusterNum
     * @return
     */
    private Vector<PersonCluster> agnes(Vector<Person> persons, int clusterNum) {
        // 聚类结果集（初始时每个person是一个簇）
        Vector<PersonCluster> clusters = initClusters(persons);

        while (clusters.size() > clusterNum) { // 达到指定的簇个数时结束算法
            double max = Double.MIN_VALUE;

            int indexP = 0;
            int indexQ = 0;

            // 选择两个簇进行比较
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = 0; j < clusters.size(); j++) {
                    if (i != j && i < j) {
                        PersonCluster clusterP = clusters.get(i);
                        PersonCluster clusterQ = clusters.get(j);

                        double clusterSimilarity = getTwoClustersSimilarityByGroupAverage(clusterP, clusterQ);
                        if (clusterSimilarity > max) {
                            max = clusterSimilarity;
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
     * 两个簇之间的相似度
     * 单链：两个不同簇中任意两点之间的最大相似度（最短距离）
     *
     * @param clusterP
     * @param clusterQ
     * @return
     */
    private double getTwoClustersSimilarityBySingleLink(PersonCluster clusterP, PersonCluster clusterQ) {
        Vector<Person> personsP = clusterP.getPersons();
        Vector<Person> personsQ = clusterQ.getPersons();

        double max = 0;
        // 比较两个簇中所有人
        for (Person personP : personsP) {
            for (Person personQ : personsQ) {
                double similarity = Utils.getPlaceSimilarity(personP, personQ);
                if (similarity > max) {
                    max = similarity;
                }
            }
        }
        return max;
    }

    /**
     * 两个簇之间的相似度
     * 全链：两个不同簇中任意两点之间的最小相似度（最长距离）
     *
     * @param clusterP
     * @param clusterQ
     * @return
     */
    private double getTwoClustersSimilarityByCompleteLink(PersonCluster clusterP, PersonCluster clusterQ) {
        Vector<Person> personsP = clusterP.getPersons();
        Vector<Person> personsQ = clusterQ.getPersons();

        double min = Double.MAX_VALUE;
        // 比较两个簇中所有人
        for (Person personP : personsP) {
            for (Person personQ : personsQ) {
                double similarity = Utils.getPlaceSimilarity(personP, personQ);
                if (similarity > min) {
                    min = similarity;
                }
            }
        }
        return min;
    }

    /**
     * 两个簇之间的相似度
     * 组平均：两个不同簇中所有点对邻近度的平均值
     *
     * @param clusterP
     * @param clusterQ
     * @return
     */
    private double getTwoClustersSimilarityByGroupAverage(PersonCluster clusterP, PersonCluster clusterQ) {
        Vector<Person> personsP = clusterP.getPersons();
        Vector<Person> personsQ = clusterQ.getPersons();

        double sum = 0;
        double num = personsP.size() * personsQ.size();
        // 比较两个簇中所有人
        for (Person personP : personsP) {
            for (Person personQ : personsQ) {
                double similarity = Utils.getPlaceSimilarity(personP, personQ);
                sum += similarity;
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
    private Vector<PersonCluster> mergeClusters(Vector<PersonCluster> clusters, int indexP, int indexQ) {
        PersonCluster clusterP = clusters.get(indexP);
        PersonCluster clusterQ = clusters.get(indexQ);

        Vector<Person> personsP = clusterP.getPersons();
        Vector<Person> personsQ = clusterQ.getPersons();

        personsP.addAll(personsQ);

        clusterP.setPersons(personsP);
        clusters.remove(indexQ);

        return clusters;
    }


    /**
     * 初始化聚簇（每个person都是一个单独的簇）
     *
     * @param persons
     * @return
     */
    private Vector<PersonCluster> initClusters(Vector<Person> persons) {
        Vector<PersonCluster> initialClusters = new Vector<>();
        for (int i = 0; i < persons.size(); i++) {
            Vector<Person> tmpPersons = new Vector<>();
            tmpPersons.add(persons.get(i));

            PersonCluster tmpCluster = new PersonCluster();
            tmpCluster.setPersons(tmpPersons);
            tmpCluster.setClusterName("SimpleCluster:" + i);

            initialClusters.add(tmpCluster);
        }
        return initialClusters;
    }


}
