package common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Silocean on 2017-02-24.
 */
public class ToGeoJsonFile {

    public static String toGeoJsonFile(File file) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str;
        ArrayList<Feature> features = new ArrayList<>();
        while ((str = br.readLine()) != null) {
            double lat = Double.parseDouble(str.split(",")[0]);
            double lon = Double.parseDouble(str.split(",")[1]);
            Geometry geometry = new Geometry("Point", new double[]{lon, lat});
            Feature feature = new Feature("Feature", geometry);
            features.add(feature);
        }
        FeatureCollection featureCollection = new FeatureCollection("FeatureCollection", features);
        return mapper.writeValueAsString(featureCollection);
    }

    @Test
    public void test() {
        try {
            File f = new File("trajectory/012");
            for (File file : f.listFiles()) {
                System.out.println(toGeoJsonFile(file));
                /*File saveFile = new File("trajectoryJSON/" + f.getName());
                if (!saveFile.exists()) {
                    saveFile.mkdirs();
                }*/
                BufferedWriter bw = new BufferedWriter(new FileWriter("trajectoryJSON/" + f.getName()
                        + "/" + file.getName().split("\\.")[0] + ".json"));
                bw.append(toGeoJsonFile(file));
                bw.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

class FeatureCollection {
    String type;
    ArrayList<Feature> features;

    public FeatureCollection(String type, ArrayList<Feature> features) {
        this.type = type;
        this.features = features;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<Feature> features) {
        this.features = features;
    }
}

class Feature {
    String type;
    Geometry geometry;

    public Feature(String type, Geometry geometry) {
        this.type = type;
        this.geometry = geometry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}

class Geometry {
    String type;
    double[] coordinates;

    public Geometry(String type, double[] coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }
}