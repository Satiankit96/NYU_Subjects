import java.io.*;
import java.util.Properties;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.CoreMap;



public class Preprocessor {
    public static void main(String[] args) throws IOException {
        InputStream ins = new FileInputStream("src/resources/dataset_3/data.txt");
        if (ins != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader((ins)));
            String fileDestination;
            ArrayList<String> currStr = new ArrayList<>();
            ArrayList<String> initFname = new ArrayList<>();
            while ((fileDestination = br.readLine()) != null) {
            	initFname.add(fileDestination);
            }
            Properties setProp = new Properties();
            setProp.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(setProp, false);
            for (String name : initFname) {
            	String processedString = preprocessStrings(readTextFile(name), setProp, pipeline).toString();
                currStr.add(processedString);
            }
            double[][] docOrigin = createMatrix(currStr);
            KMeans.buildKMeans(docOrigin);
        }
    }

    public static String readTextFile(String file) throws IOException {
        ArrayList<String> fText = new ArrayList<>();
        String dName = "src/resources/dataset_3/data/" + file;
        InputStream in = new FileInputStream(dName);
        if (in != null) {
        	BufferedReader br = new BufferedReader(new InputStreamReader((in)));
            String l;
            while ((l = br.readLine()) != null) {
                fText.add(l.trim() + " ");
            }
        }
        return fText.toString();
    }
    public static ArrayList<String> preprocessStrings(String tStr, Properties prop, StanfordCoreNLP pipeline) throws IOException {
        ArrayList<String> filteredText = new ArrayList<>();
        String[] currString;
        currString = tStr.toLowerCase().split("[-!~,.():\\[\\]\"\\s]+");
        HashSet<String> stopWords = new HashSet<>();
        InputStream ins = new FileInputStream("src/resources/stopWords.txt");
        if (ins != null) {
        	BufferedReader br = new BufferedReader(new InputStreamReader((ins)));
            String word;
            while ((word = br.readLine()) != null) {
                stopWords.add(word.trim());
            }
        }

        for (String s : currString) {
            if (!stopWords.contains(s.trim())) {
                filteredText.add(s);
            }
        }
        filteredText.remove(0);

        String textWSW = String.join(" ", filteredText.toArray(new String[0]));

        ArrayList<String> tListN = new ArrayList<>();
        Annotation document = pipeline.process(textWSW);

        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();
                tListN.add(lemma);
            }
        }
        NGramHandler ngramObj = new NGramHandler(tListN);
        ngramObj.buildFreqMap(2);
        ngramObj.buildNGrams(2);

        return tListN;
    }
    
    private static double[][] createMatrix(ArrayList<String> processedString) throws IOException {
        String[] matrixString = new String[processedString.size()];
        String temp;
        int counter = 0;
        while (counter < processedString.size())
        {
            temp = processedString.get(counter);
            temp = temp.replaceAll(", x", " ");
            matrixString[counter] = temp;
            counter++;
        }
        return TFIDF.buildMatrix(matrixString);
    }
}