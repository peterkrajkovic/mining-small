/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import minig.classification.fdt.criterrions.FuzzyCriterion;
import minig.classification.fdt.criterrions.GainRatio;
import minig.classification.fdt.fdtclassificationoperator.Classificator;
import minig.classification.fdt.fdtclassificationoperator.FDTFuzzyClassifictaor;
import minig.classification.fdt.prunig.postpruning.Postpruner;
import minig.classification.fdt.prunig.postpruning.ZeroFequencesPruner;
import minig.classification.fdt.prunig.prepruning.ConfidancePruner;
import minig.classification.fdt.prunig.prepruning.FrequencePruner;
import minig.classification.fdt.prunig.prepruning.LevelPruner;
import minig.classification.fdt.prunig.prepruning.Prepruner;
import minig.classification.fdt.rules.ClassificationRules;
import minig.classification.trees.ClassificationTree;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.Instance;
import minig.models.Classifier;
import projectutils.BranchValues;
import projectutils.BranchValuesMinUnion;
import projectutils.ConsolePrintable;
import projectutils.ProductUnionSlow;
import visualization.graphviz.script.FDTGraphvizScript;

/**
 *
 * @author jrabc
 */
public abstract class FuzzyDecisionTree extends ClassificationTree<FDTNode> implements Cloneable, Serializable, Classifier, ConsolePrintable {

    public enum EdgeStrategy {
        speed_productUnion, speed_minUnion, memory;
    }
    public static final int FTTo = 0;
    public static final int FTTu = 1;
    public static final int DTig = 2;
    public static final int FDTuRF = 3;

    private FDTNode root = null;
    private EdgeStrategy branchValuesType = EdgeStrategy.speed_productUnion;

    private transient DataSet dataset;
    //protected Clasificator clasificator = new Clasificator(this);
    private transient List<Prepruner> prepruners = new ArrayList<>(2);
    private transient List<Postpruner> postpruners = new ArrayList<>(1);
    private transient ClassificationRules classificationRules = null;
    private transient Classificator classificator = new FDTFuzzyClassifictaor(this);
    protected int level = 1;

    private final ZeroFequencesPruner zeroFequences = new ZeroFequencesPruner(this);
    private final FrequencePruner alfaPruner = new FrequencePruner(0.005);
    private final ConfidancePruner betaPruner = new ConfidancePruner(0.95);
    private final LevelPruner maxDepthPruner = new LevelPruner(100);

    private transient FuzzyCriterion criterion = new GainRatio();

    public FuzzyDecisionTree(double alfa, double beta, DataSet dataset) {
        super();
        init();
        root = null;
        alfaPruner.setMinimalFrequency(alfa);
        betaPruner.setMaxConfidanceLevel(beta);
        setDataset(dataset);
    }

    public FuzzyDecisionTree() {
        init();
    }

    private void init() {
        addPrepruner(alfaPruner);
        addPrepruner(betaPruner);
        addPrepruner(maxDepthPruner);
        postpruners.add(zeroFequences);
    }

    @Deprecated
    public FuzzyDecisionTree(FuzzyDecisionTree tree) {
        addPrepruner(new FrequencePruner(tree.getAlfa()));
        addPrepruner(new ConfidancePruner(tree.getBeta()));
        postpruners.add(new ZeroFequencesPruner(this));
        //  this.root = tree.root;
        //  this.classificationRules = tree.classificationRules;
        // initPruners();
    }

    public FuzzyDecisionTree(DataSet dt) {
        super();
        setDataset(dt);
        init();
    }

    protected final void setRoot(FDTNode root) {
        this.root = root;
    }

    public void setMaxDepth(int maxLevel) {
        maxDepthPruner.setMaxLevel(maxLevel - 1);
    }

    public void destroyTreeData(DataSet dataset) {
        this.dataset = dataset;
        root = null;
        classificationRules = null;
    }

    public final void addPrepruner(Prepruner pruner) {
        prepruners.add(pruner);
    }

    public final void addPostpruner(Postpruner pruner) {
        postpruners.add(pruner);
    }

    public void destroy() {
        classificationRules = null;
        dataset = null;
        level = 1;
        root = null;
    }

    public int getNodeCount() {
        int nodes = 0;
        FDTNode node = getRoot();
        ArrayDeque<FDTNode> toHandle = new ArrayDeque<>(50);
        toHandle.add(node);
        while (!toHandle.isEmpty()) {
            node = toHandle.removeLast();
            for (FDTNode child : node.<FDTNode>getChildren()) {
                toHandle.add(child);
            }
            nodes++;
        }
        return nodes;
    }

    protected boolean applyPrePrunners(FDTNode node) {
        for (Prepruner prunner : prepruners) {
            if (prunner.isLeaf(node)) {
                node.setIsLeeaf(true);
                return true;
            }
        }
        return false;
    }

