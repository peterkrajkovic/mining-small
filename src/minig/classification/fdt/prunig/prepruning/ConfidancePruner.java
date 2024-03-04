/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt.prunig.prepruning;

import projectutils.ErrorMessages;
import java.util.Collections;
import java.util.List;
import minig.classification.fdt.DTNode;
import minig.classification.fdt.FDTNode;

/**
 *
 * @author jrabc
 */
public class ConfidancePruner implements Prepruner {

    private double maxConfidanceLevel = 0.75;

    public ConfidancePruner(double maxConfidanceLevel) {
        if (maxConfidanceLevel > 1 || maxConfidanceLevel < 0) {
            throw new Error(ErrorMessages.FREQUENCY_MUST_BE_BETWEEN_O_1);
        }
        this.maxConfidanceLevel = maxConfidanceLevel;
    }

    @Override
    public boolean isLeaf(DTNode node) {
        List<Double> truthLevels = node.getConfidenceLevels();
        double max = Collections.max(truthLevels);
        return max >= maxConfidanceLevel;
    }

    public double getMaxConfidanceLevel() {
        return maxConfidanceLevel;
    }

    public void setMaxConfidanceLevel(double maxConfidanceLevel) {

        this.maxConfidanceLevel = maxConfidanceLevel;
    }

}
