package common;

import com.fasterxml.jackson.databind.ObjectMapper;
import personClustering.Person;
import semanticDetection.PersonFeature;
import stayPointDetection.Point;
import stayPointDetection.StayPoint;
import stayRegionDetection.StayRegion;

import java.io.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 工具类
 * Created by Silocean on 2016-12-08.
 */
public class Utils {

    /**
     * 从json文件获取人员地理位置和语义位置信息
     *
     * @return
     */
    public static Vector<Person> getPersonFeaturesFromJSONFile() {
        Vector<Person> persons = new Vector<>();
        try {
            File[] files = new File("./features/").listFiles();
            for (File file : files) { // 每个人
                LinkedHashMap<Point, Double> placeFeatures = new LinkedHashMap<>();
                LinkedHashMap<HashMap<String, Double>, Double> semanticFeatures = new LinkedHashMap<>();
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
                    Point point = new Point(lon, lat);
                    placeFeatures.put(point, frequency);
                    if (!semanticPlace.equals(new HashMap<String, Double>())) {
                        if (!semanticFeatures.containsKey(semanticPlace)) {
                            semanticFeatures.put(semanticPlace, frequency);
                        } else {
                            semanticFeatures.put(semanticPlace, semanticFeatures.get(semanticPlace) + frequency);
                        }
                    }
                    //System.out.println("[" + lon + ", " + lat + "]:" + frequency);
                    //System.out.println(semanticPlace);
                }
                br.close();
                //System.out.println("一个人的" + file.getName());
                Person person = new Person(placeFeatures, semanticFeatures);
                persons.add(person);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return persons;
    }

    /**
     * 计算累计空间距离
     *
     * @param list
     * @param i
     * @param j
     * @return
     */
    public static double accumulatedSpatialDistance(List<Point> list, int i, int j) {
        double sumDistance = 0;
        for (int k = i; k < j; k++) {
            sumDistance += Utils.spatialDistance(list.get(k), list.get(k + 1));
        }
        return sumDistance;
    }

    /**
     * 计算两点之间空间距离
     *
     * @param p1
     * @param p2
     * @return
     */
    public static double spatialDistance(Point p1, Point p2) {
        int earthR = 6371000;
        double x = Math.cos(p1.getLat() * Math.PI / 180) * Math.cos(p2.getLat() * Math.PI / 180) * Math.cos((p1.getLon() - p2.getLon()) * Math.PI / 180);
        double y = Math.sin(p1.getLat() * Math.PI / 180) * Math.sin(p2.getLat() * Math.PI / 180);
        double s = x + y;
        if (s > 1) s = 1;
        if (s < -1) s = -1;
        double alpha = Math.acos(s);
        return alpha * earthR;
    }

    /**
     * 获取停留区域的经纬度范围（根据事先计算好的半径）
     *
     * @param stayRegion
     * @return
     */
    public static HyperSpace getQueryRange(StayRegion stayRegion) {
        double radius = stayRegion.getRadius();
        HyperPoint centerPoint = new HyperPoint(new double[]{stayRegion.getMeanLon(), stayRegion.getMeanLat()});

        double minLon = centerPoint.coords[0] - radius * 0.00001;
        double minLat = centerPoint.coords[1] - radius * 0.00001;
        double maxLon = centerPoint.coords[0] + radius * 0.00001;
        double maxLat = centerPoint.coords[1] + radius * 0.00001;
        HyperPoint minPoint = new HyperPoint(new double[]{minLon, minLat});
        HyperPoint maxPoint = new HyperPoint(new double[]{maxLon, maxLat});

        HyperSpace hyperSpace = new HyperSpace(minPoint, maxPoint);
        return hyperSpace;
    }

    /**
     * 获取停留区域的经纬度范围（根据停留区域实际范围）
     *
     * @param stayRegion
     * @return
     */
    public static HyperSpace getQueryRange2(StayRegion stayRegion) {
        if (stayRegion.getStayPoints().size() == 1) {
            double radius = 50;
            HyperPoint centerPoint = new HyperPoint(new double[]{stayRegion.getMeanLon(), stayRegion.getMeanLat()});
            double minLon = centerPoint.coords[0] - radius * 0.00001;
            double minLat = centerPoint.coords[1] - radius * 0.00001;
            double maxLon = centerPoint.coords[0] + radius * 0.00001;
            double maxLat = centerPoint.coords[1] + radius * 0.00001;
            HyperPoint minPoint = new HyperPoint(new double[]{minLon, minLat});
            HyperPoint maxPoint = new HyperPoint(new double[]{maxLon, maxLat});
            HyperSpace hyperSpace = new HyperSpace(minPoint, maxPoint);
            return hyperSpace;
        } else {
            double minLat = Double.MAX_VALUE;
            double minLon = Double.MAX_VALUE;
            double maxLat = Double.MIN_VALUE;
            double maxLon = Double.MIN_VALUE;
            for (StayPoint stayPoint : stayRegion.getStayPoints()) {
                if (stayPoint.getLat() < minLat) {
                    minLat = stayPoint.getLat();
                }
                if (stayPoint.getLon() < minLon) {
                    minLon = stayPoint.getLon();
                }
                if (stayPoint.getLat() > maxLat) {
                    maxLat = stayPoint.getLat();
                }
                if (stayPoint.getLon() > maxLon) {
                    maxLon = stayPoint.getLon();
                }
            }
            HyperPoint minPoint = new HyperPoint(new double[]{minLon, minLat});
            HyperPoint maxPoint = new HyperPoint(new double[]{maxLon, maxLat});
            HyperSpace hyperSpace = new HyperSpace(minPoint, maxPoint);
            return hyperSpace;
        }
    }

    /**
     * 构造KD树
     *
     * @return
     */
    public static KDTree constructKDTree() {
        KDTree tree = null;
        try {
            File file = new File("src/main/resources/haidian.csv");
            double[][] ps = new double[Utils.getFileLineNumber(file)][2];
            int num = ps.length;
            String[] semantics = new String[num];

            BufferedReader br = new BufferedReader(new FileReader(file));
            String str;
            int i = 0;
            while ((str = br.readLine()) != null) {
                int length = str.split(", ").length;
                String s = str.split(", ")[length - 3]; // wgs-84坐标
                double lon = Double.parseDouble(s.split(",")[0]);
                double lat = Double.parseDouble(s.split(",")[1]);
                ps[i] = new double[]{lon, lat};
                //String ss = str.split(", ")[length - 4];
                //String ss = str.split(", ")[0] + ", " + str.split(", ")[2];
                String ss = str.split(", ")[2];
                semantics[i] = ss;
                i++;
            }

            HyperPoint[] hps = new HyperPoint[num];
            for (int j = 0; j < num; j++) {
                hps[j] = new HyperPoint(ps[j], semantics[j]);
            }

            double[][] scope = {{115, 39}, {118, 42}};
            HyperPoint min = new HyperPoint(scope[0]);
            HyperPoint max = new HyperPoint(scope[1]);
            int k = scope[0].length;
            tree = new KDTree(k, min, max);
            tree.insertByMedianFinding(hps);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tree;
    }

    /**
     * 获取文件行数
     *
     * @param file
     * @return
     */
    public static int getFileLineNumber(File file) {
        int number = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String str;
            while ((str = br.readLine()) != null) {
                number++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }


    /**
     * 计算两点之间时间距离
     *
     * @param p1
     * @param p2
     * @return
     */
    public static long timeDistance(Point p1, Point p2) {
        return Math.abs(p1.time - p2.time);
    }

    public static long convertTimeFromStringToLong(String time) {
        long result = 0L;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            result = simpleDateFormat.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String convertTimeFromLongToString(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }

    /**
     * 余弦相似度
     *
     * @param frequencyP1
     * @param frequencyP2
     * @return
     */
    public static double cosineSimilarity(ArrayList<Double> frequencyP1, ArrayList<Double> frequencyP2) {
        int size = frequencyP1.size();

        // 分子
        double numerator = 0;
        for (int i = 0; i < size; i++) {
            numerator += frequencyP1.get(i) * frequencyP2.get(i);
        }

        // 分母
        double denominator = 0;
        double tmpP1 = 0;
        double tmpP2 = 0;
        for (int i = 0; i < size; i++) {
            tmpP1 += Math.pow(frequencyP1.get(i), 2);
            tmpP2 += Math.pow(frequencyP2.get(i), 2);
        }
        denominator = Math.sqrt(tmpP1) * Math.sqrt(tmpP2);

        return numerator / denominator;
    }


    /**
     * 广义Jaccard系数（又称Tanimoto系数）
     *
     * @param frequencyP1
     * @param frequencyP2
     * @return
     */
    public static double tanimotoSimilarity(ArrayList<Double> frequencyP1, ArrayList<Double> frequencyP2) {
        int size = frequencyP1.size();

        // 分子
        double numerator = 0;
        for (int i = 0; i < size; i++) {
            numerator += frequencyP1.get(i) * frequencyP2.get(i);
        }

        // 分母
        double denominator = 0;
        double tmpP1 = 0;
        double tmpP2 = 0;
        for (int i = 0; i < size; i++) {
            tmpP1 += Math.pow(frequencyP1.get(i), 2);
            tmpP2 += Math.pow(frequencyP2.get(i), 2);
        }
        denominator = tmpP1 + tmpP2;

        return numerator / (denominator - numerator);
    }

    /**
     * Jaccard系数（狭义）
     *
     * @param frequencyP1
     * @param frequencyP2
     * @return
     */
    public static double jaccardSimilarity(ArrayList<Double> frequencyP1, ArrayList<Double> frequencyP2) {
        double size = frequencyP1.size();
        double sum = 0;
        for (int i = 0; i < size; i++) {
            if (frequencyP1.get(i) != 0 && frequencyP2.get(i) != 0) {
                sum += 1;
            }
        }
        return sum / size;
    }

    /**
     * JS相似度
     *
     * @param frequencyP1
     * @param frequencyP2
     * @return
     */
    public static double jsSimilarity(ArrayList<Double> frequencyP1, ArrayList<Double> frequencyP2) {
        int size = frequencyP1.size();
        double sumP1 = 0;
        double sumP2 = 0;
        for (int i = 0; i < size; i++) {
            sumP1 += frequencyP1.get(i);
            sumP2 += frequencyP2.get(i);
        }

        for (int i = 0; i < size; i++) {
            frequencyP1.set(i, frequencyP1.get(i) / sumP1);
            frequencyP2.set(i, frequencyP2.get(i) / sumP2);
        }

        ArrayList<Double> q = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            q.add(i, (frequencyP1.get(i) + frequencyP2.get(i)) / 2.0);
        }
        double kl1 = KLDistance(frequencyP1, q);
        double kl2 = KLDistance(frequencyP2, q);
        return 1 - JSDistance(kl1, kl2);
    }

    /**
     * JS距离
     *
     * @param kl1
     * @param kl2
     * @return
     */
    private static double JSDistance(double kl1, double kl2) {
        return (kl1 + kl2) / 2.0;
    }

    /**
     * KL距离
     *
     * @param p1
     * @param p2
     * @return
     */
    private static double KLDistance(ArrayList<Double> p1, ArrayList<Double> p2) {
        double result = 0;
        for (int i = 0; i < p1.size(); i++) {
            if (p1.get(i) != 0) {
                result += p1.get(i) * Math.log(p1.get(i) / p2.get(i));
            }
        }
        return result;
    }

    /**
     * 获取人员相似度（从以保存的相似度文件中获取，以节省时间）
     *
     * @param p1Id
     * @param p2Id
     * @return
     */
    public static double getPersonSimilarityFromFile(int p1Id, int p2Id) throws Exception {
        double similarity = 0;
        BufferedReader br = new BufferedReader(new FileReader("similarities/" + p1Id + ".txt"));
        String str;
        int index = 0;
        while ((str = br.readLine()) != null) {
            if (index == p2Id) {
                similarity = Double.parseDouble(str);
            }
            index++;
        }
        br.close();
        return similarity;
    }

    /**
     * 获取人员相似度（包括地理位置相似度和语义相似度）
     *
     * @param person1
     * @param person2
     * @param alpha
     * @return
     */
    public static double getPersonSimilarity(Person person1, Person person2, double alpha) {
        return alpha * getPlaceSimilarity(person1, person2) + (1 - alpha) * getSemanticSimilarity(person1, person2);
    }


    /**
     * 计算两人之间语义信息相似度
     *
     * @param person1
     * @param person2
     * @return
     */
    public static double getSemanticSimilarity(Person person1, Person person2) {
        HashMap<HashMap<String, Double>, Double> featuresP1 = person1.getSemanticFeatures();
        HashMap<HashMap<String, Double>, Double> featuresP2 = person2.getSemanticFeatures();

        if (featuresP1.size() == 0 || featuresP2.size() == 0) return 0.0;

        // 提取两人对各自语义places的到访次数
        ArrayList<Double> frequencyP1 = new ArrayList<>();
        ArrayList<Double> frequencyP2 = new ArrayList<>();

        ArrayList<HashMap<String, Double>> temp2 = new ArrayList<>();
        temp2.addAll(featuresP2.keySet());

        boolean flag;
        for (HashMap<String, Double> ss1 : featuresP1.keySet()) {
            flag = true;
            for (HashMap<String, Double> ss2 : featuresP2.keySet()) {
                if (isSemanticSimilar(getSemanticSimilarityOfOneStayRegion(ss1, ss2), ss1, featuresP2)) { // 判断两语义是否相似度最大且超过阈值δ
                    flag = false;
                    frequencyP1.add(featuresP1.get(ss1));
                    frequencyP2.add(featuresP2.get(ss2));
                    temp2.remove(ss2);
                    break;
                }
            }
            if (flag) {
                frequencyP1.add(featuresP1.get(ss1));
                frequencyP2.add(0.0);
            }
        }
        for (HashMap<String, Double> ss : temp2) {
            frequencyP1.add(0.0);
            frequencyP2.add(featuresP2.get(ss));
        }

        /*for (Double aDouble : frequencyP1) {
            System.out.print(aDouble + "    ");
        }
        System.out.println();
        for (Double aDouble : frequencyP2) {
            System.out.print(aDouble + "    ");
        }
        System.out.println();*/

        double tanimoto = Utils.tanimotoSimilarity(frequencyP1, frequencyP2);
        double cosine = Utils.cosineSimilarity(frequencyP1, frequencyP2);

        return tanimoto;
    }

    /**
     * 判断两语义是否相似度最大且超过阈值δ
     *
     * @param similarity
     * @param ss1
     * @param featuresP2
     * @return
     */
    private static boolean isSemanticSimilar(double similarity, HashMap<String, Double> ss1, HashMap<HashMap<String, Double>, Double> featuresP2) {
        boolean flag = false;
        double max = Double.MIN_VALUE;
        for (HashMap<String, Double> ss : featuresP2.keySet()) {
            double tempS = getSemanticSimilarityOfOneStayRegion(ss1, ss);
            if (tempS >= max) {
                max = tempS;
            }
        }

        if (similarity == max && max >= 0.5) {
            flag = true;
        }

        return flag;
    }


    /**
     * 获取两个停留区域中的语义信息集合的相似度
     *
     * @param ss1
     * @param ss2
     * @return
     */
    private static double getSemanticSimilarityOfOneStayRegion(HashMap<String, Double> ss1, HashMap<String, Double> ss2) {
        ArrayList<Double> frequencyS1 = new ArrayList<>();
        ArrayList<Double> frequencyS2 = new ArrayList<>();

        ArrayList<String> temp2 = new ArrayList<>();
        temp2.addAll(ss2.keySet());

        boolean flag;
        for (String s1 : ss1.keySet()) {
            flag = true;
            for (String s2 : ss2.keySet()) {
                if (s1.equals(s2)) { // 如果两语义相同
                    flag = false;
                    frequencyS1.add(ss1.get(s1));
                    frequencyS2.add(ss2.get(s2));
                    temp2.remove(s2);
                    break;
                }
            }
            if (flag) {
                frequencyS1.add(ss1.get(s1));
                frequencyS2.add(0.0);
            }
        }
        for (String p : temp2) {
            frequencyS1.add(0.0);
            frequencyS2.add(ss2.get(p));
        }

        double js = Utils.jsSimilarity(frequencyS1, frequencyS2);

        return js;
    }

    /**
     * 计算两人之间地理位置相似度
     *
     * @param person1
     * @param person2
     * @return
     */
    public static double getPlaceSimilarity(Person person1, Person person2) {
        // TODO: 2016-12-24

        HashMap<Point, Double> featuresP1 = person1.getPlaceFeatures();
        HashMap<Point, Double> featuresP2 = person2.getPlaceFeatures();

        if (featuresP1.size() == 0 || featuresP2.size() == 0) return 0.0;

        // 提取两人对各自places的到访次数
        ArrayList<Double> frequencyP1 = new ArrayList<>();
        ArrayList<Double> frequencyP2 = new ArrayList<>();

        ArrayList<Point> temp2 = new ArrayList<>();
        temp2.addAll(featuresP2.keySet());

        boolean flag;
        for (Point p1 : featuresP1.keySet()) {
            flag = true;
            for (Point p2 : featuresP2.keySet()) {
                //if (isPlaceSimilar(Utils.spatialDistance(p1, p2), p1, featuresP2)) { // 判断两地理位置是否距离最近，最小距离小于Common.eps
                if (Utils.spatialDistance(p1, p2) <= Common.eps) { // 如果两点距离小于Common.eps米，说明两个点代表同一个地理位置
                    flag = false;
                    frequencyP1.add(featuresP1.get(p1));
                    frequencyP2.add(featuresP2.get(p2));
                    temp2.remove(p2);
                    break;
                }
            }
            if (flag) { // p1中有的，p2中没有
                frequencyP1.add(featuresP1.get(p1));
                frequencyP2.add(0.0);
            }
        }
        for (Point p : temp2) {
            frequencyP1.add(0.0);
            frequencyP2.add(featuresP2.get(p));
        }

        double tanimoto = Utils.tanimotoSimilarity(frequencyP1, frequencyP2);

        return tanimoto;
    }

    /**
     * 判断两地理位置是否距离最近，最小距离小于Common.eps
     *
     * @param similarity
     * @param p1
     * @param featuresP2
     * @return
     */
    private static boolean isPlaceSimilar(double similarity, Point p1, HashMap<Point, Double> featuresP2) {
        boolean flag = false;
        double min = Double.MAX_VALUE;
        for (Point p : featuresP2.keySet()) {
            double tempD = Utils.spatialDistance(p1, p);
            if (tempD < min) {
                min = tempD;
            }
        }

        if (similarity == min && min <= Common.eps) {
            flag = true;
        }

        return flag;
    }

    /**
     * 打印聚类后的每个簇
     *
     * @param components
     */
    public static void printComponents(Vector<Integer>[] components) {
        int M = components.length;
        System.out.println(M + " components");
        for (int i = 0; i < M; i++) {
            for (int v : components[i]) {
                System.out.print(v + " ");
            }
            System.out.println();
        }
    }

    /**
     * 保存所有用户彼此之间相似度到文件中
     *
     * @param alpha
     */
    public static void saveAllUsersSimilarities(double alpha) {
        try {
            deleteAllFiles(new File("similarities"));
            Vector<Person> persons = Utils.getPersonFeaturesFromJSONFile();
            for (int i = 0; i < persons.size(); i++) {
                BufferedWriter bw = new BufferedWriter(new FileWriter("similarities/" + i + ".txt"));
                for (int j = 0; j < i; j++) {
                    double similarity = Utils.getPersonSimilarityFromFile(j, i);
                    bw.append(similarity + "\n");
                }
                for (int j = i; j < persons.size(); j++) {
                    double similarity = Utils.getPersonSimilarity(persons.get(i), persons.get(j), alpha);
                    bw.append(similarity + "\n");
                }
                bw.close();
                System.out.println("The similarities between " + i + " and others have been saved to file!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllFiles(File file) throws Exception {
        if (file.isFile()) {
            file.delete();
        } else {
            for (File f : file.listFiles()) {
                deleteAllFiles(f);
            }
        }
    }

    /**
     * 打印给簇排序后的相似度矩阵
     *
     * @param components
     * @throws Exception
     */
    public static void printSortedComponentsSimilarityMatrix(Vector<Integer>[] components) throws Exception {
        Vector<Integer> sortedPersons = new Vector<>();
        for (Vector<Integer> component : components) {
            for (Integer integer : component) {
                sortedPersons.add(integer);
            }
        }
        for (int i = 0; i < sortedPersons.size(); i++) {
            for (int j = 0; j < sortedPersons.size(); j++) {
                System.out.print(new DecimalFormat("#0.00000").format(Utils.getPersonSimilarityFromFile(sortedPersons.get(i), sortedPersons.get(j))) + ",");
            }
            System.out.println();
        }
    }

    /**
     * 去除噪声点（也就是去除那些只有一个点的簇）
     *
     * @param components
     * @return
     */
    public static Vector<Integer>[] removeNoise(Vector<Integer>[] components) {
        int count = 0;
        for (Vector<Integer> component : components) {
            if (component.size() == 1) count++;
        }
        Vector<Integer>[] result = new Vector[components.length - count];
        int i = 0;
        for (Vector<Integer> component : components) {
            if (component.size() > 1) {
                result[i] = component;
                i++;
            }
        }
        return result;
    }

}
