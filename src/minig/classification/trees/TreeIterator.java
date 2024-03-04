/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minig.classification.trees;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 *
 * @author rabcan
 */
public class TreeIterator<T extends TreeNode> implements Iterator<T> {

    private Tree tree;
    private T node;
    private ArrayDeque<T> toHandle = new ArrayDeque<>();

    public TreeIterator(Tree tree) {
        this.tree = tree;
        node = (T) tree.getRoot();
        toHandle.add(node);
    }

    @Override
    public boolean hasNext() {
        return toHandle.isEmpty() == false;

    }

    @Override
    public T next() {
        T next = toHandle.removeLast();
        for (T child : next.<T>getChildren()) {
            toHandle.add(child);
        }
        return next;
    }

}
