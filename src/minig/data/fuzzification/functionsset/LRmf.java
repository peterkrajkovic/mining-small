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
 *
 * @author jrabc
 */
public class LRmf implements FuzzyMapper {

    private List<Double> intervals;

    public LRmf(List<Double> centers) {
        this.intervals = centers;
    }

    public LRmf() {
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

    private void firstTerm(double x, List<Double> membershipValues) {
        final Double lcenter = intervals.get(0);
        final Double rcenter = intervals.get(1);
        if (x <= lcenter) {
            membershipValues.add(1d);
        } else if (lcenter < x && x <= rcenter) {
            double d = mf(rcenter, x, lcenter);
            membershipValues.add(d);
        } else if (x > rcenter) {
            membershipValues.add(0d);
        }
    }

    public double mf(Double leftCenter, double x, Double rightCenter) {
         // double d = 1 / (1 - (Math.exp(Math.pow(x - rightCenter,leftCenter))));
       // double d = Math.pow(x - leftCenter, 2) / Math.pow(rightCenter - leftCenter, 2);
           double d = Math.pow((x - leftCenter) / (rightCenter - leftCenter),2);
        return d;
    }

    private void middleTerm(double x, int q, List<Double> membershipValues) {
        final Double leftC = intervals.get(q - 1);
        final Double center = intervals.get(q);
        final Double rifgtC = intervals.get(q + 1);
        if (x <= leftC) {
            membershipValues.add(0d);
        } else if (leftC < x && x <= center) {
            double d = 1 - mf(center, x, leftC);
            // double d = ((x - leftC) / (center - leftC));
            membershipValues.add(d);
        } else if (center < x && x <= rifgtC) {
            double d = mf(rifgtC, x, center);
            //double d = ((rifgtC - x) / (rifgtC - center));
            membershipValues.add(d);
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
            double d = 1 - mf(center, x, centerl);
            //  double d = (x - center) / (center - centerl);
            membershipValues.add(d);
        } else if (x >= center) {
            membershipValues.add(1d);
        }
    }

    public void setIntervals(List<Double> intervals) {
        this.intervals = intervals;
    }

    public List<Double> getIntervals() {
        return intervals;
    }
    
    public static void main(String[] args) {
        FuzzyMapper fcmf = new LRmf(Arrays.asList(1d, 11d, 17d, 44d));
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

    @Override
    public int getTermsCount() {
        return intervals.size();
    }

    @Override
    public int getMinInputTermsNumber() {
        return 2;
    }
}
