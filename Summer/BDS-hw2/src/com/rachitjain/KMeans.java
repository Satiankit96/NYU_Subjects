package com.rachitjain;

import java.io.IOException;
import java.util.Random;
import java.util.HashSet;

public class KMeans {
    private final int k;
    private final int numberOfDocs;
    private final int features;
    private final boolean L1norm;
    private double WCSS;
    private final double[][] words;
    private double[][] centroids;
    private int[] labels;
    private final double epsilon;
    private final boolean isKmeansPlusplus;

    KMeans(Builder builder) {
        k = builder.k;
        words = builder.words;
        int iterations = builder.iterations;
        isKmeansPlusplus = builder.isKmeansPlusplus;
        epsilon = builder.epsilon;
        L1norm = builder.L1norm;
        numberOfDocs = words.length;
        features = words[0].length;
        double bestWCSS = Double.POSITIVE_INFINITY;
        double[][] bestCentroids = new double[0][0];
        int[] bestAssignment = new int[0];
        for (int n = 0; n < iterations; n++) {
            cluster();
            if (WCSS < bestWCSS) {
                bestWCSS = WCSS;
                bestCentroids = centroids;
                bestAssignment = labels;
            }
        }
        labels = bestAssignment;
        centroids = bestCentroids;
        WCSS = bestWCSS;
    }

    public static class Builder {
        private final int k;
        private final double[][] words;
        private double epsilon = .001;
        private boolean L1norm = true;
        private boolean isKmeansPlusplus = false;
        private int iterations = 10;


        public Builder(int k, double[][] points) {
            HashSet<double[]> hashSet = new HashSet<>(k);
            int distinct = 0;
            for (double[] point : points) {
                if (!hashSet.contains(point)) {
                    distinct++;
                    hashSet.add(point);
                    if (distinct >= k)
                        break;
                }
            }
            this.k = k;
            this.words = points;
        }

        public Builder epsilon(double epsilon) {
            this.epsilon = epsilon;
            return this;
        }

        public Builder iterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public Builder L1norm(boolean L1norm) {
            this.L1norm = L1norm;
            return this;
        }

        public Builder isKmeansPlusplus(boolean isKmeansPlusplus) {
            this.isKmeansPlusplus = isKmeansPlusplus;
            return this;
        }

        public KMeans build() {
            return new KMeans(this);
        }
    }

    private void cluster() {
        findFirstCentroids();
        WCSS = Double.POSITIVE_INFINITY;
        double prevWCSS;
        do {
            assignLabels();
            updateCentroids();
            prevWCSS = WCSS;
            buildWCSS();
        } while (!stopClustering(prevWCSS));
    }

    private void assignLabels() {
        labels = new int[numberOfDocs];
        double tempDist;
        double minValue;
        int minLocation;
        for (int i = 0; i < numberOfDocs; i++) {
            minLocation = 0;
            minValue = Double.POSITIVE_INFINITY;
            for (int j = 0; j < k; j++) {
                tempDist = distance(words[i], centroids[j]);
                if (tempDist < minValue) {
                    minValue = tempDist;
                    minLocation = j;
                }
            }
            labels[i] = minLocation;
        }
    }

    private void updateCentroids() {
        for (int i = 0; i < k; i++)
            for (int j = 0; j < features; j++)
                centroids[i][j] = 0;

        int[] clusterSize = new int[k];
        for (int i = 0; i < numberOfDocs; i++) {
            clusterSize[labels[i]]++;
            for (int j = 0; j < features; j++)
                centroids[labels[i]][j] += words[i][j];
        }
        HashSet<Integer> emptyCentroids = new HashSet<>();
        for (int i = 0; i < k; i++) {
            if (clusterSize[i] == 0)
                emptyCentroids.add(i);

            else
                for (int j = 0; j < features; j++)
                    centroids[i][j] /= clusterSize[i];
        }
        if (emptyCentroids.size() != 0) {
            HashSet<double[]> nonemptyCentroids = new HashSet<>(k - emptyCentroids.size());
            for (int i = 0; i < k; i++)
                if (!emptyCentroids.contains(i))
                    nonemptyCentroids.add(centroids[i]);

            Random r = new Random();
            for (int i : emptyCentroids)
                while (true) {
                    int rand = r.nextInt(words.length);
                    if (!nonemptyCentroids.contains(words[rand])) {
                        nonemptyCentroids.add(words[rand]);
                        centroids[i] = words[rand];
                        break;
                    }
                }
        }
    }

