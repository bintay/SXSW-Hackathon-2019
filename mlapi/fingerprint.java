
import java.io.*;
import java.util.*;
import com.musicg.wave.*;
import com.musicg.fingerprint.*;
public class fingerprint {
    public static void main(String[] args) throws IOException {
        System.out.println(Arrays.toString(args));
        float score = find_score(args[0], args[1]);
        File file = new File("similarity_score.txt");
        file.delete();
        BufferedWriter writer = new BufferedWriter(new FileWriter("similarity_score.txt"));
        String toWrite = "" + score;
        writer.write(toWrite);
        writer.close();
    }
    public static float find_score(String input, String popular){
        Wave w1 = new Wave(input);
        Wave w2 = new Wave(popular);
        
        FingerprintSimilarity fingerprintSimilarity = w1.getFingerprintSimilarity(w2);
        float score = fingerprintSimilarity.getScore();
        float similarity = fingerprintSimilarity.getSimilarity();
        return similarity;
    }
}