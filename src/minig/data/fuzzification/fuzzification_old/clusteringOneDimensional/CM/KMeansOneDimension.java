/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.CM;

import java.util.ArrayList;
import java.util.List;
import minig.data.core.attribute.NumericAttr;
import projectutils.ProjectUtils;
import projectutils.stat.Max;
import projectutils.stat.Min;

/**
 *
 * @author jrabc
 */
public class KMeansOneDimension {

    private List<Double> values;
    private int Q;
    private double limit = 10000;
    private List<Cluster> clusters;
    
    private double min;
    private double max;
    

    public KMeansOneDimension(List<Double> values, int Q) {
        max = Max.from(values);
        min = Min.from(values);
        this.values = values;
        this.Q = Q;
    }

    public KMeansOneDimension(NumericAttr attr, int Q) {
        this.values = attr.getValues();
        this.Q = Q;
        min = attr.getStat().getMin();
        max = attr.getStat().getMax();
    }

    public List<Double> getValues() {
        return values;
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    public double getLimit() {
        return limit;
    }

    public int getDataCount() {
        return this.values.size();
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public List<Cluster> clustering() {
        List<Cluster> intervals = new ArrayList<>(Q);
        for (int i = 0; i < Q; i++) {  
            intervals.add(new Cluster(min, max, i, Q, this));
        }
        int iterationsCount = 0;
        do {
            iterationsCount++;
            intervals.forEach(cluster -> cluster.clearData());
            for (int i = 0; i < values.size(); i++) {
                Double x = values.get(i);
                Cluster.addNumberToCorrectInterval(intervals, x, i);
            }
        } while (continueAfterCenterValidation(intervals) && iterationsCount < limit);//zmena centier, a ak bola zmena tak da false
        clusters = intervals;
        return intervals;
    }

    public static boolean continueAfterCenterValidation(List<Cluster> intervals) {
        boolean changed = false;
        for (Cluster interval : intervals) {
            boolean centerChanged = interval.updateCenter();
            if (centerChanged) {
                changed = true;
            }
        }
        return changed;
    }
}