    private void findFirstCentroids() {
        if (isKmeansPlusplus)
            KmeansPP();
        else
            Kmeans();
    }

    private void Kmeans() {
        centroids = new double[k][features];
        double[][] copy = words;
        Random gen = new Random();
        int rand;
        for (int i = 0; i < k; i++) {
            rand = gen.nextInt(8) + 8 * i;
            System.arraycopy(copy[rand], 0, centroids[i], 0, features);
        }
    }

    private void KmeansPP() {
        centroids = new double[k][features];
        double[] distToClosestCentroid = new double[numberOfDocs];
        double[] weightedDistribution = new double[numberOfDocs];
        Random gen = new Random();
        int choose = 0;

        for (int c = 0; c < k; c++) {
            if (c == 0)
                choose = gen.nextInt(numberOfDocs);
            else {
                for (int p = 0; p < numberOfDocs; p++) {
                    double tempDistance = cosine(words[p], centroids[c - 1]);
                    if (c == 1)
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
                for (int j = numberOfDocs - 1; j > 0; j--) {
                    if (rand > weightedDistribution[j - 1] / weightedDistribution[numberOfDocs - 1]) {
                        choose = j;
                        break;
                    } else
                        choose = 0;
                }
            }
            System.arraycopy(words[choose], 0, centroids[c], 0, features);
        }
    }

    private boolean stopClustering(double prevWCSS) {
        return epsilon > 1 - (WCSS / prevWCSS);
    }

    private double distance(double[] x, double[] y) {
        return L1norm ? cosine(x, y) : euclidean(x, y);
    }

    private double euclidean(double[] x, double[] y) {
        double dist = 0;
        for (int i = 0; i < x.length; i++)
            dist += Math.abs((x[i] - y[i]) * (x[i] - y[i]));
        dist = Math.sqrt(dist);
        return dist;
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

    private void buildWCSS() {
        double WCSS = 0;
        int assignedClust;

        for (int i = 0; i < numberOfDocs; i++) {
            assignedClust = labels[i];
            WCSS += distance(words[i], centroids[assignedClust]);
        }
        this.WCSS = WCSS;
    }

    public double[][] getCentroids() {
        return centroids;
    }

    public int[] getClusters() {
        return labels;
    }

    private static void KMeansEuclidean(int k, int featuresSize, double[][] data) {
        System.out.println("\nKMeans Euclidean");
        KMeans KclusteringE = new KMeans.Builder(k, data)
                .iterations(50)
                .isKmeansPlusplus(false)
                .L1norm(false)
                .epsilon(.001).build();
        double[][] KcentroidsE = KclusteringE.getCentroids();
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < featuresSize; j++) {
                System.out.print(KcentroidsE[i][j] + ", ");
            }
            System.out.println();
        }
        int[] clus = KclusteringE.getClusters();
        System.out.print("Cluster indices ");
        for (int value : clus) System.out.print(value + " ");
        buildConfusionMatrix(clus);
    }

