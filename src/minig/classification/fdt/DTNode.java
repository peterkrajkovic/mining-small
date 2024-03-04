/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt;

import minig.classification.trees.ClassificationTreeNode;
import java.util.List;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.FuzzyAttr;
import projectutils.ConsolePrintable;
import projectutils.Union;

/**
 *
 * @author jrabc
 */
public interface DTNode extends ConsolePrintable, ClassificationTreeNode {

    public void dealocate();

    public void addBranch(AttrValue attrVal);

    public void setBranchValues(Union sumproduct);

    public Union getBranchValues();

    public void setFreeAttr(List<FuzzyAttr> freeAttr);

    public boolean isRoot();

    public List<Double> getConfidenceLevels();

    public void setConfidenceLevels(List<Double> confidanceLevels);

    public double getFrequence();

    public void setFrequence(double frequence);

    public <T extends DTNode> T getParent();

    public void setParent(FDTNode parent);

    public void setLevel(int level);

    public <T extends Attribute> T getAsocAttr();

    public void setAsocAttr(FuzzyAttr asocAttr);

    public int getChildIndex();

    public void setChildIndex(int childIndex);

    public void addChildren(FDTNode node);

    public void setChildren(List children);

    public boolean isIsLeeaf();

    public void setIsLeeaf(boolean isLeeaf);

    public List<FuzzyAttr> getFreeAttr();

    public double getEntrophy();

    public void setEntropy(double entrophy);

    public double getCriterionValue();

    public AttrValue getIntputBranchValue();

    public int getId();

    public double getPredictedValue();

}
