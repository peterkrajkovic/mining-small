/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt.prunig.postpruning;

import java.util.List;
import minig.classification.fdt.DTNode;
import minig.classification.fdt.FuzzyDecisionTree;
import minig.classification.fdt.FDTNode;
import minig.data.core.attribute.FuzzyAttr;

/**
 *
 * @author jrabc
 */
public class ZeroFequencesPruner extends Postpruner {

    private List<? extends DTNode> leafs;

    public ZeroFequencesPruner(FuzzyDecisionTree fdt) {
        super(fdt);
    }

    @Override
    public void apply() {
        leafs = getFdt().getLeaves();
        FuzzyAttr attr = (FuzzyAttr) getFdt().getDataset().getOutbputAttribute();
        int classCout = attr.getDomainSize();
        double val = 1d / classCout;
        for (DTNode leaf : leafs) {
            if (leaf.getFrequence() < 0.00001) {
                for (int i = 0; i < classCout; i++) {
                    leaf.getConfidenceLevels().set(i, val);
                }
            }
        }
    }

}