    protected void prepareNode(FuzzyAttr asocAttr, FDTNode node, List<Double> finalBranchEntropies, boolean ordered) {
        for (int i = 0; i < asocAttr.getDomainSize(); i++) {
            FDTNode child;
            if (!ordered) {
                child = new FDTNode(new ArrayList<>(node.getFreeAttr()));
            } else {
                child = new FDTNode();
            }
            node.addChildren(child);
            child.setLevel(node.getLevel() + 1);
            if (finalBranchEntropies != null) {
                child.setInputBranchEntrophy(finalBranchEntropies.get(i));
            }
            if (i == asocAttr.getDomainSize() - 1) {
                child.setBranchValues(node.getBranchValues());
            } else {
                child.setBranchValues(node.getBranchValues().getCopy());
            }
            child.addBranch(node.getAsocAttr().getAttrValue(i));
            child.setIntputBranchVallue(node.getAsocAttr().getAttrValue(i));
        }
    }

    protected void prepareNode(FDTNode node, boolean removeAsocAttrFromFreeAttr) {
        FuzzyAttr asocAttr = node.getAsocAttr();
        List<Double> branchEntropies = node.getBranchEntropies();
        if (!removeAsocAttrFromFreeAttr) {
            node.getFreeAttr().remove(asocAttr);
        }
        for (int i = 0; i < asocAttr.getDomainSize(); i++) {
            FDTNode child;
            if (!removeAsocAttrFromFreeAttr) {
                child = new FDTNode(new ArrayList<>(node.getFreeAttr()));
            } else {
                child = new FDTNode(new ArrayList<>(node.getFreeAttr())); //may be improved
            }
            node.addChildren(child);
            child.setLevel(node.getLevel() + 1);
            if (branchEntropies != null) {
                child.setInputBranchEntrophy(branchEntropies.get(i));
            }
            child.setParent(node);
            if (i == asocAttr.getDomainSize() - 1) {
                child.setBranchValues(node.getBranchValues());
            } else {
                child.setBranchValues(node.getBranchValues().getCopy());
            }
            child.addBranch(node.getAsocAttr().getAttrValue(i));
            child.setIntputBranchVallue(node.getAsocAttr().getAttrValue(i));
            child.setLevel(node.getLevel() + 1);
        }
    }

    @Override
    public final DataSet getDataset() {
        return dataset;
    }

    public void setDataset(DataSet dt) {
        this.dataset = dt;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public void buildModel() {
        getCriterion().setDataset(dataset);
        inductTree();
        applyPostPrunners();
    }

    protected abstract void inductTree();

    public abstract int getTreeType();

    public void saveToFile(String path) throws FileNotFoundException, IOException {
        LinkedList<FDTNode> queue = new LinkedList();
        StringBuilder sb = new StringBuilder(10000);
        FDTNode current = root;
        queue.add(current);
        int nodeCount = 0;
        while (!queue.isEmpty()) {
            current = queue.removeLast();
            sb.append(current.getFileString()).append(System.lineSeparator());
            nodeCount++;
            for (FDTNode fNode : current.getChildren()) {
                queue.add(fNode);
            }
        }
        FileWriter fw = new FileWriter(path);
        String head = Integer.toString(nodeCount);
        // System.out.println(sb.toString());
        fw.write(head + System.lineSeparator() + sb.toString());
        fw.flush();
        fw.close();
    }

    @Deprecated
    public String getGraphvizCode(int fontSize, boolean shortPrint) {
        FDTGraphvizScript code = new FDTGraphvizScript(this);
        return code.getGraphvizCode();
    }

    @Deprecated
    public String getGraphvizCode(boolean shortPrint) {
        return getGraphvizCode(20, shortPrint);
    }

    @Deprecated
    public String getGraphvizCode() {
        return getGraphvizCode(20, false);
    }

    public void setBranchValuesType(EdgeStrategy branchValuesType) {
        this.branchValuesType = branchValuesType;
    }

    protected void intBranchValuesType() {
        if (null == branchValuesType) {
            getRoot().setBranchValues(new ProductUnionSlow());
        } else {
            switch (branchValuesType) {
                case speed_minUnion:
                    getRoot().setBranchValues(new BranchValuesMinUnion());
                    break;
                case speed_productUnion:
                    getRoot().setBranchValues(new BranchValues());
                    break;
                default:
                    getRoot().setBranchValues(new ProductUnionSlow());
                    break;
            }
        }
    }

    @Override
    public FDTNode getRoot() {
        return root;
    }

    public ClassificationRules getClassificationRules() {
        if (classificationRules == null) {
            classificationRules = new ClassificationRules(this, getDataset().getNumberOfClasses());
        }
        return classificationRules;
    }

    @Override
    public List<Double> classify(Instance instance) {
        //instance.setDataset(dataset);
        return (List<Double>) classificator.classify(instance);
    }

    public FuzzyCriterion getCriterion() {
        return criterion;
    }

    public void setCriterion(FuzzyCriterion criterion) {
        this.criterion = criterion;
    }

    public final double getAlfa() {
        return alfaPruner.getMinimalFrequency();
    }

    public final double getBeta() {
        return betaPruner.getMaxConfidanceLevel();
    }

    public final void setAlfa(double alfa) {
        alfaPruner.setMinimalFrequency(alfa);
    }

    public final void setBeta(double beta) {
        betaPruner.setMaxConfidanceLevel(beta);
    }

    public final void setAlfaBeta(double alfa, double beta) {
        this.setAlfa(alfa);
        this.setBeta(beta);
    }

    public int getMaxDepth() {
        return this.maxDepthPruner.getMaxLevel();
    }

    public void applyPostPrunners() {
        for (Postpruner postpruner : postpruners) {
            postpruner.apply();
        }
    }

}
