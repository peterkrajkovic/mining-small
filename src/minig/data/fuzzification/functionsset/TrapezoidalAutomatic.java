/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.functionsset;

import static projectutils.ProjectUtils.isBetween;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jrabc
 */
public class TrapezoidalAutomatic implements FuzzyMapper {

    private List<Double> intervals = new ArrayList<>();
    private double m = 2;

    public TrapezoidalAutomatic(List<Double> centers) {
        initIntervals(centers);
    }

    private void initIntervals(List<Double> centers) {
        for (int i = 0; i < centers.size() - 1; i++) {
            double c1 = centers.get(i);
            double c4 = centers.get(i + 1);
            double mc = ((c4 - c1) / 2);
            mc = mc / m;
            double c2 = c1 + mc;
            double c3 = c4 - mc;
            intervals.add(c1);
            intervals.add(c2);
            intervals.add(c3);
            //  intervals.add(c4);
        }
        intervals.add(centers.get(centers.size() - 1));
    }

    public List<Double> getIntervals() {
        return intervals;
    }

    public TrapezoidalAutomatic() {
    }

    @Override
    public List<Double> getFuzzyTerm(double number) {
        double x = number;
        List<Double> membershipValues = new LinkedList<>();
        firstTerm(x, membershipValues);
        int termCount = intervals.size() % 2 == 0 ? intervals.size() - 1 : intervals.size() - 2;
        for (int q = 1; q < termCount;) {
            middleTerm(x, q++, membershipValues);
            q++;
        }
        lastTerm(x, intervals.size() - 1, membershipValues);
        return membershipValues;
    }

    private void firstTerm(double x, List<Double> membershipValues) {
        final Double lcenter = intervals.get(0);
        final Double rcenter = intervals.get(1);
        if (x <= lcenter) {
            membershipValues.add(1d);
        } else if (lcenter < x && x <= rcenter) {
            double d = 1 - mf(x, lcenter, rcenter);
            membershipValues.add(d);
        } else if (x > rcenter) {
            membershipValues.add(0d);
        }
    }

    private static double mf(double x, double lc, double rc) {
        return (x - lc) / (rc - lc);
    }

    private void middleTerm(double x, int q, List<Double> membershipValues) {
        double c1 = intervals.get(q - 1);
        double c2 = intervals.get(q);
        double c3 = intervals.get(q + 1);
        double c4 = intervals.get(q + 2);
        // System.out.println(c1+" " + c2 +" "+ c3 + " " + c4);
        if (x <= c1) {
            membershipValues.add(0d);
        } else if (isBetween(c1, x, c2)) {
            membershipValues.add(mf(x, c1, c2));
        } else if (isBetween(c2, x, c3)) {
            membershipValues.add(1d);
        } else if (isBetween(c3, x, c4)) {
            membershipValues.add(1 - (mf(x, c3, c4)));
        } else {
            membershipValues.add(0d);
        }
    }

    private void lastTerm(double x, int Q, List<Double> membershipValues) {
        final Double center = intervals.get(Q);
        final Double centerl = intervals.get(Q - 1);
        if (x <= centerl) {
            membershipValues.add(0d);
        } else if (centerl < x && x <= center) {
            double d = mf(x, centerl, center);
            membershipValues.add(d);
        } else if (x >= center) {
            membershipValues.add(1d);
        }
    }

    @Override
    public void setIntervals(List<Double> intervals) {
        this.intervals.clear();
        initIntervals(intervals);
    }

    public static void main(String[] args) {
        FuzzyMapper zs = new ZShaped(Arrays.asList(1d, 10d, 15d, 20d));
        FuzzyMapper tf = new Triangular(Arrays.asList(1d, 10d, 15d, 20d));
        FuzzyMapper gf = new Gaussian(Arrays.asList(0d, 11d, 17d, 44d));
        TrapezoidalAutomatic fcmf = new TrapezoidalAutomatic(Arrays.asList(1d, 11d, 17d, 44d));
        DecimalFormat df = new DecimalFormat("0.000 ");
        System.out.println(fcmf.getIntervals());
        for (double i = 0; i <= 44; i += 0.01) {
            //    System.out.println(i);
//            zs.getFuzzyTerm(i).forEach(v->System.out.print(df.format(v)));
//            System.out.println("");
//            tf.getFuzzyTerm(i).forEach(v->System.out.print(df.format(v)));
//            System.out.println("");
            System.out.println("");
            fcmf.getFuzzyTerm(i).forEach(v -> System.out.print(df.format(v)));

            //  System.out.println("-------------------------------------------");
        }
    }

    @Override
    public int getTermsCount() {
        int termCount = intervals.size() % 2 == 0 ? intervals.size() : intervals.size() - 1;
        return termCount / 2 + 1;
    }

    @Override
    public int getMinInputTermsNumber() {
        return 2;
    }

}
