/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.trees;

import java.util.HashMap;
import minig.data.core.attribute.Attribute;
import minig.data.core.dataset.DataSet;
import minig.importance.AttributeImportance;

/**
 *
 * @author rabcan
 */
public abstract class ClassificationTree<T extends ClassificationTreeNode> extends Tree<T> {

    public abstract DataSet getDataset();

    public abstract T getRoot();

    /**
     * 
     * @return HashMap<getAttributeIndex (Integer), AttributeImportance>;
     */
    public HashMap<Integer, AttributeImportance> getAttributeImportance() {
        HashMap<Integer, AttributeImportance> ai = new HashMap<>();
        for (Attribute inputAttr : getDataset().getInputAttrs()) {
            ai.put(inputAttr.getAttributeIndex(), new AttributeImportance(inputAttr));
        }
        this.forEachNode((node) -> {
            if (!node.isLeaf()) {
                AttributeImportance a = ai.get(node.getAsocAttr().getAttributeIndex());
                a.addToImportance(node.getFrequence(), 1);
            }
        });
        return ai;
    }

}
