package com.rachitjain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

class NGram {

    public int windowSize;
    public ArrayList<String> rows;
    public Map<String, ArrayList<Integer>> columnsMap;
    public int nRow;
    public int nCol;
    ArrayList<String> initialTextList;

    public NGram(ArrayList<String> textList) {
        initialTextList = textList;
        ArrayList<String> uniqueTextArray = new ArrayList<>(new LinkedHashSet<>(textList));
        rows = uniqueTextArray;
        nRow = rows.size();
        nCol = rows.size();
        Map<String, ArrayList<Integer>> columnsMap = new HashMap<>();
        for (int i = 0; i < nRow; i++) {
            ArrayList<Integer> freq = new ArrayList<>(Collections.nCopies(nRow, 0));
            columnsMap.put(uniqueTextArray.get(i), freq);
        }
        this.columnsMap = columnsMap;
    }

    public void buildNGrams(int threshold) {
        ArrayList<String[]> ngramArray = new ArrayList<>();
        for (int i = 0; i < nRow; i++) {
            for (String key : this.columnsMap.keySet()) {
                String currentRow = this.rows.get(i);
                int currentFreq = this.columnsMap.get(key).get(i);
                if (currentFreq >= threshold) {
                    String[] oneResult = {currentRow, key, String.valueOf(currentFreq)};
                    ngramArray.add(oneResult);
                }
            }
        }
        System.out.println("N-grams array: ");
        for (String[] currentArray : ngramArray) {
            for (String s : currentArray) {
                System.out.printf("%s ", s);
            }
            System.out.println();
        }
    }

    public void buildFrequencyMap(int n) {
        windowSize = n;
        for (int i = 0; i < initialTextList.size() - 1; i++) {
            String currentWord = initialTextList.get(i);
            String followingWord = initialTextList.get(i + 1);
            int rowPosition = rows.indexOf(currentWord); //position of row in table
            int currentFreq = columnsMap.get(followingWord).get(rowPosition);
            int newFreq = currentFreq + 1;
            columnsMap.get(followingWord).set(rowPosition, newFreq);
        }
    }
}