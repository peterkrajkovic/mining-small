/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demo;
import minig.classification.mdd.MDD;
import minig.classification.mdd.MDDnode;
import minig.data.core.attribute.Attribute;
import projectutils.ProjectUtils;
import visualization.graphviz.script.GraphvizScript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
/**
 * @author petok
 */
public class DPLD {

    private HashMap<MDDnode, MDDnode> memo;
    private ArrayList<MDDnode> terminals;
    private HashMap<Tuple, MDDnode> applyCache;
    private HashMap<MDD.InternalNodeKey, MDDnode> uniqueNodes;


    public MDD UniversalIDPLD(MDD mdd, int index, int from, int to, Function<Integer, Integer> leftTransformFunction, Function<Integer, Integer> rightTransformFunction, BinaryOperator<Integer> applyFunction) {

        //cofactor computation
        MDD lhsCofactor = COFACTOR(mdd, index, from - 1);
        MDD rhsCofactor = COFACTOR(mdd, index, to - 1);

        if (lhsCofactor == null || rhsCofactor == null) {
            return null;
        }

        //transformation
        MDD lhs = transform(lhsCofactor, leftTransformFunction);
        MDD rhs = transform(rhsCofactor, rightTransformFunction);

        if (lhs == null || rhs == null) {
            return null;
        }

        //diagrams combination
        MDD result = APPLY(lhs, rhs, applyFunction);
        return result;
    }

    public MDD UniversalDPLD(MDD mdd, int index, int from, int to, BinaryOperator<Integer> applyFunction) {

        //cofactor computation
        MDD lhsCofactor = COFACTOR(mdd, index, from);
        MDD rhsCofactor = COFACTOR(mdd, index, to);
        String code;
        code = GraphvizScript.code(lhsCofactor);
        ProjectUtils.toClipboard(code);
        code = GraphvizScript.code(rhsCofactor);
        ProjectUtils.toClipboard(code);
        if (lhsCofactor == null || rhsCofactor == null) {
            return null;
        }
        //diagrams combination
        MDD result = APPLY(lhsCofactor, rhsCofactor, applyFunction);
        return result;
    }

    public MDD COFACTOR(MDD diagram, int index, int a) {
        MDDnode root = diagram.getRoot();

        if (root.isLeaf()) {
            return diagram;

        } else if (root.getAsocAttr().getAttributeIndex() == index) {

            var sons = root.getChildren();
            if (sons.size() > a) {
                return new MDD(sons.get(a));
            }

        } else {
            memo = new HashMap<>();
            uniqueNodes = new HashMap<>();
            return new MDD(COFACTORSTEP(root, index, a));
        }

        return null;
    }

    public MDDnode COFACTORSTEP(MDDnode node, int i, int a) {
        // Check if the cofactor of the node has already been computed
        if (memo.containsKey(node)) {
            return memo.get(node);
        }

        // Check if the node is a terminal node
        if (node.isLeaf()) {
            return node;
        }

        int index = node.getAsocAttr().getAttributeIndex();

        // Check if the index of the node is equal to i
        if (index == i) {
            var sons = node.getChildren();

            if (sons.size() > a) {
                return sons.get(a);
            } else {
                return null;
            }
        }

        // Check if the index of the node is greater than i
        if (index < i) {
            return node;
        }

        var sons = node.getChildren();

        // Compute the cofactor of the node
        int mj = sons.size();
        MDDnode[] cofactoredSons = new MDDnode[mj];
        for (int k = 0; k < mj; k++) {
            MDDnode oldSon = sons.get(k);
            cofactoredSons[k] = COFACTORSTEP(oldSon, i, a);
        }

        // Create new internal node
        MDDnode newNode = new MDDnode();
        for (int k = 0; k < mj; k++) {
            newNode.addChild(cofactoredSons[k]);
        }
        newNode.setId(node.getId());
        newNode.setAsocAttr(node.getAsocAttr());

        // Memorize the result
        memo.put(node, newNode);

        return newNode;
    }

    public MDD transform(MDD diagram, Function<Integer, Integer> gamma) {
        MDDnode root = diagram.getRoot();
        memo = new HashMap();
        terminals = new ArrayList();
        uniqueNodes = new HashMap<>();
        MDDnode newRoot = TRANSFORMSTEP(root, gamma);
        MDD newMDD = new MDD(newRoot);
        return newMDD;
    }

    public MDDnode TRANSFORMSTEP(MDDnode node, Function<Integer, Integer> gamma) {
        if (node.isLeaf()) {
            int newOutput = gamma.apply((int) node.getOutputClass());
            return CreateTerminalNode(node.getAsocAttr(), newOutput);
        }
        // Check if the transform of the node has already been computed
        if (memo.containsKey(node)) {
            return memo.get(node);
        }

        var sons = node.getChildren();
        MDDnode[] newSons = new MDDnode[sons.size()];

        for (int k = 0; k < sons.size(); k++) {
            MDDnode oldSon = sons.get(k);
            newSons[k] = TRANSFORMSTEP(oldSon, gamma);
        }

        MDDnode newNode = CreateInternalNode(node, newSons);
        // Memorize the result
        memo.put(node, newNode);

        return newNode;
    }

