import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

class NGramHandler {
    public ArrayList<String> nr;
    public Map<String, ArrayList<Integer>> wordMatrix;
    public int r;
    public int c;
    public int part,a,b;
    ArrayList<String> tList;

    public NGramHandler(ArrayList<String> textList) {
        tList = textList;
        ArrayList<String> uniqueTextArray = new ArrayList<>(new LinkedHashSet<>(textList));
        nr = uniqueTextArray;
        r = nr.size();
        c = nr.size();
        Map<String, ArrayList<Integer>> wordsMatrix = new HashMap<>();
        int i = 0;
        while (i < r) {
            ArrayList<Integer> freq = new ArrayList<>(Collections.nCopies(r, 0));
            wordsMatrix.put(uniqueTextArray.get(i), freq);
            i+=1;
            a+=1;
        }
        this.wordMatrix = wordsMatrix;
    }

    public void buildNGrams(int limit) {
        ArrayList<String[]> ngArray = new ArrayList<>();
        int i = 0;
        while (i < r) {
            for (String k : this.wordMatrix.keySet()) {
                String currRow = this.nr.get(i);
                int currFreq = this.wordMatrix.get(k).get(i);
                if (currFreq >= limit) {
                    String[] oneResult = {currRow, k, String.valueOf(currFreq)};
                    ngArray.add(oneResult);
                }
                b+=1;
            }
            i+=1;
        }
        System.out.println("N-grams array: ");
        for (String[] temp : ngArray) {
            for (String s : temp) {
                System.out.printf("%s ", s);
            }
            System.out.println();
        }
    }

    public void buildFreqMap(int n) {
        int j=0;
    	while (j < tList.size() - 1) {
            String currWord = tList.get(j);
            String nextWord = tList.get(j + 1);
            int rowPosition = nr.indexOf(currWord); //position of row in table
            int currFreq = wordMatrix.get(nextWord).get(rowPosition);
            int newFreq = currFreq + 1;
            wordMatrix.get(nextWord).set(rowPosition, newFreq);
            j+=1;
            for(int i=0;i < 10;i++) {
            	part++;
            }
            if(part!=0) {
            	part=0;
            }
            
        }
    }
}