/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt.prunig.postpruning;

import minig.classification.fdt.FuzzyDecisionTree;

/**
 *
 * @author jrabc
 */
public abstract class Postpruner {
    
    private FuzzyDecisionTree fdt;

    public Postpruner(FuzzyDecisionTree fdt) {
        this.fdt = fdt;
    }

    public FuzzyDecisionTree getFdt() {
        return fdt;
    }

    public void setFdt(FuzzyDecisionTree fdt) {
        this.fdt = fdt;
    }
    public abstract void apply();
}
