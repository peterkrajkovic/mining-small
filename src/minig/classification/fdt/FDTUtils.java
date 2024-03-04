/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt;

import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.FuzzyAttr;
import projectutils.structures.DoubleVector;
import projectutils.Union;

/**
 *
 * @author jrabc
 */
public class FDTUtils {



    public static void setFrequence(DTNode node) {
        if (node.isRoot()) {
            node.setFrequence(1);
            return;
        }
        // node.getBranchValues().addAttrVal(node.getIntputBranchValue());
        if (node.getBranchValues() == null) {
            return;
        }
        double ff = node.getBranchValues().getSumProduct() / node.getBranchValues().getSize();
        node.setFrequence(ff);
    }


    public static void setConfidances(FDTNode n, FuzzyAttr outputattr) {
        Union br = n.getBranchValues();
        DoubleVector a = new DoubleVector(outputattr.getDomainSize());
        for (AttrValue val : outputattr.getDomain()) {
            if (br.getCount() > 0) {
                a.add(br.getProduct(val.getValues()) / br.getSumProduct());
            } else {
                a.add(val.getSum() / outputattr.getDataCount());
            }
        }
        n.setConfidenceLevels(a);
    }

}
