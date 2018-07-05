package personClustering;

import stayPointDetection.Point;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Silocean on 2016-12-24.
 */
public class Person {

    private LinkedHashMap<Point, Double> placeFeatures; // 地理位置
    private LinkedHashMap<HashMap<String, Double>, Double> semanticFeatures; // 语义位置

    public Person(LinkedHashMap<Point, Double> placeFeatures, LinkedHashMap<HashMap<String, Double>, Double> semanticFeatures) {
        this.placeFeatures = placeFeatures;
        this.semanticFeatures = semanticFeatures;
    }

    public HashMap<Point, Double> getPlaceFeatures() {
        return placeFeatures;
    }

    public LinkedHashMap<HashMap<String, Double>, Double> getSemanticFeatures() {
        return semanticFeatures;
    }

    public void setPlaceFeatures(LinkedHashMap<Point, Double> placeFeatures) {
        this.placeFeatures = placeFeatures;
    }

    public void setSemanticFeatures(LinkedHashMap<HashMap<String, Double>, Double> semanticFeatures) {
        this.semanticFeatures = semanticFeatures;
    }

    @Override
    public String toString() {
        return "personClustering.Person{" +
                "placeFeatures=" + placeFeatures +
                ", semanticFeatures=" + semanticFeatures +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (placeFeatures != null ? !placeFeatures.equals(person.placeFeatures) : person.placeFeatures != null)
            return false;
        return semanticFeatures != null ? semanticFeatures.equals(person.semanticFeatures) : person.semanticFeatures == null;

    }

    @Override
    public int hashCode() {
        int result = placeFeatures != null ? placeFeatures.hashCode() : 0;
        result = 31 * result + (semanticFeatures != null ? semanticFeatures.hashCode() : 0);
        return result;
    }
}
