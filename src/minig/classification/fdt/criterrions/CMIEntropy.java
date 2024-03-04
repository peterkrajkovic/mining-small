/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt.criterrions;

import java.util.ArrayList;
import java.util.List;
import minig.classification.fdt.FDTNode;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.FuzzyAttr;
import projectutils.BranchValues;
import static projectutils.ProjectUtils.log2;
import projectutils.Union;
import projectutils.structures.Vector;

/**
 *
 * @author jrabc
 */
public class CMIEntropy extends FuzzyCriterion {

    @Override
    public void findAssocAttr(FDTNode node, List<FuzzyAttr> freeAttrs) {
        double minEntropy = Double.MAX_VALUE;
        FuzzyAttr asocAttr = null;
        List<Double> finalBranchEntropies = null;
        for (FuzzyAttr attr : freeAttrs) {
            List<Double> branchEntropies = new ArrayList<>(attr.getDomainSize());
            double entropy = this.getEntropy(attr, node.getBranchValues(), branchEntropies);
            if (minEntropy > entropy) {
                asocAttr = attr;
                finalBranchEntropies = branchEntropies;
                minEntropy = entropy;
            }
        }
        node.setEntropy(minEntropy);
        node.setAsocAttr(asocAttr);
        node.setBranchEntropies(finalBranchEntropies);
    }

    @Override
    public double getCriterionValue(FDTNode node, FuzzyAttr attr) {
        FuzzyAttr asocAttr = attr;
        List<Double> finalBranchEntropies;
        List<Double> branchEntropies = new ArrayList<>(attr.getDomainSize());
        double entropy = this.getEntropy(attr, node.getBranchValues(), branchEntropies);
        finalBranchEntropies = branchEntropies;
        return entropy;
        //  prepareNode(asocAttr, node, finalBranchEntropies);
    }

    private double getHi(AttrValue inputAttrVal, AttrValue outputAttrVal, Union v) {
        double m;
        double sumproduct;
        if ( v.getCount() == 0) {
            sumproduct = inputAttrVal.getSum();
            m = v.getSumProdOf(inputAttrVal, outputAttrVal);
        } else {
            sumproduct = v.getSumproductOf(inputAttrVal);
            m = v.getSumproductOf(inputAttrVal, outputAttrVal);
        }
        double hi = m * (log2(sumproduct) - log2(m));
        return hi;
    }

    @Override
    public double getCriterionValue(FuzzyAttr output, FuzzyAttr input) {
        double m, sumproduct, h = 0;
        for (AttrValue inputAttrVal : input.getValues()) {
            for (AttrValue outputAttrVal : output.getValues()) {
                sumproduct = inputAttrVal.getSum();
                m = Vector.sumproduct(inputAttrVal.getValues(), outputAttrVal.getValues());
                h += m * (log2(sumproduct) - log2(m));
            }
        }
        return h;
    }

    /**
     *
     * @param attribute investigated attribute
     * @param BranchSequence Sequence of branch from the root to the current
     * node
     * @param outParamBranchBEntrophy This list is filled during the method
     * execution. After the method execution, it contains the branch entropy for
     * each children of node
     * @return Split of attribute
     */
    public double getEntropy(FuzzyAttr attribute, Union BranchSequence, List<Double> outParamBranchBEntrophy) {
        double sumH = 0;
        double branchEntrophy = 0;
        FuzzyAttr B = (FuzzyAttr) attribute.getDataset().getOutbputAttribute();
        for (int j = 0; j < attribute.getDomainSize(); j++) {
            for (int i = 0; i < B.getDomainSize(); i++) {
                AttrValue outputVal = B.getAttrValue(i);
                AttrValue inputVal = attribute.getAttrValue(j);
                double ret = getHi(inputVal, outputVal, BranchSequence);
                branchEntrophy += ret;
            }
            outParamBranchBEntrophy.add(branchEntrophy);
            sumH += branchEntrophy;
            branchEntrophy = 0;
        }
        return sumH;
    }

}
