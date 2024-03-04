/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.functionsset;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jrabc
 */
public class Sin implements FuzzyMapper {

    private List<Double> intervals;

    public Sin(List<Double> centers) {
        this.intervals = centers;
    }

    public Sin() {
    }

    @Override
    public List<Double> getFuzzyTerm(double number) {
        double x = number;
        ArrayList<Double> membershipValues = new ArrayList<>(intervals.size());
        for (int q = 0; q < intervals.size(); q++) {
            if (q == 0) {
                double d = firstTerm(x);
                membershipValues.add(sinFunction(d));
            } else if (q > 0 && q < intervals.size() - 1) {
                double d = middleTerm(x, q);
                membershipValues.add(sinFunction(d));
            } else {
                double d = lastTerm(x, q);
                membershipValues.add(sinFunction(d));
            }
        }
        return membershipValues;
    }

    public List<Double> getIntervals() {
        return intervals;
    }

    private final double t = Math.PI / 2;

    private double sinFunction(double x) {
        return (sin(2 * x * t - t) + sin(t)) / (2 * sin(t));
    }

    private Double firstTerm(double x) {
        final Double rightCenter = intervals.get(0);
        final Double leftCenter = intervals.get(1);
        if (x <= rightCenter) {
            return 1d;
        } else if (rightCenter < x && x <= leftCenter) {
            return (leftCenter - x) / (leftCenter - rightCenter);
        } else if (x > leftCenter) {
            return (0d);
        }
        return 0d;
    }

    private Double middleTerm(double x, int q) {
        final Double leftC = intervals.get(q - 1);
        final Double center = intervals.get(q);
        final Double rifgtC = intervals.get(q + 1);
        if (x <= leftC) {
            return (0d);
        } else if (leftC < x && x <= center) {
            double d = ((x - leftC) / (center - leftC));
            return (d);
        } else if (center < x && x <= rifgtC) {
            double d = ((rifgtC - x) / (rifgtC - center));
            return (d);
        } else {
            return (0d);
        }
    }

    private Double lastTerm(double x, int Q) {
        final Double center = intervals.get(Q);
        final Double centerl = intervals.get(Q - 1);
        if (x <= centerl) {
            return (0d);
        } else if (centerl < x && x <= center) {
            double d = (x - center) / (center - centerl);
            return (1 + d);
        } else if (x >= center) {
            return (1d);
        }
        return (0d);
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

    public static void main(String[] args) {
        FuzzyMapper zs = new ZShaped(Arrays.asList(1d, 10d, 15d, 20d));
        FuzzyMapper tf = new Triangular(Arrays.asList(1d, 10d, 15d, 20d));
        FuzzyMapper gf = new Gaussian(Arrays.asList(0d, 11d, 17d, 44d));
        FuzzyMapper fcmf = new Sin(Arrays.asList(1d, 11d, 17d, 44d));
        //  FuzzyMapper mf = new Fcmf(Arrays.asList(1d, 11d, 17d, 44d));
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
