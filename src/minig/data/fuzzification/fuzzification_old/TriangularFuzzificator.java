/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old;

import java.util.ArrayList;
import java.util.List;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.Cluster;

/**
 * Yuan, Shaw 1995, Induction of fuzzy decision trees, doi
 * 10.1016/0165-0114(94)00229-Z
 *
 * @author jrabc
 */
public class TriangularFuzzificator implements Fuzzyficator {

    private List<Double> centers;

    public TriangularFuzzificator(List<Cluster> clusters) {
        centers = new ArrayList<>(clusters.size());
        clusters.forEach(v -> centers.add(v.getCenter()));
    }

    @Override
    public FuzzyAttr getFuzzyAttr(NumericAttr attr) {
        return Fuzzyficator.super.getFuzzyAttr(attr);
    }

    @Override
    public List<Double> getFuzzyTerm(double number) {
        double x = number;
        List<Double> membershipValues = new ArrayList<>(centers.size());
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

    private void firstTerm(double x, List<Double> membershipValues) {
        if (x <= centers.get(0)) {
            membershipValues.add(1d);
        } else if (centers.get(0) < x && x <= centers.get(1)) {
            double d = ((centers.get(1) - x) / (centers.get(1) - centers.get(0)));
            membershipValues.add(d);
        } else if (x > centers.get(1)) {
            membershipValues.add(0d);
        }
    }

    private void middleTerm(double x, int q, List<Double> membershipValues) {
        if (x <= centers.get(q - 1)) {
            membershipValues.add(0d);
        } else if (centers.get(q - 1) < x && x <= centers.get(q)) {
            double d = ((x - centers.get(q - 1)) / (centers.get(q) - centers.get(q - 1)));
            membershipValues.add(d);
        } else if (centers.get(q) < x && x <= centers.get(q + 1)) {
            double d = ((centers.get(q + 1) - x) / (centers.get(q + 1) - centers.get(q)));
            membershipValues.add(d);
        } else {
            membershipValues.add(0d);
        }
    }

    private void lastTerm(double x, int Q, List<Double> membershipValues) {
        if (x <= centers.get(Q - 1)) {
            membershipValues.add(0d);
        } else if (centers.get(Q - 1) < x && x <= centers.get(Q)) {
            double d = (x - centers.get(Q)) / (centers.get(Q) - centers.get(Q - 1));
            membershipValues.add(1 + d);
        } else if (x >= centers.get(Q)) {
            membershipValues.add(1d);
        }
    }

    @Override
    public int getTermsCount() {
        return centers.size();
    }

}
