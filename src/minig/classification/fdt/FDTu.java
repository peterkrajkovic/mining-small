/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.dataset.DataSet;

/**
 *
 * @author jrabc
 */
public class FDTu extends FuzzyDecisionTree {

    private int leafCount = 0;
    private HashMap<Attribute, Attribute> usedAttrs = new HashMap<>();

    // private LinkedList leaves = new LinkedList();
    public FDTu(double alpha, double beta, DataSet dataset) {
        super(alpha, beta, dataset);
    }

    public FDTu(DataSet dataset) {
        super(dataset);
    }

    public FDTu(FuzzyDecisionTree tree) {
        super(tree);
    }

    public FDTu() {
        super();
    }

    public HashMap<Attribute, Attribute> getUsedAttrs() {
        return usedAttrs;
    }


    @Override
    public void inductTree() {
        usedAttrs = new HashMap<>();
        // leaves = new LinkedList();
        ArrayDeque<FDTNode> toHandle = new ArrayDeque();
        final List<FuzzyAttr> freeAttrs = getDataset().getAttributesIf((attr) -> attr.isInputAttr() && attr.isFuzzy());
        FDTNode root = new FDTNode(freeAttrs);
        this.setRoot(root);
        super.intBranchValuesType();
        toHandle.add(root);
        FuzzyAttr outputAttr = getDataset().getOutbputAttribute();
        while (!toHandle.isEmpty()) {
            FDTNode node = toHandle.removeLast();
            FDTUtils.setFrequence(node);
            FDTUtils.setConfidances(node, outputAttr);
            if (node.getFreeAttr().isEmpty()) {
                node.setIsLeeaf(true);
                node.dealocate();
                leafCount++;
                //       leaves.add(node);
                continue;
            }
            boolean isLeaf = applyPrePrunners(node);
            if (!isLeaf) {
                getCriterion().findAssocAttr(node, node.getFreeAttr());
                prepareNode(node, removeSplittingAttrs);
                usedAttrs.putIfAbsent(node.getAsocAttr(), node.getAsocAttr());
                List<FDTNode> children = node.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    toHandle.add(children.get(i));
                }
            } else {
                leafCount++;
                //       leaves.add(node);
            }
            node.dealocate();
        }
    }

    private boolean removeSplittingAttrs = false;

    public void removeSplittingAttributes(boolean remove) {
        removeSplittingAttrs = !remove;
    }

    public int getLeafCount() {
        return leafCount;
    }

    @Override
    public int getTreeType() {
        return FuzzyDecisionTree.FTTu;
    }


}
