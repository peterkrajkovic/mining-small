/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt.fdtclassificationoperator;

import minig.data.core.dataset.NewInstance;
import java.util.List;
import minig.classification.fdt.FuzzyDecisionTree;
import minig.classification.fdt.FDTNode;
import minig.data.core.attribute.FuzzyAttr;
import projectutils.ProjectUtils;
import minig.data.core.dataset.Instance;

/**
 *
 * @author jrabc
 */
public class FDTCrispClassificator implements Classificator {

    private FuzzyDecisionTree fdt;
    
    public FDTCrispClassificator(FuzzyDecisionTree fdt) {
        this.fdt = fdt;
    }
    
    @Override
    public List<Double> classify(Instance instance) {
        FDTNode n = fdt.getRoot();
        while (!n.isLeaf()) {
            FuzzyAttr asoc = n.getAsocAttr();
            List<Double> nodeVals = (List<Double>) instance.getValueOfAttribute(asoc);
            int pathIndex = ProjectUtils.getMaxValueIndex(nodeVals);
            n = n.getChildren().get(pathIndex);
        }
        return n.getConfidenceLevels();
    }

}
