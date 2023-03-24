import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.*;

public class TFIDF {
	
    private final double[][] tfIDF;
    private final int numWord;
    private final int[][] tfcurr;
    private final ArrayList<String> wordToCheck;
    private final List<List<String>> currArticlesSl;


    public TFIDF(String[] documents) {

    	currArticlesSl = this.buildArticles(documents);
        int totalArticles = currArticlesSl.size();
        wordToCheck = this.buildUniqueTerms(currArticlesSl);
        numWord = wordToCheck.size();
        int[] articleFrequency = new int[numWord];
        tfcurr = new int[numWord][totalArticles];
        tfIDF = new double[numWord][totalArticles];
        
        int map = 0 ; 
        while (map < currArticlesSl.size()) 
        {
            List<String> doc = currArticlesSl.get(map);
            HashMap<String, Integer> tfMap = this.buildIDFFrequencyMap(doc);
            for (Entry<String, Integer> entry : tfMap.entrySet()) {
                String word = entry.getKey();
                int wordFreq = entry.getValue();
                int termIndex = wordToCheck.indexOf(word);

                tfcurr[termIndex][map] = wordFreq;
                articleFrequency[termIndex]++;
            }
            map++;
        }
        for (int i = 0; i < numWord; i++) {
            for (int j = 0; j < totalArticles; j++) {
                double tf = Math.sqrt(this.tfcurr[i][j]);
                double idf = 1.0 + Math.log((double) (totalArticles) / (1.0 + articleFrequency[i]));
                tfIDF[i][j] = tf * idf;
            }
        }
    }

    private List<List<String>> buildArticles(String[] docs) {
        List<List<String>> docsParsed = new ArrayList<>();
        for (String doc : docs) {
            String[] wordToCheck = doc.replaceAll("\\p{Punct}", "")
                    .toLowerCase().split("\\s");
            List<String> wordList = new ArrayList<>();
            for (String word : wordToCheck) {
                word = word.trim();
                if (word.length() > 0) {
                    wordList.add(word);
                }
            }
            docsParsed.add(wordList);
        }
        return docsParsed;
    }

    private ArrayList<String> buildUniqueTerms(List<List<String>> docs) {
        ArrayList<String> uniqueTerms = new ArrayList<>();
        for (List<String> doc : docs) {
            for (String word : doc) {
                if (!uniqueTerms.contains(word)) {
                    uniqueTerms.add(word);
                }
            }
        }
        return uniqueTerms;
    }

    private HashMap<String, Integer> buildIDFFrequencyMap(List<String> doc) {
        HashMap<String, Integer> tfMap = new HashMap<>();
        for (String word : doc) {
            int count = 0;
            for (String str : doc) {
                if (str.equals(word)) {
                    count++;
                }
            }
            tfMap.put(word, count);
        }
        return tfMap;
    }

    public double getSimilarity(int doc_i, int doc_j) {
        double[] primeVec = new double[numWord];
        for (int i = 0; i < numWord; i++) {
            primeVec[i] = tfIDF[i][doc_i];
        }
        double[] secondVec = new double[numWord];
        int set = 0; 
        while (set < numWord) 
        {
            secondVec[set] = tfIDF[set][doc_j];
            set ++;
        }
        return CalcCosine(primeVec, secondVec);
    }

    private double CalcCosine(double[] vectorA, double[] vectorB) {
        double normalValB = 0.0;
        double normalValA = 0.0;
        double prodDot = 0.0;
        
        int count = 0;
        while (count  < vectorA.length) 

        {
            prodDot =prodDot +  vectorA[count] * vectorB[count];
            normalValA = normalValA +  Math.pow(vectorA[count], 2);
            normalValB = normalValB +  Math.pow(vectorB[count], 2);
            count++;
        }
        if (normalValA == 0 || normalValB == 0) {
            return 1;
        }
        return 1 - (prodDot / (Math.sqrt(normalValA) * Math.sqrt(normalValB)));
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());
        Collections.reverse(list);

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static double[][] buildMatrix(String[] cleanedStrings) throws IOException {
        TFIDF tfIdf = new TFIDF(cleanedStrings);
        System.out.println("\nSimilarities");
        for (int i = 0; i < tfIdf.currArticlesSl.size(); i++) {
            System.out.print(i + 1 + "\t");
            int check = 0 ;
            while(check < tfIdf.currArticlesSl.size()) {
                System.out.print(tfIdf.getSimilarity(i, check) + "\t");
                check++;
            }
            System.out.println();
        }

        System.out.println("\nDocument term Matrix");
        List<Map<String, Integer>> documentMapList = new ArrayList<>();
        double[][] tfIDfMatix = new double[24][tfIdf.numWord];
        int count = 0 ;
        while ( count < tfIdf.currArticlesSl.size())
         {
            Map<String, Integer> tempMap = new HashMap<>();
            int get = 0; 
            while (get < tfIdf.wordToCheck.size()) 
            {
                tempMap.put(tfIdf.wordToCheck.get(get), tfIdf.tfcurr[get][count]);
                tempMap = sortByValue(tempMap);
                get++;
            }
            int set = 0;
            while (set < tfIdf.numWord)
            {
                tfIDfMatix[count][set] = tfIdf.tfIDF[set][count];
                set++; 
            }
            documentMapList.add(tempMap);
            count++;
        }

        System.out.println("[");
        FileWriter fw = new FileWriter("src/resources/topics.txt");
        for (Map<String, Integer> temp : documentMapList) {
            int setVal = 1;
            System.out.print("{");
            fw.write("{");
            for (String key : temp.keySet()) {
                System.out.print(key + " : " + temp.get(key) + " ");
                fw.write(key + " : " + temp.get(key) + " ");
                setVal++;
                if (setVal > 100) {
                    break;
                }
            }
            System.out.println("},");
            fw.write("}\n");
        }
        System.out.println("]");
        fw.close();
        return tfIDfMatix;
    }
}