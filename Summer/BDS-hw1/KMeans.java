import java.io.IOException;
import java.util.Random;
import java.util.HashSet;

public class KMeans {
    private final int ts;
    private final int nDocVar;
    private final int propertVar;
    private final boolean normalL1;
    private double wcs;
    private final double[][] wordArrMatrix;
    private double[][] pArrMatrix;
    private int[] lArrMatrix;
    private final double epsVar;
    private final boolean isKmeansPPFlag;

    KMeans(Builder builder) {
        ts = builder.pin;
        wordArrMatrix = builder.wordsArrMatrix;
        int buildedItr = builder.itr;
        isKmeansPPFlag = builder.isKmeanFlag;
        epsVar = builder.eps;
        normalL1 = builder.l1normalized;
        nDocVar = wordArrMatrix.length;
        propertVar = wordArrMatrix[0].length;
        double bwc = Double.POSITIVE_INFINITY;
        double[][] bestCentres = new double[0][0];
        int[] bAssignedPos = new int[0];
        int a=0;
        while ( a < buildedItr) {
            cluster();
            if (wcs < bwc) {
                bwc = wcs;
                bestCentres = pArrMatrix;
                bAssignedPos = lArrMatrix;
            }
            a++;
        }
        lArrMatrix = bAssignedPos;
        pArrMatrix = bestCentres;
        wcs = bwc;
    }

    public static class Builder {
        private final int pin;
        private final double[][] wordsArrMatrix;
        private double eps = .001;
        private boolean l1normalized = true;
        private boolean isKmeanFlag = false;
        private int itr = 10;


        public Builder(int var, double[][] centreArray) {
            HashSet<double[]> hashSet = new HashSet<>(var);
            int dis = 0;
            for (double[] point : centreArray) {
                if (!hashSet.contains(point)) {
                    dis++;
                    hashSet.add(point);
                    if (dis >= var)
                        break;
                }
            }
            this.pin = var;
            this.wordsArrMatrix = centreArray;
        }

        public Builder epsilon(double epsVar) {
            this.eps = epsVar;
            return this;
        }

        public Builder iterations(int iter) {
            this.itr = iter;
            return this;
        }

        public Builder L1norm(boolean l1Norm) {
            this.l1normalized = l1Norm;
            return this;
        }

        public Builder isKmeansPlusplus(boolean isKmeanFlag) {
            this.isKmeanFlag = isKmeanFlag;
            return this;
        }

        public KMeans build() {
            return new KMeans(this);
        }
    }

    private void cluster() {
        findFirstCentroids();
        wcs = Double.POSITIVE_INFINITY;
        double prevWCSS;
        do {
            assignLabels();
            updateCentroids();
            prevWCSS = wcs;
            buildWCSS();
        } while (!stopClustering(prevWCSS));
    }

    private void assignLabels() {
        lArrMatrix = new int[nDocVar];
        double tDist;
        double minVal;
        int minLoc,i=0,j=0;
        while ( i < nDocVar) {
            minLoc = 0;
            minVal = Double.POSITIVE_INFINITY;
            j=0;
            while (j < ts) {
                tDist = distance(wordArrMatrix[i], pArrMatrix[j]);
                if (tDist < minVal) {
                    minVal = tDist;
                    minLoc = j;
                }
                j++;
            }
            lArrMatrix[i] = minLoc;
            i++;
        }
    }

    private void updateCentroids() {
    	int i=0,j=0;
        while (i < ts) {
        	j=0;
            while(j < propertVar) {
                pArrMatrix[i][j] = 0;
                j++;
            }
            i++;
        }

        int[] cSizeArray = new int[ts];
        i=0;
        while (i < nDocVar) {
            cSizeArray[lArrMatrix[i]]++;
            j=0;
            while( j < propertVar) {
                pArrMatrix[lArrMatrix[i]][j] += wordArrMatrix[i][j];
                j++;
            }
            i++;
        }
        HashSet<Integer> emptyCentroids = new HashSet<>();
        i=0;
        while (i < ts) {
            if (cSizeArray[i] == 0)
                emptyCentroids.add(i);

            else {
	            	j=0;
	                while (j < propertVar) {
	                    pArrMatrix[i][j] /= cSizeArray[i];
	                	j++;
	                }
                }
            i++;
        }
        if (emptyCentroids.size() != 0) {
            HashSet<double[]> nonemptyCentroids = new HashSet<>(ts - emptyCentroids.size());
            i=0;
            while (i < ts) {
                if (!emptyCentroids.contains(i))
                    nonemptyCentroids.add(pArrMatrix[i]); 
                i++;
            }

            Random r = new Random();
            for (int eCen : emptyCentroids)
                while (true) {
                    int rand = r.nextInt(wordArrMatrix.length);
                    if (!nonemptyCentroids.contains(wordArrMatrix[rand])) {
                        nonemptyCentroids.add(wordArrMatrix[rand]);
                        pArrMatrix[eCen] = wordArrMatrix[rand];
                        break;
                    }
                }
        }
    }

