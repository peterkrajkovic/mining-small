/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old;

import java.util.LinkedList;
import java.util.List;
import minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.Cluster;
import static projectutils.ProjectUtils.isBetween;

/**
 *
 * @author jrabc
 */
public class GaussianFuzzificator implements Fuzzyficator {

    private double pow = 2;
    private List<Double> centers = new LinkedList<>();

    public GaussianFuzzificator(List<Cluster> clusters) {
        clusters.forEach(v -> centers.add(v.getCenter()));
    }

    @Override
    public List<Double> getFuzzyTerm(double number) {
        double x = number;
        List<Double> membershipValues = new LinkedList<>();
        for (int q = 0; q < centers.size(); q++) {
            if (q == 0) {
                firstTerm(x, membershipValues);
            } else if (q > 0 && q < centers.size() - 1) {
                middleTerm(x, q, membershipValues);
            } else {
                lastTerm(x, q, membershipValues);
            }
        }
        return membershipValues;
    }

    private double getTerm(double x, double leftC, double rightC) {
        double middle = (leftC + rightC) / 2;
        if (x <= leftC) {
            return 1;
        } else if (isBetween(leftC, x, middle)) {
            double zl = ((x - leftC) / (rightC - leftC));
            double d = 1 - 2 * (Math.pow(zl, pow));
            return d;
        } else if (isBetween(middle, x, rightC)) {
            double zl = ((x - rightC) / (rightC - leftC));
            double d = 2 * (Math.pow(zl, pow));
            return d;
        } else {
            return 0;
        }
    }

    private void firstTerm(double x, List<Double> membershipValues) {
        final double leftC = centers.get(0);
        final double rightC = centers.get(1);
        membershipValues.add(getTerm(x, leftC, rightC));
    }

    private void middleTerm(double x, int q, List<Double> membershipValues) {
        final Double leftC = centers.get(q - 1);
        final Double center = centers.get(q);
        final Double rightCenter = centers.get(q + 1);
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
        final Double centerL = centers.get(Q - 1);
        final Double centerR = centers.get(Q);
        if (x <= centerL) {
            membershipValues.add(0d);
        } else if (isBetween(centerL, x, centerR)) {
            membershipValues.add(1 - getTerm(x, centerL, centerR));
        } else if (x >= centerR) {
            membershipValues.add(1d);
        }
    }

    @Override
    public int getTermsCount() {
        return centers.size();
    }
}
