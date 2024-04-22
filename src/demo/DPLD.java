/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demo;

import minig.classification.mdd.MDD;
import minig.classification.mdd.MDDnode;
import minig.data.core.attribute.Attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * @author petok
 */
public class DPLD {
    //computation helper attributes and methods
    private HashMap<Integer, Integer> logicalLevelMemo;
    private HashMap<MDDnode, MDDnode> memo;
    private ArrayList<MDDnode> terminals;
    private HashMap<Tuple, MDDnode> applyCache;
    private HashMap<MDD.InternalNodeKey, MDDnode> uniqueNodes;
    private Integer cofactorLogicalLevelNode;

    public void setLogicalLevelMemo(HashMap<Integer, Integer> logicalLevelMemo) {
        this.logicalLevelMemo = logicalLevelMemo;
    }

    public int getLevel(int attributeIndex) {
        return this.logicalLevelMemo.get(attributeIndex);
    }

    /**
     * Calculates integrated derivation type III of MDD by component index
     *
     * @param mdd   MDD
     * @param index component index
     * @param from  component state
     * @param to    component state
     * @param j     input to transform functions
     * @return derivated MDD
     */
    public MDD IDPLDtypeIII(MDD mdd, int index, int from, int to, int j) {

        //transformation functions
        Function<Integer, Integer> lowerThanOne = x -> (x < j) ? 1 : 0;
        Function<Integer, Integer> atLeastOne = x -> (x >= j) ? 1 : 0;

        //apply function
        BinaryOperator<Integer> binaryAND = (x, y) -> x == 1 && y == 1 ? 1 : 0;

        //dpld computation
        MDD result = universalIDPLD(mdd, index, from, to, lowerThanOne, atLeastOne, binaryAND);

        return result;
    }

    /**
     * Calculates integrated derivatation type I of MDD by component index
     *
     * @param mdd   MDD
     * @param index component index
     * @param from  component state
     * @param to    component state
     * @param j     input to transform functions
     * @return derivated MDD
     */
    public MDD IDPLDtypeI(MDD mdd, int index, int from, int to, int j) {

        //transformation functions
        Function<Integer, Integer> equalsJ = x -> (x == j) ? 1 : 0;
        Function<Integer, Integer> lowerThanJ = x -> (x < j) ? 1 : 0;

        //apply function
        BinaryOperator<Integer> binaryAND = (x, y) -> x == 1 && y == 1 ? 1 : 0;

        //dpld computation
        MDD result = universalIDPLD(mdd, index, from, to, equalsJ, lowerThanJ, binaryAND);

        return result;
    }

    /**
     * returns derivated MDD by component index
     *
     * @param mdd   MDD to be derivated
     * @param index component index
     * @param from  component state
     * @param to    component state
     * @return derivated MDD
     */
    public MDD standardDPLD(MDD mdd, int index, int from, int to) {

        //apply function
        BinaryOperator<Integer> equals = (x, y) -> Objects.equals(x, y) ? 1 : 0;

        //dpld computation
        return universalDPLD(mdd, index, from, to, equals);
    }

    /**
     * Calculates any integrated derivation of MDD by component index apply and transformation functions
     *
     * @param mdd                    MDD
     * @param index                  component index
     * @param from                   component state
     * @param to                     component state
     * @param leftTransformFunction  lambda function applied to left cofactor part
     * @param rightTransformFunction lambda function applied to right cofactor part
     * @param applyFunction          lambda function for Apply algorithm
     * @return derivated MDD
     */
    public MDD universalIDPLD(MDD mdd, int index, int from, int to, Function<Integer, Integer> leftTransformFunction, Function<Integer, Integer> rightTransformFunction, BinaryOperator<Integer> applyFunction) {

        //cofactor computation
        MDD lhsCofactor = cofactor(mdd, index, from);
        MDD rhsCofactor = cofactor(mdd, index, to);

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
        return apply(lhs, rhs, applyFunction);
    }