    private void findFirstCentroids() {
        if (isKmeansPPFlag)
            KmeansPP();
        else
            Kmeans();
    }

    private void Kmeans() {
        pArrMatrix = new double[ts][propertVar];
        double[][] cp = wordArrMatrix;
        Random gen = new Random();
        int rand;
        for (int i = 0; i < ts; i++) {
            rand = gen.nextInt(8) + 8 * i;
            System.arraycopy(cp[rand], 0, pArrMatrix[i], 0, propertVar);
        }
    }

    private void KmeansPP() {
        pArrMatrix = new double[ts][propertVar];
        double[] distToClosestCentroid = new double[nDocVar];
        double[] weightedDistribution = new double[nDocVar];
        Random gen = new Random();
        int choose = 0,foo=0;
        
        while (foo < ts) {
            if (foo == 0)
                choose = gen.nextInt(nDocVar);
            else {
                for (int p = 0; p < nDocVar; p++) {
                    double tempDistance = cosine(wordArrMatrix[p], pArrMatrix[foo - 1]);
                    if (foo == 1)
                        distToClosestCentroid[p] = tempDistance;

                    else {
                        if (tempDistance < distToClosestCentroid[p])
                            distToClosestCentroid[p] = tempDistance;
                    }
                    if (p == 0)
                        weightedDistribution[0] = distToClosestCentroid[0];
                    else weightedDistribution[p] = weightedDistribution[p - 1] + distToClosestCentroid[p];
                }
                double rand = gen.nextDouble();
                for (int j = nDocVar - 1; j > 0; j--) {
                    if (rand > weightedDistribution[j - 1] / weightedDistribution[nDocVar - 1]) {
                        choose = j;
                        break;
                    } else
                        choose = 0;
                }
            }
            System.arraycopy(wordArrMatrix[choose], 0, pArrMatrix[foo], 0, propertVar);
            foo++;
        }
    }

    private boolean stopClustering(double prevWc) {
        return epsVar > 1 - (wcs / prevWc);
    }

    private double distance(double[] a, double[] b) {
        return normalL1 ? cosine(a, b) : euclidean(a, b);
    }

    private double euclidean(double[] a, double[] b) {
        double dist = 0;
        for (int i = 0; i < a.length; i++)
            dist += Math.abs((a[i] - b[i]) * (a[i] - b[i]));
        dist = Math.sqrt(dist);
        return dist;
    }

    private double cosine(double[] vecA, double[] vecB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        int i=0;
        double x;
        while (i < vecA.length) {
            dotProduct += vecA[i] * vecB[i];
            normA += Math.pow(vecA[i], 2);
            normB += Math.pow(vecB[i], 2);
            i++;
        }
        if (normA == 0 || normB == 0) {
            return 1;
        }
        x = (Math.sqrt(normA) * Math.sqrt(normB));
        return 1 - (dotProduct / x);
    }

    private void buildWCSS() {
        double wC = 0;
        int assginC,i=0;

        while (i < nDocVar) {
            assginC = lArrMatrix[i];
            wC += distance(wordArrMatrix[i], pArrMatrix[assginC]);
            i++;
        }
        this.wcs = wC;
    }

    public double[][] getCentres() {
        return pArrMatrix;
    }

    public int[] getClusters() {
        return lArrMatrix;
    }

    private static void KMeansEuclidean(int k, int propSize, double[][] dataArray) {
        System.out.println("\nKMeans Euclidean");
        KMeans KclusterE = new KMeans.Builder(k, dataArray)
                .iterations(50)
                .isKmeansPlusplus(false)
                .L1norm(false)
                .epsilon(.001).build();
        double[][] KcentroidsE = KclusterE.getCentres();
        int i=0,j;
        while (i < k) {
        	j=0;
            while (j < propSize) {
                System.out.print(KcentroidsE[i][j] + ", ");
                j++;
            }
            System.out.println();
            i++;
        }
        int[] clus = KclusterE.getClusters();
        System.out.print("Cluster indices ");
        for (int value : clus) System.out.print(value + " ");
        builConfArray(clus);
    }

