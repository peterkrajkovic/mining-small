/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.trees;

import java.util.List;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public interface TreeNode {

    public boolean isLeaf();

    public <T extends TreeNode> List<T> getChildren();

    public int getLevel();


}