    /**
     * Calculates any non-integrated derivation of MDD by component index and apply  function
     *
     * @param mdd           MDD
     * @param index         component index
     * @param from          component state
     * @param to            component state
     * @param applyFunction lambda function for Apply algorithm
     * @return derivated MDD
     */
    public MDD universalDPLD(MDD mdd, int index, int from, int to, BinaryOperator<Integer> applyFunction) {

        //cofactor computation
        MDD lhsCofactor = cofactor(mdd, index, from);
        MDD rhsCofactor = cofactor(mdd, index, to);
        if (lhsCofactor == null || rhsCofactor == null) {
            return null;
        }

        //diagrams combination
        return apply(lhsCofactor, rhsCofactor, applyFunction);
    }

    /**
     * @param diagram MDD
     * @param index   component index
     * @param state   component state
     * @return cofactored MDD
     */
    public MDD cofactor(MDD diagram, int index, int state) {
        MDDnode root = diagram.getRoot();

        if (root.isLeaf()) {
            return diagram;

        } else if (root.getAsocAttr().getAttributeIndex() == index) {

            var sons = root.getChildren();
            if (sons.size() > state) {
                return new MDD(sons.get(state));
            }

        } else {
            memo = new HashMap<>();
            cofactorLogicalLevelNode = null;
            return new MDD(cofactorStep(root, index, state));
        }

        return null;
    }

