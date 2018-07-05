package personClustering;

import common.Utils;
import org.junit.Test;

import java.util.Vector;

/**
 * Created by Silocean on 2017-05-26.
 */
public class DBSCAN {

    @Test
    public void test() {
        try {
            Vector<Person> persons = Utils.getPersonFeaturesFromJSONFile();
            Vector<Integer>[] components = dbscan(persons, 0.5, 1);
            Utils.printComponents(components);
            //Utils.printSortedComponentsSimilarityMatrix(components);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Vector<Integer>[] start(double eps, int minpts) throws Exception {
        Vector<Person> persons = Utils.getPersonFeaturesFromJSONFile();
        return dbscan(persons, eps, minpts);
    }


    private Vector<Integer>[] dbscan(Vector<Person> persons, double eps, int minpts) throws Exception {
        Vector<Integer> neighbours; // 单个cluster
        Vector<Vector<Integer>> clusters = new Vector<>(); // 聚类结果集（clusters）
        Vector<Integer> visitedPersons = new Vector<>(); // 访问过的点

        for (int i = 0; i < persons.size(); i++) {
            if (!isVisited(visitedPersons, i)) { // 如果该点没有被访问过
                visit(visitedPersons, i); // 标记为访问过
                neighbours = getNeighbours(persons, i, eps);
                if (neighbours.size() >= minpts) { // 如果邻域内的点数超过阈值，对其中每个点分别做进一步判断
                    int index = 0;
                    while (index < neighbours.size()) {
                        int p = neighbours.get(index);
                        if (!isVisited(visitedPersons, p)) {
                            visit(visitedPersons, p);
                            Vector<Integer> neigh = getNeighbours(persons, p, eps);
                            if (neigh.size() >= minpts) { // 如果邻域内某个点的邻域中的点数也超过阈值，加入到cluster中
                                neighbours = merge(neighbours, neigh);
                            }
                        }
                        index++;
                    }
                    clusters.add(neighbours);
                }
            }
        }
        Vector<Integer>[] result = new Vector[clusters.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = clusters.get(i);
        }
        return result;
    }

    /**
     * 判断该点是否被访问过
     *
     * @param person
     * @return
     */
    private boolean isVisited(Vector<Integer> visitedPersons, int person) {
        return visitedPersons.contains(person);
    }

    /**
     * 访问该点
     *
     * @param person
     */
    private void visit(Vector<Integer> visitedPersons, int person) {
        visitedPersons.add(person);
    }

    /**
     * 获取某个点指定邻域内的所有点（初级版）
     * 进阶版可以使用kd-tree来降低时间复杂度（待实现）
     *
     * @param persons
     * @param person
     * @param eps
     * @return
     */
    private Vector<Integer> getNeighbours(Vector<Person> persons, int person, double eps) throws Exception {
        Vector<Integer> neighbours = new Vector<>();
        for (int i = 0; i < persons.size(); i++) {
            if (Utils.getPersonSimilarityFromFile(i, person) >= eps) {
                neighbours.add(i);
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
    private Vector<Integer> merge(Vector<Integer> a, Vector<Integer> b) {
        for (Integer person : b) {
            if (!a.contains(person)) {
                a.add(person);
            }
        }
        return a;
    }


}
