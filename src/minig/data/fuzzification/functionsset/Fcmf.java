/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.functionsset;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jrabc
 */
public class Fcmf implements FuzzyMapper {

    private List<Double> intervals;
    private double m = 2;

    public Fcmf(List<Double> centers) {
        this.intervals = centers;
    }

    public Fcmf(double m) {
        this.m = m;
    }

    public Fcmf() {
    }

//    @Override
//    public List<Double> getFuzzyTerm(double x) {
//        double totalDist = 0;
//        List<Double> ret = new ArrayList<>(intervals.size());
//        for (int j = 0; j < intervals.size(); j++) {
//            double c = intervals.get(j);
//            double dist = getDist(x, c);
//            dist = dist == 0 ? 1 / 0.000000001 : 1 / dist;
//            double u = dist;
//            totalDist = totalDist + dist;
//            ret.add(u);
//        }
//        for (int j = 0; j < intervals.size(); j++) {
//            ret.set(j, ret.get(j) / totalDist);
//        }
//        return ret;
//    }
    @Override
    public List<Double> getFuzzyTerm(double x) {
        double c, dist, totalDist = 0;
        double[] ret = new double[intervals.size()];
        final int size = intervals.size();
        for (int j = 0; j < size; j++) {
            c = intervals.get(j);
            dist = getDist(x, c);
            if (dist != 0) {
                dist = 1 / dist;
                final double pow = Math.pow(dist, m);
                totalDist += pow;
                ret[j] = pow;
            } else {
                ret[j] = 0;
            }
        }
        for (int j = 0; j < size; j++) {
            ret[j] = ret[j] / totalDist;
        }
        return new projectutils.structures.BoxedDoubleArray(ret);
    }

    private double getDist(double x, double c) {
        if (x == c) {
            return 0;
        }
        double abs = Math.abs(x - c);
        return abs;
    }

    public List<Double> getIntervals() {
        return intervals;
    }

    @Override
    public void setIntervals(List<Double> intervals) {
        this.intervals = intervals;
    }

    @Override
    public int getTermsCount() {
        return intervals.size();
    }

    @Override
    public int getMinInputTermsNumber() {
        return 2;
    }

    public void setFuzziness(double m) {
        this.m = m;
    }

    public static void main(String[] args) {
        Fcmf fcmf = new Fcmf(Arrays.asList(1d, 11d, 17d, 44d));
        fcmf.setFuzziness(2);
        DecimalFormat df = new DecimalFormat("0.000 ");
        for (double i = 0; i <= 44; i += 0.01) {
            //    System.out.println(i);
//            zs.getFuzzyTerm(i).forEach(v->System.out.print(df.format(v)));
//            System.out.println("");
//            tf.getFuzzyTerm(i).forEach(v->System.out.print(df.format(v)));
//            System.out.println("");
            System.out.println("");
            fcmf.getFuzzyTerm(i).forEach(v -> System.out.print(df.format(v)));
            //      System.out.println("");
            //      mf.getFuzzyTerm(i).forEach(v -> System.out.print(df.format(v)));
        }
    }

}
