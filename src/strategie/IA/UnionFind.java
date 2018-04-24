package strategie.IA;

public class UnionFind {

    private int[] parent;
    private int[] rank;

    public UnionFind(int max) {

        parent = new int[max];
        rank = new int[max];

        for (int i = 0; i < max; i++) {
            parent[i] = i;
        }
    }


    public int find(int i) {

        int p = parent[i];
        if (i == p) {
            return i;
        }
        return parent[i] = find(p);

    }


    public void union(int i, int j) {

        int root1 = find(i);
        int root2 = find(j);

        if (root2 == root1) return;

        if (rank[root1] > rank[root2]) {
            parent[root2] = root1;
        } else if (rank[root2] > rank[root1]) {
            parent[root1] = root2;
        } else {
            parent[root2] = root1;
            rank[root1]++;
        }
    }
}
