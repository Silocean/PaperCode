package common;

/**
 * Created by Silocean on 2017-03-14.
 */
public class HyperSpace {
    public HyperPoint min, max;
    int K = 0;

    public HyperSpace(HyperPoint min, HyperPoint max) {
        if (min == null || max == null)
            throw new NullPointerException("");
        K = min.K;
        if (K == 0 || K != max.K)
            throw new IllegalArgumentException("");
        this.min = new HyperPoint(min);
        this.max = new HyperPoint(max);
    }

    // Detect whether intersects with other common.HyperSpace or not
    public boolean intersects(HyperSpace p) {
        for (int i = 0; i < K; i++)
            if (min.coords[i] > p.max.coords[i] || max.coords[i] < p.min.coords[i])
                return false;
        return true;
    }

    public boolean contains(HyperPoint p) {
        if (K != p.K)
            throw new IllegalArgumentException("");
        for (int i = 0; i < K; i++)
            if (min.coords[i] > p.coords[i] || p.coords[i] > max.coords[i])
                return false;
        return true;
    }

    // The square of Euclidean Distance
    public double squareDistanceTo(HyperPoint p) {
        if (K != p.K)
            throw new IllegalArgumentException("");
        double res = 0;
        for (int i = 0; i < K; i++)
            if (min.coords[i] > p.coords[i])
                res += (min.coords[i] - p.coords[i]) * (min.coords[i] - p.coords[i]);
            else if (p.coords[i] > max.coords[i])
                res += (p.coords[i] - max.coords[i]) * (p.coords[i] - max.coords[i]);
        return res;
    }

    // Euclidean Distance
    public double distanceTo(HyperPoint p) {
        return Math.sqrt(squareDistanceTo(p));
    }

    public String toString() {
        return min.toString() + "->" + max.toString();
    }
}
