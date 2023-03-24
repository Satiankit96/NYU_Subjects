package com.rachitjain;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class DocumentPreprocessing {
    public static void main(String[] args) throws IOException {
        InputStream in = new FileInputStream("src/main/resources/data.txt");
        try (in) {
            BufferedReader br = new BufferedReader(new InputStreamReader((in)));
            String fName;
            ArrayList<String> fNames = new ArrayList<>();
            ArrayList<String> processedString = new ArrayList<>();
            while ((fName = br.readLine()) != null) {
                fNames.add(fName);
            }
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props, false);
            for (String name : fNames) {
                processedString.add(preprocessStrings(readTextFile(name), props, pipeline).toString());
            }
            double[][] documentMap = buildMatrix(processedString);
            KMeans.buildKMeans(documentMap);
        }
    }

    private static double[][] buildMatrix(ArrayList<String> processedString) throws IOException {
        String[] matrixString = new String[processedString.size()];
        String temp;
        for (int counter = 0; counter < processedString.size(); counter++) {
            temp = processedString.get(counter);
            temp = temp.replaceAll(",", " ");
            matrixString[counter] = temp;
        }
        return TFIDFArray.buildMatrix(matrixString);
    }

    public static String readTextFile(String fileName) throws IOException {
        ArrayList<String> filetext = new ArrayList<>();
        String docName = "src/main/resources/data/" + fileName;
        InputStream in = new FileInputStream(docName);
        try (in) {
            BufferedReader br = new BufferedReader(new InputStreamReader((in)));
            String line;
            while ((line = br.readLine()) != null) {
                filetext.add(line.trim() + " ");
            }
        }
        return filetext.toString();
    }

    public static ArrayList<String> preprocessStrings(String textString, Properties properties, StanfordCoreNLP pipeline) throws IOException {
        ArrayList<String> cleanedText = new ArrayList<>();
        String[] textArray;
        // This split is due to the stop words having youd instead of you'd
        textArray = textString.toLowerCase().split("[-!~,.():\\[\\]\"\\s]+");
        HashSet<String> stopWordSet = new HashSet<>();
        InputStream in = new FileInputStream("src/main/resources/stopWords.txt");
        try (in) {
            BufferedReader br = new BufferedReader(new InputStreamReader((in)));
            String word;
            while ((word = br.readLine()) != null) {
                stopWordSet.add(word.trim());
            }
        }

        for (String s : textArray) {
            if (!stopWordSet.contains(s.trim())) {
                cleanedText.add(s);
            }
        }
        // Removing space from the first index
        cleanedText.remove(0);

        String stringWithoutStopWords = String.join(" ", cleanedText.toArray(new String[0]));

        ArrayList<String> modifiedTextList = new ArrayList<>();
        Annotation document = pipeline.process(stringWithoutStopWords);

        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();
                modifiedTextList.add(lemma);
            }
        }
        NGram testNGram = new NGram(modifiedTextList);
        testNGram.buildFrequencyMap(2);
        testNGram.buildNGrams(2);

        return modifiedTextList;
    }
}