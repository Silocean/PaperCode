/**
 * Created by Silocean on 2016-12-12.
 */
public class SimplePoint {

    double x;
    double y;

    public SimplePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "SimplePoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
