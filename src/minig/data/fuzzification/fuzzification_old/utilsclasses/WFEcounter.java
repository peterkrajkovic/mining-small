/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old.utilsclasses;

import java.util.List;
import minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.CM.Cluster;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.FuzzyAttr;
import projectutils.ProjectUtils;

/**
 * levashenko, zaitseva kniha, str 27
 *
 * @author jrabc
 */
public class WFEcounter {

    private FuzzyAttr inputAttribute;
    private FuzzyAttr outputAttribute;
    private List<Cluster> clusters;

    public WFEcounter(FuzzyAttr inputAttribute, FuzzyAttr outputAttribute, List<Cluster> clusters) {
        this.clusters = clusters;
        this.inputAttribute = inputAttribute;
        this.outputAttribute = outputAttribute;
    }

    private double getProbability(AttrValue inputTerm, AttrValue outputTerm) {
        double menovatel = 0;
        double citatel = 0;
        List<Double> input = inputTerm.getValues();
        List<Double> output = outputTerm.getValues();
        for (int i = 0; i < output.size(); i++) {
            Double x = input.get(i);
            Double b = output.get(i);
            //System.out.println(x +"   "+b);
            menovatel += x * b;
            citatel += x;
        }
        return menovatel / citatel;
    }

    /**
     * v knihe oznacovane ako D
     * @param inputTerm
     * @param oututAttr
     * @return 
     */
    private double getProbability(AttrValue inputTerm, FuzzyAttr oututAttr) {
        double menovatel = 0;
        double citatel = 0;

        List<Double> input = inputTerm.getValues();
        for (AttrValue value : oututAttr.getDomain()) {
            List<Double> output = value.getValues();
            for (int i = 0; i < output.size(); i++) {
                Double x = input.get(i);
                Double b = output.get(i);
                //System.out.println(x +"   "+b);
                menovatel += x * b;
                citatel += x;
            }
        }
        return menovatel / citatel;
    }

    public double getEntropy(AttrValue inputTerm) {
        double d = getProbability(inputTerm, outputAttribute);
        double entropy = -d * ProjectUtils.log2(d);
        return Double.isNaN(entropy) ? 0 : entropy;
    }

    private double getTermsEntropy(AttrValue inputTerm, AttrValue outputTerm) {
        double d = getProbability(inputTerm, outputTerm);
        double entropy = -d * ProjectUtils.log2(d);
        return Double.isNaN(entropy) ? 0 : entropy;
    }

    private double getWeight(int clusterIndex) {
        return ((double) clusters.get(clusterIndex).getStat().getCount()) / clusters.get(clusterIndex).getClusterAlg().getDataCount();
    }

    public double getWeightedEntropy() {
        double entropy = 0;
        for (int i = 0; i < inputAttribute.getDomainSize(); i++) {
            for (AttrValue outputVal : outputAttribute.getDomain()) {
                AttrValue inputVal = inputAttribute.getAttrValue(i);
                entropy += getWeight(i) * getTermsEntropy(inputVal, outputVal);
            }
        }
        return entropy;
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
    }

}
