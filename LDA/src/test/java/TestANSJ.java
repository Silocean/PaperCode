import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.*;

/**
 * Created by Silocean on 2017-02-28.
 */
public class TestANSJ {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("src/main/resources/poi.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("src/main/resources/data3/poi_result.txt"));
        String str;
        while ((str = br.readLine()) != null) {
            Result terms = ToAnalysis.parse(str);
            for (Term term  : terms) {
                bw.append(term.getName() + " ");
            }
        }
        bw.close();
    }
}
