/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.trees;

import java.util.List;
import minig.data.core.attribute.Attribute;

/**
 *
 * @author rabcan
 */
public interface ClassificationTreeNode extends TreeNode {

    public <T extends Attribute> T getAsocAttr();

    public double getOutputClass();
    
    public <T extends TreeNode> List<T> getChildren();
    
     public double getFrequence();
}