    /**
     * Helper method for Cofactor algorithm
     *
     * @param node           MDDnode in diagram
     * @param componentIndex component index
     * @param state          component state
     */
    public MDDnode cofactorStep(MDDnode node, int componentIndex, int state) {
        // Check if the cofactor of the node has already been computed
        if (memo.containsKey(node)) {
            return memo.get(node);
        }

        // Check if the node is a terminal node
        if (node.isLeaf()) {
            return node;
        }

        int index = node.getAsocAttr().getAttributeIndex();

        // Check if the index of the node is equal to componentIndex
        if (index == componentIndex) {
            cofactorLogicalLevelNode = node.getLogicalLevel();
            var sons = node.getChildren();

            if (sons.size() > state) {
                return sons.get(state);
            } else {
                return null;
            }
        }

        // Check if the index of the node is greater than componentIndex
        if (cofactorLogicalLevelNode != null && node.getLogicalLevel() > this.cofactorLogicalLevelNode) {
            return node;
        }

        var sons = node.getChildren();

        // Compute the cofactor of the node
        int mj = sons.size();
        MDDnode[] cofactoredSons = new MDDnode[mj];
        for (int k = 0; k < mj; k++) {
            MDDnode oldSon = sons.get(k);
            cofactoredSons[k] = cofactorStep(oldSon, componentIndex, state);
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

    /**
     * Transforms diagram leaves set to other set based on lambda function
     *
     * @param diagram MDD
     * @param gamma   transform lambda function
     */
    public MDD transform(MDD diagram, Function<Integer, Integer> gamma) {
        MDDnode root = diagram.getRoot();
        memo = new HashMap();
        terminals = new ArrayList();
        uniqueNodes = new HashMap<>();
        MDDnode newRoot = transformStep(root, gamma);
        MDD newMDD = new MDD(newRoot);
        return newMDD;
    }

    /**
     * Helper method for Transform algorithm
     *
     * @param node  MDDnode in diagram
     * @param gamma transform lambda function
     */
    public MDDnode transformStep(MDDnode node, Function<Integer, Integer> gamma) {
        if (node.isLeaf()) {
            int newOutput = gamma.apply((int) node.getOutputClass());
            return createTerminalNode(node.getAsocAttr(), newOutput);
        }
        // Check if the transform of the node has already been computed
        if (memo.containsKey(node)) {
            return memo.get(node);
        }

        var sons = node.getChildren();
        MDDnode[] newSons = new MDDnode[sons.size()];

        for (int k = 0; k < sons.size(); k++) {
            MDDnode oldSon = sons.get(k);
            newSons[k] = transformStep(oldSon, gamma);
        }

        MDDnode newNode = createInternalNode(node, newSons);

        // Memorize the result
        memo.put(node, newNode);

        return newNode;
    }

    /**
     * Creates new internal MDDnode by copying old node
     *
     * @param oldNode MDDnode
     * @param sons    new sons
     * @return new MDDnode
     */
    public MDDnode createInternalNode(MDDnode oldNode, MDDnode[] sons) {
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

    /**
     * Creates new leaf or returns existing with outputClass
     *
     * @return new or existing Leaf node
     */
    public MDDnode createTerminalNode(Attribute a, int outputClass) {
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

    /**
     * Creates new MDD by joining two MDDs together
     *
     * @param left  MDD
     * @param right MDD
     * @param op    Apply operation applied to leaves
     * @return merged MDD
     */
    public MDD apply(MDD left, MDD right, BinaryOperator<Integer> op) {
        applyCache = new HashMap<>();
        this.terminals = new ArrayList<MDDnode>();
        uniqueNodes = new HashMap<>();
        MDDnode root = applyStep(left.getRoot(), right.getRoot(), op);
        return new MDD(root);
    }

    /**
     * Helper method for Apply algorithm, merges two nodes and their sons
     *
     * @param left  MDDnode from left MDD
     * @param right MDDnode from right MDD
     * @param op    lambda operation applied to leaves
     * @return merged MDDnode
     */
    MDDnode applyStep(MDDnode left, MDDnode right, BinaryOperator<Integer> op) {
        if (applyCache.containsKey(new Tuple(left, right))) {
            return applyCache.get(new Tuple(left, right));
        }

        MDDnode node;

        if (left.isLeaf() && right.isLeaf()) {
            int outputClass = op.apply((int) left.getOutputClass(), (int) right.getOutputClass());
            node = createTerminalNode(left.getAsocAttr(), outputClass);

        } else if (right.isLeaf()) {

            var leftChildren = left.getChildren();
            MDDnode[] sons = new MDDnode[leftChildren.size()];
            for (int k = 0; k < leftChildren.size(); k++) {
                sons[k] = applyStep(leftChildren.get(k), right, op);
            }
            node = createInternalNode(left, sons);

        } else if (left.isLeaf()) {

            var rightChildren = right.getChildren();
            var sons = new MDDnode[rightChildren.size()];
            for (int k = 0; k < rightChildren.size(); k++) {
                sons[k] = applyStep(left, rightChildren.get(k), op);
            }
            node = createInternalNode(right, sons);

        } else {

            int ilhs = getLevel(left.getAsocAttr().getAttributeIndex());
            int irhs = getLevel(right.getAsocAttr().getAttributeIndex());

            MDDnode[] sons;
            if (ilhs < irhs) {

                var leftChildren = left.getChildren();
                sons = new MDDnode[leftChildren.size()];
                for (int k = 0; k < leftChildren.size(); k++) {
                    sons[k] = applyStep(leftChildren.get(k), right, op);
                }
                node = createInternalNode(left, sons);

            } else if (ilhs == irhs && left.getChildren().size() == right.getChildren().size()) {

                var rightChildren = right.getChildren();
                var leftChildren = left.getChildren();
                sons = new MDDnode[rightChildren.size()];
                for (int k = 0; k < rightChildren.size(); k++) {
                    sons[k] = applyStep(leftChildren.get(k), rightChildren.get(k), op);
                }
                node = createInternalNode(right, sons);

            } else {

                var rightChildren = right.getChildren();
                sons = new MDDnode[rightChildren.size()];
                for (int k = 0; k < rightChildren.size(); k++) {
                    sons[k] = applyStep(left, rightChildren.get(k), op);
                }
                node = createInternalNode(right, sons);
            }

        }

        applyCache.put(new Tuple(left, right), node);
        return node;
    }

    /**
     * Helper structure
     *
     * @param <T>
     */
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