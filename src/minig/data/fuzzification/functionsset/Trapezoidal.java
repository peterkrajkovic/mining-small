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
public class Trapezoidal implements FuzzyMapper {

    private List<Double> intervals;

    /**
     * 5 centers are needed.
     *
     * @param centers
     */
    public Trapezoidal(List<Double> centers) {
        if (centers.size() < 4) {
            //neparny pocet centier
            throw new Error("Trapezoidal fuzzy mapper needs at least 5 centers");
        }
        this.intervals = centers;
    }

    public Trapezoidal() {
    }

    @Override
    public List<Double> getFuzzyTerm(double number) {
        double x = number;
        List<Double> membershipValues = new LinkedList<>();
        for (int q = 0; q < intervals.size() - 1; q++) {
            if (q == 0) {
                firstTerm(x, membershipValues);
            } else if (q < intervals.size() - 2) {
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

    private void firstTerm(double x, List<Double> membershipValues) {
        final Double rightCenter = intervals.get(0);
        final Double leftCenter = intervals.get(1);
        if (x <= rightCenter) {
            membershipValues.add(1d);
        } else if (rightCenter < x && x <= leftCenter) {
            double d = ((leftCenter - x) / (leftCenter - rightCenter));
            membershipValues.add(d);
        } else if (x > leftCenter) {
            membershipValues.add(0d);
        }
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
            membershipValues.add((x - c1) / (c2 - c1));
        } else if (isBetween(c2, x, c3)) {
            membershipValues.add(1d);
        } else if (isBetween(c3, x, c4)) {
            membershipValues.add(1 - ((x - c3) / (c4 - c3)));
        } else {
            membershipValues.add(0d);
        }
    }

    private void lastTerm(double x, int q, List<Double> membershipValues) {
        final Double center = intervals.get(q);
        final Double centerl = intervals.get(q - 1);
        if (x <= centerl) {
            membershipValues.add(0d);
        } else if (centerl < x && x <= center) {
            double d = (x - centerl) / (center - centerl);
            membershipValues.add(d);
        } else if (x >= center) {
            membershipValues.add(1d);
        }
    }

    public static void main(String[] args) {
        FuzzyMapper zs = new ZShaped(Arrays.asList(1d, 10d, 15d, 20d));
        FuzzyMapper tf = new Triangular(Arrays.asList(1d, 10d, 15d, 20d));
        FuzzyMapper gf = new Gaussian(Arrays.asList(0d, 10d, 17d, 20d));
        FuzzyMapper tp = new Trapezoidal(Arrays.asList(0d, 3d, 10d, 20d, 29d));
        DecimalFormat df = new DecimalFormat("0.00 ");
        for (double i = 0; i <= 60; i += 0.1) {
            //    System.out.println(i);
//            zs.getFuzzyTerm(i).forEach(v->System.out.print(df.format(v)));
//            System.out.println("");
//            tf.getFuzzyTerm(i).forEach(v->System.out.print(df.format(v)));
//            System.out.println("");
            tp.getFuzzyTerm(i).forEach(v -> System.out.print(df.format(v)));
            System.out.println("");
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
