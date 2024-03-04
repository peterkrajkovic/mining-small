/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.mdd;

import java.util.ArrayList;
import java.util.List;
import minig.classification.trees.ClassificationTreeNode;
import minig.data.core.attribute.Attribute;
import projectutils.Sequencer;

/**
 *
 * @author rabcan
 */
public class MDDnode implements ClassificationTreeNode {

    private Attribute asocAttr;
    private int outputClass;
    private ArrayList<MDDnode> children = new ArrayList<>();
    private int level;
    private List<MDDnode> parents = new ArrayList<>();
    
    private int id = Sequencer.getSequencer().getNextValue();

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public List<MDDnode> getChildren() {
        return children;
    }

    @Override
    public int getLevel() {
        return level;
    }

    public List<MDDnode> getParents() {
        return parents;
    }

    public void addParent(MDDnode parent) {
        parents.add(parent);
    }

    @Override
    public Attribute getAsocAttr() {
        return asocAttr;
    }

    public void addChild(MDDnode node) {
        node.addParent(this);
        children.add(node);
    }

    public void setAsocAttr(Attribute asocAttr) {
        this.asocAttr = asocAttr;
    }

    public void setOutputClass(int outputClass) {
        this.outputClass = outputClass;
    }
    

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        String s =  isLeaf() ? outputClass + "" : asocAttr.getName();
        return s + " id[" + id+"]";
    }

    @Override
    public double getOutputClass() {
        return outputClass;
    }

    @Override
    /**
     * not important for this type of nodes.
     */
    public double getFrequence() {
        return 1;
    }

}
