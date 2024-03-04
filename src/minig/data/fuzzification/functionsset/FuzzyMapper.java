/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.functionsset;

import java.util.List;
import projectutils.ProjectUtils;

/**
 *
 * @author jrabc
 */
public interface FuzzyMapper {

    public List<Double> getFuzzyTerm(double number);

    public int getTermsCount();

    public default int getTermsCount(int length) {
        return length + 1;
    }

    public int getMinInputTermsNumber();

    public void setIntervals(List<Double> intervals);

    public default String getName(int index) {
        return ProjectUtils.formatDouble(getIntervals().get(index));
    }

    public List<Double> getIntervals();

    public static boolean isBetween(final double leftC, double x, final double rightC) {
        return leftC < x && x <= rightC;
    }
}
