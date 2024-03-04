/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old.utilsclasses;

import java.util.List;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.FuzzyAttr;
import projectutils.ProjectUtils;

/**
 * levashenko, zaitseva kniha, str 27
 *
 * @author jrabc
 */
public class FEcounter {

    private FuzzyAttr inputAttribute;
    private FuzzyAttr outputAttribute;

    private int countInCluster = 1;
    private int countOfAllObservations = 1;

    public FEcounter(FuzzyAttr inputAttribute, FuzzyAttr outputAttribute) {
        this.inputAttribute = inputAttribute;
        this.outputAttribute = outputAttribute;
    }

    public FEcounter(FuzzyAttr inputAttribute, FuzzyAttr outputAttribute, int countInCluster, int countOfAllObservations) {
        this.inputAttribute = inputAttribute;
        this.outputAttribute = outputAttribute;
        this.countOfAllObservations = countOfAllObservations;
        this.countInCluster = countInCluster;
    }

    private double getD(AttrValue inputTerm, AttrValue outputTerm) {
        double menovatel = 0;
        double citatel = 0;
        List<Double> input = inputTerm.getValues();
        List<Double> output = outputTerm.getValues();
        for (int i = 0; i < output.size(); i++) {
            Double x = input.get(i);
            Double b = output.get(i);
            menovatel += x * b;
            citatel += x;
        }
        return menovatel / citatel;
    }

    private double getTermsEntropy(AttrValue inputTerm, AttrValue outputTerm) {
        double d = getD(inputTerm, outputTerm);
        double entropy = -d * ProjectUtils.log2(d);
        return Double.isNaN(entropy) ? 0 : entropy;
    }

    private double getWeight() {
        return ((double) countInCluster) / countOfAllObservations;
    }

    private double getWeightedEntropy() {
        double entrophy = 0;
        for (AttrValue value : outputAttribute.getDomain()) {
            for (AttrValue value1 : inputAttribute.getDomain()) {
                entrophy += getWeight() * getTermsEntropy(value1, value);
            }
        }
        return entrophy;
    }
    
    public static void main(String[] args) {
        FuzzyAttr b = new FuzzyAttr("b");
        b.addValue("b1");
        b.addValue("b2");
        b.addFuzzyRow(1, 0);
        b.addFuzzyRow(1, 0);
        b.addFuzzyRow(0, 1);
        b.addFuzzyRow(1, 0);
        b.addFuzzyRow(0, 1);
        FuzzyAttr a = new FuzzyAttr("a");
        a.addValue("a2");
        a.addValue("a3");
        a.addValue("a4");
        a.addFuzzyRow(0.4, 0.6, 0);
        a.addFuzzyRow(0.2, 0.8, 0);
        a.addFuzzyRow(0, 1, 0);
        a.addFuzzyRow(0, 0.9, 0.1);
        a.addFuzzyRow(0, 0.7, 0.3);
        FEcounter as = new FEcounter(a, b);
        System.out.println(as.getWeightedEntropy());
    }

}
