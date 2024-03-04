/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.FuzzyAttr;
import projectutils.BranchValues;
import projectutils.ProjectUtils;
import static projectutils.ProjectUtils.listToString;
import projectutils.Sequencer;
import projectutils.Union;

/**
 *
 * @author jrabc
 */
public class FDTNode implements Serializable, DTNode {

    private FuzzyAttr asocAttr;
    private LinkedList<FDTNode> children = new LinkedList<>();
    private boolean isLeeaf;
    private List<FuzzyAttr> freeAttr;
    private AttrValue intputBranchVallue;
    private double entropy;
    private double inputBranchEntrophy = Double.NaN;
    private double frequence = -1;
    private int level = 0;
    private List<Double> confidanceLevels;
    private FDTNode parent;
    private static DecimalFormat df = new DecimalFormat("##.###");
    private Union branchValues = new BranchValues();
    private int childIndex = -1;
    private double predictedValue;

    private int id = Sequencer.getSequencer().getNextValue();
    private List<Double> branchEntropies;

    public FDTNode(DTNode node) {
        this.asocAttr = node.getAsocAttr();
        this.isLeeaf = node.isIsLeeaf();
        this.freeAttr = node.getFreeAttr();
        this.intputBranchVallue = node.getIntputBranchValue();
        this.entropy = node.getCriterionValue();
        this.parent = node.getParent();
    }

    public FDTNode(List<FuzzyAttr> freeAttr) {
        this.freeAttr = freeAttr;
    }

    public List<Double> getBranchEntropies() {
        return branchEntropies;
    }

    public void setBranchEntropies(List<Double> branchEntropies) {
        this.branchEntropies = branchEntropies;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void dealocate() {
        branchValues = null;
        freeAttr = null;
    }

    public FDTNode() {
        this.freeAttr = null;
    }

    public double getPredictedValue() {
        return predictedValue;
    }

    public void setPredictedValue(double predictedValue) {
        this.predictedValue = predictedValue;
    }

    public void addBranch(AttrValue attrVal) {
        branchValues.addAttrVal(attrVal);
    }

    public void setBranchValues(Union sumproduct) {
        this.branchValues = sumproduct;
    }

    public Union getBranchValues() {
        return branchValues;
    }

    public void setFreeAttr(List<FuzzyAttr> freeAttr) {
        this.freeAttr = freeAttr;
    }

    public double getComulativeMutalInformation() {
        return inputBranchEntrophy - entropy;
    }

    @Override
    public AttrValue getIntputBranchValue() {
        return intputBranchVallue;
    }

    public void setIntputBranchVallue(AttrValue intputBranchValles) {
        this.intputBranchVallue = intputBranchValles;
    }

    public DecimalFormat getDf() {
        return df;
    }

    public void setDf(DecimalFormat df) {
        this.df = df;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public List<Double> getConfidenceLevels() {
        return confidanceLevels;
    }

    public void setConfidenceLevels(List<Double> confidanceLevels) {
        this.confidanceLevels = confidanceLevels;
    }

    public int getLevel() {
        return level;
    }

    public double getFrequence() {
        return frequence;
    }

    public void setFrequence(double frequence) {
        this.frequence = frequence;
    }

    public FDTNode getParent() {
        return parent;
    }

    public void setParent(FDTNode parent) {
        this.parent = parent;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public FuzzyAttr getAsocAttr() {
        return asocAttr;
    }

    public void setAsocAttrAndRemoveFromFreeAttr(FuzzyAttr asocAttr) {
        this.asocAttr = asocAttr;
        if (!freeAttr.remove(asocAttr)) {
            throw new Error("asociovany atribut sa nepodarilo zmazat z vrchola");
        }
    }

    public void setAsocAttr(FuzzyAttr asocAttr) {
        this.asocAttr = asocAttr;
    }

    public int getChildIndex() {
        return childIndex;
    }

    public void setChildIndex(int childIndex) {
        this.childIndex = childIndex;
    }

    @Override
    public List<FDTNode> getChildren() {
        return children;
    }

    public void addChildren(FDTNode node) {
        node.setChildIndex(getChildren().size());
        node.setIntputBranchVallue(getAsocAttr().getAttrValue(getChildren().size()));
        node.setLevel(level + 1);
        node.setParent(this);
        children.add(node);
    }

    public void setChildren(List children) {
        this.children = (LinkedList<FDTNode>) children;
    }

    public boolean isIsLeeaf() {
        return isLeeaf;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public void setIsLeeaf(boolean isLeeaf) {
        this.isLeeaf = isLeeaf;
    }

    public List<FuzzyAttr> getFreeAttr() {
        return freeAttr;
    }

    public double getEntrophy() {
        return entropy;
    }

    public void setEntropy(double entrophy) {
        this.entropy = entrophy;
    }

    public double getCriterionValue() {
        return entropy;
    }

    @Override
    public String toString() {
        return (isIsLeeaf() ? "L" : asocAttr.getName().toUpperCase())
                + (isRoot() ? "" : " [" + getIntputBranchValue().getName()) + "]"
                + "  Criterion value = " + df.format(getCriterionValue()) + "  "
                // + "|InpBrH = " + df.format(inputBranchEntrophy)
                // + " I(A;...) = " + df.format(getComulativeMutalInformation())
                + listToString(confidanceLevels)
                + " f = " + df.format(frequence);

    }

    public double getInputBranchEntrophy() {
        return inputBranchEntrophy;
    }

    public void setInputBranchEntrophy(double inputBranchEntrophy) {
        this.inputBranchEntrophy = inputBranchEntrophy;
    }

    public void print() {
        print("", true);
    }

    public int getId() {
        return id;
    }

    public String getFileString() {
        String attr = asocAttr == null ? "Leaf" : asocAttr.getName();
        String pid = getParent() == null ? "-1" : Integer.toString(getParent().getId());
        return attr + "#" + getId() + "#" + pid + "#" + getCriterionValue() + "#" + getFrequence() + "#" + getConfidenceLevels();
    }

    @Override
    public double getOutputClass() {
        return ProjectUtils.getMaxValueIndex(confidanceLevels);
    }

    private void print(String prefix, boolean isTail) {
        if (asocAttr != null) {
            System.out.println(prefix + (isTail ? "|___ " : "|--- ") + toString());
            for (int i = 0; i < children.size() - 1; i++) {
                children.get(i).print(prefix + (isTail ? "      " : "|   "), false);
            }
            if (!children.isEmpty()) {
                children.getLast().print(prefix + (isTail ? "      " : "|   "), true);
            }
        }
    }

    public void reinit() {
        asocAttr = null;
        children = new LinkedList<>();
        isLeeaf = false;
        frequence = -1;
        confidanceLevels = new ArrayList<>();
    }

}
