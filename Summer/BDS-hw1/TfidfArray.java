package com.rachitjain;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.*;

public class TFIDFArray {
    private final int[][] tf;
    private final List<List<String>> articles;
    private final ArrayList<String> words;
    private final double[][] tfIDF;
    private final int numWords;

    public TFIDFArray(String[] documents) {

        // tf(t,d) = count of t in d / number of words in d
        // idf(t) = log(N/(df + 1))
        // tf-idf(t, d) = tf(t, d) * log(N/(df + 1))
        articles = this.buildArticles(documents);
        int numOfArticles = articles.size();
        words = this.buildUniqueTerms(articles);
        numWords = words.size();
        int[] articleFrequency = new int[numWords];
        tf = new int[numWords][numOfArticles];
        tfIDF = new double[numWords][numOfArticles];

        for (int i = 0; i < articles.size(); i++) {
            List<String> doc = articles.get(i);
            HashMap<String, Integer> tfMap = this.buildIDFFrequencyMap(doc);
            for (Entry<String, Integer> entry : tfMap.entrySet()) {
                String word = entry.getKey();
                int wordFreq = entry.getValue();
                int termIndex = words.indexOf(word);

                tf[termIndex][i] = wordFreq;
                articleFrequency[termIndex]++;
            }
        }
        for (int i = 0; i < numWords; i++) {
            for (int j = 0; j < numOfArticles; j++) {
                double tf = Math.sqrt(this.tf[i][j]);
                double idf = 1.0 + Math.log((double) (numOfArticles) / (1.0 + articleFrequency[i]));
                tfIDF[i][j] = tf * idf;
            }
        }
    }

    private List<List<String>> buildArticles(String[] docs) {
        List<List<String>> docsParsed = new ArrayList<>();
        for (String doc : docs) {
            String[] words = doc.replaceAll("\\p{Punct}", "")
                    .toLowerCase().split("\\s");
            List<String> wordList = new ArrayList<>();
            for (String word : words) {
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
        double[] vector1 = new double[numWords];
        for (int i = 0; i < numWords; i++) {
            vector1[i] = tfIDF[i][doc_i];
        }
        double[] vector2 = new double[numWords];
        for (int i = 0; i < numWords; i++) {
            vector2[i] = tfIDF[i][doc_j];
        }
        return cosine(vector1, vector2);
    }

    private double cosine(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        if (normA == 0 || normB == 0) {
            return 1;
        }
        return 1 - (dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)));
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
        TFIDFArray tfIdf = new TFIDFArray(cleanedStrings);
        System.out.println("\nSimilarities");
        for (int i = 0; i < tfIdf.articles.size(); i++) {
            System.out.print(i + 1 + "\t");
            for (int j = 0; j < tfIdf.articles.size(); j++) {
                System.out.print(tfIdf.getSimilarity(i, j) + "\t");
            }
            System.out.println();
        }

        System.out.println("\nDocument term Matrix");
        List<Map<String, Integer>> documentMapList = new ArrayList<>();
        double[][] tfIDfMatix = new double[24][tfIdf.numWords];
        for (int i = 0; i < tfIdf.articles.size(); i++) {
            Map<String, Integer> tempMap = new HashMap<>();
            for (int j = 0; j < tfIdf.words.size(); j++) {
                tempMap.put(tfIdf.words.get(j), tfIdf.tf[j][i]);
                tempMap = sortByValue(tempMap);
            }
            for (int j = 0; j < tfIdf.numWords; j++) {
                tfIDfMatix[i][j] = tfIdf.tfIDF[j][i];
            }
            documentMapList.add(tempMap);
        }

        System.out.println("[");
        FileWriter fw = new FileWriter("src/main/topics.txt");
        for (Map<String, Integer> temp : documentMapList) {
            int counter = 1;
            System.out.print("{");
            fw.write("{");
            for (String key : temp.keySet()) {
                System.out.print(key + " : " + temp.get(key) + " ");
                fw.write(key + " : " + temp.get(key) + " ");
                counter++;
                // Taking top 100 terms. Can be done using Counter class in Python
                if (counter > 100) {
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