/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.functionsset;

import projectutils.ProjectUtils;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jrabc
 */
public class Hard implements FuzzyMapper {

    private List<Double> intervals;

    public Hard(List<Double> centers) {
        this.intervals = centers;
    }

    public Hard() {
    }

    public List<Double> getIntervals() {
        return intervals;
    }

    public String getName(int intervalIndex) {
        if (intervalIndex == 0) {
            return "-inf, " + ProjectUtils.formatDouble(intervals.get(0));
        } else if (intervalIndex == intervals.size()) {
            return ProjectUtils.formatDouble(intervals.get(intervals.size() - 1)) + ", inf";
        } else {
            double x = intervals.get(intervalIndex - 1);
            double y = intervals.get(intervalIndex);
            return ProjectUtils.formatDouble(x) + ", " + ProjectUtils.formatDouble(y);
        }
    }

    @Override
    public List<Double> getFuzzyTerm(double number) {
        double x = number;
        List<Double> membershipValues = new ArrayList<>(intervals.size());
        if (ProjectUtils.isBetweenL(Double.NEGATIVE_INFINITY, x, intervals.get(0))) {
            membershipValues.add(1d);
        } else {
            membershipValues.add(0d);
        }
        for (int q = 1; q < intervals.size(); q++) {
            if (ProjectUtils.isBetweenL(intervals.get(q - 1), x, intervals.get(q))) {
                membershipValues.add(1d);
            } else {
                membershipValues.add(0d);
            }
        }
        if (ProjectUtils.isBetweenL(Double.NEGATIVE_INFINITY, x, intervals.get(intervals.size() - 1))) {
            membershipValues.add(0d);
        } else {
            membershipValues.add(1d);
        }
        return membershipValues;
    }

    @Override
    public void setIntervals(List<Double> intervals) {
        this.intervals = intervals;
    }

    @Override
    public int getTermsCount() {
        return intervals.size() + 1;
    }

    @Override
    public int getTermsCount(int length) {
        return length;
    }

    public static void main(String[] args) {
        Hard fcmf = new Hard(Arrays.asList(1d, 11d, 17d, 40d));
        DecimalFormat df = new DecimalFormat("0.000 ");
        for (double i = 0; i <= 44; i += 0.01) {
            System.out.print(df.format(i) + "   ");
//            zs.getFuzzyTerm(i).forEach(v->System.out.print(df.format(v)));
//            System.out.println("");
//            tf.getFuzzyTerm(i).forEach(v->System.out.print(df.format(v)));
//            System.out.println("");

            fcmf.getFuzzyTerm(i).forEach(v -> System.out.print(df.format(v)));
            System.out.println("");
            //  System.out.println("-------------------------------------------");
        }
    }

    @Override
    public int getMinInputTermsNumber() {
        return 1;
    }

}
