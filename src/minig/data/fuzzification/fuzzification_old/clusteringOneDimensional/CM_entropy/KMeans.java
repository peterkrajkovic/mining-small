/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.CM_entropy;

import static projectutils.ProjectUtils.formFrom0To1;
import java.util.LinkedList;
import java.util.List;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.NumericAttr;

/**
 *
 * @author jrabc
 */
public class KMeans {

    private List<Double> values;
    private int Q;
    private double limit = 1000;
    private List<Cluster> clusters;
    private FuzzyAttr outputAttr;
    
    private int dataCount;

    public KMeans(List<Double> values, int Q, FuzzyAttr outputAttr) {
        this.values = formFrom0To1(values, true);
     // this.values= values;
        this.outputAttr = outputAttr;
        this.Q = Q;
    }

    public KMeans(NumericAttr attr, int Q, FuzzyAttr outputAttr) {
        this.values = formFrom0To1(values, true);
        this.outputAttr = outputAttr;
        this.Q = Q;
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    public double getLimit() {
        return limit;
    }

    public int getDataCount() {
        return dataCount;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public List<Cluster> clustering() {
        dataCount = values.size();
        clusters = new LinkedList<>();
        //vytvorenie intervalov + vyber centier
        for (int i = 1; i <= Q; i++) {  //musi ist od 1, pre vypocet centra podla vzorca
            clusters.add(new Cluster(i, Q, this));
            //clusters.add(new Cluster(values.get(i), this));  // ina strategia, je tu moznost ako centrum vybrat nahodny prvok z listu, potom netreba normalizvaciu do intervalu 0 az 1 a asi aj zoradit centra
        }
        boolean isFinish;
        do {
            clusters.parallelStream().forEach(cluster -> cluster.clearData());
            for (int i = 0; i < values.size(); i++) {
                double x = values.get(i);
                addNumberToCorrectInterval(x, outputAttr.getRow(i), i);
            }
            isFinish = updateCenters(clusters);
        } while (isFinish && 0 < limit--);//zmena centier, a ak bola zmena tak da false
        values = null;
        return clusters;
    }

    private void addNumberToCorrectInterval(double number, List<Double> outputAttrRow, int indexInDataset) {
        double minDistance = Double.MAX_VALUE;
        Cluster cluster = null;
        for (Cluster c : clusters) {
            double distance = c.getEuclideDistance(number);
            if (distance < minDistance) {
                minDistance = distance;
                cluster = c;
            }
        }
        cluster.add(new ClusterPair(outputAttrRow, number, indexInDataset));
    }

    /**
     *
     * @param clusters
     * @return true if any of centers has been not changed
     */
    private boolean updateCenters(List<Cluster> clusters) {
        boolean changed = false;
        for (Cluster cluster : clusters) {
            boolean centerChanged = cluster.updateCenter();
            if (centerChanged) {
                changed = true;
            }
        }
        return changed;
    }

    public FuzzyAttr getOutputAttr() {
        return outputAttr;
    }

    public double getEntropy(FuzzyAttr inputattr) {
        double e = 0;
        for (Cluster cluster : clusters) {
//            if (clusters.size() == 3) {
//                System.out.println(cluster.getEntropy(inputattr));
//            }
            e += cluster.getEntropy(inputattr);
        }
        return e;
    }

    public double getWeightedEntropy(FuzzyAttr inputattr) {
        double e = 0;
        for (Cluster cluster : clusters) {
            e += cluster.getWeightEntropy(inputattr);
        }
        return e;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Cluster cluster : clusters) {
            sb.append(cluster.toFullString());
        }
        return sb.toString();
    }

    public double getIndexCalinskiHarabasz() {
        double N = this.getDataCount();
        double C = this.getClusterCount();
        double w = (N - C) / (C - 1);
        double U = 0;
        for (Double value : values) {
            U += value;
        }
        U = U / getDataCount();
        double menovatel = 0;
        double citatel = 0;
        for (Cluster cluster : clusters) {
            citatel += cluster.getWGGS();
            menovatel += Math.abs(cluster.getCenter() - U);
        }
        return w * (menovatel / citatel);
    }

    public double getClusterCount() {
        return this.getClusters().size();
    }

    public static void main(String[] args) {
        List<Double> d = new LinkedList<Double>() {
            {
                add(10d);
                add(12d);
                add(16d);
                add(12d);
                add(18d);
                add(11d);
                add(12d);
                add(13d);
                add(14d);
                add(14d);
                add(15d);
                add(110d);
                add(112d);
                add(116d);
                add(112d);
                add(118d);
                add(111d);
                add(112d);
                add(113d);
                add(114d);
                add(114d);
                add(115d);
            }
        };
        FuzzyAttr a = new FuzzyAttr("", "", "");
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.2, 0.9);
        a.addFuzzyRow(0.9, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        a.addFuzzyRow(0.1, 0.9);
        KMeans al = new KMeans(d, 2, a);
        al.clustering();
        al.getClusters().forEach(v -> System.out.println(v.getEntropy(a)));

        // System.out.println(al.toString());
    }

}
