/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt.prunig.prepruning;

import projectutils.ErrorMessages;
import minig.classification.fdt.DTNode;
import minig.classification.fdt.FDTNode;

/**
 *
 * @author jrabc
 */
public class FrequencePruner implements Prepruner {

    private double minimalFrequency = 0.15;

    public FrequencePruner(double minimalFrequency) {
        if (minimalFrequency > 1 || minimalFrequency < 0) {
            throw new Error(ErrorMessages.FREQUENCY_MUST_BE_BETWEEN_O_1);
        }
        this.minimalFrequency = minimalFrequency;
    }

    @Override
    public boolean isLeaf(DTNode node) {
        return (node.getFrequence() - minimalFrequency) <= 0;
    }

    public double getMinimalFrequency() {
        return minimalFrequency;
    }

    public void setMinimalFrequency(double minimalFrequency) {
        this.minimalFrequency = minimalFrequency ;
    }

}
