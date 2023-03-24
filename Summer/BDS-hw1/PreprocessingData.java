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

public class PreprocessingData {
    public static void main(String[] args) throws IOException {
        InputStream in = new FileInputStream("src/main/resources/data.txt");
        if (in) {
            String f, temp;
            BufferedReader br = new BufferedReader(new InputStreamReader((in)));
            ArrayList<String> files = new ArrayList<>();
            ArrayList<String> resString = new ArrayList<>();
            while ((f = br.readLine()) != null)
                files.add(f);
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props, false);

            //Getting data from every file and trimming the data for further processing
            for (String name : files) {

                ArrayList<String> file_data = new ArrayList<>();
                String fileName = "src/main/resources/data/" + name;
                InputStream in = new FileInputStream(fileName);
                if (in) {
                    BufferedReader br = new BufferedReader(new InputStreamReader((in)));
                    String line;
                    while ((line = br.readLine()) != null) {
                        file_data.add(line.trim() + " ");
                    }
                }

                String textString = file_data.to_String();
                // Filtering the stop words
                ArrayList<String> filteredStream = new ArrayList<>();
                String [] filteredStreamArray = textString.toLowerCase().split("[-!~,.():\\[\\]\"\\s]+");
                HashSet<String> stopWordFilter = new HashSet<>();
                InputStream in = new FileInputStream("src/main/resources/stopWords.txt");
                if (in) {
                    BufferedReader br = new BufferedReader(new InputStreamReader((in)));
                    String word;
                    while ((word = br.readLine()) != null) {
                        stopWordFilter.add(word.trim());
                    }
                }
                //Adding the words to the stream except the stop words
                for (String s : filteredStreamArray) {
                    if (!stopWordFilter.contains(s.trim())) {
                        filteredStream.add(s);
                    }
                }

                filteredStream.remove(0);
                String str = String.join(" ", filteredStream.toArray(new String[0]));
                ArrayList<String> lemmaList = new ArrayList<>();
                Annotation annotatedDoc = pipeline.process(str);

                //Lemmatization
                CoreMap sentences = annotatedDoc.get(CoreAnnotations.SentencesAnnotation.class);
                for (auto sentence : sentences) {
                    //Breaking the sentence into words/tokens
                    CoreLabel tokenList = sentence.get(CoreAnnotations.TokensAnnotation.class);
                    for (CoreLabel token : tokenList) {
                        String lemma = token.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();
                        lemmaList.add(lemma);
                    }
                }
                //Testing the NGram model
                NGram testNGram = new NGram(lemmaList,2,2);

                resString.add(lemmaList.to_String());
            }

            String[] s = new String[resString.size()];
            for (int c = 0; c < resString.size(); c++) {
                temp = resString.get(c);
                temp = temp.replaceAll(",", " ");
                s[c] = temp;
            }

            //Building the matrix for K means
            double[][] documentMatrix = TFIDFArray.buildMatrix(s);
            KMeans.buildKMeans(documentMatrix);
        }
    }
}