import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class GenerateMatrix {
    public static void main(String[] args) {
        String line = "";
        final Pattern WORD_BOUNDARY = Pattern.compile("\\s*\\b\\s*");
        HashMap<String, Integer> combinations = new HashMap<String, Integer>();

        for (String word: WORD_BOUNDARY.split(line.toLowerCase())) {
            if (word.isEmpty()) {
                continue;
            }
            for (int i = 0; i < word.length()-1; i++) {
                String twoLetters;
                StringBuilder sb = new StringBuilder();
                sb.append(word.charAt(i));
                sb.append(word.charAt(i+1));
                twoLetters = sb.toString();
                if (combinations.containsKey(twoLetters)) {
                    combinations.put(twoLetters, combinations.get(twoLetters) + 1);
                } else {
                    combinations.put(twoLetters, 1);
                }
            }
        }

        Map<String, Integer> sortedMap = new TreeMap<>(combinations);
        sortedMap.entrySet().forEach(System.out::println);

        HashMap<Character, Integer> lettersCount = new HashMap<Character, Integer>();
        for (Map.Entry<String, Integer> entry : combinations.entrySet()) {
            String letters = entry.getKey();
            char firstLetter = letters.charAt(0);

            if (lettersCount.containsKey(firstLetter)) {
                lettersCount.put(firstLetter, lettersCount.get(firstLetter) + entry.getValue());
            } else {
                lettersCount.put(firstLetter, entry.getValue());
            }
        }

        HashMap<String, Double> classifier = new HashMap<String, Double>();
        for (Map.Entry<String, Integer> entrySorted : sortedMap.entrySet()) {
            for (Map.Entry<Character, Integer> entryCount : lettersCount.entrySet()) {
                String combination = entrySorted.getKey();
                char letter = entryCount.getKey();
                if (combination.charAt(0) == letter) {
                    double chance = ((double) entrySorted.getValue()) / entryCount.getValue();
                    classifier.put(entrySorted.getKey(), chance);
                }
            }
        }
        System.out.println(Arrays.asList(classifier));

        String outputFilePath = "D:/INNO-Project/repo/test/nederlands.txt";
        File file = new File(outputFilePath);
        BufferedWriter bf = null;

        try {

            // create new BufferedWriter for the output file
            bf = new BufferedWriter(new FileWriter(file));

            // iterate map entries
            for (Map.Entry<String, Double> entry :
                    classifier.entrySet()) {

                // put key and value separated by a colon
                bf.write(entry.getKey() + ":"
                        + entry.getValue());

                // new line
                bf.newLine();
            }

            bf.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {

            try {

                // always close the writer
                bf.close();
            }
            catch (Exception e) {
            }
        }
    }
}