    public MDDnode CreateInternalNode(MDDnode oldNode, MDDnode[] sons) {
        //check if node already exists
        int[] sonsIndexes = new int[sons.length];
        for (int i = 0; i < sons.length; i++) {
            sonsIndexes[i] = sons[i].getId();
        }
        MDD.InternalNodeKey nodeKey = new MDD.InternalNodeKey(oldNode.getAsocAttr().getAttributeIndex(), sonsIndexes);
        if (this.uniqueNodes.containsKey(nodeKey)) {
            return uniqueNodes.get(nodeKey);
        }
        //create new node
        MDDnode node = new MDDnode();
        for (MDDnode son : sons) {
            node.addChild(son);
        }
        node.setId(oldNode.getId());
        node.setAsocAttr(oldNode.getAsocAttr());

        return node;
    }

    public MDDnode CreateTerminalNode(Attribute a, int outputClass) {
        //check if terminal node already exists
        for (MDDnode k : this.terminals) {
            if ((int) k.getOutputClass() == outputClass) {
                return k;
            }
        }
        //create new node
        MDDnode node = new MDDnode();
        node.setOutputClass(outputClass);
        node.setAsocAttr(a);
        terminals.add(node);
        return node;
    }

    public MDD APPLY(MDD left, MDD right, BinaryOperator<Integer> op) {
        applyCache = new HashMap<>();
        this.terminals = new ArrayList<MDDnode>();
        uniqueNodes = new HashMap<>();
        MDDnode root = APPLYSTEP(left.getRoot(), right.getRoot(), op);
        return new MDD(root);
    }

    MDDnode APPLYSTEP(MDDnode left, MDDnode right, BinaryOperator<Integer> op) {
        if (applyCache.containsKey(new Tuple(left, right))) {
            return applyCache.get(new Tuple(left, right));
        }
        //TODO treba skraslit
        MDDnode node;
        if (left.isLeaf() && right.isLeaf()) {
            int outputClass = op.apply((int) left.getOutputClass(), (int) right.getOutputClass());
            node = CreateTerminalNode(left.getAsocAttr(), outputClass);

        } else if (right.isLeaf()) {
            var leftChildren = left.getChildren();
            MDDnode[] sons = new MDDnode[leftChildren.size()];
            for (int k = 0; k < leftChildren.size(); k++) {
                sons[k] = APPLYSTEP(leftChildren.get(k), right, op);
            }
            node = CreateInternalNode(left, sons);
            node.setLevel(left.getLevel());

        } else if (left.isLeaf()) {
            var rightChildren = right.getChildren();
            var sons = new MDDnode[rightChildren.size()];
            for (int k = 0; k < rightChildren.size(); k++) {
                sons[k] = APPLYSTEP(left, rightChildren.get(k), op);
            }
            node = CreateInternalNode(right, sons);
            node.setLevel(right.getLevel());
        } else {
            int ilhs = left.getAsocAttr().getAttributeIndex();
            int irhs = right.getAsocAttr().getAttributeIndex();

            MDDnode[] sons;
            if (ilhs > irhs) {
                var leftChildren = left.getChildren();
                sons = new MDDnode[leftChildren.size()];
                for (int k = 0; k < leftChildren.size(); k++) {
                    sons[k] = APPLYSTEP(leftChildren.get(k), right, op);
                }
                node = CreateInternalNode(left, sons);
                node.setLevel(ilhs);
            } else if (ilhs == irhs) {
                var rightChildren = right.getChildren();
                var leftChildren = left.getChildren();
                sons = new MDDnode[rightChildren.size()];
                for (int k = 0; k < rightChildren.size(); k++) {
                    sons[k] = APPLYSTEP(leftChildren.get(k), rightChildren.get(k), op);
                }
                node = CreateInternalNode(right, sons);
                node.setLevel(irhs);
            } else {
                var rightChildren = right.getChildren();
                sons = new MDDnode[rightChildren.size()];
                for (int k = 0; k < rightChildren.size(); k++) {
                    sons[k] = APPLYSTEP(left, rightChildren.get(k), op);
                }
                node = CreateInternalNode(right, sons);
                node.setLevel(irhs);
            }

        }
        applyCache.put(new Tuple(left, right), node);
        return node;
    }

    public static class Tuple<T> {
        T x1;
        T x2;

        Tuple(T x1, T x2) {
            this.x1 = x1;
            this.x2 = x2;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Tuple other)) {
                return false;
            }

            return (this.x1.equals(other.x1) && this.x2.equals(other.x2)) || (this.x2.equals(other.x1) && this.x1.equals(other.x2));
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + Objects.hashCode(this.x1);
            hash = 67 * hash + Objects.hashCode(this.x2);
            return hash;
        }
    }
}