import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * Created by Silocean on 2017-02-28.
 */
public class TestANSJ {
    public static void main(String[] args) throws Exception {
        File file = new File("src/main/resources/poi.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str;
        while ((str = br.readLine()) != null) {
            Result terms = ToAnalysis.parse(str);
            for (Term term : terms) {
                System.out.print(term.getName() + ", ");
            }
            System.out.println();
        }
    }
}
