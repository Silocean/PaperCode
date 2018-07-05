package semanticDetection;

import java.util.HashMap;

/**
 * 人员地理位置和语义位置信息
 */
public class PersonFeature {
    double[] coordinates;
    double frequency;
    HashMap<String, Double> semanticPlace;

    public PersonFeature() {
    }

    public PersonFeature(double[] coordinates, double frequency, HashMap<String, Double> semanticPlace) {
        this.coordinates = coordinates;
        this.frequency = frequency;
        this.semanticPlace = semanticPlace;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public HashMap<String, Double> getSemanticPlace() {
        return semanticPlace;
    }

    public void setSemanticPlace(HashMap<String, Double> semanticPlace) {
        this.semanticPlace = semanticPlace;
    }
}
