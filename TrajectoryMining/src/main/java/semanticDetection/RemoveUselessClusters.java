package semanticDetection;

import common.Utils;
import org.junit.Test;
import stayPointDetection.StayPoint;
import stayRegionDetection.StayRegion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

/**
 * 去除无用语义位置信息
 * Created by Silocean on 2017-03-07.
 */
public class RemoveUselessClusters {

    public Vector<StayRegion> getPersonStayRegions(File file) throws Exception {
        Vector<StayRegion> stayRegions = new Vector<>();
        File[] files = file.listFiles();
        for (File f : files) { // 该人的每个蔟
            Vector<StayPoint> stayPoints = new Vector<>();
            BufferedReader br = new BufferedReader(new FileReader(f));
            String str;
            while ((str = br.readLine()) != null) {
                double lon = Double.parseDouble(str.split(",")[1]);
                double lat = Double.parseDouble(str.split(",")[0]);
                long arrTime = Utils.convertTimeFromStringToLong(str.split(",")[2]);
                long levTime = Utils.convertTimeFromStringToLong(str.split(",")[3]);
                StayPoint stayPoint = new StayPoint(lon, lat, arrTime, levTime);
                stayPoints.add(stayPoint);
            }
            StayRegion stayRegion = new StayRegion(stayPoints);
            stayRegions.add(stayRegion);
        }
        return stayRegions;
    }

    @Test
    public void test() {
        try {
            File file = new File("./clusters/179");
            Vector<StayRegion> stayRegions = getPersonStayRegions(file);
            for (StayRegion stayRegion : stayRegions) {
                Vector<StayPoint> stayPoints = stayRegion.getStayPoints();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