    private static void KMeansCosine(int k, int featuresSize, double[][] data) {
        System.out.println("\nKMeans Cosine");
        KMeans KclusteringC = new KMeans.Builder(k, data).
                iterations(50).
                isKmeansPlusplus(false).
                L1norm(true).build();
        double[][] KcentroidsC = KclusteringC.getCentroids();
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < featuresSize; j++) {
                System.out.print(KcentroidsC[i][j] + ", ");
            }
            System.out.println();
        }
        int[] clusC = KclusteringC.getClusters();
        System.out.print("Cluster indices ");
        for (int j : clusC) System.out.print(j + " ");
        buildConfusionMatrix(clusC);
    }

    private static void KMeansPPCosine(int k, double[][] data) {
        System.out.println("\nKMeans++ using cosine");
        KMeans clustering = new KMeans.Builder(k, data)
                .iterations(50)
                .L1norm(true)
                .isKmeansPlusplus(true).build();
        double[][] centroids = clustering.getCentroids();
        for (int i = 0; i < k; i++)
            System.out.println("KMeans++ (" + centroids[i][0] + ", " + centroids[i][1] + ")");
        int[] cluspp = clustering.getClusters();
        System.out.print("Cluster indices ");
        for (int j : cluspp) System.out.print(j + " ");
        buildConfusionMatrix(cluspp);
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

    public static void buildConfusionMatrix(int[] clusters) {
        System.out.println("\n\nCalculating precision matrix of topics aviation, disease, bank");
        System.out.println("\nPredicted vs Actual");
        int[][] confusionMatrix = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
        int l = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = l; j < l + 8; j++) {
                confusionMatrix[clusters[j]][i]++;
            }
            l = (l == 0) ? 8 : 16;
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(confusionMatrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();

        float den = (confusionMatrix[0][0] + confusionMatrix[0][1] + confusionMatrix[0][2]);
        float pre = 0;
        if (den != 0)
            pre = (float) confusionMatrix[0][0] / (confusionMatrix[0][0] + confusionMatrix[0][1] + confusionMatrix[0][2]);

        System.out.println("Precision aviation - " + pre);
        den = (confusionMatrix[0][0] + confusionMatrix[1][0] + confusionMatrix[2][0]);
        float re = 0;
        if (den != 0)
            re = (float) confusionMatrix[0][0] / (confusionMatrix[0][0] + confusionMatrix[1][0] + confusionMatrix[2][0]);
        System.out.println("Recall aviation - " + re);
        float f1 = 0;
        if ((pre + re) != 0)
            f1 = 2 * ((pre * re) / (pre + re));
        System.out.println("F1 score aviation - " + f1);

        System.out.println();
        den = (confusionMatrix[1][0] + confusionMatrix[1][1] + confusionMatrix[1][2]);
        pre = 0;
        if (den != 0)
            pre = (float) confusionMatrix[1][1] / (confusionMatrix[1][0] + confusionMatrix[1][1] + confusionMatrix[1][2]);
        System.out.println("Precision disease - " + pre);
        den = confusionMatrix[0][1] + confusionMatrix[1][1] + confusionMatrix[2][1];
        re = 0;
        if (den != 0)
            re = (float) confusionMatrix[1][1] / (confusionMatrix[0][1] + confusionMatrix[1][1] + confusionMatrix[2][1]);
        System.out.println("Recall disease - " + re);
        f1 = 0;
        if ((pre + re) != 0)
            f1 = 2 * ((pre * re) / (pre + re));
        System.out.println("F1 score disease - " + f1);

        System.out.println();
        pre = 0;
        den = confusionMatrix[2][0] + confusionMatrix[2][1] + confusionMatrix[2][2];
        if (den != 0)
            pre = (float) confusionMatrix[2][2] / (confusionMatrix[2][0] + confusionMatrix[2][1] + confusionMatrix[2][2]);
        System.out.println("Precision bank - " + pre);
        re = 0;
        den = confusionMatrix[0][2] + confusionMatrix[1][2] + confusionMatrix[2][2];
        if (den != 0)
            re = (float) confusionMatrix[2][2] / (confusionMatrix[0][2] + confusionMatrix[1][2] + confusionMatrix[2][2]);
        System.out.println("Recall bank - " + re);
        f1 = 0;
        if ((pre + re) != 0)
            f1 = 2 * ((pre * re) / (pre + re));
        System.out.println("F1 score bank - " + f1);
    }
}