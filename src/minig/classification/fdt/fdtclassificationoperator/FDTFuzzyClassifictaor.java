/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt.fdtclassificationoperator;

import java.util.List;
import minig.classification.fdt.FuzzyDecisionTree;
import minig.data.core.dataset.Instance;

/**
 *
 * @author jrabc
 */
public class FDTFuzzyClassifictaor implements Classificator{

    private FuzzyDecisionTree fdt;

    public FDTFuzzyClassifictaor(FuzzyDecisionTree fdt) {
        this.fdt = fdt;
    }

    /**
     * pokial nie je sucet 1, tak sa dopocita automaticky.
     * @param instance
     * @return 
     */
    public List<Double> classify(Instance instance) {
        return fdt.getClassificationRules().classify(instance);
    }
}
