/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.FCM;

import static projectutils.ProjectUtils.formFrom0To1;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import minig.data.core.attribute.FuzzyAttr;

/**
 *
 * @author jrabc
 */
public class FCM {

    private List<Double> values;
    private int k = 0; // iteration counter
    private ArrayList<FCluster> clusters;
    private double oldMax = Double.MIN_VALUE;
    private int maxIteration = 10000;
    private double m = 1;

    public FCM(List<Double> values, int clusterCount, int maxIterations) {
        init(values, clusterCount, maxIterations);
    }

    public void setM(double m) {
        this.m = m;
    }

    public List<Double> getValues() {
        return values;
    }

    public double getM() {
        return m;
    }

    public FCM(List<Double> values, int clusterCount) {
        init(values, clusterCount, maxIteration);
    }

    public FCM(List<Double> values) {
        init(values, 3, 10000);
    }

    public void setMaxIteration(int maxIteration) {
        this.maxIteration = maxIteration;
    }

    private void init2(List<Double> values1, int clusterCount, int maxIterations) {
        this.values = values1;
        this.maxIteration = maxIterations;
        clusters = new ArrayList<>(clusterCount);
        for (int q = 1; q <= clusterCount; q++) {
            double center = ((double) (q - 1)) / (clusterCount - 1);
            clusters.add(new FCluster(this, center));
        }
    }

    private void init(List<Double> values1, int clusterCount, int maxIterations) {
        int step = values1.size() / clusterCount;
        if (step <= 1) {
            step = 2;
            clusterCount = 2;
        }
        this.values = values1;
        this.maxIteration = maxIterations;
        clusters = new ArrayList<>(clusterCount);
        for (int q = 1; q <= clusterCount; q++) {
            double center = ((double) (q - 1)) / (clusterCount - 1);
            clusters.add(new FCluster(this, center));
        }
    }

    public void print() {
        for (int i = 0; i < values.size(); i++) {
            this.printPartitionRow(i);
            System.out.println("--------");
        }
    }

    public List<FCluster> getClusters() {
        return clusters;
    }

    public int getClusterCount() {
        return clusters.size();
    }

    public void clustering() {
        List<Double> oldCenters = getCenters();
        k = 0;
        while (!isFinish(oldCenters)) {
            prepareIteration();
            assignValuesToClusters();
            updateCenters();
        }
    }

    /**
     * assign values to the clusters and update centers
     */
    protected void assignValuesToClusters() {
        for (FCluster cluster : clusters) {
            cluster.addAll(values);
        }
    }

    public List<Double> getPartitions(int index) {
        List<Double> partitions = new ArrayList<>(clusters.size());
        for (FCluster cluster : clusters) {
            partitions.add(cluster.getPartitions().get(index));
        }
        return partitions;
    }

    public List<Double> getPartitions2(double val) {
        List<Double> partitions = new ArrayList<>(clusters.size());
        for (FCluster cluster : clusters) {
            partitions.add(cluster.getMembershipDegree(val));
        }
        return partitions;
    }

    protected void updateCenters() {
        for (FCluster cluster : clusters) {
            cluster.updateCenter();
        }
    }

    public List<Double> getCenters() {
        List<Double> centers = new ArrayList<>(clusters.size());
        for (FCluster cluster : clusters) {
            centers.add(cluster.getCenter());
        }
        return centers;
    }

    protected void prepareIteration() {
        k++;
        for (FCluster cluster : clusters) {
            cluster.prepareCluster();
        }
    }

    public int getFinishedIterations() {
        return k;
    }

    public int getMaxIteration() {
        return maxIteration;
    }

    protected boolean isFinish(List<Double> oldCenters) {
        if (maxIteration <= k) {
            return true;
        }
        if (k < 1) {
            return false;
        }
        double max = Double.MIN_VALUE;
        for (int i = 0; i < oldCenters.size(); i++) {
            double oldCenter = oldCenters.get(i);
            double newCenter = clusters.get(i).getCenter();
            double distance = Math.abs(oldCenter - newCenter);
            if (distance > max) {
                max = distance;
            }
        }
        boolean finish = max <= oldMax;
        oldMax = max;
        return finish;
    }

    protected void restIterations() {
        this.k = 0;
    }

    public void printPartitionRow(int i) {
        for (FCluster cluster : clusters) {
            System.out.print(cluster.getPartitions().get(i) + "  ");
        }
        System.out.println("\n");
    }

    public static void main(String[] args) {
        FCM pac = new FCM(new LinkedList<Double>() {
            {
                add(20d);
                add(20d);
                add(20d);
                add(21d);
                add(20d);
                add(21d);
                //  add(10000d);
                add(1.0);
                add(0.3);
                add(0.1);
                add(0.4);
                add(0.1);
                add(0.4);
                add(0.4);
                add(0.4);
                add(0.4);
                add(0.4);
                add(0.4);
            }
        }, 3);
        pac.setM(0.5);
        pac.clustering();
        for (int i = 0; i < 17; i++) {
            pac.printPartitionRow(i);
            System.out.println("--------");
        }
    }

    public double getEntopy(FuzzyAttr attr) {
        double e = 0;
        for (FCluster cluster : clusters) {
            e += cluster.getEntropy(attr);
        }
        return e;
    }
}
