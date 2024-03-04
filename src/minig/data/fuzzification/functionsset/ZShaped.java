/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.functionsset;

import static projectutils.ProjectUtils.isBetween;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jrabc
 */
public class ZShaped implements FuzzyMapper {

    private List<Double> intervals;
    private double pow = 2;

    public ZShaped(List<Double> centers) {
        this.intervals = centers;
    }

    public ZShaped() {
    }

    @Override
    public List<Double> getFuzzyTerm(double number) {
        double x = number;
        List<Double> membershipValues = new LinkedList<>();
        for (int q = 0; q < intervals.size(); q++) {
            if (q == 0) {
                firstTerm(x, membershipValues);
            } else if (q > 0 && q < intervals.size() - 1) {
                middleTerm(x, q, membershipValues);
            } else {
                lastTerm(x, q, membershipValues);
            }
        }
        return membershipValues;
    }

    public List<Double> getIntervals() {
        return intervals;
    }

    private double getTerm(double x, double leftC, double rightC) {
        double middle = (leftC + rightC) / 2;
        if (x <= leftC) {
            return 1;
        } else if (isBetween(leftC, x, middle)) {
            double zl = ((x - leftC) / (rightC - leftC));
            double d = 1 - 0.5 * (Math.pow(zl, pow));
            return d;
        } else if (isBetween(middle, x, rightC)) {
            double zl = ((x - rightC) / (rightC - leftC));
            double d = 0.5 * (Math.pow(zl, pow));
            return d;
        } else {
            return 0;
        }
    }

    private void firstTerm(double x, List<Double> membershipValues) {
        final double leftC = intervals.get(0);
        final double rightC = intervals.get(1);
        membershipValues.add(getTerm(x, leftC, rightC));
    }

    private void middleTerm(double x, int q, List<Double> membershipValues) {
        final Double leftC = intervals.get(q - 1);
        final Double center = intervals.get(q);
        final Double rightCenter = intervals.get(q + 1);
        if (x <= leftC) {
            membershipValues.add(0d);
        } else if (isBetween(leftC, x, center)) {
            membershipValues.add(1 - getTerm(x, leftC, center));
        } else if (isBetween(center, x, rightCenter)) {
            membershipValues.add(getTerm(x, center, rightCenter));
        } else {
            membershipValues.add(0d);
        }
    }

    private void lastTerm(double x, int Q, List<Double> membershipValues) {
        final Double centerL = intervals.get(Q - 1);
        final Double centerR = intervals.get(Q);
        if (x <= centerL) {
            membershipValues.add(0d);
        } else if (isBetween(centerL, x, centerR)) {
            membershipValues.add(1 - getTerm(x, centerL, centerR));
        } else if (x >= centerR) {
            membershipValues.add(1d);
        }
    }

    public static void main(String[] args) {
        FuzzyMapper zs = new ZShaped(Arrays.asList(1d, 10d, 15d, 20d));
        FuzzyMapper tf = new Triangular(Arrays.asList(1d, 10d, 15d, 20d));
        FuzzyMapper gf = new Gaussian(Arrays.asList(0d, 10d, 17d, 20d));
        FuzzyMapper fcmf = new Fcmf(Arrays.asList(0d, 10d, 17d, 20d));
        DecimalFormat df = new DecimalFormat("0.000 ");
        for (double i = 0; i <= 20; i += 0.01) {
            //    System.out.println(i);
//            zs.getFuzzyTerm(i).forEach(v->System.out.print(df.format(v)));
//            System.out.println("");
//            tf.getFuzzyTerm(i).forEach(v->System.out.print(df.format(v)));
//            System.out.println("");
            System.out.println("");
            System.out.println(i);
            fcmf.getFuzzyTerm(i).forEach(v -> System.out.print(df.format(v)));

            //  System.out.println("-------------------------------------------");
        }
    }

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

}
