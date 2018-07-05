package personClustering;

/**
 * 使用深度优先搜索找出图中的所有连通分量
 * Created by Silocean on 2016-04-21.
 */
public class ConnectedComponent {
    private boolean[] marked;
    private int[] id;
    private int count;

    public ConnectedComponent(Graph G) {
        marked = new boolean[G.V()];
        id = new int[G.V()];
        for (int s = 0; s < G.V(); s++) {
            if (!marked[s]) {
                dfs(G, s);
                count++;
            }
        }
    }

    private void dfs(Graph G, int v) {
        marked[v] = true;
        id[v] = count;
        for (int w : G.adj(v)) {
            if (!marked[w]) {
                dfs(G, w);
            }
        }
    }

    /**
     * v和w是否连通
     *
     * @param v
     * @param w
     * @return
     */
    public boolean connected(int v, int w) {
        return id[v] == id[w]; // 看v和w是否在同一个连通分量里
    }

    /**
     * v所在的连通分量的标识符（0~count-1）
     *
     * @param v
     * @return
     */
    public int id(int v) {
        return id[v];
    }

    /**
     * 连通分量数
     *
     * @return
     */
    public int count() {
        return count;
    }


}
