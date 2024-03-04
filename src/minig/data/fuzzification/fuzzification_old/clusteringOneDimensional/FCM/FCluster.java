/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.FCM;

import java.util.ArrayList;
import java.util.List;
import minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.Cluster;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.FuzzyAttr;
import projectutils.ProjectUtils;
import projectutils.structures.DoubleVector;

/**
 *
 * @author jrabc
 */
public class FCluster implements Cluster {

    private double center = 0;
    private DoubleVector partitions;
    private List<Double> values;
    private List<Double> distances;
    private FCM fcm;

    double menovatel = 0;
    double citatel = 0;
    double partitionSum = 0;

    public FCluster(FCM pac) {
        this.fcm = pac;
        init(pac.getValues().size());
    }

    public FCluster(FCM pac, double center) {
        this.fcm = pac;
        this.center = center;
        init(pac.getValues().size());
    }

    private void init(int initSize) {
        partitions = new DoubleVector(initSize);
        values = new ArrayList<>(initSize);
        distances = new ArrayList<>(initSize);
    }

    @Override
    public double getCenter() {
        return center;
    }

    public void add(double x) {
        //TODO check
        if (Double.isNaN(x)) {
            partitions.add(0d);
            values.add(x);
            return;
        }
        //TODO check
        double distance = Math.abs(x - center);
        double partition;
        distances.add(distance);
        values.add(x);
        partition = getFCMu(x);
        partitions.add(partition);
        partitionSum += partition;
        double powPartition = Math.pow(partition, fcm.getM());
        menovatel += powPartition * x;
        citatel += powPartition;
    }

    public double getMembershipDegree(double x) {
        return getFCMu(x);
    }

    public double getClusterCardinality() {
        return partitionSum / partitions.size();
    }

    public void addAll(List<Double> values) {
        values.forEach(v -> add(v));
    }

    public double getCardinality() {
        return partitions.sum();
    }

    private double getFCMu(double x) {
        double distance = Math.abs(x - center);
        if (distance == 0) {
            return 1;
        }
        double menovatel = 1 / distance;
        double citatel = 0;
        for (FCluster cluster : fcm.getClusters()) {
            double kCenter = cluster.getCenter();
            final double dist = Math.abs(x - kCenter);
            citatel += 1 / dist;
        }
        double fcm = menovatel / citatel;
        return fcm;
    }

    private double getFCMu2(double x) {
        double distance = Math.abs(x - center);
        if (distance == 0) {
            return 1;
        }
        double sum = 0;
        for (FCluster cluster : fcm.getClusters()) {
            double kCenter = cluster.getCenter();
            double delitel = Math.abs(x - kCenter);
            sum += Math.pow(distance / delitel, 2 / (fcm.getM() - 1));
        }
        double u = 1 / sum;
        return u;
    }

    public void prepareCluster() {
        values.clear();
        partitions.clear();
        distances.clear();
        citatel = 0;
        menovatel = 0;
    }

    public void updateCenter() {
        center = menovatel / citatel;
        citatel = 0;
        menovatel = 0;
        partitionSum = 0;
    }

    public List<Double> getPartitions() {
        return partitions;
    }

    public List<Double> getValues() {
        return values;
    }

    public List<Double> getDistances() {
        return distances;
    }

    public double getEntropy(FuzzyAttr outputAttr) {
        double e = 0;
        double cit = 0;
        double men;
        for (AttrValue value : outputAttr.getDomain()) {
            cit = 0;
            men = 0;
            List<Double> output = value.getValues();
            for (int i = 0; i < output.size(); i++) {
                double x = partitions.get(i);
                double b = output.get(i);
                //System.out.println(x +"   "+b);
                men += x * b;
                cit += x;
            }
            double D = men / cit;
            if (Double.isNaN(D)) {
                D = 0;
            }
            e += D * ProjectUtils.log2(D);
        }
        double probablilityA = -(cit / partitions.size());
        return probablilityA * e;
    }

}
