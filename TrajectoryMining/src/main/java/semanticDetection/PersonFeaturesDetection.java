package semanticDetection;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.HyperPoint;
import common.HyperSpace;
import common.KDTree;
import common.Utils;
import org.junit.Test;
import stayPointDetection.Point;
import stayPointDetection.StayPoint;
import stayRegionDetection.StayRegion;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Created by Silocean on 2017-03-13.
 */
public class PersonFeaturesDetection {

    @Test
    public void testExtractSinglePersonFeatures() {
        try {
            KDTree tree = Utils.constructKDTree();
            File file = new File("./clusters/180");
            File[] files = file.listFiles();
            Vector<PersonFeature> personFeatures = new Vector<>();
            for (File f : files) {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String str;
                Vector<StayPoint> stayPoints = new Vector<>();
                double frequency = Utils.getFileLineNumber(f); // 到访每个地理位置的次数
                while ((str = br.readLine()) != null) {
                    double lon = Double.parseDouble(str.split(",")[1]);
                    double lat = Double.parseDouble(str.split(",")[0]);
                    StayPoint stayPoint = new StayPoint(lon, lat);
                    stayPoints.add(stayPoint);
                }
                StayRegion stayRegion = new StayRegion(stayPoints);
                Point point = new Point(stayRegion.getMeanLon(), stayRegion.getMeanLat());
                // 地理位置
                HashMap<Point, Double> geographicPlace = new HashMap<>();
                geographicPlace.put(point, frequency);

                HyperSpace hyperSpace = Utils.getQueryRange2(stayRegion);
                Set<HyperPoint> set = tree.rangeQuery(hyperSpace);
                // 语义位置
                HashMap<String, Double> semanticPlace = getSemanticPlace(set);

                for (HyperPoint p : set) {
                    System.out.println(p);
                }
                System.out.println("Rectangle:" + hyperSpace.min + "," + hyperSpace.max);
                System.out.println("semantic:" + semanticPlace);
                System.out.println("===============================================================================================");

                PersonFeature personFeature = new PersonFeature(new double[]{point.getLon(), point.getLat()}, frequency, semanticPlace);
                personFeatures.add(personFeature);

                br.close();
            }
            ObjectMapper mapper = new ObjectMapper();
            String result = mapper.writeValueAsString(personFeatures);
            System.out.println(result);
            File saveToFile = new File("./features/" + file.getName() + ".json");
            BufferedWriter bw = new BufferedWriter(new FileWriter(saveToFile));
            bw.append(result);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExtractAllPersonFeatures() {
        try {
            KDTree tree = Utils.constructKDTree();
            File file = new File("./clusters");
            for (File f : file.listFiles()) {
                extractSinglePersonFeatures(tree, f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从json文件中获取人员地理位置和语义位置信息集合
     */
    @Test
    public void testGetObjectFromJSON() {
        try {
            File file = new File("./features/179.json");
            BufferedReader br = new BufferedReader(new FileReader(file));
            ObjectMapper mapper = new ObjectMapper();
            //mapper.configure(JsonParser.common.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            List personFeatures = mapper.readValue(br.readLine(), List.class);
            for (int i = 0; i < personFeatures.size(); i++) {
                //System.out.println(personFeatures.get(i).getClass().getName());
                String str = mapper.writeValueAsString(personFeatures.get(i));
                PersonFeature personFeature = mapper.readValue(str, PersonFeature.class);
                double lon = personFeature.getCoordinates()[0];
                double lat = personFeature.getCoordinates()[1];
                double frequency = personFeature.getFrequency();
                HashMap<String, Double> semanticPlace = personFeature.getSemanticPlace();
                System.out.println("[" + lon + ", " + lat + "]:" + frequency);
                System.out.println(semanticPlace);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 抽取人员地理位置和语义位置信息集合，并保存到json文件中
     *
     * @param tree
     * @param file
     * @throws Exception
     */
    public void extractSinglePersonFeatures(KDTree tree, File file) throws Exception {
        File[] files = file.listFiles();
        Vector<PersonFeature> personFeatures = new Vector<>();
        for (File f : files) {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String str;
            Vector<StayPoint> stayPoints = new Vector<>();
            double frequency = Utils.getFileLineNumber(f); // 到访每个地理位置的次数
            while ((str = br.readLine()) != null) {
                double lon = Double.parseDouble(str.split(",")[1]);
                double lat = Double.parseDouble(str.split(",")[0]);
                StayPoint stayPoint = new StayPoint(lon, lat);
                stayPoints.add(stayPoint);
            }
            StayRegion stayRegion = new StayRegion(stayPoints);
            Point point = new Point(stayRegion.getMeanLon(), stayRegion.getMeanLat());
            // 地理位置
            HashMap<Point, Double> geographicPlace = new HashMap<>();
            geographicPlace.put(point, frequency);

            HyperSpace hyperSpace = Utils.getQueryRange2(stayRegion);
            Set<HyperPoint> set = tree.rangeQuery(hyperSpace);
            // 语义位置
            HashMap<String, Double> semanticPlace = getSemanticPlace(set);

            PersonFeature personFeature = new PersonFeature(new double[]{point.getLon(), point.getLat()}, frequency, semanticPlace);
            personFeatures.add(personFeature);

            br.close();
        }
        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(personFeatures);
        File saveToFile = new File("./features/" + file.getName() + ".json");
        BufferedWriter bw = new BufferedWriter(new FileWriter(saveToFile));
        bw.append(result);
        bw.close();
        System.out.println("User:" + file.getName() + "'s features have been extracted!");
    }

    /**
     * 从指定stayRegion范围内计算出代表语义信息（语义信息集合）
     *
     * @param set
     * @return
     */
    private HashMap<String, Double> getSemanticPlace(Set<HyperPoint> set) {
        HashMap<String, Double> map = new HashMap<>();
        for (HyperPoint point : set) {
            //System.out.println(point.semantic);
            if (point.semantic.contains("苏州街")) continue;
            String semantic = point.semantic.split(";")[2];
            map.put(semantic, map.get(semantic) == null ? 1 : map.get(semantic) + 1);
        }
        return map;
    }
}

