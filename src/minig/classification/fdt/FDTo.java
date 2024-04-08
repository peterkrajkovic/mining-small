/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt;

import projectutils.structures.DoubleVector;
import projectutils.Value;
import java.util.ArrayList;
import java.util.List;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.dataset.DataSet;

/**
 *
 * @author jrabc
 */
public class FDTo extends FuzzyDecisionTree {

    private int leafCount = 0;
    private List<FuzzyAttr> freeAttr;
    private List<FuzzyAttr> usedAttrs = new ArrayList<>();
    private FuzzyAttr outputAttr;

    public FDTo(double alpha, double beta, DataSet dataset) {
        super(alpha, beta, dataset);
    }

    public FDTo(DataSet dataset) {
        super(dataset);
    }

    public FDTo() {
        super();
    }

    @Override
    public void destroy() {
        super.destroy();
        freeAttr.clear();
        freeAttr = null;
    }

    @Override
    public final void setDataset(DataSet dt) {
        super.setDataset(dt);
    }

    @Override
    protected void inductTree() {
        if (getRoot() != null) {
            setRoot(null);
        }
        freeAttr = getDataset().getAttributesIf((attr) -> attr.isInputAttr() && attr.isFuzzy());
        outputAttr = getDataset().getOutbputAttribute();
        getCriterion().setOrdered(true);
        ArrayList<FDTNode> newLevel = new ArrayList<>();
        FDTNode root = new FDTNode();
        this.setRoot(root);
        super.intBranchValuesType();
        newLevel.add(root);
        if (isLeaf(root)) {
            return;
        }
        final Object lock = new Object();
        while (!newLevel.isEmpty()) {
            ArrayList<FDTNode> newNodes = new ArrayList<>();
            FuzzyAttr asocAttr = null;
            double minEntropy = Double.NEGATIVE_INFINITY;
            DoubleVector entropies = new DoubleVector(newLevel.size());
            DoubleVector bestEntropies = new DoubleVector(newLevel.size());
            for (int i = 0; i < freeAttr.size(); i++) {
                FuzzyAttr unusedAttr = freeAttr.get(i);
                Value<Double> entropySum = new Value<>(0d);
                ArrayList<FDTNode> nodes = new ArrayList<>(newLevel.size());
                entropies.clear();
                newLevel.parallelStream().forEach((n) -> {
                    double entropy = getCriterion().getCriterionValue(n, unusedAttr);
                    synchronized (lock) {
                        entropies.addUnsafeNoSum(entropy);
                        entropySum.set(entropySum.get() + entropy);
                        nodes.add(n);
                    }
                });
                if (entropySum.get() > minEntropy) {
                    bestEntropies = new DoubleVector(entropies);
                    newNodes = new ArrayList<>(nodes);
                    asocAttr = unusedAttr;
                    minEntropy = entropySum.get();
                }
                nodes.clear();
            }
            freeAttr.remove(asocAttr);
            if (asocAttr != null) {
                usedAttrs.add(asocAttr);
            }
            newLevel.clear();
            CreateNewNodes(newNodes, asocAttr, bestEntropies, newLevel);
        }
    }

    private void CreateNewNodes(ArrayList<FDTNode> newNodes, FuzzyAttr asocAttr, DoubleVector bestEntropies, ArrayList<FDTNode> level) {
        int i = 0;
        for (int j = 0; j < newNodes.size(); j++) {
            FDTNode newNode = newNodes.get(j);
            newNode.setAsocAttr(asocAttr);
            newNode.setEntropy(bestEntropies.get(i++));
            prepareNode(newNode.getAsocAttr(), newNode, null, true);
            newNode.getChildren().forEach(node -> {
                if (!isLeaf(node)) {
                    level.add(node);
                }
            });
        }
    }

    public List<FuzzyAttr> getUsedAttrs() {
        return usedAttrs;
    }

    private boolean isLeaf(FDTNode node) {
        FDTUtils.setFrequence(node);
        FDTUtils.setConfidances(node, outputAttr);
        boolean isLeaf = applyPrePrunners(node);
        if (isLeaf || freeAttr.isEmpty()) {
            leafCount++;
            node.setIsLeeaf(true);
            node.dealocate();
        }
        return isLeaf;
    }

    @Override
    public int getTreeType() {
        return FuzzyDecisionTree.FTTo;
    }

}
