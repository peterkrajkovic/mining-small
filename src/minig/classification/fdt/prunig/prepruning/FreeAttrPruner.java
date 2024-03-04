/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt.prunig.prepruning;

import minig.classification.fdt.DTNode;
import minig.classification.fdt.FDTNode;

/**
 *
 * @author jrabc
 */
public class FreeAttrPruner implements Prepruner{
    
    @Override
    public boolean isLeaf(DTNode node) {
        return node.getFreeAttr().isEmpty();
    }
    
}
