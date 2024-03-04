/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.FCM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jrabc
 */
public class FCMmerging extends FCM {

    private double treshold = 0.005;
    private int iterations = 100;

    public FCMmerging(List<Double> values, int clusterCount, int maxIterations) {
        super(values, clusterCount, maxIterations);
    }

    public FCMmerging(List<Double> values, int clusterCount) {
        super(values, clusterCount);
    }

    public FCMmerging(List<Double> values) {
        super(values);
    }

    @Override
    public void clustering() {
        boolean removed;
        while (iterations-- > 0 ) {
            
            super.clustering();
            removed = removeAllSmallCluster();
            if (removed == false) {
             //   break;
            }else{
                addCluster();
            }
        }
        Collections.sort(getClusters(), (o1, o2) -> {
            return Double.compare(o1.getCenter(), o2.getCenter());
        });
    }

    @Override
    protected boolean isFinish(List<Double> oldCenters) {
        return !(getFinishedIterations() < 1 || getMaxIteration() <= getFinishedIterations());
    }

    public boolean removeCluster() {
        if (getClusters().size() > 0) {
            FCluster cl = getClusters().get(0);
            for (FCluster cluster : getClusters()) {
                if (cl.getCardinality() > cluster.getCardinality()) {
                    cl = cluster;
                }
            }
            if (cl.getCardinality() / getValues().size() < treshold) {
                getClusters().remove(cl);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean removeAllSmallCluster() {
        boolean del = false;
        if (getClusters().size() > 1) {
            Iterator<FCluster> iterator = getClusters().iterator();
            while (iterator.hasNext()) {
                FCluster cl = iterator.next();
                if (cl.getCardinality() / getValues().size() < treshold) {
                    iterator.remove();
                    del = true;
                }
            }
        }
        return del;
    }

    public void addCluster() {
        double center = 0;
        for (FCluster cluster : getClusters()) {
            center += cluster.getCenter();
        }
        center /= getCenters().size();
        FCluster cls = new FCluster(this, center);
        getClusters().add(cls);
    }

    public static void main(String[] args) {
        ArrayList<Double> var = new ArrayList<Double>(); //{
//            {
//                add(1.0);
//                add(0.3);
//                add(0.1);
//                add(0.4);
//                add(0.1);
//                add(0.4);
//                add(0.4);
//                add(0.4);
//                add(0.4);
//                add(0.4);
//                add(0.4);
//            }
//        };
        for (int i = 0; i < 1000; i++) {
            var.add(Math.random());
        }
        FCMmerging pac = new FCMmerging(var, 5);
        pac.iterations = 1000;
        pac.clustering();

        FCM fcm = new FCM(var, 5);
        fcm.setMaxIteration(1000);
        fcm.clustering();

        for (int i = 0; i < var.size(); i++) {
            System.out.println( var.get(i));
            fcm.printPartitionRow(i);
            pac.printPartitionRow(i);
            System.out.println("--------------");
        }

    }
}
