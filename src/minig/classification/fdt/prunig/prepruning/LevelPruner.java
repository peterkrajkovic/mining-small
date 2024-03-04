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
public class LevelPruner implements Prepruner {

    private int maxLevel = 20;

    public LevelPruner(int maxLevel) {
        if (maxLevel < 1) {
            throw new Error(ErrorMessages.MAX_LEVEL_MUST_BE_LARGER_THEN_1);
        }
        this.maxLevel = maxLevel - 1;
    }

    @Override
    public boolean isLeaf(DTNode node) {
        int level = node.getLevel();
        return level >= maxLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }
    
}
