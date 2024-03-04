/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.functionsset;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jrabc
 */
public class Triangular implements FuzzyMapper {

    private List<Double> intervals;

    public Triangular(List<Double> centers) {
        this.intervals = centers;
    }

    public Triangular() {
    }

    @Override
    public List<Double> getFuzzyTerm(double number) {
        double x = number;
        ArrayList<Double> membershipValues = new ArrayList<>(intervals.size());
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
        final Double leftC = intervals.get(q - 1);
        final Double center = intervals.get(q);
        final Double rifgtC = intervals.get(q + 1);
        if (x <= leftC) {
            membershipValues.add(0d);
        } else if (leftC < x && x <= center) {
            double d = ((x - leftC) / (center - leftC));
            membershipValues.add(d);
        } else if (center < x && x <= rifgtC) {
            double d = ((rifgtC - x) / (rifgtC - center));
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
            double d = (x - center) / (center - centerl);
            membershipValues.add(1 + d);
        } else if (x >= center) {
            membershipValues.add(1d);
        }
    }

    @Override
    public void setIntervals(List<Double> intervals) {
        this.intervals = intervals;
    }

    @Override
    public List<Double> getIntervals() {
        return intervals;
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