    private static void KMeansCosine(int k, int propSize, double[][] dataArray) {
        System.out.println("\nKMeans Cosine");
        KMeans KclusterC = new KMeans.Builder(k, dataArray).
                iterations(50).
                isKmeansPlusplus(false).
                L1norm(true).build();
        double[][] KcentroidsC = KclusterC.getCentres();
        int i=0,j;
        while (i < k) {
        	j=0;
            while (j < propSize) {
                System.out.print(KcentroidsC[i][j] + ", ");
                j++;
            }
            System.out.println();
            i++;
        }
        int[] clusC = KclusterC.getClusters();
        System.out.print("Cluster indices ");
        for (int p : clusC) System.out.print(p + " ");
        builConfArray(clusC);
    }

    private static void KMeansPPCosine(int k, double[][] dataArray) {
        System.out.println("\nKMeans++ using cosine");
        KMeans clustering = new KMeans.Builder(k, dataArray)
                .iterations(50)
                .L1norm(true)
                .isKmeansPlusplus(true).build();
        double[][] centreArrayMatrix = clustering.getCentres();
        for (int i = 0; i < k; i++)
            System.out.println("KMeans++ (" + centreArrayMatrix[i][0] + ", " + centreArrayMatrix[i][1] + ")");
        int[] cluspp = clustering.getClusters();
        System.out.print("Cluster indices ");
        for (int j : cluspp) System.out.print(j + " ");
        builConfArray(cluspp);
    }

    public static void buildKMeans(double[][] data) throws IOException {
        int k = 3;
        int featuresSize = 1920;

        // KMeans algorithm with Euclidean distance
        KMeansEuclidean(k, featuresSize, data);

        // KMeans algorithm with Cosine distance
        KMeansCosine(k, featuresSize, data);

        // KMeans++ algorithm with Cosine distance
        KMeansPPCosine(k, data);
    }

    public static void builConfArray(int[] clusterArray) {
        System.out.println("\nCalculating precision matrix of topics aviation, disease, bank");
        System.out.println("\nPredicted vs Actual");
        int[][] confArray = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
        int foo = 0,i=0,j;
        while ( i < 3) {
        	j=foo;
            while ( j < foo + 8) {
                confArray[clusterArray[j]][i]++;
                j++;
            }
            foo = (foo == 0) ? 8 : 16;
            i++;
        }
        i=0;
        while (i < 3) {
        	j=0;
            while (j < 3) {
                System.out.print(confArray[i][j] + " ");
                j++;
            }
            System.out.println();
            i++;
        }
        System.out.println();

        float d = (confArray[0][0] + confArray[0][1] + confArray[0][2]);
        float bel = 0,a,c;
        if (d != 0)
            bel = (float) confArray[0][0] / (confArray[0][0] + confArray[0][1] + confArray[0][2]);

        System.out.println("Precision aviation - " + bel);
        d = (confArray[0][0] + confArray[1][0] + confArray[2][0]);
        float re = 0;
        if (d != 0)
            re = (float) confArray[0][0] / (confArray[0][0] + confArray[1][0] + confArray[2][0]);
        System.out.println("Recall aviation - " + re);
        float f1 = 0;
        a=bel+re;
        c=bel*re;
        if (a != 0)
            f1 = 2 * (c / a);
        System.out.println("F1 score aviation - " + f1);

        System.out.println();
        d = (confArray[1][0] + confArray[1][1] + confArray[1][2]);
        bel = 0;
        if (d != 0)
            bel = (float) confArray[1][1] / (confArray[1][0] + confArray[1][1] + confArray[1][2]);
        System.out.println("Precision disease - " + bel);
        d = confArray[0][1] + confArray[1][1] + confArray[2][1];
        re = 0;
        if (d != 0)
            re = (float) confArray[1][1] / (confArray[0][1] + confArray[1][1] + confArray[2][1]);
        System.out.println("Recall disease - " + re);
        f1 = 0;
        a=bel+re;
        c=bel*re;
        if (a != 0)
            f1 = 2 * (c / a);
        System.out.println("F1 score disease - " + f1);

        System.out.println();
        bel = 0;
        d = confArray[2][0] + confArray[2][1] + confArray[2][2];
        if (d != 0)
            bel = (float) confArray[2][2] / (confArray[2][0] + confArray[2][1] + confArray[2][2]);
        System.out.println("Precision bank - " + bel);
        re = 0;
        d = confArray[0][2] + confArray[1][2] + confArray[2][2];
        if (d != 0)
            re = (float) confArray[2][2] / (confArray[0][2] + confArray[1][2] + confArray[2][2]);
        System.out.println("Recall bank - " + re);
        f1 = 0;
        a=bel+re;
        c=bel*re;
        if (a != 0)
            f1 = 2 * (c/a);
        System.out.println("F1 score bank - " + f1);
    }
}