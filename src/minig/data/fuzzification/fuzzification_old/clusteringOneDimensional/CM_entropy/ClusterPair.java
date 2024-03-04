/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.CM_entropy;

import java.util.List;
import minig.data.core.attribute.FuzzyAttr;

/**
 *
 * @author jrabc
 */
public class ClusterPair {

    private final List<Double> outputVaules;
    private final double x;
    private final int indexInDataset;

    public ClusterPair(List<Double> outputVaules, double x, int indexInDataset) {
        this.outputVaules = outputVaules;
        this.x = x;
        this.indexInDataset = indexInDataset;
    }

    public List<Double> getOutputVaules() {
        return outputVaules;
    }

    public double getX() {
        return x;
    }

    public int getIndexInDataset() {
        return indexInDataset;
    }
    
    public double getTerm(FuzzyAttr attr, int valIndex, int indexInDataset) {
        return attr.getAttrValue(valIndex).getValues().get(indexInDataset);
    }

}
