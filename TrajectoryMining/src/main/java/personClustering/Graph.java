package personClustering;

import java.util.Vector;

/**
 * 邻接表数组实现的graph数据类型
 * Created by Silocean on 2016-04-20.
 */
public class Graph {
    private final int V; // 顶点数目
    private int E; // 边的数目
    private Vector<Integer>[] adj; //邻接表

    /**
     * 创建一个含有V个顶点但不含有边的图
     *
     * @param V
     */
    public Graph(int V) {
        this.V = V;
        this.E = 0;
        adj = (Vector<Integer>[]) new Vector[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new Vector<Integer>();
        }
    }

    /**
     * 邻近度矩阵方式初构造图
     *
     * @param matrix
     */
    public Graph(int[][] matrix) {
        this(matrix.length);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix[i].length; j++) {
                if (matrix[i][j] != 0) {
                    addEdge(i, j);
                }
            }
        }
    }

    public int V() {
        return V;
    }

    public int E() {
        return E;
    }

    /**
     * 向图中添加一条边v-w
     *
     * @param v
     * @param w
     */
    public void addEdge(int v, int w) {
        adj[v].add(w);
        adj[w].add(v);
        E++;
    }

    /**
     * 和v相邻的所有顶点
     *
     * @param v
     * @return
     */
    public Iterable<Integer> adj(int v) {
        return adj[v];
    }

    /**
     * 计算v的度数
     *
     * @param v
     * @return
     */
    public int degree(int v) {
        int degree = 0;
        for (int w : adj(v)) {
            degree++;
        }
        return degree;
    }

    /**
     * 计算所有顶点的最大度数
     *
     * @return
     */
    public int maxDegree() {
        int max = 0;
        for (int v = 0; v < V; v++) {
            if (degree(v) > max) {
                max = degree(v);
            }
        }
        return max;
    }

    /**
     * 计算所有顶点的平均度数
     *
     * @return
     */
    public double avgDegree() {
        return 2.0 * E / V;
    }

    public int numberOfSelfLoops() {
        int count = 0;
        for (int v = 0; v < V; v++) {
            for (int w : adj(v)) {
                if (w == v) count++;
            }
        }
        return count / 2; //因为每条边都被记过两次，所以要除以2
    }

    @Override
    public String toString() {
        String s = V + " vertices, " + E + " edges\n";
        for (int v = 0; v < V; v++) {
            s += v + ": ";
            for (int w : this.adj(v)) {
                s += w + " ";
            }
            s += "\n";
        }
        return s;
    }
}
