/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.trees;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author jrabc
 * @param <T> type of the tree node
 */
public abstract class Tree<T extends TreeNode> implements Iterable<T> {

    public abstract T getRoot();

    @Override
    public TreeIterator<T> iterator() {
        return new TreeIterator<>(this); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    protected void getAllPaths(T node, List<List<T>> allPaths, List<T> path) {
        path.add(node);
        if (node.isLeaf()) {
            allPaths.add(new LinkedList(path));
        } else {
            for (T child : node.<T>getChildren()) {
                getAllPaths(child, allPaths, path);
            }
        }
        path.remove(node);
    }

    protected List<List<T>> getAllPaths(T node) {
        List<List<T>> paths = new ArrayList<>();
        getAllPaths(node, paths, new ArrayList<>());
        return paths;
    }

    public List<List<T>> getAllPaths() {
        return getAllPaths(getRoot());
    }

    public void forEachNode(Consumer<T> action) {
        T node = getRoot();
        ArrayDeque<T> toHandle = new ArrayDeque<>();
        toHandle.add(node);
        while (!toHandle.isEmpty()) {
            node = toHandle.removeLast();
            for (T child : node.<T>getChildren()) {
                toHandle.add(child);
            }
            action.accept(node);
        }
    }

    public List<T> getLeaves() {
        LinkedList<T> leaves = new LinkedList<>();
        LinkedList<T> toHandle = new LinkedList<>();
        toHandle.add(getRoot());
        do {
            T node = toHandle.removeFirst();
            if (node.isLeaf()) {
                leaves.add(node);
            } else {
                for (T child : node.<T>getChildren()) {
                    toHandle.add(child);
                }
            }
        } while (!toHandle.isEmpty());
        return leaves;
    }

    public List<T>[] getNodesAtLevels() {
        LinkedList<T>[] levels = new LinkedList[100];
        LinkedList<T> toHandle = new LinkedList<>();
        toHandle.add(getRoot());
        do {
            T node = toHandle.removeFirst();
            int level = node.getLevel();
            levels[level].add(node);
        } while (toHandle.isEmpty());
        return levels;
    }

    protected String getPrintString(T node, String prefix, boolean isTail) {
        String ret = "";
        ret += prefix + (isTail ? "|___ " : "|--- ") + node.toString();
        Iterator<T> i = node.<T>getChildren().iterator();
        int count = node.getChildren().size() - 1;
        while (count-- > 0) {
            ret += System.lineSeparator() + getPrintString(i.next(), prefix + (isTail ? "      " : "|   "), false);
        }
        if (!node.isLeaf()) {
            ret += System.lineSeparator() + getPrintString(i.next(), prefix + (isTail ? "      " : "|   "), true);
        }
        return ret;
    }

    @Override
    public String toString() {
        if (getRoot() == null) {
            throw new Error("Tree is not inducted");
        }
        return getPrintString(getRoot(), "", true);
    }

}
