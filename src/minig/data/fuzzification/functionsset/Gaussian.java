/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.functionsset;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author jrabc
 */
public class Gaussian implements FuzzyMapper {

    private List<Double> intervals;

    public Gaussian(List<Double> centers) {
        this.intervals = centers;
    }

    public Gaussian() {
    }

    public List<Double> getIntervals() {
        return intervals;
    }

    @Override
    public List<Double> getFuzzyTerm(double number) {
        double x = number;
        List<Double> membershipValues = new LinkedList<>();
        for (int q = 0; q < intervals.size(); q++) {
            if (q == 0) {
                firstTerm(x, membershipValues);
            } else if (q > 0 && q < intervals.size() - 1) {
                if (number > 30) {
                    System.out.print("");
                }
                middleTerm(x, q, membershipValues);
            } else {
                lastTerm(x, q, membershipValues);
            }
        }
        return membershipValues;
    }

    private int m = 1;

    private double getTerm(double x, int index) {
        double center = intervals.get(index + 1);
        double leftCenter = intervals.get(index);
        double w = m * (center + leftCenter);
        //    System.out.println(center + " " + leftCenter + " " + w);
        if (x <= leftCenter) {
            return 1;
        } else if (x > leftCenter && x <= center) {
            //  double d = Math.exp(-Math.pow(((x - center)), 2) / (2 * w));
            double d = Math.exp(-Math.pow(((x - center)) / (w / Math.sqrt(w)), 2));
            return (1 - d);
        } else {
            return (0d);
        }
    }

    private void firstTerm(double x, List<Double> membershipValues) {
        membershipValues.add(getTerm(x, 0));
    }

    private void middleTerm(double x, int q, List<Double> membershipValues) {
        final Double leftC = intervals.get(q - 1);
        final Double center = intervals.get(q);
        final Double rightCenter = intervals.get(q + 1);
        if (x <= leftC) {
            membershipValues.add(0d);
        } else if (isBetween(leftC, x, center)) {
            membershipValues.add(1 - getTerm(x, q - 1));
        } else if (isBetween(center, x, rightCenter)) {
            membershipValues.add(getTerm(x, q));
        } else {
            membershipValues.add(0d);
        }
    }

    private static boolean isBetween(final double leftC, double x, final double rightC) {
        return leftC < x && x <= rightC;
    }

    private void lastTerm(double x, int Q, List<Double> membershipValues) {
        final Double center = intervals.get(Q);
        final Double centerl = intervals.get(Q - 1);
        if (x <= centerl) {
            membershipValues.add(0d);
        } else if (centerl < x && x <= center) {
            double d = getTerm(x, Q - 1);
            membershipValues.add(1 - d);
        } else if (x >= center) {
            membershipValues.add(1d);
        }
    }

    public void setIntervals(List<Double> intervals) {
        this.intervals = intervals;
    }

    public static void main(String[] args) {
        FuzzyMapper zs = new ZShaped(Arrays.asList(1d, 10d, 15d, 20d));
        FuzzyMapper tf = new Triangular(Arrays.asList(1d, 10d, 15d, 20d));
        FuzzyMapper gf = new Gaussian(Arrays.asList(0d, 11d, 17d, 44d));
        FuzzyMapper fcmf = new Fcmf(Arrays.asList(0d, 10d, 17d, 20d));
        DecimalFormat df = new DecimalFormat("0.000 ");
        for (double i = 0; i <= 44; i += 0.01) {
            //    System.out.println(i);
//            zs.getFuzzyTerm(i).forEach(v->System.out.print(df.format(v)));
//            System.out.println("");
//            tf.getFuzzyTerm(i).forEach(v->System.out.print(df.format(v)));
//            System.out.println("");
            System.out.println("");
            gf.getFuzzyTerm(i).forEach(v -> System.out.print(df.format(v)));

            //  System.out.println("-------------------------------------------");
        }
    }

    @Override
    public int getMinInputTermsNumber() {
        return 2;
    }

    @Override
    public int getTermsCount() {
        return intervals.size();
    }

}
