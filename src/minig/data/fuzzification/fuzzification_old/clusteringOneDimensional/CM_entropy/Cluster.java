/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.CM_entropy;

import java.util.ArrayList;
import minig.data.core.attribute.FuzzyAttr;
import java.util.List;
import projectutils.ProjectUtils;

/**
 *
 * @author jrabc
 */
public class Cluster implements Comparable<Cluster>, minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.Cluster {

    private double center = 0;
    private KMeans clusterAlg;
    private List<ClusterPair> pairs;

    private int count;
    private double sum;

    /**
     * initial center ((double) (q - 1)) / (Q - 1)
     *
     * @param q
     * @param Q
     * @param clusterAlg
     * @pa
     */
    public Cluster(int q, int Q, KMeans clusterAlg) {
        init(clusterAlg);
        this.center = ((double) (q - 1)) / (Q - 1);
    }

    private void init(KMeans clusterAlg1) {
        this.clusterAlg = clusterAlg1;
        pairs = new ArrayList<>(clusterAlg1.getDataCount());
    }

    public Cluster(double center, KMeans clusterAlg) {
        this.clusterAlg = clusterAlg;
        this.center = center;
    }

    public KMeans getClusterAlg() {
        return clusterAlg;
    }

    private double getEntropy(double num) {
        return -num * ProjectUtils.log2(num);
    }

    private double getEntropy(FuzzyAttr inputAttr, int indexInputVal, int indexOutputVal) {
        double menovatel = 0;
        double citatel = 0;
        for (ClusterPair pair : pairs) {
            int index = pair.getIndexInDataset();
            double b = pair.getOutputVaules().get(indexOutputVal);
            double u = pair.getTerm(inputAttr, indexInputVal, index);
            menovatel += u * b;
            citatel += u;
        }
        if (citatel == 0) {
            return 0;
        }
        double pom = menovatel / citatel;
        // System.out.println("------------- "+ menovatel + "    "+citatel + "   "+pom);
        double e = getEntropy(pom);
        return e;
    }

    public double getEntropy(FuzzyAttr inputAttr) {
        double entropy = 0;
        for (int i = 0; i < inputAttr.getDomainSize(); i++) {
            for (int j = 0; j < clusterAlg.getOutputAttr().getDomainSize(); j++) {
                entropy += getEntropy(inputAttr, i, j);
            }
        }
        //  System.out.println(this);
        return entropy;
    }

    public double getWeightEntropy(FuzzyAttr inputAttr) {
        double datCount = clusterAlg.getDataCount();
        double datInCluster = pairs.size();
        double weight = datInCluster / datCount;
        double e = weight * getEntropy(inputAttr);
        return e;
    }

    public double getCenter() {
        return center;
    }

    public void add(ClusterPair outputValues) {
        count++;
        sum += outputValues.getX();
        this.pairs.add(outputValues);
    }

    public double getEuclideDistance(double x) {
        return Math.abs(x - center);
    }

    public boolean updateCenter() {
        double avg = sum / count;
        if (center == avg) {
            return false;
        } else {

            if (Double.isNaN(avg)) {
                center = 0;
            } else {
                center = avg;
            }
            return true;
        }
    }

    public void setCenter(double center) {
        this.center = center;
    }

    public static void addNumberToCorrectInterval(List<Cluster> clusters, double number, List<Double> outputAttrRow, int indexInDataset) {
        double minDistance = Double.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.get(i);
            double distance = cluster.getEuclideDistance(number);
            if (distance < minDistance) {
                index = i;
                minDistance = distance;
            }
        }
        clusters.get(index).add(new ClusterPair(outputAttrRow, number, indexInDataset));
    }

    /**
     * pooled within-cluster sum of squares WGSS
     *
     * @return
     */
    public double getWGGS() {
        double wggs = 0;
        for (ClusterPair pair : pairs) {
            double x = pair.getX();
            wggs += Math.abs(x - center);
        }
        return wggs;
    }

    public void clearData() {
        sum = 0;
        count = 0;
        pairs.clear();
    }

    @Override
    public int compareTo(Cluster o) {
        if (center == o.center) {
            return 0;
        } else if (center > o.center) {
            return 1;
        }
        return -1;
    }

    public String toFullString() {
        StringBuilder sb = new StringBuilder();
        for (ClusterPair pair : pairs) {
            sb.append(pair.getX()).append("->").append(pair.getOutputVaules()).append(" ").append(this.center).append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        FuzzyAttr input = new FuzzyAttr("a", "", "", "");
        input.addFuzzyRow(0.4, 0.6, 0.0);
        input.addFuzzyRow(0.2, 0.8, 0.0);
        input.addFuzzyRow(0.0, 1.0, 0.0);
        input.addFuzzyRow(0.0, 0.9, 0.1);
        input.addFuzzyRow(0.0, 0.7, 0.3);
        FuzzyAttr output = new FuzzyAttr("", "", "");
        output.addFuzzyRow(1.0, 0.0);
        output.addFuzzyRow(1.0, 0.0);
        output.addFuzzyRow(0.0, 1.0);
        output.addFuzzyRow(1.0, 0.0);
        output.addFuzzyRow(0.0, 1.0);
        // Cluster1 a = new Cluster1(0);
        //   System.out.println(a.getWeightEntrophy(input, output));

    }

}
